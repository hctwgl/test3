package com.ald.jsd.admin.server.webapp;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.ald.jsd.admin.ioc.start.Bootstrap4Jetty;

/**
 * Jetty嵌入式启动类
 * 
 * @author rongbo
 *
 */
public class JettyServerStart {
	private int port;
	private String context;
	private String webappPath;
	private int scanIntervalSeconds;
	private boolean jmxEnabled;
	private Server server;
	private WebAppContext webapp;

	private String[] _dftConfigurationClasses = { "org.eclipse.jetty.webapp.WebInfConfiguration",
			"org.eclipse.jetty.webapp.WebXmlConfiguration", "org.eclipse.jetty.webapp.MetaInfConfiguration",
			"org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
			"com.ald.fanbei.api.server.webapp.FanbeiConfiguration" };

	public static final String[] IGNORE_DIRS = { ".git", "classes", "test-classes", ".settings", "target", "test" };

	public JettyServerStart(String webappPath, int port, String context) {
		this(webappPath, port, context, 0, false);
	}

	public JettyServerStart(String webappPath, int port, String context, int scanIntervalSeconds, boolean jmxEnabled) {
		this.webappPath = webappPath;
		this.port = port;
		this.context = context;
		this.scanIntervalSeconds = scanIntervalSeconds;
		this.jmxEnabled = jmxEnabled;
		validateConfig();
	}

	private void validateConfig() {
		if (port < 0 || port > 65536) {
			throw new IllegalArgumentException("Invalid port of web server: " + port);
		}
		if (context == null) {
			throw new IllegalStateException("Invalid context of web server: " + context);
		}
		if (webappPath == null) {
			throw new IllegalStateException("Invalid context of web server: " + webappPath);
		}
	}

	public void start() {
		if (server == null || server.isStopped()) {
			try {
				doStart();
			} catch (Throwable e) {
				e.printStackTrace();
				System.err.println("System.exit() ......");
				System.exit(1);
			}
		} else {
			throw new RuntimeException("Jetty Server already started.");
		}
	}

	private void doStart() throws Throwable {
		if (!portAvailable(port)) {
			throw new IllegalStateException("port: " + port + " already in use!");
		}
		System.setProperty("org.eclipse.jetty.util.URI.charset", "UTF-8");
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.Slf4jLog");
		System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", "20000000");
		server = new Server(port);
		server.setHandler(getHandler());
		if (jmxEnabled) {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
			server.addBean(mBeanContainer);
		}
		if (scanIntervalSeconds > 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						//由于jetty内置的Scanner监听文件修改效率极低，采用Nio监控文件修改
						// startFileWatchScanner();
						startNioFileWatcher();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();

		}
		long ts = System.currentTimeMillis();

		server.start();
		ts = System.currentTimeMillis() - ts;
		System.err.println("Jetty Server started: " + String.format("%.2f sec", ts / 1000d));
		server.join();
	}

	protected Handler getHandler() {
		webapp = new WebAppContext(webappPath, context);
		webapp.setConfigurationClasses(_dftConfigurationClasses);
		return webapp;
	}

	@SuppressWarnings("rawtypes")
	private void startNioFileWatcher() throws Exception {
		List<File> scanList = new ArrayList<File>();
		getAllDirectory(new File(Bootstrap4Jetty.ROOT_PATH).getParentFile(), scanList);
		FileSystem fileSystem = FileSystems.getDefault();
		WatchService watcher = fileSystem.newWatchService();
		for (File dir : scanList) {
			Path listenPath = Paths.get(dir.getAbsolutePath());
			listenPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);
		}
		while (true) {
			WatchKey watckKey = watcher.take();
			List<WatchEvent<?>> events = watckKey.pollEvents();
			for (WatchEvent event : events) {
				if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
					System.out.println("file create: " + event.context().toString());
					webapp.stop();
					webapp.start();
					System.out.println("Loading complete.\n");
					break;
				}
				if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
					System.out.println("file delete: " + event.context().toString());
					webapp.stop();
					webapp.start();
					System.out.println("Loading complete.\n");
					break;
				}
				if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
					System.out.println("file modify: " + event.context().toString());
					webapp.stop();
					webapp.start();
					System.out.println("Loading complete.\n");
					break;
				}
			}
			watckKey.reset();
		}

	}

	private static boolean portAvailable(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}
			if (ss != null)
				try {
					ss.close();
				} catch (IOException e) {
				}
		}
		return false;
	}

	public void getAllDirectory(File file, List<File> subDirList) {
		File[] files = file.listFiles();
		for (File subFile : files) {
			if (subFile.isDirectory()) {
				String path = subFile.getAbsolutePath();
				boolean ignore = false;
				for (String ignoreDir : IGNORE_DIRS) {
					if (path.contains(ignoreDir)) {
						ignore = true;
						break;
					}
				}
				if (ignore == false) {
					getAllDirectory(subFile, subDirList);
					subDirList.add(new File(path));
				}
			}
		}
	}
}
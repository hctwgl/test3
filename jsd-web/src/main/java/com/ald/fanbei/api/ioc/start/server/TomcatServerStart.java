package com.ald.fanbei.api.ioc.start.server;

import java.io.File;
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

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import com.ald.fanbei.api.ioc.start.Bootstrap4Tomcat;
/**
 * tomcat嵌入式启动类
 * @author rongbo
 *
 */
public class TomcatServerStart {

	private int port;
	private String context;
	private String webappPath;
	private boolean autoDeploy = false;
	private StandardContext ctx;
	
	public static final String[] IGNORE_DIRS = { ".git", "classes", "test-classes", ".settings", "target", "test" ,"tomcat"};


	PlaceHolderParser placeHolderParser = new PlaceHolderParser();

	public TomcatServerStart(int port, String context, String webappPath, boolean autoDeploy) {
		this.port = port;
		this.context = context;
		this.webappPath = webappPath;
		this.autoDeploy = autoDeploy;
	}

	public void start() throws Exception {
		placeHolderParser.repalce();
		String tmpDir = System.getProperty("java.io.tmpdir");
		String autoConfigPath = tmpDir + "/jettty_fanbei_api/WEB-INF/classes";

		Tomcat tomcat = new Tomcat();
		tomcat.setPort(port);
		this.ctx = (StandardContext) tomcat.addWebapp(this.context, webappPath);
		tomcat.getHost().setAutoDeploy(autoDeploy);
		
		File additionWebInfClasses = new File(autoConfigPath);
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(
				new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
		ctx.setResources(resources);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					startNioFileWatcher();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		tomcat.start();
		tomcat.getServer().await();
		
	}
	
	@SuppressWarnings("rawtypes")
	private void startNioFileWatcher() throws Exception {
		List<File> scanList = new ArrayList<File>();
		getAllDirectory(new File(Bootstrap4Tomcat.ROOT_PATH).getParentFile(), scanList);
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
					this.ctx.reload();
					System.out.println("Loading complete.\n");
					break;
				}
				if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
					System.out.println("file delete: " + event.context().toString());
					this.ctx.reload();
					System.out.println("Loading complete.\n");
					break;
				}
				if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
					System.out.println("file modify: " + event.context().toString());
					this.ctx.reload();
					System.out.println("Loading complete.\n");
					break;
				}
			}
			watckKey.reset();
		}

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

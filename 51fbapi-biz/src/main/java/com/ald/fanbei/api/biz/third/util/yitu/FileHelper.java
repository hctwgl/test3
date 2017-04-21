package com.ald.fanbei.api.biz.third.util.yitu;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.codec.binary.Base64;

public class FileHelper {

	/**
	 * 按字符读取文件
	 * 
	 * @param fileName
	 * @return 字符串的文件内容
	 */
	public static String readFile(String fileName) {

		String result = "";
		File file = new File(fileName);
		if (file.exists()) {
			Reader reader = null;
			try {
				// 一次读一个字符
				reader = new InputStreamReader(new FileInputStream(file));
				int tempchar;
				while ((tempchar = reader.read()) != -1) {
					result = result + (char) (tempchar);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No such file");
		}

		return result;
	}

	/**
	 * 按字符保存
	 * 
	 * @param content
	 *            , 保存内容
	 * @param filepath
	 *            , 文件路径
	 */
	public static void saveFile(final String content, final String filepath) {

		try {
			FileWriter fw = new FileWriter(filepath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 按二进制方式读取文件
	 * 
	 * @param fileName
	 * @return 二进制数组的文件内容
	 */
	public static byte[] readBinaryFile(String fileName) {

		byte[] result = null;
		byte[] tempBytes = new byte[256];
		File file = new File(fileName);
		if (file.exists()) {
			try {
				// 一次读一个字符
				int byteread = 0;
				InputStream in = new FileInputStream(file);
				while ((byteread = in.read(tempBytes)) != -1) {
					result = new byte[byteread];
					System.arraycopy(tempBytes, 0, result, 0, byteread);
				}
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
		return result;
	}

	/**
	 * 按二进制保存文件
	 * 
	 * @param content
	 *            , 保存内容
	 * @param filepath
	 *            , 文件路径
	 */
	public static void saveBinaryFile(final byte[] content,
			final String filepath) {
		File file = new File(filepath);
		try {
			BufferedOutputStream fos = new BufferedOutputStream(
					new FileOutputStream(file));
			fos.write(content, 0, content.length);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 读取图片文件内容，并转为Base64编码
	 * 
	 * @param filePath 文件路径
	 * @return 图片内容Base64编码的字符串
	 */
	public static String getImageBase64Content(String filePath)
			throws Exception {
		File imgFile = new File(filePath);
		byte[] bytes = null;
		try {
			InputStream is = new FileInputStream(imgFile);
			long length = imgFile.length();
			bytes = new byte[(int) length];

			int offset = 0, numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(bytes);
	}
}

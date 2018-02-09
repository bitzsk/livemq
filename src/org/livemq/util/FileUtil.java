package org.livemq.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <h1>文件操作类</h1>
 * <p></p>
 */
public class FileUtil {

	
	/**
	 * <h1>拷贝某文件到目标目录下</h1>
	 * <h2>注意事项：</h2>
	 * <ul>
	 * 		<li>必须使用全路径</li>
	 * 		<li>文件的全路径中必须使用 “\\”，可使用 file.getAbsolutePath()，使用 “/” 是错误</li>
	 * 		<li>cmd 的拷贝只能在文件已经存在的情况下进行(文件不能存在于缓冲区中)</li>
	 * </ul>
	 * 
	 * @param sour
	 *            源文件全路径
	 * @param dest
	 *            目标目录全路径
	 */
	public static Process copyFile(String sour, String dest) {
		// 获取进程
		Runtime run = Runtime.getRuntime();
		Process p = null;
		String command = "cmd /c copy " + sour + " " + dest;
		System.out.println(command);
		// 执行doc命令
		try {
			p = run.exec(command);
		} catch (Exception e) {
			System.err.println("文件拷贝失败：" + sour);
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 拷贝某目录下的所有文件到目标目录下
	 * 
	 * @param sour
	 *            源目录
	 * @param dest
	 *            目标补录
	 */
	public static void copyFiles(String sour, String dest) {
		// 获取进程
		Runtime run = Runtime.getRuntime();

		// 得到目标文件名
		File sourFile = new File(sour);
		String[] files = sourFile.list();

		String inputname = null;
		String command = null;

		for (String str : files) {
			inputname = sour + str;
			command = "cmd /c copy " + inputname + " " + dest;
			// 执行doc命令
			try {
				run.exec(command);
				System.out.println(command);
			} catch (Exception e) {
				System.err.println("文件拷贝失败：" + str);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 压缩
	 * @param src
	 * @param zip
	 */
	public static void zipFile(String src, String zip) {
		try {
			FileOutputStream zipFile = new FileOutputStream(zip);
			BufferedOutputStream buffer = new BufferedOutputStream(zipFile);
			ZipOutputStream out = new ZipOutputStream(buffer);
			zipFiles(src, out, "");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void zipFiles(String src, ZipOutputStream out, String prefix) throws IOException {
		File file = new File(src);
		if (file.isDirectory()) {
			if (file.listFiles().length == 0) {
				ZipEntry zipEntry = new ZipEntry(prefix + file.getName() + "/");
				out.putNextEntry(zipEntry);
				out.closeEntry();
			} else {
				prefix += file.getName() + File.separator;
				for (File f : file.listFiles())
					zipFiles(f.getAbsolutePath(), out, prefix);
			}
		} else {
			FileInputStream in = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(prefix + file.getName());
			out.putNextEntry(zipEntry);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.closeEntry();
			in.close();
		}
	}

	/**
	 * 解压
	 * @param bytes
	 * @param prefix
	 * @throws IOException
	 */
	public static void unzipFilesWithTier(byte[] bytes, String prefix) throws IOException {

		InputStream bais = new ByteArrayInputStream(bytes);
		ZipInputStream zin = new ZipInputStream(bais);
		ZipEntry ze;
		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				File file = new File(prefix + ze.getName());
				if (!file.exists())
					file.mkdirs();
				continue;
			}
			File file = new File(prefix + ze.getName());
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			ByteArrayOutputStream toScan = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = zin.read(buf)) > 0) {
				toScan.write(buf, 0, len);
			}
			byte[] fileOut = toScan.toByteArray();
			toScan.close();
			writeByteFile(fileOut, new File(prefix + ze.getName()));
		}
		zin.close();
		bais.close();
	}

	/**
	 * 读取文件的字节流
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static byte[] readFileByte(String filename) throws IOException{

		if (filename == null || filename.equals("")) {
			throw new NullPointerException("File is not exist!");
		}
		File file = new File(filename);
		long len = file.length();
		byte[] bytes = new byte[(int) len];

		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		int r = bufferedInputStream.read(bytes);
		if (r != len){
			throw new IOException("Read file failure!");
		}
		bufferedInputStream.close();

		return bytes;

	}
	
	@SuppressWarnings("resource")
	public static byte[] readFileByte(File file) throws IOException{
		long len = file.length();
		byte[] bytes = new byte[(int) len];

		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		int r = bufferedInputStream.read(bytes);
		if (r != len){
			throw new IOException("Read file failure!");
		}
		bufferedInputStream.close();

		return bytes;

	}
	
	public static byte[] read(File file){
		byte[] bytes = new byte[0];
		FileInputStream fis = null;
		FileChannel channel = null;
		try {
			fis = new FileInputStream(file);
			channel = fis.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
			while(channel.read(buffer) > 0){
				System.out.println("读取文件中");
			}
			bytes = buffer.array();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(channel != null) channel.close();
				if(fis != null) fis.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return bytes;
	}

	/**
	 * 将字节流写入文件
	 * @param bytes
	 * @param file
	 * @return
	 */
	public static void writeByteFile(byte[] bytes, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 压缩文件或文件夹
	 * 
	 * @param src
	 *            源文件或文件夹全路径
	 * @param zip
	 *            目标文件全路径
	 */
	public static void zip(String src, String zip) {
		System.out.println("压缩中...");
		System.out.println("src: " + src);
		System.out.println("zip: " + zip);
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zip));
			File srcFile = new File(src);
			compress(out, srcFile);
			System.out.println("压缩完成");
		} catch (Exception e) {
			System.err.println("压缩输出失败");
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception ex) {
				System.err.println("压缩输出流关闭失败");
				ex.printStackTrace();
			}
		}
	}

	private static void compress(ZipOutputStream out, File source) {
		String base = source.getName();
		// 如果路径为目录（文件夹）
		if (source.isDirectory()) {
			System.out.println("dir: " + base);
			// 取出文件夹中的文件（或子文件夹）
			File[] files = source.listFiles();
			// 如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
			if (files.length == 0) {
				try {
					out.putNextEntry(new ZipEntry(base + "/"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
			else {
				for (File file : files) {
					compress(out, file);
				}
			}
		}
		// 如果不是目录（文件夹），即是文件，则先写入目录，之后再将文件写入zip文件中
		else {
			System.out.println("file: " + base);

			FileInputStream fos = null;
			BufferedInputStream bis = null;
			try {
				out.putNextEntry(new ZipEntry(base));
				fos = new FileInputStream(source);
				bis = new BufferedInputStream(fos);
				int tag;
				while ((tag = bis.read()) != -1) {
					out.write(tag);
				}
			} catch (Exception e) {
				System.err.println("压缩输入失败");
				e.printStackTrace();
			} finally {
				try {
					if (bis != null) {
						bis.close();
					}
					if (fos != null) {
						fos.close();
					}
				} catch (Exception ex) {
					System.err.println("压缩输入流关闭失败");
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 删除文件或文件夹
	 * @param file
	 */
	public static void deleteFile(File file){
		if(file.exists()){
			if(file.isFile()){
				file.delete();
			}else if(file.isDirectory()){
				File[] files = file.listFiles();
				for(int i = 0;i < files.length;i++){
					deleteFile(files[i]);
				}
				file.delete();
			}
		}else{
			System.err.println("所删文件:" + file.getAbsolutePath() + " 不存在");
		}
	}

}

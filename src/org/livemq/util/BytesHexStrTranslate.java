package org.livemq.util;

import java.io.File;
import java.io.FileOutputStream;

public class BytesHexStrTranslate {

	private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	/**
	 * 方法一： byte[] to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexFun1(byte[] bytes) {
		// 一个byte为8位，可用两个十六进制位标识
		char[] buf = new char[bytes.length * 2];
		int a = 0;
		int index = 0;
		for (byte b : bytes) { // 使用除与取余进行转换
			if (b < 0) {
				a = 256 + b;
			} else {
				a = b;
			}

			buf[index++] = HEX_CHAR[a / 16];
			buf[index++] = HEX_CHAR[a % 16];
		}

		return new String(buf);
	}

	/**
	 * 方法二： byte[] to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexFun2(byte[] bytes) {
		char[] buf = new char[bytes.length * 2];
		int index = 0;
		for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
			buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
			buf[index++] = HEX_CHAR[b & 0xf];
		}

		return new String(buf);
	}

	/**
	 * 方法三： byte[] to hex string
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHexFun3(byte[] bytes) {
		StringBuilder buf = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) { // 使用String的format方法进行转换
			buf.append(String.format("%02x", new Integer(b & 0xff)));
			buf.append(" ");
		}
		return buf.toString();
	}

	/**
	 * 将16进制字符串转换为byte[]
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] toBytes(String str) {
		if (str == null || str.trim().equals("")) {
			return new byte[0];
		}
		str = str.replace(" ", "").replace("\n", "");
		
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}

		return bytes;
	}
	
	/**
	 * 将 byte 数组转换为16进制字符串
	 * 目前业务中需要用到的
	 * @author w.x
	 * @date 2018年2月9日 下午3:47:06
	 */
	public static String[] bytes2Hex(byte[] bytes){
		String[] arrs = new String[bytes.length];
//		StringBuilder buf = new StringBuilder(bytes.length * 2);
		for(int i = 0;i < bytes.length;i++){
			arrs[i] = String.format("%02x", new Integer(bytes[i] & 0xff));
//			buf.append(String.format("%02x", new Integer(bytes[i] & 0xff)));
//			if((i+1) % 2 == 0){
//				if((i+1) % 16 == 0){
//					buf.append("\n");
//				}else{
//					buf.append(" ");
//				}
//			}
		}
		return arrs;
	}
	
	/**
	 * 将16进制字符串转换为 byte 数组
	 * 目前业务中需要用到的
	 * @author w.x
	 * @date 2018年2月9日 下午3:47:34
	 */
	public static byte[] hex2Bytes(String str) {
		if (str == null || str.trim().equals("")) {
			return new byte[0];
		}
		str = str.replace(" ", "").replace("\n", "");
		
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}

		return bytes;
	}
	
	public static void main(String[] args) {
		//文字
		String content = "大家好，我是 Jack。";
		byte[] bytes = content.getBytes();
		String[] arrs = bytes2Hex(bytes);
		System.out.println(arrs.length);
		StringBuilder builder = new StringBuilder(arrs.length);
		for(int i = 0;i < arrs.length;i++){
			builder.append(arrs[i]);
			if((i+1) % 2 == 0){
				if((i+1) % 16 == 0){
					builder.append("\n");
				}else{
					builder.append(" ");
				}
			}
		}
		System.out.println(builder.toString());
		byte[] bs = hex2Bytes(builder.toString());
		System.out.println(new String(bs));
		
		int qos = 1;
		String qs = String.format("%02x", new Integer(qos & 0xff));
		System.out.println("qs:" + qs);
		
		String[] array = new String[arrs.length + 1];
		array[0] = qs;
		System.arraycopy(arrs, 0, array, 1, arrs.length);
		System.out.println(array.length);
		builder = new StringBuilder(array.length);
		for(int i = 0;i < array.length;i++){
			builder.append(array[i]);
			if((i+1) % 2 == 0){
				if((i+1) % 16 == 0){
					builder.append("\n");
				}else{
					builder.append(" ");
				}
			}
		}
		System.out.println(builder.toString());
		bs = hex2Bytes(builder.toString());
		System.out.println(new String(bs));
		
		System.out.println(bs[0]);
		
		
		

		
		
		
		
		
		
//		String[] array = new String[arrs.length + 1];
//		array[1] = String.valueOf(1);
//		System.arraycopy(arrs, 0, array, 0, arrs.length);
//		StringBuilder builder = new StringBuilder();
//		String str = "";
//		for(int i = 0;i < array.length;i++){
//			builder.append(array[i]);
//			if((i+1) % 2 == 0){
//				if((i+1) % 16 == 0){
//					builder.append("\n");
//				}else{
//					builder.append(" ");
//				}
//			}
//		}
//		str = builder.toString();
//		System.out.println(str);
//		byte[] result = hex2Bytes(str);
		
//		//文件
//		String path = "C:/Users/Administrator/Desktop/mq.txt";
//		File file = new File(path);
//		bytes = FileUtil.read(file);
//		arrs = bytes2Hex(bytes);
//		array = new String[arrs.length + 1];
//		array[1] = String.valueOf(1);
//		System.arraycopy(arrs, 0, array, 1, arrs.length);
//		builder = new StringBuilder();
//		str = "";
//		for(int i = 0;i < array.length;i++){
//			if((i+1) % 2 == 0){
//				if((i+1) % 16 == 0){
//					builder.append("\n");
//				}else{
//					builder.append(" ");
//				}
//			}
//		}
//		str = builder.toString();
//		result = hex2Bytes(str);
		
		//将转换后的16进制字符串写出到文件中
//		FileOutputStream fos = null;
//		File target = null;
//		try {
//			target = new File("C:/Users/Administrator/Desktop/target.mq");
//			fos = new FileOutputStream(target);
//			fos.write(2);
//			fos.write(str.getBytes(), 0, str.getBytes().length);
//			fos.getFD().sync();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			try {
//				if(fos != null) fos.close();
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//		
//		StringBuffer buffer = null;
//		System.out.println("==================原 byte 数组==================");
//		System.out.println(bytes);
//		System.out.println(bytes.length);
//		buffer = new StringBuffer();
//		for(byte b : bytes){
//			buffer.append(b + ",");
//		}
//		System.out.println(buffer.toString());
//		System.out.println("==================转换后的 16 进制字符串==================");
//		System.out.println(str);
//		System.out.println("==================16 进制字符串转为 byte 数组==================");
//		System.out.println(result);
//		System.out.println(result.length);
//		buffer = new StringBuffer();
//		for(byte b : bytes){
//			buffer.append(b + ",");
//		}
//		System.out.println(buffer.toString());
	}
}

package org.livemq.util;

public class StringUtil {

	/**
	 * 字符串空判断
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || str.trim().equals("") || str.trim().length() == 0){
			return true;
		}
		return false;
	}

	/**
	 * 字符串非空判断
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		if(isEmpty(str)){
			return false;
		}
		return true;
	}
	
	/**
	 * 字符串转为二进制串
	 * @param str
	 * @return
	 */
	public static String str2Binary(String str){
		char[] arrs = str.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i = 0;i < arrs.length;i++){
			buffer.append(Integer.toBinaryString(arrs[i]) + " ");
			if((i % 2) == 0){
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 二进制字符串转普通字符串
	 * @param str
	 * @return
	 */
	public static String binary2Str(String str){
		return str;
	}
	
	private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', 
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	/**
	 * 转换 byte 数组为十六进制编码
	 * @param bytes
	 * @return
	 */
	public static String bytes2Hex(byte[] bytes){
		int a = 0;
		StringBuilder builder = new StringBuilder();
		for(int i = 0;i < bytes.length;i++){
			byte b = bytes[i];
			if(bytes[i] < 0){
				a = 256 + b;
			}else{
				a = b;
			}
			builder.append(HEX_CHAR[a / 16]);
			builder.append(HEX_CHAR[a % 16]);
			
			if((i+1) % 2 == 0 && (i+1) % 16 != 0){
				builder.append(" ");
			}
			if((i+1) % 16 == 0){
				builder.append("\n");
			}
		}
		return new String(builder.toString());
	}
	
	/**
	 * 十六进制编码转换为 byte 数组
	 * @param str
	 * @return
	 */
	public static byte[] hex2Bytes(String str){
		if(str == null || str.trim().equals("")){
			return new byte[0];
		}
		str = str.trim().replace(" ", "").replace("\n", "");
		
		byte[] bytes = new byte[str.length() / 2];
		String sub = null;
		for(int i = 0;i < str.length() / 2;i++){
			sub = str.substring(i*2, i*2+2);
			bytes[i] = (byte) Integer.parseInt(sub, 16);
		}
		return bytes;
	}
	
	public static void main(String[] args) {
		String source = "地方搜房开始对方发送了发快递傻大姐奥斯卡佛挡杀佛考虑打开吗abc";
		System.out.println(source.getBytes().length);
		String fun1 = bytes2Hex(source.getBytes());
		String fun2 = bytes2Hex(source.getBytes());
		System.out.println(fun1);
		System.out.println(fun2);
		
		byte[] b1 = hex2Bytes(fun1);
		byte[] b2 = hex2Bytes(fun2);
		System.out.println(new String(b1));
		System.out.println(new String(b2));
	}
}

package org.livemq.util;

public class ByteUtil {

	public static void main(String[] args) {
		byte[] bytes = new byte[2];
		bytes[0] = 0;
		bytes[1] = 10;
		int result = bytes2int(bytes);
		System.out.println(result);
	}
	
	/**
	 * 将 byte 转换为一个长度为 8 的 byte 数组，数组每个值代表 bit
	 * @param 1 byte -64
	 * @return byte[8] [1, 1, 0, 0, 0, 0, 1, 0]
	 */
	public static byte[] byte2BitArray(byte b) {  
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;  
    }
	
	/**
	 * 二进制字符串转 byte
	 * @param bitStr "01100010"
	 * @return 1 byte -64
	 */
	public static byte bitString2Byte(String bitStr){
		int re = 0, len;
		if(null == bitStr){
			return 0;
		}
		len = bitStr.length();
		if(len != 4 && len != 8){
			return 0;
		}
		// 8 bit 处理
		if(len == 8){
			// 正数
			if(bitStr.charAt(0) == '0'){
				re = Integer.parseInt(bitStr, 2);
			}
			// 负数
			else{
				re = Integer.parseInt(bitStr, 2) - 256;
			}
		}
		// 4 bit 处理
		else if(len == 4){
			re = Integer.parseInt(bitStr, 2);
		}
		return (byte) re;
	}
	
	/**
	 * int 转为 byte 数组
	 * 高位在前，低位在后
	 * @param num
	 * @return
	 */
	public static byte[] int2bytes(int num){
		byte[] result = new byte[4];  
        result[0] = (byte)((num >>> 24) & 0xff);//说明一  
        result[1] = (byte)((num >>> 16)& 0xff );  
        result[2] = (byte)((num >>> 8) & 0xff );  
        result[3] = (byte)((num >>> 0) & 0xff );  
        return result;
	}
	
	/**
	 * byte 数组转 int
	 * 高位在前，低位在后
	 * @param bytes
	 * @return
	 */
	public static int bytes2int(byte[] bytes){
//		int result = 0;  
//        if(bytes.length == 4){  
//            int a = (bytes[0] & 0xff) << 24;//说明二  
//            int b = (bytes[1] & 0xff) << 16;  
//            int c = (bytes[2] & 0xff) << 8;  
//            int d = (bytes[3] & 0xff);  
//            result = a | b | c | d;  
//        }  
//        return result;
		
		int result = 0;  
        if(bytes.length == 4){  
            int a = (bytes[0] & 0xff) << 8;  
            int b = (bytes[1] & 0xff);  
            result = a | b;  
        }  
        return result;
	}
	
}

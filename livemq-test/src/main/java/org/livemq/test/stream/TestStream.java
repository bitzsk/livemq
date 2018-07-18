package org.livemq.test.stream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.junit.Test;

public class TestStream {

	@Test
	public void testInputStream() {
		int n = 1000000;
		byte[] bytes = generateRandomArray(n, 0, n);
//		show(bytes);
		
		long s1 = System.currentTimeMillis();
		ByteArrayInputStream b1 = new ByteArrayInputStream(bytes);
		DataInputStream d1 = new DataInputStream(b1);
		try {
			for (int i = 0; i < bytes.length; i++) {
				int first = d1.readUnsignedByte();
//				System.out.print(first + " ");
			}
//			System.out.println();
			d1.close();
		} catch (IOException e) {}
		long e1 = System.currentTimeMillis();
		System.out.println("close time: " + (e1 - s1) + " ms");
		
		long s2 = System.currentTimeMillis();
		ByteArrayInputStream b2 = new ByteArrayInputStream(bytes);
		DataInputStream d2 = new DataInputStream(b2);
		try {
			for (int i = 0; i < bytes.length; i++) {
				int first = d2.readUnsignedByte();
//				System.out.print(first + " ");
			}
//			System.out.println();
		} catch (IOException e) {}
		long e2 = System.currentTimeMillis();
		System.out.println("not close time: " + (e2 - s2) + " ms");
	}
	
	public static void show(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			System.out.print(bytes[i] + " ");
		}
		System.out.println();
	}
	
	/**
	 * 生成 byte 数组
	 * @param n 数组的长度
	 * @param start 最小值
	 * @param end 最大值
	 * @return
	 */
	public static byte[] generateRandomArray(int n, int start, int end) {
		byte[] bytes = new byte[n];
		for(int i = 0;i < n;i++) {
			byte val = (byte) (Math.random() * (end - start+1) + start);
			bytes[i] = val;
		}
		return bytes;
	}
}

package org.livemq.test.bytes;

import org.junit.Test;

/**
 * java 十进制和二进制之间的相互转换
 * java 逻辑运算符 与(&)、非(~)、或(|)、异或(^)
 * java 右移，左移，无符号右移
 * 
 * 
 * 逻辑运算符与(&)、非(~)、或(|)、异或(^)：https://www.cnblogs.com/yesiamhere/p/6675067.html
 * 运算符优先级：https://blog.csdn.net/qq_35114086/article/details/70173329#AutoNumber1
 * 位移：http://baihe747.iteye.com/blog/2078029
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-13 11:07
 */
public class TestByte {

	/**
	 * 十进制转二进制
	 * 
	 * 原理：给定的数循环除以 2，直到商为 0 或者 1 为止。将每一步除的结果的余数记录下来，然后反过来就得到相应的二进制了。
	 * 
	 * 比如 8 转二进制，第一次除以 2 等于 4（余数 0），第二次除以 2 等于 2（余数 0），第三次除以 2 等于 1（余数 0），最后余数 1，
	 * 得到的余数依次是 0 0 0 1，反过来就是 1000
	 * 
	 * 计算机内部表示数的字节长度是固定的，比如 8 位，16 位，32 位。所以在高位补齐，java 中字节码是 8 位的，所以高位补齐就是 00001000
	 * 
	 * 写法位 (8) 10 = (00001000) 2;
	 */
	@Test
	public void testToBinary() {
		System.out.println(toBinary(8)); 	// 1000
		System.out.println(toBinary(9)); 	// 1001
		System.out.println(toBinary(10)); 	// 1010
	}
	
	/**
	 * 二进制转十进制
	 * 
	 * 比如 8 的二进制表示位 00001000，去掉补齐的高位就是 1000，此时从个位开始计算 2 的幂(个位是 0，依次往后推)乘以对应位数上的数，然后得到的值相加。
	 * 于是有了, (2 的 0 次幂)*0 + (2 的 1 次幂)*0 + (2 的 2 次幂)*0 + (2 的 3 次幂)*1 = 8
	 */
	@Test
	public void testBinary2Int() {
		System.out.println(binary2Int("1000"));		// 8
		System.out.println(binary2Int("1001"));		// 9
		System.out.println(binary2Int("1010"));		// 10
	}
	
	/**
	 * 位异或运算符(^)
	 * 
	 * 运算规则：两个数转为二进制，然后从高位开始比较，如果相同则为 0 ，不同则为 1。
	 * 
	 * 比如： 8 ^ 11
	 * 
	 * 8 转为二进制是 1000，11 转为二进制为 1011，从高位开始比较得到的是 0011，然后二进制转为十进制，就是 3
	 */
	@Test
	public void test1() {
		int a = 8;
		int b = 11;
		int c = a ^ b;
		System.out.println(a + " ^ " + b + " = " + c);
		
		System.out.println(a + " 的二进制为：" + toBinary(a));
		System.out.println(b + " 的二进制为：" + toBinary(b));
		
		String result = contrast1(toBinary(a), toBinary(b));
		System.out.println(result);
		
		System.out.println(binary2Int(result));
	}
	
	/**
	 * 与运算符(&)
	 * 
	 * 运算规则：两个数都转为二进制，然后从高位开始比较，如果两个数都为 1 则为 1，否则为 0。
	 * 比如：8 & 11
	 * 8 转为二进制是 1000，11 转为二进制为 1011，从高位开始比较得到的是 1000，然后二进制转为十进制，就是 8
	 */
	@Test
	public void test2() {
		int a = 8;
		int b = 11;
		int c = a & b;
		System.out.println(a + " & " + b + " = " + c);
		
		System.out.println(a + " 的二进制为：" + toBinary(a));
		System.out.println(b + " 的二进制为：" + toBinary(b));
		
		String result = contrast2(toBinary(a), toBinary(b));
		System.out.println(result);
		
		System.out.println(binary2Int(result));
	}
	
	/**
	 * 位或运算符(|)
	 * 
	 * 运算规则：两个数都转为二进制，然后从高位开始比较，两个数只要有一个为 1 则为 1，否则为 0。
	 * 比如：8 | 11
	 * 8 转为二进制是 1000，11 转为二进制为 1011，从高位开始比较得到的是 1011，然后二进制转为十进制，就是 11
	 */
	@Test
	public void test3() {
		int a = 8;
		int b = 11;
		int c = a | b;
		System.out.println(a + " | " + b + " = " + c);
		
		System.out.println(a + " 的二进制为：" + toBinary(a));
		System.out.println(b + " 的二进制为：" + toBinary(b));
		
		String result = contrast3(toBinary(a), toBinary(b));
		System.out.println(result);
		
		System.out.println(binary2Int(result));
	}
	
	/**
	 * 位非运算符(~)
	 * 
	 * 运算规则：如果位为 0，结果为 1，如果位为 1，结果为 0；
	 * 
	 * 注意：首先我们要知道在 Java 中，正数的二进制是它本身，负数的二进制是它的绝对值的二进制取反加1。
	 * 
	 * 在 Java 中，所有数据的表示方法都是以补码的形式表示，如果没有特殊说明，Java 中的数据类型默认是 int，
	 * int 数据类型的长度是 8 位，一位是四个字节，就是 32 字节，32bit
	 * 
	 * 如：~8
	 * 
	 * 8的二进制原码:	1000
	 * 8的二进制补码:	00000000 00000000 00000000 00001000  (该二进制也就是在 Java 中存储的 8 的二进制数)
	 * 非运算取反:  	11111111 11111111 11111111 11110111	 (~8 的二进制在 Java 中存储的格式)
	 * 
	 * 我们看到最高位是 1，所以是负数。而又因为负数的二进制是它的绝对值的二进制取反加1。
	 * 所以：
	 * 先减 1:		11111111 11111111 11111111 11110110
	 * 再取反:		00000000 00000000 00000000 00001001
	 * 得到该负数的绝对值的二进制为：1001
	 * 转换为十进制为：9
	 * 所以得到：~8 = -9
	 * 
	 * 如：~-8
	 * 
	 * 因为是负数，所以底层存储的是它的绝对值的二进制取反加1。
	 * 绝对值:8
	 * 绝对值的二进制原码:		1000
	 * 绝对值的二进制补码:		00000000 00000000 00000000 00001000
	 * 绝对值的二进制补码取反:	11111111 11111111 11111111 11110111
	 * 加1:					11111111 11111111 11111111 11111000   (该二进制也就是在 Java 中存储的 -8 的二进制数)
	 * 非运算取反:				00000000 00000000 00000000 00000111   (~-8 的二进制在 Java 中存储的格式)
	 * 
	 * 我们看到最高位是 0，所以是正数。
	 * 所以 ~-8 的二进制为： 111
	 * 转换为十进制为：7
	 * 所以得到：~-8 = 7
	 * 
	 */
	@Test
	public void test4() {
		int a = 8;
		int b = -8;
		System.out.println(~a);		// -9
		System.out.println(~b);		// 7
	}
	
	/**
	 * 左移、右移、无符号右移
	 * 
	 * 左移、右移
	 * 
	 * 正数(左移都是右边补零，右移都是左边补零)：
	 * 如：5 >> 2
	 * 将该正数 5 的二进制原码 101 向右移动 2 位，左边补零
	 * 00000000 00000000 00000000 00000101
	 * 00000000 00000000 00000000 00000001
	 * 1 转换为十进制为 1
	 * 
	 * 如：5 << 2
	 * 将该正数 5 的二进制原码 101 向左移动 2 位，右边补零
	 * 00000000 00000000 00000000 00000101
	 * 00000000 00000000 00000000 00010100
	 * 10100 转换为十进制为 20
	 * 
	 * 当位移的数大于等于 32 时，5 >> n(n >= 32) 或者 5 << n(n >= 32)
	 * 在 Java 中 int 为 32 位，当位移大于等于 32 时，先进行取模运算，再进行位移
	 * 5 >> (n % 32) 或者 5 << (n % 32)
	 * 
	 * 如：5 >> 32
	 * 首先取模：32 % 32 = 0
	 * 再进行位移：5 >> 0 = 5
	 * 
	 * 如：5 >> 34
	 * 首先取模：34 % 32 = 2
	 * 再进行位移：5 >> 2 = 1
	 * 
	 * 如：5 << 32
	 * 首先取模：32 % 32 = 0
	 * 再进行位移：5 << 0 = 5
	 * 
	 * 如：5 << 34
	 * 首先取模：34 % 32 = 2
	 * 再进行位移：5 << 2 = 20
	 * 
	 * 如：5 << 31
	 * 将该正数 5 的二进制原码 101 向左移动 31 位，右边补零
	 * 00000000 00000000 00000000 00000101
	 * 10000000 00000000 00000000 00000000
	 * 我们看到最高位为 1 是负数。
	 * 先减一：01111111 11111111 11111111 11111111
	 * 再取反：10000000 00000000 00000000 00000000
	 * 得到该数左移 31 位后的数为负数，并且该负数的绝对值的二进制为：10000000 00000000 00000000 00000000
	 * 转为十进制为 2147483647，所以 5 << 31 = -2147483647 TODO:但是这里算出来是错的，答案为：-2147483648
	 * 
	 * 
	 * 
	 * 负数：
	 * 如：-5 >> 1
	 * 首先将该负数转换为 Java 中存储的二进制码
	 * 绝对值二进制原码：101
	 * 补码：00000000 00000000 00000000 00000101
	 * 反码：11111111 11111111 11111111 11111010
	 * 加一：11111111 11111111 11111111 11111011		(这是该负数在 Java 中存储的二进制数)
	 * 
	 * 右移 1 位（左边补 1 个1，右边舍去 1 位）：11111111 11111111 11111111 11111101
	 * 我们看到最高位为 1 是负数。
	 * 先减一：11111111 11111111 11111111 11111100
	 * 再取反：00000000 00000000 00000000 00000011
	 * 得到该负数右移一位后绝对值的二进制为：11
	 * 11 转为十进制为 3，所以 -5 >> 1 = -3
	 * 
	 * 右移 2 位（左边补 2 个1，右边舍去 2 位）：11111111 11111111 11111111 11111110
	 * 我们看到最高位为 1 是负数。
	 * 先减一：11111111 11111111 11111111 11111101
	 * 再取反：00000000 00000000 00000000 00000010
	 * 得到该负数右移一位后绝对值的二进制为：10
	 * 10 转为十进制为 2，所以 -5 >> 1 = -2
	 * 
	 * 
	 * 无符号左移、无符号右移
	 * 
	 * 
	 * 
	 */
	@Test
	public void testMove() {
		int a = 5;
		System.out.println(toBinary(a));	// 101
//		System.out.println(a >> 2);			// 1
//		System.out.println(a >> 32);		// 5
//		System.out.println(a >> 34);		// 1
		
		System.out.println(a << 2);			// 20
		System.out.println(a << 31);		// -2147483648
		System.out.println(a << 32);		// 5
		System.out.println(a << 34);		// 20
		
		System.out.println(binary2Int("10000000000000000000000000000000"));
		
//		int b = -5;
//		System.out.println(b >> 1);			// -3
//		System.out.println(b >> 2);			// -2
	}
	
	/**
	 * 对两个二进制数进行比较(十进制的位或运算)
	 * @param a
	 * @param b
	 * @return 比较后的二进制数
	 */
	private String contrast3(String a, String b) {
		int aLen = a.length();
		int bLen = b.length();
		if(aLen != bLen) {
			int x = aLen - bLen;
			if(x < 0) x *= -1;
			
			String str = "";
			for (int i = 0; i < x; i++) {
				str += "0";
			}
			
			if(aLen > bLen) {
				b = str + b;
			}else if(aLen < bLen) {
				a = str + a;
			}
		}
		
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			if(a.charAt(i) == '1' || b.charAt(i) == '1') {
				result += "1";
			}else {
				result += "0";
			}
		}
		return result;
	}
	
	/**
	 * 对两个二进制数进行比较(十进制的位与运算)
	 * @param a
	 * @param b
	 * @return 比较后的二进制数
	 */
	private String contrast2(String a, String b) {
		int aLen = a.length();
		int bLen = b.length();
		if(aLen != bLen) {
			int x = aLen - bLen;
			if(x < 0) x *= -1;
			
			String str = "";
			for (int i = 0; i < x; i++) {
				str += "0";
			}
			
			if(aLen > bLen) {
				b = str + b;
			}else if(aLen < bLen) {
				a = str + a;
			}
		}
		
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			if(a.charAt(i) == '1' && b.charAt(i) == '1') {
				result += "1";
			}else {
				result += "0";
			}
		}
		return result;
	}
	
	/**
	 * 对两个二进制数进行比较(十进制的位异或运算)
	 * @param a
	 * @param b
	 * @return 比较后的二进制数
	 */
	private String contrast1(String a, String b) {
		int aLen = a.length();
		int bLen = b.length();
		if(aLen != bLen) {
			int x = aLen - bLen;
			if(x < 0) x *= -1;
			
			String str = "";
			for (int i = 0; i < x; i++) {
				str += "0";
			}
			
			if(aLen > bLen) {
				b = str + b;
			}else if(aLen < bLen) {
				a = str + a;
			}
		}
		
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			if(a.charAt(i) == b.charAt(i)) {
				result += "0";
			}else {
				result += "1";
			}
		}
		return result;
	}

	/**
	 * 二进制转十进制
	 * @param binary
	 * @return
	 */
	public static int binary2Int(String binary) {
		String[] arr = binary.split("");
		int r = 0;
		int index = 0;
		
		for (int i = arr.length - 1; i >= 0; i--) {
			r += ((int) Math.pow(2, index)) * Integer.parseInt(arr[i]);
			index ++;
		}
		return r;
	}
	
	/**
	 * 十进制转二进制
	 * @param num
	 * @return
	 */
	public static String toBinary(int num) {
		String str = "";
		while(num != 0) {
			str = num % 2 + str;
			num /= 2;
		}
		return str;
	}
	
	
}

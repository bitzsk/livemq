package org.livemq.test.bytes;

import org.junit.Test;

/**
 * java 十进制和二进制之间的相互转换
 * java 逻辑(位)运算符 与(&)、非(~)、或(|)、异或(^)
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
		System.out.println(toBinary(20)); 	// 10100
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
		System.out.println(binary2Int("101000"));	// 40
		System.out.println(binary2Int("10000000 00000000 00000000 00000000"));	// 2147483647
		System.out.println(binary2Long("10000000 00000000 00000000 00000000"));	// -2147483648
		System.out.println(binary2Long("1000000 00000000 00000000 00000000"));	// 
		System.out.println(binary2Long("00000000 00000000 00000000 10000001"));	// 129
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
	 * &: 按位与
	 * &&: 短路与
	 * 
	 * & 既是位运算符又是逻辑运算符, & 的两侧可以是 int,也可以是 boolean表达式。
	 * 而短路与 && 两侧要求必须是布尔表达式。
	 * 
	 * 当 & 两侧为 int 时, 先把运算符两侧的数转换为二进制数再进行运算。
	 * (运算规则:两个数都转为二进制，然后从高位开始比较，如果两个数都为 1 则为 1，否则为 0)
	 * 如: 5 & 8
	 * 5 的二进制为 00000101, 8 的二进制为 00001000, 比较后为: 00000000. 所以: 5 & 8 = 0
	 * 
	 * 如：8 & 11
	 * 8 转为二进制是 1000，11 转为二进制为 1011，从高位开始比较得到的是 1000，然后二进制转为十进制，就是 8
	 * 
	 * 当 & 两侧为布尔表达式时，要求运算符两侧的值都为真，结果才为真。
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
	 * << 左移运算符，num << 1，相当于 num 乘以 2
	 * >> 左移运算符，num >> 1，相当于 num 除以 2
	 * 8*2 ==> 8 << 1
	 * 8/2 ==> 8 >> 1
	 * 
	 * 拓展：
	 * 如何高效的算出 2*8 的值,为什么 8 << 1, 4 << 2, 2 << 3, 1 << 4 的结果为 16
	 * 首先 16 的二进制为 10000, 所以:
	 * 16 也就是 1000 的十进制 << 1, 100 的十进制 << 2, 10 的十进制 << 3, 1 的十进制 << 4
	 * 2*8 ==> 8 << 1, 4 << 2, 2 << 3, 1 << 4
	 * 进一步猜想1<<4与8<<1哪个效率更高？与什么有关？
	 * 
	 * 0 或正整数的二进制码和它本身+1再*-1的二进制吗成反码关系
	 * (x) 2 和 ((x+1) * -1) 2 成反码关系
	 * 
	 * 0000 0000	0
	 * 1111 1111	-1
	 * 
	 * 0000 0001	1
	 * 1111 1110	-2
	 * 
	 * 0000 0010	2
	 * 1111 1101	-3
	 * 
	 * 负整数二进制的反码就是该数绝对值-1的二进制数
	 * (x) 2 和 (x * -1 - 1) 2 成反码关系
	 * 
	 * 1111 1101	-3
	 * 0000 0010	2
	 * 
	 * 1111 1110	-2
	 * 0000 0001	1
	 * 
	 * 1111 1111	-1
	 * 0000 0000	0
	 * 
	 */
	@Test
	public void testMove() {
		/**
		 * TODO: 
		 * 1. 机器都是使用补码，运算也是使用补码运算
		 * 2. 正数的原码补码反码都是一样的
		 * 3. 负数存储的是其绝对值二进制的反码加一
		 * 
		 * 如:
		 * (理解一:原码除符号位取反加一)
		 * -5原码:10000000 00000000 00000000 00000101
		 * -5补码:11111111 11111111 11111111 11111011 
		 * 
		 * (理解二:绝对值的原码取反加一)
		 * -5绝对值:5
		 * 5原码:00000000 00000000 00000000 00000101
		 * 取反: 11111111 11111111 11111111 11111010
		 * 加一: 11111111 11111111 11111111 11111011
		 * 
		 * 
		 * TODO: 在 Java 中，如果对一个整数(int)位移操作大于等于 int 的最大位 32 位时，则先进去取模运算再位移。
		 * i >> n, 则先取模 n % 32, 计算 i >> (n % 32)
		 * 如: 5 >> 34, 则先 34 % 32 = 2, 所以计算:5 >> 2 = 1
		 * 
		 * TODO: 没有无符号左移 (因为左移会改变符号位)
		 */
		
		/**
		 * 00000101
		 * 
		 * 正数的有符号左移右移
		 */
		int a = 5;
		/**
		 * 00000101
		 * 00000001
		 * 二进制数 1 转十进制为 1,所以 5 >> 2 = 1
		 */
		System.out.println(a + " >> 2 = " + (a >> 2));			// 1
		/**
		 * 先取模再位移
		 * 32 % 32 = 0, 5 >> 0 = 5
		 */
		System.out.println(a + " >> 32 = " + (a >> 32));		// 5
		/**
		 * 先取模再位移
		 * 34 % 32 = 1, 5 >> 1 = 1
		 */
		System.out.println(a + " >> 34 = " + (a >> 34));		// 1

		/**
		 * 00000101
		 * 00010100
		 * 二进制数 10100 转十进制为 20,所以 5 << 2 = 20
		 */
		System.out.println(a + " << 2 = " + (a << 2));			// 20
		/**
		 * 先取模再位移
		 * 32 % 32 = 0, 5 << 0 = 5
		 */
		System.out.println(a + " << 32 = " + (a << 32));		// 5
		/**
		 * 先取模再位移
		 * 34 % 32 = 2, 5 << 2 = 20
		 */
		System.out.println(a + " << 34 = " + (a << 34));		// 20
		
		
		/**
		 * 00000101
		 * 11111011
		 * 
		 * 负数的有符号左移右移
		 */
		int b = -5;
		/**
		 * 11111011
		 * 11111110
		 * 先减一:11111101
		 * 再取反:00000010
		 * 二进制数 10 转十进制为 2,所以 -5 >> 2 = -2
		 */
		System.out.println(b + " >> 2 = " + (b >> 2));			// -2
		/**
		 * 先取模再位移
		 * 32 % 32 = 0, -5 >> 0 = -5
		 */
		System.out.println(b + " >> 32 = " + (b >> 32));		// -5
		/**
		 * 先取模再位移
		 * 34 % 32 = 2, -5 >> 2 = -2
		 */
		System.out.println(b + " >> 34 = " + (b >> 34));		// -2
		
		/**
		 * 11111011
		 * 11101100
		 * 先减一:11101011
		 * 再取反:00010100
		 * 10100 转十进制为:20, 所以:-5 << 1 = -20
		 */
		System.out.println(b + " << 2 = " + (b << 2)); 		// -20
		/**
		 * 先取模再位移
		 * 32 % 32 = 0, -5 << 0 = -5
		 */
		System.out.println(b + " << 32 = " + (b << 32)); 		// -5
		/**
		 * 先取模再位移
		 * 34 % 32 = 2, -5 << 2 = -20
		 */
		System.out.println(b + " << 34 = " + (b << 34));		// -20
		
		// TODO: 特殊例子
		/**
		 * 00000000 00000000 00000000 00000101
		 * 10000000 00000000 00000000 00000000
		 */
		System.out.println(a + " << 31 = " + (a << 31));		// -2147483648
		/**
		 * 00000000 00000000 00000000 00000101
		 * 00000000 00000000 00000000 00000000
		 * 二进制数 0 转十进制为 0
		 */
		System.out.println(a + " >> 31 = " + (a >> 31));		// 0
		/**
		 * 11111111 11111111 11111111 11111011
		 * 10000000 00000000 00000000 00000000
		 */
		System.out.println(b + " << 31 = " + (b << 31));		// -2147483648
		/**
		 * 11111111 11111111 11111111 11111011
		 * 11111111 11111111 11111111 11111111
		 * 最高位为 1 是负数
		 * 先减一：11111111 11111111 11111111 11111110
		 * 在取反：00000000 00000000 00000000 00000001
		 * 得到位移后的数绝对值的二进制为：00000000 00000000 00000000 00000001, 转十进制为 1
		 * 所以: -5 >> 31 = -1
		 */
		System.out.println(b + " >> 31 = " + (b >> 31));		// -1
		
		/**
		 * 00000101
		 * 00000001
		 * 二进制数 00000001 转十进制为 1
		 * 所以:5 >>> 2 = 1
		 */
		System.out.println(a + " >>> 2 = " + (a >>> 2));		// 1
		/**
		 * 先取模再位移
		 * 32 % 32 = 0, 5 >>> 0 = 5
		 */
		System.out.println(a + " >>> 32 = " + (a >>> 32));		// 5
		/**
		 * 先取模再位移
		 * 34 % 32 = 2, 5 >>> 2 = 1
		 */
		System.out.println(a + " >>> 34 = " + (a >>> 34));		// 1
		
		/**
		 * -5原码:10000000 00000000 00000000 00000101
		 * -5补码:11111111 11111111 11111111 11111011 (原码除符号位取反加一)
		 * 无符号右移2位:00111111 11111111 11111111 11111110
		 * 二进制数 1111111 11111111 11111111 11111110 转十进制为：1073741822
		 * 所以:-5 >>> 2 = 1073741822
		 */
		System.out.println(b + " >>> 2 = " + (b >>> 2));		// 1073741822
		/**
		 * 先取模再位移
		 * 32 % 32 = 0, -5 >>> 0 = 5
		 */
		System.out.println(b + " >>> 32 = " + (b >>> 32));		// -5
		/**
		 * 先取模再位移
		 * 34 % 32 = 2, -5 >>> 2 = 1073741822
		 */
		System.out.println(b + " >>> 34 = " + (b >>> 34));		// 1073741822
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
	 * 二进制转十进制(该二进制数只能是正整数的二进制数)
	 * @param binary
	 * @return
	 */
	public static int binary2Int(String binary) {
		binary = binary.trim().replace(" ", "");
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
	 * 二进制转十进制(该二进制数只能是正整数的二进制数)
	 * @param binary
	 * @return
	 */
	public static int binary2Long(String binary) {
		binary = binary.trim().replace(" ", "");
		String[] arr = binary.split("");
		int r = 0;
		int index = 0;
		
		for (int i = arr.length - 1; i >= 0; i--) {
			r += ((long) Math.pow(2, index)) * Long.parseLong(arr[i]);
			index ++;
		}
		return r;
	}
	
	/**
	 * 十进制转二进制(只能转正整数)
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

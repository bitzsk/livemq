package org.livemq.test.bytes;

import org.junit.Test;

/**
 * 解析 byte & 0x0f 的意义
 * 
 * 
 * 参考:https://www.cnblogs.com/think-in-java/p/5527389.html
 * 
 * #14
 * byte 类型转为 int 类型时，希望保持低 8 比特数据一致，前 24 比特为 0 时要与上 0xff
 * 
 * #1
 * 其实是从数字类型扩展到较宽的类型时，补零扩展还是补符号位扩展。
 * 这是因为 Java 中只有有符号数，当 byte 扩展到 short, int 时，即正数都是一样，因为符号位是 0，所以无论如何扩展都是补零扩展；
 * 但负数补零扩展和按符号位扩展 结果是完全不同的。
 * 补符号位，原数值不变。
 * 补零时，相当于把有符号数看成无符号数，比如 -127 = 0x81，看成无符号数就是 129, 256 + (-127) TODO: 256 + (-127) 是什么?
 * 对于有符号数，从小扩展大时，需要用 & 0xff 这样方式来确保是按补零扩展。
 * 而从大向小处理，符号位自动无效，所以不用处理。 TODO: ?
 * 
 * #2
 * 也就是说在 byte 向 int 扩展时，自动转换是按符号位扩展的，这样子能保证十进制的数值不会变化，
 * 而 & 0xff 是补 0 扩展的，这样子能保证二进制存储的一致性，但是十进制数值已经发生变化了。
 * 也就是说按符号位扩展能保证十进制数值不变，补 0 扩展能保证二进制存储不会变。
 * 而正数可以说既是按符号位扩展，又是补 0 扩展，所以在二进制存储和十进制数值上都能保持一致。
 * 
 * @author xinxisimple@163.com
 * @date 2018-07-16 16:16
 */
public class Test0x0f {

	/**
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
	 * 当 & 两侧为布尔表达式时，要求运算符两侧的值都为真，结果才为真。
	 */
	
	
	/**
	 * 二进制, 十进制, 十六进制之间的关系
	 * 
	 * 组成
	 * 二进制:由 0, 1 组成
	 * 十进制:由 0-9 组成
	 * 十六进制:由 0-9, a-f 组成，字母不区分大小写。与十进制的对应关系是：0-9 对应 0-9, a-f 对应 10-15 
	 * 
	 * 二进制4位一组,如一个byte 8位就是两组,一个int 32位就是8组。
	 * 用十六进制帮助记忆是相对于二进制的。二进制的每 4 个位为十六进制中的 1 个位。
	 * 如十进制数 15 在二进制中表示为 1111，在十六进制中表示为 f
	 * 0xff <==> 1111 1111
	 * 0x00 <==> 0000 0000
	 */
	
	@Test
	public void testAnd(){
		int a = 5, b = 8;
		if(a == 5 & b == 8) {
			System.out.println("success");
		}else {
			System.out.println("fail");
		}
		
		/**
		 * 00000101
		 * 00001000
		 * ===>
		 * 00000000
		 * 
		 * 所以:a & b = 0
		 */
		System.out.println(a & b);
	}
	
	@Test
	public void test() {
		byte a = 5;
		byte b = -127;
		System.out.println(a + " 的二进制为：" + Integer.toBinaryString(a));
		System.out.println(b + " 的二进制为：" + Integer.toBinaryString(b));
		
		System.out.println(a & 0xff);		// 0000 0101 & 1111 1111 ==> 0000 0101 = 5		打印:5
		System.out.println(b & 0xff);		// 1000 0001 & 1111 1111 ==> 1000 0001 = -127 	打印:129
											// 按 & 运算结果 1000 0001 应该也是 -127 啊，为什么结果是 129 呢?
		
		
		/**
		 * 这里需要先复习下原码反码补码这三个概念
		 * 
		 * 对于正数(00000001) 原码来说，最高位表示符号位，反码、补码都是本身
		 * 对于负数(10000001) 原码来说，反码是对原码除了符号位之外作取反运算即(11111110)，补码是对反码作+1运算即(11111111)
		 */
		
		/**
		 * 当将 -127 赋值给 b 的时候，b 作为一个 byte 类型，其计算机存储的补码是 10000001 (8 位)
		 * 将 b 作为 int 类型向控制台输出的时候，jvm 作了一个补位的处理，因为 int 类型是 32 位所以补位后的补码就是
		 * 11111111 11111111 11111111 10000001 (32 位)
		 * 这个 32 位二进制补码表示的也是 -127。
		 * 虽然 byte -> int 计算机背后存储的二进制补码由 10000001 (8 位) 转换成 11111111 11111111 11111111 10000001 (32 位)
		 * 但是很显然这两个补码表示的十进制数字依然是相同的。
		 * 
		 * 但是做 byte -> int 的转换，难道所有时候都只是为了保持十进制的一致性吗？
		 * 不一定吧？好比我们拿到的文件流转换成 byte 数组，难道我们关心的是 byte 数组的十进制的值是多少吗？我们关心的是其背后二进制存储的补码吧。
		 * 所以这里应该能猜到为什么 byte 类型的数字要 & 0xff 再赋值给 int类型，其本质原因是向保持二进制补码的一致性。
		 * 当 byte 负数要转换为 int 的时候，高的 24 位必然会补 1，这样，其二进制码其实已经不一致了，& 0xff 可以将高的 24 位置为 0，低 8 位保持原样。这样做的目的就是为了保持二进制数据的一致性。
		 * 当然了，保证了二进制数据一致性的同时，如果二进制被当作 byte 和 int 来解读，其 十进制的值必然是不同的，因为符号位已经发生了变化。
		 * 
		 * 
		 * 例子解析:
		 * byte b = -127;
		 * 二进制表示为:11111110
		 * 控制台打印时 byte -> int 为:11111111 11111111 11111111 10000001
		 * 0xff 二进制为:11111111
		 * 0xff 是一个 int 类型
		 * 所以:11111111 11111111 11111111 10000001 & 11111111 = 00000000 00000000 00000000 10000001
		 * 这个值转十进制为:129
		 * 
		 * 当系统检测到 byte 可能会转换成 int 或者说 byte 与 int 类型进行运算的时候，
		 * 就会将 byte 的内存空间高位补位(负数补 1，正数补 0)扩充到 32 位，再参与运算。
		 * 0xff 是 int 类型，所以可以说 byte 与 int 在进行运算。
		 * 
		 * #14
		 * 0xff 是一个 int 类型的数据，实际上是 0x000000ff
		 * b & 0xff ==> (int) b & 0x000000ff
		 * 实际上取的是 (int) b 的低 8 比特数据而已
		 */
	}
	
	@Test
	public void test1() {
		byte a = -127;	// 二进制为:10000001
		System.out.println(a & 0xff);		// 129
		/**
		 * & 0x7f 意义是:将做高位清零，也就是保留低 7 比特数据
		 * 如:-127 & 0x7f ==> 100000001 & 01111111 => 00000001 = (1) 10
		 */
		System.out.println(a & 0x7f);		// 1
		/**
		 * & 0x80 意义是:将低 7 比特清零，也就是保留最高位第 8 比特的数据
		 * 如:-127 & 0x80 ==> 100000001 & 10000000 => 10000000 = 128 (10)
		 */
		System.out.println(a & 0x80);		// 128
		
		/**
		 * & 0x0f 意义是:将高 4 比特清零，也就是保留低 4 比特数据
		 * 如:-127 & 0x0f ==> 100000001 & 00001111 => 00000001 = (1) 10
		 */
		System.out.println((a & 0x0f) + " ======= " + a);		// 1 ======= -127
		/**
		 * 相当于:a = a & 0x0f
		 */
		System.out.println((a &= 0x0f) + " ======= " + a);		// 1 ======= 1
	}
}
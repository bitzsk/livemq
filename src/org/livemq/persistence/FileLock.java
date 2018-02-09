package org.livemq.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;

/**
 * FileLock - 获取用于防止其他MQTT客户机使用相同持久性存储的锁。如果锁已经被保存，则抛出一个异常。
 * 
 * <p>
 * 源：Some Java runtimes such as JME MIDP do not support file locking or even 
 * the Java classes that support locking.  The class is coded to both compile 
 * and work on all Java runtimes.  In Java runtimes that do not support 
 * locking it will look as though a lock has been obtained but in reality
 * no lock has been obtained. 
 * </p>
 * 
 * <p>
 * 翻译：有些 Java 运行时(如JME MIDP)不支持文件锁定，甚至不支持锁定的Java类。
 * 该类被编码到所有Java运行时的编译和工作中。
 * 在Java运行时不支持锁的情况下，它看起来就像已经获得了一个锁，但实际上并没有得到锁。
 * </p>
 * 
 * @author Administrator
 *
 */
public class FileLock {

	private File lockFile;
	/**
	 * RandomAccessFile 用来访问那些保存数据记录的文件的，你就可以用seek( )方法来访问记录，并进行读写了
	 */
	private RandomAccessFile file;
	private Object fileLock;
	
	/**
	 * NIO 中的文件锁
	 * @param clientDir
	 * @param lockFilename
	 */
	public FileLock(File clientDir, String lockFilename){
		lockFile = new File(clientDir, lockFilename);
		try {
			file = new RandomAccessFile(lockFile, "rw");
			
			//源码中此处代码用反射
			//返回与此文件关联的唯一  FileChannel 对象
			FileChannel channel = file.getChannel();
			//获取对此通道的文件的独占锁定
			fileLock = channel.tryLock();
		} catch (FileNotFoundException e) {
			fileLock = null;
		} catch (IOException e) {
			fileLock = null;
		}
		if(fileLock == null){
			release();
		}
	}

	/**
	 * 释放文件锁
	 */
	public void release() {
		try {
			if(fileLock != null){
				// 此处用的源码中的反射，因为返回的文件锁类名 FileLock 和本类命名重复
				Method m = fileLock.getClass().getMethod("release",new Class[]{});
				m.invoke(fileLock, new Object[]{});
				fileLock =  null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(file != null){
			try {
				file.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			file = null;
		}
		
		if(lockFile != null && lockFile.exists()){
			lockFile.delete();
		}
		lockFile = null;
	}
	
}

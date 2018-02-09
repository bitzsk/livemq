package org.livemq.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Vector;

import org.livemq.MqttClientPersistence;

/**
 * 客户端默认的文件持久化实现
 * @author w.x
 * @date 2018年2月8日 下午2:32:46
 */
public class MqttClientFilePersistence implements MqttClientPersistence {

	private File dataDir;
	private File clientDir;
	private FileLock fileLock;
	private static final String MESSAGE_FILE_EXTENSION = ".msg";
	private static final String MESSAGE_BACKUP_FILE_EXTENSION = ".bup";
	private static final String LOCK_FILENAME = ".lck";
	
	private static final FilenameFilter FILE_FILTER = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			return name.endsWith(MESSAGE_FILE_EXTENSION);
		}
	};
	
	public MqttClientFilePersistence(){
		this(System.getProperty("user.dir"));
	}

	public MqttClientFilePersistence(String directory){
		dataDir = new File(directory);
	}
	
	public void open(String clientId, String connectParam) {
		//1.校验
		if(dataDir.exists() && !dataDir.isDirectory()){
			throw new IllegalAccessError(dataDir.getAbsolutePath() + " 不是一个目录.");
		}else if(!dataDir.exists()){
			if(!dataDir.mkdirs()){
				throw new IllegalAccessError(dataDir.getAbsolutePath() + " 创建失败.");
			}
		}
		if(!dataDir.canRead() || !dataDir.canWrite()){
			throw new IllegalAccessError(dataDir.getAbsolutePath() + " 没有读写权限.");
		}
		
		//2.组装文件名
		StringBuffer buffer = new StringBuffer();
		for (int i = 0;i < clientId.length();i++) {
			char c = clientId.charAt(i);
			if (isSafeChar(c)) {
				buffer.append(c);
			}
		}
		buffer.append("-");
		for (int i = 0;i < connectParam.length();i++) {
			char c = connectParam.charAt(i);
			if (isSafeChar(c)) {
				buffer.append(c);
			}
		}
		
		//3.同步创建/消费客户端数据
		synchronized (this) {
			//3.1 创建客户端本地目录
			if(clientDir == null){
				String key = buffer.toString();
				clientDir = new File(dataDir, key);
				
				if(!clientDir.exists()){
					clientDir.mkdir();
				}
			}
			
			//3.2 创建文件锁
			try {
				fileLock = new FileLock(clientDir, LOCK_FILENAME);
			} catch (Exception e) {
				throw new IllegalAccessError("文件锁获取失败");
			}
			
			//3.3 初始化备份文件为原文件
			restoreBackups(clientDir);
		}
	}

	/**
	 * 初始化备份文件为原文件
	 * .bup -> .msg
	 * @param dir
	 */
	private void restoreBackups(File dir) {
		File[] files = clientDir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(MESSAGE_BACKUP_FILE_EXTENSION);
			}
		});
		
		for(File backupFile : files){
			File originalFile = new File(dir,backupFile.getName().substring(0, backupFile.getName().length() - MESSAGE_BACKUP_FILE_EXTENSION.length()));
			boolean result = backupFile.renameTo(originalFile);
			if (!result) {
				originalFile.delete();
				backupFile.renameTo(originalFile);
			}
		}
	}

	public void close() {
		synchronized (this) {
			if(fileLock != null){
				fileLock.release();
			}
			
			if(getFiles().length == 0){
				clientDir.delete();
			}
			clientDir = null;
		}
	}

	public void put(String key, Object object) {
		checkIsOpen();
		File file = new File(clientDir, key+MESSAGE_FILE_EXTENSION);
		File backupFile = new File(clientDir, key+MESSAGE_FILE_EXTENSION+MESSAGE_BACKUP_FILE_EXTENSION);
		
		// 写入消息之前先备份
		if(file.exists()){
			boolean result = file.renameTo(backupFile);
			if(!result){
				// 如果已存在备份文件，则删除重新备份
				backupFile.delete();
				file.renameTo(backupFile);
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(file);
//			fos.write(message.getHeaderBytes(), message.getHeaderOffset(), message.getHeaderLength());
//			if(message.getPayloadBytes() != null){
//				fos.write(message.getPayloadBytes(), message.getPayloadOffset(), message.getPayloadLength());
//			}
			// 同步写入
			fos.getFD().sync();
			fos.close();
			
			// 写入完则删除备份文件
			if(backupFile.exists()){
				backupFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw new IllegalAccessError("消息写入失败");
		} finally{
			if (backupFile.exists()) {
				// 写入失败，则回滚
				boolean result = backupFile.renameTo(file);
				if (!result) {
					file.delete();
					backupFile.renameTo(file);
				}
			}
		}
	}

	/**
	 * 获取持久化消息
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		checkIsOpen();
		File file = new File(clientDir, key + MESSAGE_FILE_EXTENSION);
		if(!file.exists()){
			throw new IllegalAccessError("持久化消息不存在");
		}
		
		Object result = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			int size = fis.available();
			byte[] data = new byte[size];
			int read = 0;
			while (read<size) {
				read += fis.read(data,read,size-read);
			}
			fis.close();
			result = new Object();
		} catch (Exception e) {
			throw new IllegalAccessError("获取持久化消息失败");
		}
		return result;
	}

	/**
	 * 删除持久化消息
	 * @param key
	 */
	public void remove(String key) {
		checkIsOpen();
		File file = new File(clientDir, key + MESSAGE_FILE_EXTENSION);
		if(file.exists()){
			file.delete();
		}
	}

	/**
	 * 获取所有的消息缓存文件 key 集
	 */
	public Enumeration<String> keys() {
		checkIsOpen();
		File[] files = getFiles();
		Vector<String> result = new Vector<String>(files.length);
		for (int i=0;i<files.length;i++) {
			String filename = files[i].getName();
			String key = filename.substring(0,filename.length()-MESSAGE_FILE_EXTENSION.length());
			result.addElement(key);
		}
		return result.elements();
	}

	/**
	 * 清除所有的消息缓存文件
	 */
	public void clear() {
		checkIsOpen();
		File[] files = getFiles();
		for (int i=0; i<files.length; i++) {
			files[i].delete();
		}
	}

	/**
	 * 获取所有的消息缓存文件
	 * @return
	 */
	private File[] getFiles() {
		checkIsOpen();
		File[] files = clientDir.listFiles(FILE_FILTER);
		if (files == null) {
			throw new IllegalAccessError("获取消息缓存文件失败");
		}
		return files;
	}

	/**
	 * 判断该 key 锁对应的消息缓存文件是否存在
	 */
	public boolean containsKey(String key) {
		checkIsOpen();
		File file = new File(clientDir, key + MESSAGE_FILE_EXTENSION);
		return file.exists();
	}

	/**
	 * 检查客户端持久化文件是否打开
	 */
	private void checkIsOpen() {
		if (clientDir == null) {
			throw new IllegalAccessError("客户端持久化文件未打开");
		}
	}
	
	private boolean isSafeChar(char c) {
		return Character.isJavaIdentifierPart(c) || c=='-';
	}
}

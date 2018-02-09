package org.livemq.util;

import java.io.File;
import java.io.IOException;

/**
 * 该类用于生成 Linux 下的 xxx.zip 压缩包
 * @author Administrator
 *
 */
public class Script {

	private static final String LIVEMQ = "livemq";
	private static final String LIVEMQ_VERSION = "1.0.0";

	private static final String LIVEMQ_RESOURCE = "resource";

	private static final String LIVEMQ_DIR = LIVEMQ + "-" + LIVEMQ_VERSION;
	private static final String LIVEMQ_ZIP = LIVEMQ_DIR + ".zip";
	
	private static final String LIVEMQ_DIR_BIN = "bin";
	private static final String LIVEMQ_DIR_CONF = "conf";
	private static final String LIVEMQ_DIR_DATA = "data";
	private static final String LIVEMQ_DIR_LICENSE = "license";
	private static final String LIVEMQ_DIR_LOG = "log";

	private static final String LIVEMQ_FILE_SCRIPT = "livemq.sh";
	private static final String LIVEMQ_FILE_RUN = "run.sh";
	private static final String LIVEMQ_FILE_CONFIG = "livemq.cfg";
	private static final String LIVEMQ_FILE_README = "README.md";
	private static final String LIVEMQ_FILE_LOG = LIVEMQ + ".log";

	public static void main(String[] args) throws IOException {
		run();
	}

	private static void run() throws IllegalAccessError, IOException {
		System.out.println("Starting the LiveMQ ...");
		
		/**
		 * 1.获取当前项目所在目录
		 */
		File dataDir = new File(System.getProperty("user.dir"));
		File resource = new File(dataDir, LIVEMQ_RESOURCE);
		if (!resource.exists()) {
			throw new IllegalAccessError(resource.getAbsolutePath() + " 不存在.");
		}
		File workspace = dataDir.getParentFile();
		if (!workspace.exists()) {
			throw new IllegalAccessError(workspace.getAbsolutePath() + " 不存在.");
		}

		/**
		 * 2.在该工作空间下创建对应的文件
		 */
		//2.1 创建根目录
		File livemq = new File(workspace, LIVEMQ_DIR);
		if (!livemq.exists()) {
			if (!livemq.mkdir()) {
				throw new IllegalAccessError(livemq.getAbsolutePath() + " 创建失败.");
			}
		}
		//2.2创建bin目录
		File bin = new File(livemq, LIVEMQ_DIR_BIN);
		if (!bin.exists()) {
			if (!bin.mkdir()) {
				throw new IllegalAccessError(bin.getAbsolutePath() + " 创建失败.");
			}
		}
		//2.3创建conf目录
		File conf = new File(livemq, LIVEMQ_DIR_CONF);
		if (!conf.exists()) {
			if (!conf.mkdir()) {
				throw new IllegalAccessError(conf.getAbsolutePath() + " 创建失败.");
			}
		}
		//2.4创建data目录
		File data = new File(livemq, LIVEMQ_DIR_DATA);
		if (!data.exists()) {
			if (!data.mkdir()) {
				throw new IllegalAccessError(data.getAbsolutePath() + " 创建失败.");
			}
		}
		//2.5创建license目录
		File license = new File(livemq, LIVEMQ_DIR_LICENSE);
		if (!license.exists()) {
			if (!license.mkdir()) {
				throw new IllegalAccessError(license.getAbsolutePath() + " 创建失败.");
			}
		}
		//2.6创建log目录
		File log = new File(livemq, LIVEMQ_DIR_LOG);
		if (!log.exists()) {
			if (!log.mkdir()) {
				throw new IllegalAccessError(log.getAbsolutePath() + " 创建失败.");
			}
		}

		/**
		 * 3.创建文件
		 */
		//3.1创建README.md文件
		File readme = new File(livemq, LIVEMQ_FILE_README);
		if (readme.exists() && !readme.isFile()) {
			throw new IllegalAccessError(readme.getAbsolutePath() + " 不是文件类型.");
		} else if (!readme.exists()) {
			if (!readme.createNewFile()) {
				throw new IllegalAccessError(readme.getAbsolutePath() + " 创建失败.");
			}
		}
		//3.2创建livemq.log文件
		File fileLog = new File(log, LIVEMQ_FILE_LOG);
		if (fileLog.exists() && !fileLog.isFile()) {
			throw new IllegalAccessError(fileLog.getAbsolutePath() + " 不是日志文件类型.");
		} else if (!fileLog.exists()) {
			if (!fileLog.createNewFile()) {
				throw new IllegalAccessError(fileLog.getAbsolutePath() + " 创建失败.");
			}
		}
		//3.3创建livemq.sh脚本文件
		File script = new File(bin, LIVEMQ_FILE_SCRIPT);
		if (script.exists() && !script.isFile()) {
			throw new IllegalAccessError(script.getAbsolutePath() + " 不是日志文件类型.");
		} else if (!script.exists()) {
			if (!script.createNewFile()) {
				throw new IllegalAccessError(script.getAbsolutePath() + " 创建失败.");
			}else{
				
			}
		}
		//3.3创建run.sh脚本文件
		File run = new File(bin, LIVEMQ_FILE_RUN);
		if (run.exists() && !run.isFile()) {
			throw new IllegalAccessError(run.getAbsolutePath() + " 不是日志文件类型.");
		} else if (!run.exists()) {
			if (!run.createNewFile()) {
				throw new IllegalAccessError(run.getAbsolutePath() + " 创建失败.");
			}else{
				
			}
		}
		//3.3创建livemq.cfg配置文件
		File cfg = new File(conf, LIVEMQ_FILE_CONFIG);
		if (cfg.exists() && !cfg.isFile()) {
			throw new IllegalAccessError(cfg.getAbsolutePath() + " 不是文件类型.");
		} else if (!cfg.exists()) {
			if (!cfg.createNewFile()) {
				throw new IllegalAccessError(cfg.getAbsolutePath() + " 创建失败.");
			}else{
				
			}
		}
		
		/**
		 * 4.读取对应的资源文件内容写入到缓冲区文件中
		 */
		if (resource.exists()) {
			File scriptSh = new File(resource, LIVEMQ_FILE_SCRIPT);
			if (scriptSh.exists()) {
				if (!scriptSh.isFile()) {
					throw new IllegalAccessError(scriptSh.getAbsolutePath() + " 不是文件类型.");
				} else {
					byte[] bytes = FileUtil.readFileByte(scriptSh.getAbsolutePath());
					FileUtil.writeByteFile(bytes, script.getAbsoluteFile());
				}
			} else if (!scriptSh.exists()) {
				throw new IllegalAccessError(scriptSh.getAbsolutePath() + " 脚本资源文件不存在.");
			}
			
			File runSh = new File(resource, LIVEMQ_FILE_RUN);
			if (runSh.exists()) {
				if (!runSh.isFile()) {
					throw new IllegalAccessError(runSh.getAbsolutePath() + " 不是文件类型.");
				} else {
					byte[] bytes = FileUtil.readFileByte(runSh.getAbsolutePath());
					FileUtil.writeByteFile(bytes, run.getAbsoluteFile());
				}
			} else if (!runSh.exists()) {
				throw new IllegalAccessError(runSh.getAbsolutePath() + " 脚本资源文件不存在.");
			}
			
			File config = new File(resource, LIVEMQ_FILE_CONFIG);
			if (config.exists()) {
				if (!config.isFile()) {
					throw new IllegalAccessError(config.getAbsolutePath() + " 不是文件类型.");
				} else {
					byte[] bytes = FileUtil.readFileByte(config.getAbsolutePath());
					FileUtil.writeByteFile(bytes, cfg.getAbsoluteFile());
				}
			} else if (!config.exists()) {
				throw new IllegalAccessError(config.getAbsolutePath() + " 配置文件不存在.");
			}
		} else {
			throw new IllegalAccessError(resource.getAbsolutePath() + " 资源目录不存在.");
		}
		
		/**
		 * 5.打包成压缩包 xxx.zip 格式
		 */
		FileUtil.zipFile(livemq.getAbsolutePath(), workspace.getAbsolutePath() + "\\" + LIVEMQ_ZIP);
		
		System.out.println("[OK]");
	}
}

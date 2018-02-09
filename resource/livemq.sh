#!/bin/sh
#
# 
#
#
#


if hash java 2>/dev/null; then
	
	#java虚拟机启动参数  
	JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m"
	
	if [ -c "/dev/urandom" ]; then
        # Use /dev/urandom as standard source for secure randomness if it exists
        JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
    fi
    
    if [ -z "$LIVEMQ_HOME" ]; then
	    LIVEMQ_FOLDER="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../" && pwd )"
	else
	    LIVEMQ_FOLDER=$LIVEMQ_HOME
	fi

	LIVEMQ="LiveMQ"
	LIVEMQ_LOG_FILE=$LIVEMQ_FOLDER/log/livemq.log
	LIVEMQ_CONFIG_FILE=$LIVEMQ_FOLDER/conf/livemq.cfg
	LIVEMQ_JAR=$LIVEMQ_FOLDER/bin/livemq.jar
	LIVEMQ_MAINCLASS=cn.fmiss.livemq.server.MqttServerMain

    if [ ! -d "$LIVEMQ_FOLDER" ]; then
    	echo "ERROR! $LIVEMQ Home Folder not found."
    	exit 1
    else
    	
    	if [ ! -w "$LIVEMQ_FOLDER" ]; then
    		echo "ERROR! $LIVEMQ Home Folder Permissions not correct."
    		exit 1
    	else
    		
    		if [ ! -f "$LIVEMQ_JAR" ]; then
    			echo "ERROR! $LIVEMQ_JAR is not found."
    			exit 1
    		fi
    		
    	fi
    	
    fi
    
else
	echo "You do not have the Java Runtime Environment installed, please install Java JRE from java.com/en/download and try again."
	exit 1
fi

# 初始化PID变量(全局)
pid=0

# 获取pid
checkpid() {
	pid=`ps -f | grep "$LIVEMQ_FOLDER" | grep -v grep | awk '{print $2}'`
	if [ ! -n "$pid" ]; then
		pid=0
	fi
}

# 启动
start() {
	checkpid

	if [ $pid -ne 0 ]; then
		echo "warn: $LIVEMQ already started! (pid=$pid)"
	else
		echo "-------------------------------------------------------------------------"
		echo ""
		echo "		 __  __   ____   _______  _______ "
		echo "		|  \/  | / __ \ |__   __||__   __|"
		echo "		| \  / || |  | |   | |      | |   "
		echo "		| |\/| || |  | |   | |      | |   "
		echo "		| |  | || |__| |   | |      | |   "
		echo "		|_|  |_| \___\_\   |_|      |_|   "
		echo ""
		echo "-------------------------------------------------------------------------"
		echo ""
		echo "		MQTT Start Script for Linux/Unix v1.7"
		echo ""
		
		echo "-------------------------------------------------------------------------"
		echo ""
		echo "	$LIVEMQ:$LIVEMQ"
		echo ""
		echo "	$LIVEMQ_FOLDER:$LIVEMQ_FOLDER"
		echo ""
		echo "	JAVA_OPTS:$JAVA_OPTS"
		echo ""
		echo "-------------------------------------------------------------------------"
		echo ""
	
		echo -e "Starting the $LIVEMQ ...\c"

      	nohup java $JAVA_OPTS -classpath $LIVEMQ_JAR $LIVEMQ_MAINCLASS $LIVEMQ_CONFIG_FILE > $LIVEMQ_LOG_FILE 2>&1 &

      	checkpid

      	count=0
		while [ $pid -eq 0 ]; do
			echo -e ".\c"
			count++
			sleep 1
			
			if [ $pid -gt 0 || $count -gt 30 ]; then
        		break
    		fi
    		
    		if [ $pid -gt 0 ]; then
        		break
    		fi
		done
		
		if [ $pid -gt 0 ]; then
			echo
    		echo "OK!"
			echo "PID: $pid"
		else
			echo "[Failed]"
		fi

	fi
}

# 停止
stop() {
	checkpid
	
	if [ $pid -ne 0 ]; then
		echo -n "Stopping $LIVEMQ ...(pid=$pid) "
		su -c "kill -9 $pid"  
		if [ $? -eq 0 ]; then 
			echo "[OK]"
		else
			echo "[Failed]"  
		fi
		
		checkpid
		if [ $pid -ne 0 ]; then
			stop
		fi
	else
		echo "warn: $LIVEMQ is not running"
	fi
}

# 查看软件运行状态
status() {
	checkpid
	if [ $pid -ne 0 ]; then
		echo "$LIVEMQ is running! (pid=$pid)"
	else
		echo "$LIVEMQ is not running"
	fi
}

# 打印日志
log() {
    if [ ! -f "$LIVEMQ_LOG_FILE" ]; then
    	touch "$LIVEMQ_LOG_FILE"
    fi

    echo "running log ..."

    cmd="tail -f $LIVEMQ_LOG_FILE"
	eval $cmd
}

# 查看配置信息
info() {
	checkpid
	
	echo "-------------------------------------------------------------------------"
	echo ""
	echo "LIVEMQ: $LIVEMQ"
	echo "LIVEMQ_FOLDER: $LIVEMQ_FOLDER"
	echo "LIVEMQ_JAR: $LIVEMQ_JAR"
	echo "LIVEMQ_MAINCLASS: $LIVEMQ_MAINCLASS"
	echo "LIVEMQ_LOG_FILE: $LIVEMQ_LOG_FILE"
	echo ""
	
	if [ $pid -ne 0 ]; then
		echo "PID: $pid"
		echo "$LIVEMQ is running"
	else
		echo "$LIVEMQ is not running"
	fi
	
	echo ""
	echo "-------------------------------------------------------------------------"
}

clearlog() {
	if [ -f "$LIVEMQ_LOG_FILE" ]; then
    	echo "starting clear log ..."
    	
    	cmd="> $LIVEMQ_LOG_FILE"
		eval $cmd

		echo "clear finished."
    fi
}

###################################  
#读取脚本的第一个参数($1)，进行判断  
#参数取值范围：{start|stop|restart|status|info|log}  
#如参数不在指定范围之内，则打印帮助信息  
###################################  
case "$1" in
	'start')
		start
		;;
	'stop')
		stop
		;;
	'restart')
		stop
		start
		;;
	'status')
		status
		;;
	'info')
		info
		;;
	'log')
		log
		;;
	'clear')
		
		case "$2" in
			'log')
				clearlog
				;;
			*)
				echo "Usage: $0 $1 {log}"
				exit 1
		esac

		;;
	*)  
		echo "Usage: $0 {start|stop|restart|status|info|log}"  
		exit 1
esac
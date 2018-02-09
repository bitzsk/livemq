#!/bin/sh
#
# 
#
#
#

LIVEMQ="LiveMQ"

LIVEMQ_BIN=$(pwd)

LIVEMQ_HOME=$(dirname $LIVEMQ_BIN)

LIVEMQ_JAR=$LIVEMQ_BIN/livemq.jar

if hash java 2>/dev/null; then
	
	#java虚拟机启动参数  
	JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true -XX:MaxPermSize=128m"
	
	if [ -c "/dev/urandom" ]; then
        # Use /dev/urandom as standard source for secure randomness if it exists
        JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
    fi
    
    if [ ! -d "$LIVEMQ_HOME" ]; then
    	echo "ERROR! $LIVEMQ Home Folder not found."
    	exit 1
    else
    	
    	if [ ! -w "$LIVEMQ_HOME" ]; then
    		echo "ERROR! $LIVEMQ Home Folder Permissions not correct."
    		exit 1
    	else
    		
    		if [ ! -f "$LIVEMQ_JAR" ]; then
    			echo "ERROR! $LIVEMQ JAR not found."
    			exit 1
            else
                echo "-------------------------------------------------------------------------"
                echo ""
                echo "       __  __   ____   _______  _______ "
                echo "      |  \/  | / __ \ |__   __||__   __|"
                echo "      | \  / || |  | |   | |      | |   "
                echo "      | |\/| || |  | |   | |      | |   "
                echo "      | |  | || |__| |   | |      | |   "
                echo "      |_|  |_| \___\_\   |_|      |_|   "
                echo ""
                echo "-------------------------------------------------------------------------"
                echo ""
                echo "      MQTT Start Script for Linux/Unix v1.7"
                echo ""
                
                echo "-------------------------------------------------------------------------"
                echo ""
                echo "  LIVEMQ:$LIVEMQ_HOME"
                echo ""
                echo "  JAVA_OPTS:$JAVA_OPTS"
                echo ""
                echo "-------------------------------------------------------------------------"
                echo ""
                
                eval \"java\" "$JAVA_OPTS" -jar "$LIVEMQ_JAR"
    		fi
    		
    	fi
    	
    fi
    
else
	echo You do not have the Java Runtime Environment installed, please install Java JRE from java.com/en/download and try again.
	exit 1
fi

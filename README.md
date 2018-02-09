# livemq

基于MQTT协议实现的服务端和客户端



[![GitHub version](https://img.shields.io/badge/release-v1.0.0-blue.svg)](https://github.com/xinxisimple/livemq/releases)
[![GitHub download](https://img.shields.io/badge/downloads-10k-green.svg)](https://github.com/xinxisimple/livemq/releases)
[![Build Status](https://img.shields.io/badge/platform-android%20%7C%20win--32%20%7C%20win--64%20%7C%20linux--64-lightgrey.svg)](https://github.com/xinxisimple/livemq/releases)



### 运行要求

- Java 6 或以上 (OpenJDK, Oracle)
- 任何可以运行 java 的平台即可



### 源码目录介绍

```java
src
└──org
| └──livemq
|  |──client
|   |──AsyncLiveMQ.java
|   |──MqttCore.java
|   |──MqttHandler.java
|   |──MqttReceiver.java
|   └──MqttSender.java
|  |──exception
|   |──ExceptionHelper.java
|   └──MqttException.java
|  |──internal
|   |──net
|    |──Network.java
|    └──TCPNetwork.java
|   |──nio
|    └──NIOHandle.java
|   |──stream
|    |──CountingInputStream.java
|    |──MqttInputStream.java
|    └──MqttOutputStream.java
|   └──wire
|    |──MqttWireMessage.java
|    |──MqttAck.java
|    |──MqttConnect.java
|    |──MqttConnack.java
|    |──MqttDisconnect.java
|    |──MqttPingReq.java
|    |──MqttPingResp.java
|    |──MqttPublish.java
|    |──MqttPubAck.java
|    |──MqttPubRec.java
|    |──MqttPubRel.java
|    |──MqttPubComp.java
|    |──MqttSubscribe.java
|    |──MqttSuback.java
|    |──MqttUnsubscribe.java
|    └──MqttWireMessage.java
|   |──log
|    |──Logger.java
|    |──LoggerFactory.java
|    └──LoggerSample.java
|   |──persistence
|    |──FileLock.java
|    |──MqttClientFilePersistence.java
|    └──MqttClientMemoryPersistence.java
|   |──server
|    |──pers
|     |──DataPersistence.java
|     └──DefaultPersistence.java
|    |──ServerConfig.java
|    |──ServerHandle.java
|    |──ServerMain.java
|    └──ServerThread.java
|   |──util
|    |──ByteUtil.java
|    |──FileUtil.java
|    |──Random.java
|    |──Script.java
|    └──StringUtil.java
|   |──LiveMQ.java
|   |──LiveMQServer.java
|   |──Configuration.java
|   |──MqttMessage.java
|   |──MqttClientCallback.java
|   |──MqttServerCallback.java
|   |──MqttClientPersistence.java
|   └──MqttServerPersistence.java
└──test
resource
  |──livemq.cfg
  |──livemq.sh
  └──run.sh
```

### 压缩包目录介绍

```
livemq-1.0.0
|──bin
  |──livemq.sh
  |──run.sh
  └──livqmq.jar
|──conf
  └──livemq.cfg
|──data
|──license
|──log
  └──livemq.log
└──README.md
```




package test;

import org.livemq.MqttClientCallback;
import org.livemq.internal.wire.MqttWireMessage;
import org.livemq.log.Logger;
import org.livemq.log.LoggerFactory;

public class CustomCallback implements MqttClientCallback {

	private static final Logger logger = LoggerFactory.getLogger(CustomCallback.class);
	
	@Override
	public void connected() {
		logger.log("客户端连接成功");
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.log("客户端连接断开");
	}

	@Override
	public void messageArrived(String topic, MqttWireMessage message) {
		logger.log("收到  " + topic + ", " + MqttWireMessage.getTypeName(message.getType()) + "，ID：" + message.getMessageId());
	}

	@Override
	public void deliveryComplete(MqttWireMessage message) {
		logger.log("消息发送成功：" + MqttWireMessage.getTypeName(message.getType()) + "，ID：" + message.getMessageId());
	}

}

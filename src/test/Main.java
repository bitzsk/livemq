package test;

import org.livemq.Configuration;
import org.livemq.LiveMQ;
import org.livemq.MqttMessage;
import org.livemq.exception.MqttException;

public class Main {

	private static String clientId = "admin";
	private static String serverURI = "tcp://127.0.0.1:1883";
	private static String username = "admin";
	private static String password = "123456";

	public static void main(String[] args) throws MqttException {
		Configuration config = new Configuration();
		config.setClientId(clientId);
		config.setServerURI(serverURI);
		config.setCleanSession(true);
		config.setConnectionTimeout(30);
		config.setKeepAliveInterval(60);
		config.setUsername(username);
		config.setPassword(password.toCharArray());
		config.setWillTopic(clientId);
		
		MqttMessage willMessage = new MqttMessage();
		willMessage.setQos(2);
		willMessage.setPayload((clientId + "'s will message payload ...").getBytes());
		config.setWillMessage(willMessage);
		
		LiveMQ mq = new LiveMQ(config);
		mq.setCallback(new CustomCallback());
		mq.connect();
		
		mq.subscribe(clientId);
		
		
		String topic = "lucy";
		String payload = "订阅主题  ["+ topic +"] 的客户端们，你们好我是  " + clientId + "。";
		MqttMessage message = new MqttMessage();
		message.setQos(2);
		message.setPayload(payload.getBytes());
		mq.publish(topic, message);
	}
}

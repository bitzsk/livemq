package test;

import org.livemq.Configuration;
import org.livemq.LiveMQ;
import org.livemq.MqttMessage;
import org.livemq.exception.MqttException;

public class Lucy {

	private static String clientId = "lucy";
	private static String serverURI = "tcp://127.0.0.1:1883";
	private static String username = "lucy";
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
	}
}

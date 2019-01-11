package it.smartcommunitylab.loratb.core;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.loratb.utils.Utils;

@Component
public class MqttManager {
	private static final transient Logger logger = LoggerFactory.getLogger(MqttManager.class);
	
	@Value("${lora.mqtt.endpoint}")
	private String endpoint;

	@Value("${lora.user}")
	private String user;

	@Value("${lora.password}")
	private String password;
	
	@Value("${lora.mqtt.topic}")
	private String topic;
		
	private IMqttClient mqttClient;
	
	private MqttMessageListener messageListener;
	
	public void init() throws Exception {
		String clientId = Utils.getUUID();
		mqttClient = new MqttClient(endpoint, clientId, new MemoryPersistence());
		MqttConnectOptions options = new MqttConnectOptions();
		options.setKeepAliveInterval(120);
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(30);
		options.setUserName(user);
		options.setPassword(password.toCharArray());
		
		mqttClient.setCallback(new MqttCallbackExtended() {
			
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				//TODO move log to debug level
				if(logger.isInfoEnabled()) {
					logger.info(String.format("MQTT messageArrived: %s - %s", message.getId(), topic));
				}
				if(messageListener != null) {
					messageListener.onMessage(topic, message);
				}
			}
			
			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
			}
			
			@Override
			public void connectionLost(Throwable cause) {
				logger.warn(String.format("MQTT connectionLost: %s", cause));
			}
			
			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("MQTT connectComplete: %s - %s", serverURI, reconnect));
				}
			}
		});
		
		mqttClient.connect(options);
		mqttClient.subscribe(topic);
//		mqttClient.subscribe(topic, (topic, msg) -> {
//			if(logger.isDebugEnabled()) {
//				logger.debug("receiveMesage:" + msg.getId());
//			}
//			if(messageListener != null) {
//				messageListener.onMessage(topic, msg);
//			}
//		});
	}

	public MqttMessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(MqttMessageListener messageListener) {
		this.messageListener = messageListener;
	}
	
}

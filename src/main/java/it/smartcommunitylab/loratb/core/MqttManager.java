package it.smartcommunitylab.loratb.core;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(15);
		options.setUserName(user);
		options.setPassword(password.toCharArray());
		mqttClient.connect(options);
		if(logger.isInfoEnabled()) {
			logger.info("MQTT connected:" + endpoint);
		}
		mqttClient.subscribe(topic, (topic, msg) -> {
			if(logger.isDebugEnabled()) {
				logger.debug("receiveMesage:" + msg.getId());
			}
			if(messageListener != null) {
				messageListener.onMessage(topic, msg);
			}
		});
	}

	public MqttMessageListener getMessageListener() {
		return messageListener;
	}

	public void setMessageListener(MqttMessageListener messageListener) {
		this.messageListener = messageListener;
	}
	
}

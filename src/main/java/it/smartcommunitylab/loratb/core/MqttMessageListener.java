package it.smartcommunitylab.loratb.core;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttMessageListener {
	public void onMessage(String topic, MqttMessage message);
}

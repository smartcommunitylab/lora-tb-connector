package it.smartcommunitylab.loratb.core;

public interface MqttMessageListener {
	public void onMessage(String topic, String message);
}

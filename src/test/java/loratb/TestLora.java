package loratb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.loratb.core.DataManager;
import it.smartcommunitylab.loratb.ext.lora.LoraManager;
import it.smartcommunitylab.loratb.model.Device;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(locations= {"classpath:application.properties"})
public class TestLora {
	@Autowired
	private LoraManager loraManager;
	
	@Autowired
	private DataManager dataManager;
	
	private CountDownLatch lock = new CountDownLatch(1);
	
	@Test
	public void getToken() throws Exception {
		String token = loraManager.getToken();
		assertNotNull(token);
		System.out.println("token:" + token);
	}
	
	@Test
	public void storeApplications() throws Exception {
		dataManager.storeLoraApplications();
	}
	
	@Test
	public void storeDevices() throws Exception {
		dataManager.storeLoraDevices();
	}
	
	@Test
	public void allignDevices() throws Exception {
		dataManager.alignLoraDevices();
	}
	
	@Test
	public void getDevices() throws Exception {
		List<Device> list = loraManager.getDevicesByApp("76");
		assertTrue(list.size() > 0);
	}
	
//	@Test
//	public void subscribeMqttMsg() throws Exception {
//		lock.await(60000*15, TimeUnit.MILLISECONDS);
//	}
	
	
}

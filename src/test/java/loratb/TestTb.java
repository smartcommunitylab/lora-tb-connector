package loratb;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.loratb.core.DataManager;
import it.smartcommunitylab.loratb.ext.tb.ThingsBoardManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(locations= {"classpath:application.properties","classpath:log4j.properties"})
public class TestTb {
	@Autowired
	private ThingsBoardManager tbManager;
	
	@Autowired
	private DataManager dataManager;
	
	@Test
	public void getToken() throws Exception {
		String token = tbManager.getToken();
		assertNotNull(token);
		System.out.println("token:" + token);
	}
	
	@Test
	public void alignCustomers() throws Exception {
		dataManager.alignCustomers();
	}
	
	@Test
	public void alignDevices() throws Exception {
		dataManager.alignDevices();
	}
	
	
}

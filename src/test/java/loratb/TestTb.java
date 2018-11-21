package loratb;

import static org.junit.Assert.assertNotNull;

import java.util.Base64;

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
@TestPropertySource(locations= {"classpath:application.properties"})
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
	public void decodeToken() throws Exception {
		String jwtToken = tbManager.getToken();
		System.out.println("------------ Decode JWT ------------");
    String[] split_string = jwtToken.split("\\.");
    String base64EncodedHeader = split_string[0];
    String base64EncodedBody = split_string[1];
    String base64EncodedSignature = split_string[2];

    System.out.println("~~~~~~~~~ JWT Header ~~~~~~~");
    String header = new String(Base64.getDecoder().decode(base64EncodedHeader));
    System.out.println("JWT Header : " + header);


    System.out.println("~~~~~~~~~ JWT Body ~~~~~~~");
    String body = new String(Base64.getDecoder().decode(base64EncodedBody));
    System.out.println("JWT Body : "+body);		
	}
	
	@Test
	public void storeCustomers() throws Exception {
		dataManager.storeTbCustomers();
	}
	
	@Test
	public void storeDevices() throws Exception {
		dataManager.storeTbDevices();
	}
	
	@Test
	public void storeUser() throws Exception {
		dataManager.storeTbUser();
	}
	
	
}

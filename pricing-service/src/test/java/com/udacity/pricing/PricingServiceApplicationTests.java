package com.udacity.pricing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testGetAllPrice() {
		ResponseEntity<String> responseEntity = this.testRestTemplate.getForEntity(
							"http://localhost:"+port+"/prices",String.class);
		assert(responseEntity.getStatusCode().equals(HttpStatus.OK));

	}

	@Test
	public void testGetPrice() {
		ResponseEntity<String> responseEntity = this.testRestTemplate.getForEntity(
				"http://localhost:"+port+"/prices/1",String.class);
		assert(responseEntity.getStatusCode().equals(HttpStatus.OK));
	}

}

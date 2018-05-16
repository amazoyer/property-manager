package com.property.manager.dal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.RestClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.property.manager.dal.interfaces.IESClient;
import com.property.model.Property;
import com.property.model.PropertyPrice;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ESTestConfig.class })
public class PropertyDALIntegrationTest {
	
	@Inject
	public PropertyDAL propertyDAL;

	@Inject
	public IESClient esClient;

	@Test
	public void addPropertyTest() throws IOException {
		Property property = new Property();
		property.setAddress("80 rue de jean driac");
		PropertyPrice propertyPrice = new PropertyPrice();
		propertyPrice.setPrice(12);
		property.getPrices().add(propertyPrice);
		propertyDAL.addNewProperty(property);
	}
	
	@Test
	public void searchByAddressTest() throws IOException {
		List<Property> properties = propertyDAL.searchPropertyByAddress("driac");
		Assert.assertTrue("No data found", !properties.isEmpty());
		String address = properties.get(0).getAddress();
		Assert.assertTrue("Incorrect address returned : "+address, address.contains("driac"));
	}
	
	@Ignore
	public void addPropertyPriceTest() throws IOException {
		PropertyPrice propertyPrice = new PropertyPrice();
		propertyPrice.setPrice(13);
		//propertyDAL.addNewPropertyPrice(propertyId, price);
	}

	@Before
	public void injectMapping() throws IOException {
		RestClient restClient = esClient.getRestClient();
		org.elasticsearch.client.Response indexResponse;
//
//		try {
//			indexResponse = restClient.performRequest("DELETE", "property", Collections.<String, String>emptyMap());
//		} catch (Exception e) {
//		}
//		
		HttpEntity entity = new NStringEntity(this.getFileWithUtil("esconfig/template.json"),
				ContentType.APPLICATION_JSON);

		indexResponse = restClient.performRequest("PUT", "_template/template_1", Collections.<String, String>emptyMap(),
				entity);
		

		entity = new NStringEntity(this.getFileWithUtil("testdata/testdata.json"),
				ContentType.APPLICATION_JSON);

		indexResponse = restClient.performRequest("POST", "_bulk", Collections.<String, String>emptyMap(),
				entity);
		
		restClient.performRequest("POST", "_refresh");
	}

	private String getFileWithUtil(String fileName) {
		String result = "";
		ClassLoader classLoader = PropertyDALIntegrationTest.class.getClassLoader();
		try {
			result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}

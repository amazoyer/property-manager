package com.property.manager.dal;

import java.io.IOException;
import java.util.Collection;
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
		property.setAddress("new address");
		PropertyPrice propertyPrice = new PropertyPrice();
		propertyPrice.setPrice(12);
		property.getPrices().add(propertyPrice);
		propertyDAL.addNewProperty(property);
		refresh();
		Assert.assertEquals(1, propertyDAL.searchByAddress("new").size());
	}
	
	private void refresh() throws IOException{
		esClient.getHighLevelClient().getLowLevelClient().performRequest("POST", "_refresh");
	}
	
	@Test
	public void searchByAddressTest() throws IOException {
		Collection<Property> properties = propertyDAL.searchByAddress("driac").values();
		Assert.assertTrue("No data found", !properties.isEmpty());
		String address = properties.iterator().next().getAddress();
		Assert.assertTrue("Incorrect address returned : "+address, address.contains("driac"));
	}
	
	@Test
	public void addPropertyPriceTest() throws IOException {
		PropertyPrice propertyPrice = new PropertyPrice();
		propertyPrice.setPrice(120000);
		PropertyPrice propertyPrice2 = new PropertyPrice();
		propertyPrice2.setPrice(125000);
		propertyDAL.addNewPropertyPrice("1", propertyPrice);
		propertyDAL.addNewPropertyPrice("1", propertyPrice2);
		refresh();
		Property modifiedProperty = propertyDAL.getPropertyByID("1");
		Assert.assertEquals("No price found", 2, modifiedProperty.getPrices().size());
		
	}

	@Before
	public void injectMapping() throws IOException {
		RestClient restClient = esClient.getHighLevelClient().getLowLevelClient();
		org.elasticsearch.client.Response indexResponse;

		try {
			indexResponse = restClient.performRequest("DELETE", "property", Collections.<String, String>emptyMap());
		} catch (Exception e) {
		}
		
		HttpEntity entity = new NStringEntity(this.getFileWithUtil("esconfig/template.json"),
				ContentType.APPLICATION_JSON);

		restClient.performRequest("PUT", "_template/template_1", Collections.<String, String>emptyMap(),
				entity);
		

		entity = new NStringEntity(this.getFileWithUtil("testdata/testdata.json"),
				ContentType.APPLICATION_JSON);

		restClient.performRequest("POST", "_bulk", Collections.<String, String>emptyMap(),
				entity);

		refresh();
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

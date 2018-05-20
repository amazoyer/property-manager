package com.property.manager.dal;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;

import com.property.manager.dal.interfaces.IESClient;

@Named
public class ESClient implements IESClient{
	
	private static final Logger logger = Logger.getLogger(ESClient.class.getName());

	
	private RestHighLevelClient highLevelClient;
	

    @Value("${esHost}")
    private String esHost;
    
    private static String DEFAULT_HOST = "localhost";
	
	public ESClient(){
		logger.info("Initializing es connection");
		highLevelClient = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(esHost!=null ? esHost : DEFAULT_HOST, 9200, "http")));
		
	}
	public RestHighLevelClient getHighLevelClient(){
		return highLevelClient;
	}
	

	
	@PreDestroy
	public void cleanUp() throws Exception {
		logger.info("Closing es connection");
		highLevelClient.close();
	}

	

}

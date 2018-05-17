package com.property.manager.dal;

import javax.inject.Named;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import com.property.manager.dal.interfaces.IESClient;

@Named
public class ESClient implements IESClient{
	
	private RestHighLevelClient highLevelClient;
	
	public ESClient(){
		highLevelClient = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", 9200, "http")));
		
	}
	public RestHighLevelClient getHighLevelClient(){
		return highLevelClient;
	}
	

}

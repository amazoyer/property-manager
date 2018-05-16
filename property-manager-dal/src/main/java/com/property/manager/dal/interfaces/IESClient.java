package com.property.manager.dal.interfaces;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public interface IESClient {
	public RestHighLevelClient getHighLevelClient();
	public RestClient getRestClient();
}

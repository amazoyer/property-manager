package com.property.manager.dal.interfaces;

import org.elasticsearch.client.RestHighLevelClient;

public interface IESClient {
	public RestHighLevelClient getHighLevelClient();
}

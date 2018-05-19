package com.property.manager.dal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.property.exceptions.DataException;
import com.property.interfaces.IPropertyDAL;
import com.property.manager.dal.interfaces.IESClient;
import com.property.model.Property;
import com.property.model.PropertyPrice;

@Named
public class PropertyDAL implements IPropertyDAL{

	private static String PROPERTY_INDEX_TYPE = "property";

	private ObjectMapper mapper;
	private IESClient esClient;

	@Inject
	public PropertyDAL(IESClient esClient) {
		this.esClient = esClient;
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	public void addNewProperty(Property property) throws DataException {
		String propertyJSON;
		try {
			propertyJSON = mapper.writeValueAsString(property);
		} catch (JsonProcessingException e) {
			throw new DataException("Parsing property error", e);
		}
		IndexRequest indexRequest = new IndexRequest(PROPERTY_INDEX_TYPE, PROPERTY_INDEX_TYPE).source(propertyJSON,
				XContentType.JSON);
		indexRequest.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
		try {
			esClient.getHighLevelClient().index(indexRequest);
		} catch (IOException e) {
			throw new DataException("Cannot index property", e);
		}
	}

	public void addNewPropertyPrice(String propertyId, PropertyPrice price) throws DataException {
		String query;
		try {
			query = "{ \"script\": { \"inline\" :  \"if (ctx._source.prices == null || ctx._source.prices.size() == 0){ ctx._source.prices = params.prices}  else {ctx._source.prices.add(params.prices[0]) } \", \"lang\": \"painless\" , \"params\": { \"prices\" :  ["
					+ mapper.writeValueAsString(price) + " ]}}}";
		} catch (JsonProcessingException e) {
			throw new DataException("Parsing price error", e);
		}
		NStringEntity entity = new NStringEntity(query, ContentType.APPLICATION_JSON);
		try {
			esClient.getHighLevelClient().getLowLevelClient().performRequest("POST",
					"/" + PROPERTY_INDEX_TYPE + "/" + PROPERTY_INDEX_TYPE + "/" + propertyId + "/_update",
					Collections.<String, String>emptyMap(), entity);
		} catch (IOException e) {
			throw new DataException("Cannot index price", e);

		}
	}

	public Map<String, Property> listAllProperties() throws DataException {
		try {
			return searchProperty(QueryBuilders.matchAllQuery());
		} catch (IOException e) {
			throw new DataException("Cannot list properties", e);
		}
	}

	public Map<String, Property> searchByAddress(String address) throws DataException {
		try {
			return searchProperty(QueryBuilders.matchQuery("address", address));
		} catch (IOException e) {
			throw new DataException("Cannot search on address", e);
		}
	}

	private Map<String, Property> searchProperty(QueryBuilder query)
			throws JsonParseException, JsonMappingException, IOException {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = esClient.getHighLevelClient().search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		Map<String, Property> properties = new HashMap<String, Property>();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			properties.put(hit.getId(), mapper.readValue(hit.getSourceAsString(), Property.class));
		}
		return properties;
	}

	public Property getPropertyByID(String id) throws DataException  {
		GetRequest getRequest = new GetRequest(PROPERTY_INDEX_TYPE, PROPERTY_INDEX_TYPE, id);
		GetResponse getResponse;
		try {
			getResponse = esClient.getHighLevelClient().get(getRequest);
			return mapper.readValue(getResponse.getSourceAsString(), Property.class);
		} catch (IOException e) {
			throw new DataException("Cannot get property", e);
		}
	}

	public void removeProperty(String id) throws DataException {
		DeleteRequest deleteRequest = new DeleteRequest(PROPERTY_INDEX_TYPE, PROPERTY_INDEX_TYPE, id);
		deleteRequest.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
		try {
			esClient.getHighLevelClient().delete(deleteRequest);
		} catch (IOException e) {
			throw new DataException("Cannot remove property", e);
		}
	}

	public void updateProperty(String id, Property partialProperty) throws DataException {
		String propertyJSON;
		try {
			propertyJSON = mapper.writeValueAsString(partialProperty);
		} catch (JsonProcessingException e) {
			throw new DataException("Parsing property error", e);
		}
		UpdateRequest request = new UpdateRequest(PROPERTY_INDEX_TYPE, PROPERTY_INDEX_TYPE, id).doc(propertyJSON,
				XContentType.JSON);
		request.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
		try {
			esClient.getHighLevelClient().update(request);
		} catch (IOException e) {
			throw new DataException("Cannot update property", e);

		}
	}

}

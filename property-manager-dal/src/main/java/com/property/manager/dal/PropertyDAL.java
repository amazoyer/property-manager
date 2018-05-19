package com.property.manager.dal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
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
import com.fasterxml.jackson.databind.*;
import com.property.manager.dal.interfaces.IESClient;
import com.property.model.Property;
import com.property.model.PropertyPrice;

@Named
public class PropertyDAL {

	private ObjectMapper mapper;
	private IESClient esClient;

	@Inject
	public PropertyDAL(IESClient esClient) {
		this.esClient = esClient;
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	public void addNewProperty(Property property) throws IOException {
		String propertyJSON = mapper.writeValueAsString(property);
		IndexRequest indexRequest = new IndexRequest("property", "property").source(propertyJSON, XContentType.JSON);
		indexRequest.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
		esClient.getHighLevelClient().index(indexRequest);
	}


	public void addNewPropertyPrice(String propertyId, PropertyPrice price) throws IOException {
//		UpdateRequest request = new UpdateRequest("property", "property", propertyId);
//		Map<String, Object> parameters = Collections.singletonMap("prices", (Object) Collections.singletonList(mapper.writeValueAsString(price)));
//		Script inline = new Script(ScriptType.INLINE, "painless", "if (ctx._source.prices == null || ctx._source.prices.size() == 0){ ctx._source.prices = params.prices}  else {ctx._source.prices.add(params.prices[0]) } ", parameters);
//		request.script(inline);
//		esClient.getHighLevelClient().update(request);
		
		String query = "{ \"script\": { \"inline\" :  \"if (ctx._source.prices == null || ctx._source.prices.size() == 0){ ctx._source.prices = params.prices}  else {ctx._source.prices.add(params.prices[0]) } \", \"lang\": \"painless\" , \"params\": { \"prices\" :  ["+mapper.writeValueAsString(price)+" ]}}}"; 
		NStringEntity entity = new NStringEntity(query,
				ContentType.APPLICATION_JSON);
		esClient.getHighLevelClient().getLowLevelClient().performRequest("POST", "/property/property/"+propertyId+"/_update", Collections.<String, String>emptyMap(),
				entity);
	}

	public Map<String, Property> listAllProperties() throws JsonParseException, JsonMappingException, IOException{
		return searchProperty(QueryBuilders.matchAllQuery());
	}
	
	public Map<String, Property> searchByAddress(String address) throws JsonParseException, JsonMappingException, IOException{
		return searchProperty(QueryBuilders.matchQuery("address", address));
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


	public Property getPropertyByID(String id) throws JsonParseException, JsonMappingException, IOException {
		GetRequest getRequest = new GetRequest("property", "property", id);
		GetResponse getResponse = esClient.getHighLevelClient().get(getRequest);
		return mapper.readValue(getResponse.getSourceAsString(), Property.class);
	}
	

	public void removeProperty(String id) throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("property", "property", id);
		deleteRequest.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
		esClient.getHighLevelClient().delete(deleteRequest);
	}

	public void updateProperty(String id, Property partialProperty) throws IOException {
		String propertyJSON = mapper.writeValueAsString(partialProperty);
		UpdateRequest request = new UpdateRequest(
		        "property", 
		        "property",  
		        id).doc(propertyJSON, XContentType.JSON);
		esClient.getHighLevelClient().update(request);
	}


}

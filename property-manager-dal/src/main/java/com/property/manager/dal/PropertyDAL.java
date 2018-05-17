package com.property.manager.dal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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
	}

	public void addNewProperty(Property property) throws IOException {
		String propertyJSON = mapper.writeValueAsString(property);
		IndexRequest indexRequest = new IndexRequest("property", "property").source(propertyJSON, XContentType.JSON);
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

	public List<Property> searchPropertyByAddress(String address)
			throws JsonParseException, JsonMappingException, IOException {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("address", address));
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = esClient.getHighLevelClient().search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		List<Property> properties = new ArrayList<Property>();
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			properties.add(mapper.readValue(hit.getSourceAsString(), Property.class));
		}
		return properties;

	}

	public Property getPropertyByID(String id) throws JsonParseException, JsonMappingException, IOException {
		GetRequest getRequest = new GetRequest("property", "property", id);
		GetResponse getResponse = esClient.getHighLevelClient().get(getRequest);
		return mapper.readValue(getResponse.getSourceAsString(), Property.class);
	}

}

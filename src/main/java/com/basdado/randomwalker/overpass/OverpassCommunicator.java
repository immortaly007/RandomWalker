package com.basdado.randomwalker.overpass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;

import com.basdado.randomwalker.config.ConfigService;
import com.basdado.randomwalker.exception.OverpassException;

import de.topobyte.osm4j.core.access.OsmHandler;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;

@Stateless
public class OverpassCommunicator {
	
	@Inject private Logger logger;
	
	@Inject private ConfigService config;
	
	public void executeQuery(String query, OsmHandler osmHandler) throws OverpassException {
		
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			String overpassURL = config.getOpenStreetMapConfiguration().getOverpassURL();
			
			HttpPost request = new HttpPost(overpassURL);
			
			List<NameValuePair> postData = new ArrayList<>();
			postData.add(new BasicNameValuePair("data", query));
			request.setEntity(new UrlEncodedFormEntity(postData));
			
			logger.info("Executing query: " + query);
			
			HttpResponse response = httpClient.execute(request);
			
			OsmXmlReader osmXmlReader = new OsmXmlReader(response.getEntity().getContent(), false);
			osmXmlReader.setHandler(osmHandler);
			osmXmlReader.read();
			
			logger.info("Done");
			
		} catch (IOException | OsmInputException e) {
			
			throw new OverpassException(e);
			
		}
		
	}

}

package com.basdado.randomwalker.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.ejb.EJBException;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.basdado.randomwalker.model.LatLonCoordinate;

@Stateless
public class HackyLocationUpdater {
	
	@Inject Logger logger;
	
	@Inject RandomWalkerController randomWalkerController;
	
	@Schedule(hour="*",minute="*", second="*/10", persistent=false)
	public void updateWordpress() {
		
		LatLonCoordinate position = randomWalkerController.getCurrentPosition();
		
		logger.info("Updating wordpress plugin");
		
		SSLConnectionSocketFactory sslsf;
		
	    try {
	    	SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslsf = new SSLConnectionSocketFactory(builder.build());
		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			throw new EJBException(e);
		}
	     
		
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build()) {
			
			HttpGet request = new HttpGet("https://wheatley.basdado.com/bas-test-wp/sendlocation/immortaly007/239a6ff7/"
					+ "?lat=" + position.getLatitude() 
					+ "&lon=" + position.getLongitude());
			
			try (CloseableHttpResponse response = httpClient.execute(request)) {
				
				logger.info("Wordpress response: " + EntityUtils.toString(response.getEntity()));
				
			}
			
			
		} catch (IOException e) {
			throw new EJBException(e);
		}
		
		
	}

}

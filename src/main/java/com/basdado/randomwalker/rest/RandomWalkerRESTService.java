package com.basdado.randomwalker.rest;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import com.basdado.randomwalker.controller.RandomWalkerController;
import com.basdado.randomwalker.model.LatLonCoordinate;

/**
 * This class produces a RESTful service to access the train finder
 */
@Path("randomwalker")
@RequestScoped
public class RandomWalkerRESTService {

	@Inject private Logger logger;
	
	@Inject private RandomWalkerController randomWalkerController;
    
    @GET
    @Path("getPosition")
    @Produces(MediaType.APPLICATION_JSON)
    public LatLonCoordinate getPosition() {
    	return randomWalkerController.getCurrentPosition();
    }
}

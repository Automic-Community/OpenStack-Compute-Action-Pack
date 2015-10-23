package com.automic.openstack.filter;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicRuntimeException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the response to validate it.
 * 
 */

public class AuthenticationFilter extends ClientFilter {



    private static final Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class);

    @Override
    public ClientResponse handle(ClientRequest request) {
    	System.out.println("AuthenticationFilter");
    	
    	String [] AuthDetails = String.valueOf(request.getHeaders().get(Constants.X_AUTH_TOKEN)).split("");
    	
    	return null;
       
    }

}

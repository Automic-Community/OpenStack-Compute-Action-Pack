package com.automic.openstack.filter;



import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.model.AuthenticationToken;
import com.automic.openstack.service.AuthenticationTokenSevice;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the response to validate it.
 * 
 */

public class AuthenticationFilter extends ClientFilter {

    private String currentAEDate;
    private Client client; 
    public AuthenticationFilter(String currentDate, Client client) {
		this.currentAEDate = currentDate;
		this.client = client;
	}

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class);
    private static final 	SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    @Override
    public ClientResponse handle(ClientRequest request) {
    	
    
    	
    	System.out.println("AuthenticationFilter");
    	
    	String  authDetails = String.valueOf(request.getHeaders().get(Constants.X_AUTH_TOKEN));
    	
    	try {
    		
			AuthenticationToken authToken = new AuthenticationToken(AESEncryptDecrypt.decrypt(authDetails));
			
			if(isExpired(authToken.getTokenExpiry(), currentAEDate)){
				
				AuthenticationTokenSevice ats = AuthenticationTokenSevice.getListServerService(client);
			    JSONObject jsonObj = ats.executeListServerService(authToken.getBaseurl(), authToken.getUserName(), authToken.getPassword(), authToken.getTenantName());
				
			}
			
			
		} catch (AutomicException e) {
			
			e.printStackTrace();
		}
    	
    	
    	return null;
       
    }
    
    private boolean isExpired(String expiresDate, String currentAEDate) {
    	
    	//SimpleDateFormat currentAEdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//SimpleDateFormat expirationTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	
    	
    	long currentAETime = 0;
    	long expirationTime = 0; 
		try {
			currentAETime = datetimeFormat.parse(currentAEDate).getTime();
			expirationTime = datetimeFormat.parse(expiresDate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	
    	return currentAETime <= expirationTime;
    	
    	
    	
    }

}

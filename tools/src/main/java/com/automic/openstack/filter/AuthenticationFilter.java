package com.automic.openstack.filter;



import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.exception.AutomicRuntimeException;
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
    private int timeoutCriteria;
    
    public AuthenticationFilter(String currentDate, Client client, int timeoutCriteria) {
		this.currentAEDate = currentDate;
		this.client = client;
		this.timeoutCriteria = timeoutCriteria;
	}

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class);
    private static final SimpleDateFormat DATETIMEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    @Override
    public ClientResponse handle(ClientRequest request) {    	
    	JSONObject jsonObj = null;       	
    	String  authDetails = (String) request.getHeaders().getFirst(Constants.X_AUTH_TOKEN);
    	if(authDetails == null){
        	return	getNext().handle(request);
    	}
    	try {
    		
			AuthenticationToken authToken = new AuthenticationToken(AESEncryptDecrypt.decrypt(authDetails));	
			
			if(isExpired(authToken.getTokenExpiry(), currentAEDate, timeoutCriteria)){				
				 AuthenticationTokenSevice ats = AuthenticationTokenSevice.getListServerService(client);
			     jsonObj = ats.executeAuthenticationTokenSevice(authToken.getBaseurl(), authToken.getUserName(), authToken.getPassword(), authToken.getTenantName());
			     JSONObject tokenJson = jsonObj.getJSONObject("access").getJSONObject("token");
			     request.getHeaders().putSingle(Constants.X_AUTH_TOKEN, tokenJson.getString("id"));
			}else{// if token not expire
				
				request.getHeaders().putSingle(Constants.X_AUTH_TOKEN, authToken.getTokenId());
			}
			
		} catch (AutomicException e) {			
			   LOGGER.error(e);
	           throw new AutomicRuntimeException(e.getMessage());
		}    	
    	return getNext().handle(request);       
    }
    
    private boolean isExpired(String expiresDate, String currentAEDate, int timeoutCriteria) {    	
    	
    	long currentAETime = 0;
    	long expirationTime = 0; 
    	final long ONE_MINUTE_IN_MILLIS = 60000;//millisecond
		try {
			currentAEDate = currentAEDate.replaceAll("T", " ").replaceAll("Z", "+0000");
			expiresDate = expiresDate.replaceAll("T", " ").replaceAll("Z", "+0000");
			currentAETime = DATETIMEFORMAT.parse(currentAEDate).getTime();
			currentAETime = currentAETime + timeoutCriteria * ONE_MINUTE_IN_MILLIS;
			expirationTime = DATETIMEFORMAT.parse(expiresDate).getTime();
			
			
		} catch (ParseException e) {
			   LOGGER.error(e);
	           throw new AutomicRuntimeException(e.getMessage());
		}    	
    	return expirationTime <= currentAETime;    	
    	    }

}

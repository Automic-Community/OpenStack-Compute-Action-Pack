package com.automic.openstack.service;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.CommonUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is used to execute request service.
 * 
 *  @author Anurag Upadhyay
 */
public class ResponseProcessService {
	  private static final Logger LOGGER = LogManager.getLogger(ResponseProcessService.class);
	
	  private final Client client;	  
	/**
	 * Initializes a newly created {@code ResponseProcessService} object.
	 * 
	 * @param client
	 */
	 private ResponseProcessService(Client client) {
		
		this.client = client;
	 }
	 
	 /**
	  * This method create the instance of {@code ResponseProcessService} with provided client.
	  * @param client
	  * @return Initialize instance of {@code ResponseProcessService}
	  */
	 public static ResponseProcessService getResponseProcessService(Client client){
	    	
	    	return new ResponseProcessService(client);
	    	
	   }
	 
	   /**
	    * Get server details using reservation id by calling http://baseUrl/{tenant_id}​/servers/detail.
	    * 
	    * @param baseUrl
	    * @param tenantId
	    * @param tokenId
	    * @param reservationId
	    * @return {@code JSONObject} object
	    * @throws AutomicException
	    */
	    public JSONObject executeListServerService(String baseUrl, String tenantId, String tokenId, String reservationId) throws AutomicException {

	        ClientResponse response = null;	       
	        JSONObject jsonObj = null;
	        
	        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path("detail").queryParam("reservation_id", reservationId);

	        LOGGER.info("List servers Calling url " + webResource.getURI());

	        response = webResource.accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
	                .get(ClientResponse.class);	        
	         jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());
	         
	        return jsonObj;

	    }
	    
	   
}

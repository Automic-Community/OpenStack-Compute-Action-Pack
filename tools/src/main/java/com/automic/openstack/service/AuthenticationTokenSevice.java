package com.automic.openstack.service;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.CommonUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class AuthenticationTokenSevice {
	private static final Logger LOGGER = LogManager
			.getLogger(ListServerService.class);

	private final Client client;

	/**
	 * Initializes a newly created {@code AuthenticationTokenSevice} object.
	 * 
	 * @param client
	 */
	private AuthenticationTokenSevice(Client client) {

		this.client = client;
	}

	/**
	 * This method create the instance of {@code AuthenticationTokenSevice} with
	 * provided client.
	 * 
	 * @param client
	 * @return Initialize instance of {@code AuthenticationTokenSevice}
	 */
	public static AuthenticationTokenSevice getListServerService(Client client) {

		return new AuthenticationTokenSevice(client);

	}
	

	/**
	 * Get server details by calling http://baseUrl/{tenant_id}​/servers/detail.
	 * 
	 * @param baseUrl
	 * @param tenantId
	 * @param tokenId
	 * @param reservationId
	 * @return {@code JSONObject} object
	 * @throws AutomicException
	 */
	public JSONObject executeListServerService(String baseUrl, String username,
			String password, String tenantName) throws AutomicException {
		ClientResponse response = null;
		WebResource webResource = client.resource(baseUrl).path("tokens");

        LOGGER.info("Calling  authentication token url " + webResource.getURI());

        response = webResource.accept(MediaType.APPLICATION_JSON)
                .entity(getAuthenticationJson(username, password, tenantName).toString(), MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
         
        
		return CommonUtil.jsonResponse(response.getEntityInputStream());

	}
	
	 private JSONObject getAuthenticationJson(String username, String password, String tenantName) {

	        JSONObject passwordCreds = new JSONObject();
	        passwordCreds.put("username", username);
	        passwordCreds.put("password", password);

	        JSONObject auth = new JSONObject();

	        auth.put("passwordCredentials", passwordCreds);
	        if (tenantName != null && !tenantName.isEmpty()) {
	            auth.put("tenantName", tenantName);
	        }

	        JSONObject json = new JSONObject();
	        json.put("auth", auth);

	        return json;

	}

}

package com.automic.openstack.service;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.CommonUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is used to execute List Service request.
 * 
 * @author Anurag Upadhyay
 */
public class ListServerService {
	private static final Logger LOGGER = LogManager
			.getLogger(ListServerService.class);

	private final Client client;

	/**
	 * Initializes a newly created {@code ResponseProcessService} object.
	 * 
	 * @param client
	 */
	private ListServerService(Client client) {

		this.client = client;
	}

	/**
	 * This method create the instance of {@code ResponseProcessService} with
	 * provided client.
	 * 
	 * @param client
	 * @return Initialize instance of {@code ResponseProcessService}
	 */
	public static ListServerService getListServerService(Client client) {

		return new ListServerService(client);

	}
	/**
	 * Get server details using reservation id by calling
	 * http://baseUrl/{tenant_id}​/servers/detail.
	 * 
	 * @param baseUrl
	 * @param tenantId
	 * @param tokenId
	 * @param reservationId
	 * @return {@code JSONObject} object
	 * @throws AutomicException
	 */
	public JSONObject executeListServerService(String baseUrl, String tenantId,
			String tokenId, String reservationId) throws AutomicException {

		WebResource webResource = client.resource(baseUrl).path(tenantId)
				.path("servers").path("detail")
				.queryParam("reservation_id", reservationId);

		return executeRequest(webResource, tokenId);

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
	public JSONObject executeListServerService(String baseUrl, String tenantId,
			String tokenId) throws AutomicException {

		WebResource webResource = client.resource(baseUrl).path(tenantId)
				.path("servers").path("detail");

		return executeRequest(webResource, tokenId);

	}
	/**
	 * This method execute request by provided {@code WebResource} object
	 * @param webResource
	 * @param tokenId
	 * @return
	 */
	private JSONObject executeRequest(WebResource webResource, String tokenId) {

		ClientResponse response = null;
		JSONObject jsonObj = null;
		LOGGER.info("List servers Calling url " + webResource.getURI());
		String decryptedTokenId = AESEncryptDecrypt.decrypt(tokenId);
		response = webResource.accept(MediaType.APPLICATION_JSON)
				.header(Constants.X_AUTH_TOKEN, decryptedTokenId)
				.get(ClientResponse.class);
		jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());

		return jsonObj;

	}

}

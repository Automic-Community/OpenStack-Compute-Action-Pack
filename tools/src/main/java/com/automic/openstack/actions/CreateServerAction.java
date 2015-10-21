package com.automic.openstack.actions;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.service.ListServerService;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is used to Creates one or more servers with an optional
 * reservation ID. It publish one or more created server id.
 * 
 * @author Anurag Upadhyay
 */

public class CreateServerAction extends AbstractHttpAction {

	private static final Logger LOGGER = LogManager
			.getLogger(CreateServerAction.class);

	private static final String SERVER_ID = "id";
	private static final String RESERVATION_ID = "reservation_id";
	private static final String SERVER_KEY = "server";
	private static final String SERVERS_KEY = "servers";
	private static final String MIN_COUNT_KEY = "min_count";
	private static final String RESERVATION_ID_KEY = "return_reservation_id";
	
	private String parameterFilePath;
	private String tokenId;
	private String tenantId;
	

	public CreateServerAction() {

		addOption("computeurl", true, "Compute service endpoint");
		addOption("tokenid", true, "Token Id for authentication");
		addOption("tenantid", true, "Tenant/Project name");
		addOption("parameterfile", true, "Json file path ");

	}

	@Override
	protected void initialize() {
		baseUrl = getOptionValue("computeurl");
		tokenId = getOptionValue("tokenid");
		tenantId = getOptionValue("tenantid");
		parameterFilePath = getOptionValue("parameterfile");

	}

	@Override
	protected void validate() throws AutomicException {

		if (!Validator.checkNotEmpty(tokenId)) {
			LOGGER.error(ExceptionConstants.EMPTY_TOKENID);
			throw new AutomicException(ExceptionConstants.EMPTY_TOKENID);
		}
		if (!Validator.checkNotEmpty(tenantId)) {
			LOGGER.error(ExceptionConstants.EMPTY_TENANTID);
			throw new AutomicException(ExceptionConstants.EMPTY_TENANTID);
		}
		if (!Validator.checkFileExists(parameterFilePath)) {
			String errMsg = String.format(ExceptionConstants.INVALID_FILE,
					parameterFilePath);
			LOGGER.error(errMsg);
			throw new AutomicException(errMsg);
		}

	}

	@Override
	/**
	 * Authenticates and Create Servers by  http://baseUrl/v2/​{tenant_id}​/servers
	 * */
	protected void executeSpecific() throws AutomicException {

		ClientResponse response = null;

		String decryptedTokenId = AESEncryptDecrypt.decrypt(tokenId);

		WebResource webResource = client.resource(baseUrl).path(tenantId)
				.path("servers");

		LOGGER.info("Calling url " + webResource.getURI());

		response = webResource
				.entity(validateJsonObject(parameterFilePath),
						MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(Constants.X_AUTH_TOKEN, decryptedTokenId)
				.post(ClientResponse.class);

		prepareOutput(response);
	}

	/**
	 * This method publish one or more created server ids
	 * 
	 * @param response
	 * @throws AutomicException
	 */
	private void prepareOutput(ClientResponse response) throws AutomicException {
		JSONObject jsonObj = CommonUtil.jsonResponse(response
				.getEntityInputStream());

		if (jsonObj != null && jsonObj.has(SERVER_KEY)) {
			JSONObject server = jsonObj.getJSONObject(SERVER_KEY);
			ConsoleWriter.writeln("UC4RB_OPS_SERVER_IDS ::="
					+ server.getString(SERVER_ID));

		} else if (jsonObj != null && jsonObj.has(RESERVATION_ID)) {
			ListServerService rps = ListServerService
					.getListServerService(client);
			jsonObj = rps.executeListServerService(baseUrl, tenantId, tokenId,
					jsonObj.getString(RESERVATION_ID));

			if (jsonObj != null && jsonObj.has(SERVERS_KEY)) {
				publishSrversId(jsonObj.getJSONArray(SERVERS_KEY));
			} else {
				LOGGER.info(" Unable to find Json key[servers] in json object :"
						+ jsonObj);
			}

		} else {
			LOGGER.info(" Unable to find Json keys[server, reservation_id] in json object :"
					+ jsonObj);
		}

	}
   /**
    * This method iterate provided {@code JSONArray} and print server id
    * @param servers
    */
	private void publishSrversId(JSONArray servers) {
		if (servers != null && servers.length() > 0) {
			for (int i = 0; i < servers.length(); i++) {
				JSONObject server = servers.getJSONObject(i);
				ConsoleWriter.writeln("UC4RB_OPS_SERVER_IDS[" + i + "] ::="
						+ server.getString(SERVER_ID));
			}
		}

	}
  
    /**
     * This method validate and modify {@code JSONObject} instance integrity
     * @param parameterFilePath
     * @return
     * @throws AutomicException
     */
	private String validateJsonObject(String parameterFilePath)
			throws AutomicException {
		JSONObject jsonObj = CommonUtil.getJSONObjectByFilePath(parameterFilePath);
		if (jsonObj != null) {
			JSONObject server = jsonObj.getJSONObject(SERVER_KEY);
			if (server != null && server.has(MIN_COUNT_KEY) && server.getInt(MIN_COUNT_KEY) > 1 && !server.has(RESERVATION_ID_KEY)) {
				server.put("return_reservation_id", "True");
			}
		}
		return String.valueOf(jsonObj);

	}

}

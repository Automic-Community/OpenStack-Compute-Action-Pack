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
	private static final String MAX_COUNT_KEY = "max_count";
	private static final String MIN_COUNT_KEY = "min_count";
	private static final String RESERVATION_ID_KEY = "return_reservation_id";
	private static final String RESERVATION_ID_VAL = "True";

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
		if (!Validator.isAuthDetailsJSONValid(tokenId)) {
			LOGGER.error(ExceptionConstants.INVALID_AUTHENTICATION_TOKEN);
			throw new AutomicException(ExceptionConstants.INVALID_AUTHENTICATION_TOKEN);
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

		WebResource webResource = client.resource(baseUrl).path(tenantId)
				.path("servers");

		LOGGER.info("Calling url " + webResource.getURI());

		response = webResource
				.entity(validateJsonObject(parameterFilePath),
						MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header(Constants.X_AUTH_TOKEN, tokenId)
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
				LOGGER.error(" Unable to find Json key[servers] in json object :"
						+ jsonObj);
				throw new AutomicException(ExceptionConstants.INVALID_RESPONSE);
			}

		} else {
			LOGGER.error(" Unable to find Json keys[server, reservation_id] in json object :"
					+ jsonObj);
			throw new AutomicException(	ExceptionConstants.INVALID_RESPONSE);
		}

	}

	/**
	 * This method iterate provided {@code JSONArray} and print server id
	 * 
	 * @param servers
	 */
	private void publishSrversId(JSONArray servers) {
		if (servers != null && servers.length() > 0) {
			ConsoleWriter.writeln(servers.length()+" Server(s) has been created.");
			for (int i = 0; i < servers.length(); i++) {
				JSONObject server = servers.getJSONObject(i);
				ConsoleWriter.writeln("UC4RB_OPS_SERVER_IDS ::="
						+ server.getString(SERVER_ID));
			}
		}
	}

	/**
	 * This method validate and modify {@code JSONObject} instance integrity
	 * Should be Valid json If min_count or max_count > 1, then put
	 * reservation_id max_count should be greater than or equal to min_count
	 * 
	 * @param parameterFilePath
	 * @return {@code String} representation of {@code JSONObject} instance
	 * @throws AutomicException
	 */
	private String validateJsonObject(String parameterFilePath)
			throws AutomicException {
		JSONObject jsonObj = CommonUtil
				.getJSONObjectByFilePath(parameterFilePath);
		if (jsonObj != null) {
			JSONObject server = jsonObj.getJSONObject(SERVER_KEY);
			if (server != null) {
				// If min_count not specify, put min_count as 1
				int minCount = server.has(MIN_COUNT_KEY) ? server
						.getInt(MIN_COUNT_KEY) : 1;
				// If max_count not specify, put max_count as min_count
				int maxCount = server.has(MAX_COUNT_KEY) ? server
						.getInt(MAX_COUNT_KEY) : minCount;

				if (minCount > maxCount) {
					LOGGER.error(ExceptionConstants.MAXCOUNT_LESS_MINCOUNT);
					throw new AutomicException(
							ExceptionConstants.MAXCOUNT_LESS_MINCOUNT);
				}
				if(maxCount > 30){
					  String errMsg = String.format(ExceptionConstants.MAX_INSTANCE_COUNT, maxCount);
					LOGGER.error(errMsg);
					throw new AutomicException(errMsg);
					
				}

				if (minCount > 1 || maxCount > 1) {
					server.put(RESERVATION_ID_KEY, RESERVATION_ID_VAL);
				}
			}
		}
		return String.valueOf(jsonObj);

	}

}

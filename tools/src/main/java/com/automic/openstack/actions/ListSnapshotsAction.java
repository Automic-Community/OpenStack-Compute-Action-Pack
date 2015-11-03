package com.automic.openstack.actions;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ListSnapshotsAction extends AbstractHttpAction{

	private static final Logger LOGGER = LogManager
			.getLogger(ListServersAction.class);
	
	private static final String QUERY_DELIMETER = ",";
	private static final String VAL_DELIMETER = "=";
	
	private String filePath;
	private String tokenId;
	private String tenantId;
	private String queryParam;
	
	public ListSnapshotsAction() {
		addOption("computeurl", true, "Compute service endpoint");
		addOption("tokenid", true, "Token Id for authentication");
		addOption("tenantid", true, "Tenant/Project name");
		addOption("filepath", true, "Xml file path");
		addOption("queryparam", false, "Query parameter in comma separated format");
	}
	@Override
	protected void initialize() {
		baseUrl = getOptionValue("computeurl");
		tokenId = getOptionValue("tokenid");
		tenantId = getOptionValue("tenantid");
		filePath = getOptionValue("filepath");
		queryParam = getOptionValue("queryparam");
		
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
		if (!Validator.checkFileDirectoryExists(filePath)) {
			String errMsg = String.format(ExceptionConstants.INVALID_DIRECTORY,
					filePath);
			LOGGER.error(errMsg);
			throw new AutomicException(errMsg);
		}
		
	}

	@Override
	protected void executeSpecific() throws AutomicException {
		ClientResponse response = null;
		
		WebResource webResource = client.resource(baseUrl).path(tenantId).path("images");
		
		LOGGER.info("Calling url " + webResource.getURI());
		
		if(Validator.checkNotEmpty(queryParam)) {
			 Map<String, String> paramMap = prepareQueryParamsMap(queryParam);
	            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
	                webResource = webResource.queryParam(entry.getKey(), entry.getValue());
	            }
		}
		
		response = webResource.accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .get(ClientResponse.class);

        prepareOutput(response);
		
	}
	
    private Map<String, String> prepareQueryParamsMap(String queryArgs) {
        Map<String, String> paramMap = new HashMap<String, String>();
        String[] splitArgs = queryArgs.split(QUERY_DELIMETER);
        if (splitArgs != null && splitArgs.length > 0) {
            for (String str : splitArgs) {
                String[] queryParam = str.split(VAL_DELIMETER);
                if (queryParam != null && queryParam.length == 2) {
                    String key = queryParam[0].trim();
                    key = key.toLowerCase();
                    String value = queryParam[1].trim();
                    if (Validator.checkNotEmpty(key) && Validator.checkNotEmpty(value)) {
                        switch (key) {
                            case "changes-since":
                                paramMap.put(key, value);
                                break;
                            case "server":
                            	paramMap.put(key, value);
                                break;
                            case "name":
                                paramMap.put(key, value);
                                break;
                            case "status":
                            	paramMap.put(key, value);
                                break;
                            case "type":
                            	paramMap.put(key, value);
                                break;
                            case "limit":
                            	paramMap.put(key, value);
                                break;
                            case "marker":
                            	paramMap.put(key, value);
                                break;
                            default:
                        }
                    }
                }
            }
        }
        return paramMap;
    }
	
	private void prepareOutput(ClientResponse response) throws AutomicException {

		CommonUtil.jsonResponse2xml(response.getEntityInputStream(), filePath, "ListSnapshots");
		ConsoleWriter.writeln("UC4RB_OPS_LIST_SNAPSHOTS_XML ::=" + filePath);

    }

}


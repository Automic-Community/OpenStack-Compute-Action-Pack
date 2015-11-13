package com.automic.openstack.actions;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 * 
 * This class use to Allocate/Deallocate floating IP to VM
 * @author anuragupadhyay
 *
 */

public class AllocateDeallocateFloatingIPAction extends AbstractHttpAction{

    private static final Logger LOGGER = LogManager.getLogger(AllocateDeallocateFloatingIPAction.class);

    private String tokenId;
    private String tenantId;
    private String serverId;
    private String action;
    private String floatingIP;
    private String fixedIP;

    public AllocateDeallocateFloatingIPAction() {

        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project id");
        addOption("serverid", true, "Server id");
        addOption("action", true, "Action to execute on the server");
        addOption("floatingip", true, "Specify Floating IP for the server");
        addOption("fixedip", true, "Specify Fixed IP for the server");

    }

    @Override
    protected void initialize() {

        baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        serverId = getOptionValue("serverid");
        action = getOptionValue("action");
        floatingIP = getOptionValue("floatingip");
        fixedIP = getOptionValue("fixedip");
    }

    @Override
    protected void validate() throws AutomicException {
        if (!Validator.checkNotEmpty(baseUrl)) {
            LOGGER.error(ExceptionConstants.EMPTY_SERVICE_ENDPOINT);
            throw new AutomicException(ExceptionConstants.EMPTY_SERVICE_ENDPOINT);
        }
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
        if (!Validator.checkNotEmpty(serverId)) {
            LOGGER.error(ExceptionConstants.EMPTY_SERVERID);
            throw new AutomicException(ExceptionConstants.EMPTY_SERVERID);
        }
        if (!Validator.checkNotEmpty(action)) {
            LOGGER.error(ExceptionConstants.EMPTY_SERVER_ACTION);
            throw new AutomicException(ExceptionConstants.EMPTY_SERVER_ACTION);
        }
        if (!Validator.checkNotEmpty(floatingIP)) {
            LOGGER.error(ExceptionConstants.EMPTY_FLOATING_IP);
            throw new AutomicException(ExceptionConstants.EMPTY_FLOATING_IP);
        }
        if (!Validator.checkNotEmpty(fixedIP)) {        	
            LOGGER.error(ExceptionConstants.EMPTY_FIXED_IP);
            throw new AutomicException(ExceptionConstants.EMPTY_FIXED_IP);
        }

    }

    /**
     * This method makes a REST call to the API /v2/​{tenant_id}​/servers/​{server_id}​/action .Successful completion
     * of the action will return 202 as response code . The action also fails if provided server_id/tenant_id is invalid
     */

    @Override
    protected void executeSpecific() throws AutomicException {

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path(serverId).path("action");

        LOGGER.info("Calling url " + webResource.getURI());

        webResource.entity(getServerActionJson(), MediaType.APPLICATION_JSON)
                .header(Constants.X_AUTH_TOKEN, tokenId).post(ClientResponse.class);

    }

    private String getServerActionJson() throws AutomicException {

        JSONObject actionJson = new JSONObject();

        switch (action) {           
            case "ALLOCATE_IP":            	
                 actionJson.put("addFloatingIp", getIPDetailsJsonObj());
                break;
            case "DEALLOCATE_IP":
                actionJson.put("removeFloatingIp",  getIPDetailsJsonObj());
                break;
            default:
                LOGGER.error("invalid action");
                throw new AutomicException("Invalid  action [" + action + "]");
        }

        return actionJson.toString();
    }
    
    private JSONObject getIPDetailsJsonObj(){    	
    	 JSONObject childJson = new JSONObject();
    	 childJson.put("address", floatingIP);
    	 childJson.put("fixed_address", fixedIP);
    	 return childJson;
    }

}

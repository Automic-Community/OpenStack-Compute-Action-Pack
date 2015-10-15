/**
 * 
 */
package com.automic.openstack.actions;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author sumitsamson
 * 
 */
/**
 * This class is used to change the server state.The possible states are
 * start,stop,pause,un-pause,suspend,resume,lock,unlock,soft-reboot,hard-reboot
 */
public class ChangeServerState extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(GetServerDetailsAction.class);

    private String tokenId;
    private String tenantId;
    private String serverId;
    private String action;

    public ChangeServerState() {

        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project id");
        addOption("serverid", true, "Server id");
        addOption("serveraction", true, "Action to change server state");

    }

    @Override
    protected void initialize() {

        baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        serverId = getOptionValue("serverid");
        action = getOptionValue("serveraction").toLowerCase();
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

    }

    /**
     * This method makes a REST call to the API /v2.1/​{tenant_id}​/servers/​{server_id}​/action .Successful completion
     * of the action will return 202 as response code . The action also fails if provided server id is invalid
     */

    @Override
    protected void executeSpecific() throws AutomicException {

        tokenId = AESEncryptDecrypt.decrypt(tokenId);

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path(serverId).path("action");

        LOGGER.info("Calling url " + webResource.getURI());

        webResource.entity(getActionJson(), MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .post(ClientResponse.class);

    }

    private String getActionJson() throws AutomicException {

        JSONObject actionJson = new JSONObject();

        switch (action) {
            case "start":
                actionJson.put("os-start", "null");
                break;
            case "stop":
                actionJson.put("os-stop", "null");
                break;
            case "pause":
                actionJson.put("pause", "null");
                break;
            case "unpause":
                actionJson.put("unpause", "null");
                break;
            case "suspend":
                actionJson.put("suspend", "null");
                break;
            case "resume":
                actionJson.put("resume", "null");
                break;
            case "lock":
                actionJson.put("lock", "null");
                break;
            case "unlock":
                actionJson.put("unlock", "null");
                break;
            case "reboot_soft":
                actionJson.put("reboot", new JSONObject().put("type", "SOFT"));
                break;
            case "reboot_hard":
                actionJson.put("reboot", new JSONObject().put("type", "HARD"));
                break;
            default:
                LOGGER.error("invalid Server action");
                throw new AutomicException("Invalid server action [" + action + "]");
        }

        return actionJson.toString();
    }

}

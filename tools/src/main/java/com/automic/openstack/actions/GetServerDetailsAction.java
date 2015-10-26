package com.automic.openstack.actions;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Kamal Garg
 * 
 */
/**
 * This class is used to retrieve server details as per the specified server id. Retrieved server information is written
 * in the xml file at the path mentioned in the filePath
 * 
 */
public class GetServerDetailsAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(GetServerDetailsAction.class);

    private String tokenId;
    private String tenantId;
    private String serverId;
    private String filePath;

    public GetServerDetailsAction() {

        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project id");
        addOption("serverid", true, "Server id");
        addOption("filepath", true, "Xml file path ");

    }

    @Override
    protected void initialize() {
        baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        serverId = getOptionValue("serverid");
        filePath = getOptionValue("filepath");

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
        if (!Validator.checkFileDirectoryExists(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, filePath);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     *  Retrieves server details for the specified server id by 
     *  calling http://baseUrl/servers/{serverId}
     * */
    protected void executeSpecific() throws AutomicException {

        ClientResponse response = null;

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path(serverId);

        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .get(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * This method prepare the output xml by converting the json response into xml which is then written to the file at
     * the path provided
     */

    private void prepareOutput(ClientResponse response) throws AutomicException {

        CommonUtil.jsonResponse2xml(response.getEntityInputStream(), filePath);

    }

}

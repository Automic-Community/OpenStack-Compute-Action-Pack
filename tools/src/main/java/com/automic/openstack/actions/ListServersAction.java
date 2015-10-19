package com.automic.openstack.actions;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Anurag Upadhyay
 * 
 */
/**
 * This class is used to get all the servers with corresponding server details. It also includes server usage
 * information. It generates all the information in the XML format and store the xml file at the specified path.
 * 
 */
public class ListServersAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(ListServersAction.class);

    private String filePath;
    private String tokenId;
    private String tenantId;

    public ListServersAction() {

        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project name");
        addOption("filepath", true, "Xml file path ");

    }

    @Override
    protected void initialize() {
        baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        filePath = getOptionValue("filepath");

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
        if (!Validator.checkFileDirectoryExists(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_DIRECTORY, filePath);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     * Authenticates and generates a token by calling http://baseUrl/servers/detail
     * */
    protected void executeSpecific() throws AutomicException {

        ClientResponse response = null;

        tokenId = AESEncryptDecrypt.decrypt(tokenId);

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path("detail");

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

        CommonUtil.jsonResponse2xml(response.getEntityInputStream(), filePath, "ListServers");
        ConsoleWriter.writeln("UC4RB_OPS_LIST_SERVERS_XML ::=" + filePath);

    }

}

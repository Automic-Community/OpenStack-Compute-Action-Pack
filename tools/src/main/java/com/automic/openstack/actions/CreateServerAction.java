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

public class CreateServerAction extends AbstractHttpAction{

    private static final Logger LOGGER = LogManager.getLogger(CreateServerAction.class);

    private String filePath;
    protected String tokenId;
    protected String tenantId;

    public CreateServerAction() {

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
        if (!Validator.checkFileExists(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, filePath);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     * Authenticates and generates a token by calling http://baseUrl/v2.1/​{tenant_id}​/servers
     * */
    protected void executeSpecific() throws AutomicException {

        ClientResponse response = null;

        tokenId = AESEncryptDecrypt.decrypt(tokenId);

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers");

        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .post(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * This method prepare the output xml by converting the json response into xml which is then written to the file at
     * the path provided
     */

    private void prepareOutput(ClientResponse response) throws AutomicException {
    	
    	// JSONObject jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());

    	// System.out.println(jsonObj);
        CommonUtil.jsonResponse2xml(response.getEntityInputStream(), filePath, "CreateServer");
        ConsoleWriter.writeln("UC4RB_OPS_CREATE_SERVERS_XML ::=" + filePath);

    }

}

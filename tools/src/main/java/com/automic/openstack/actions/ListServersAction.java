package com.automic.openstack.actions;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

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
 * This class is used lists all servers and shows server details. Includes server usage information.
 * above information is written in the xml file at the path mentioned in the filePath
 * 
 */
public class ListServersAction extends AbstractAction{
	


    private static final Logger LOGGER = LogManager.getLogger(ListServersAction.class);

    private String filePath;
    
    public ListServersAction() {

        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");       
        addOption("tenantid", false, "Tenant/Project name");
        addOption("filepath", false, "Xml file path ");

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
        if (!Validator.checkNotEmpty(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, filePath);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     * Authenticates and generates a token by calling http://baseUrl/servers/detail
     * */
    protected void execute() throws AutomicException {

        ClientResponse response = null;
        try {
        	tokenId = AESEncryptDecrypt.decrypt(tokenId);
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			
			
			throw new AutomicException(e.getMessage());
		} 
        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path("detail");

        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.accept(MediaType.APPLICATION_JSON)
        		.header(Constants.X_AUTH_TOKEN, tokenId)
                .get(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * This method prepare the output xml by converting the json response into xml which is then written to the file at
     * the path provided
     */

    private void prepareOutput(ClientResponse response) throws AutomicException {

        JSONObject jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());
        CommonUtil.json2xml(jsonObj, filePath);      
       
        ConsoleWriter.writeln("UC4RB_OPS_LIST_SERVERS_XML ::=" + filePath);

    }

	@Override
	  /**
     * Preventing password to be logged
     * */
	protected List<String> noLogging() {
		return Collections.emptyList();
	}
 
}

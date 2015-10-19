package com.automic.openstack.actions;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Kamal Garg
 * 
 */
/**
 * This class is used to create server snapshot as per the specified server id. Created server snapshot image location
 * will be published.
 * 
 */
public class CreateServerSnapshotAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(CreateServerSnapshotAction.class);

    private String tokenId;
    private String tenantId;
    private String serverId;
    private String parameterFile;

    public CreateServerSnapshotAction() {

        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project id");
        addOption("serverid", true, "Server id");
        addOption("parameterfile", true, "Param file to create snapshot");

    }

    @Override
    protected void initialize() {
        baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        serverId = getOptionValue("serverid");
        parameterFile = getOptionValue("parameterfile");

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
        if (!Validator.checkFileExists(parameterFile)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, parameterFile);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     * Create server snapshot for specified server id by calling http://baseUrl/servers/{serverId}/action
     * */
    protected void executeSpecific() throws AutomicException {

        ClientResponse response = null;

        tokenId = AESEncryptDecrypt.decrypt(tokenId);

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path(serverId).path("action");

        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.entity(new File(parameterFile), MediaType.APPLICATION_JSON)
                .header(Constants.X_AUTH_TOKEN, tokenId).get(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * This method prints the server snapshot image url to AE job report
     */

    private void prepareOutput(ClientResponse response) throws AutomicException {

        List<String> tokenid = response.getHeaders().get("Location");
        ConsoleWriter.writeln("UC4RB_OPS_IMAGE_LOCATION ::= " + tokenid.get(0));

    }

}

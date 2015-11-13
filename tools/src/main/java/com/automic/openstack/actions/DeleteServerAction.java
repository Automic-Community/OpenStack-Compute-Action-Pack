/**
 * 
 */
package com.automic.openstack.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Action class to delete a server instance on OpenStack
 *
 */
public class DeleteServerAction extends AbstractHttpAction {

	private static final Logger LOGGER = LogManager.getLogger(DeleteServerAction.class);

    private String tokenId;
    private String tenantId;
    private String serverId;
    
	/**
	 * 
	 */
	public DeleteServerAction() {
		addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project id");
        addOption("serverid", true, "Server id");
	}

	/* (non-Javadoc)
	 * @see com.automic.openstack.actions.AbstractHttpAction#initialize()
	 */
	@Override
	protected void initialize() {
		// 
		baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        serverId = getOptionValue("serverid"); 

	}

	/* (non-Javadoc)
	 * @see com.automic.openstack.actions.AbstractHttpAction#validate()
	 */
	@Override
	protected void validate() throws AutomicException {
		// 
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
	}

	/* (non-Javadoc)
	 * @see com.automic.openstack.actions.AbstractHttpAction#executeSpecific()
	 */
	@Override
	protected void executeSpecific() throws AutomicException {
		// 
		WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path(serverId).path("action");

        LOGGER.info("Calling url " + webResource.getURI());
        webResource.header(Constants.X_AUTH_TOKEN, tokenId).delete(ClientResponse.class);

	}

	
}

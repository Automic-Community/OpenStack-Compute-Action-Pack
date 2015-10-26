/**
 * 
 */
package com.automic.openstack.actions;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.model.AuthenticationToken;
import com.automic.openstack.service.AuthenticationTokenSevice;
import com.automic.openstack.service.ListServerService;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author sumitsamson
 * 
 */
/**
 * This class is used to do authentication and generate the authorization token which will send as request header in all
 * the other Openstack service requests.Along with the token it provides the service end points which along with the
 * above information is written in the xml file at the path mentioned in the filePath
 */
public class GetTokenAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(GetTokenAction.class);

    private String username;
    private String password;
    private String tenantName;

    public GetTokenAction() {

        addOption("baseurl", true, "Identity service endpoint");
        addOption("username", true, "Username for openstack");
        addOption("password", true, "password for openstack");
        addOption("tenantname", false, "Tenant/Project name");

    }

    @Override
    protected void initialize() {
        baseUrl = getOptionValue("baseurl");
        username = getOptionValue("username");
        password = getOptionValue("password");
        tenantName = getOptionValue("tenantname");

    }

    @Override
    protected void validate() throws AutomicException {
        if (!Validator.checkNotEmpty(this.baseUrl)) {
            LOGGER.error(ExceptionConstants.EMPTY_SERVICE_ENDPOINT);
            throw new AutomicException(ExceptionConstants.EMPTY_SERVICE_ENDPOINT);
        }
        if (!Validator.checkNotEmpty(this.username)) {
            LOGGER.error(ExceptionConstants.EMPTY_USERNAME);
            throw new AutomicException(ExceptionConstants.EMPTY_USERNAME);
        }
        if (!Validator.checkNotEmpty(this.password)) {
            LOGGER.error(ExceptionConstants.EMPTY_PASSWORD);
            throw new AutomicException(ExceptionConstants.EMPTY_PASSWORD);
        }

    }

    @Override
    /**
     * Authenticates and generates a token by calling http://baseUrl/tokens
     * */
    protected void executeSpecific() throws AutomicException {
    	
    	AuthenticationTokenSevice ats = AuthenticationTokenSevice.getListServerService(client);
		prepareOutput(ats.executeAuthenticationTokenSevice(baseUrl, username, password, tenantName));

     
    }

    /**
     * This method prepare the output xml by converting the json response into xml which is then written to the file at
     * the path provided
     */

    private void prepareOutput(JSONObject jsonObj) throws AutomicException {      

        JSONObject tokenJson = jsonObj.getJSONObject("access").getJSONObject("token");

        if (tokenJson.has("tenant")) {
            JSONObject tenantJson = tokenJson.getJSONObject("tenant");
            ConsoleWriter.writeln("UC4RB_OPS_TENANT_ID ::=" + tenantJson.getString("id"));
        }
       
        AuthenticationToken authToken=   new AuthenticationToken(baseUrl, username, password, tenantName, tokenJson.getString("id"),
        		tokenJson.getString("expires"), tokenJson.getString("issued_at"));
        
        ConsoleWriter.writeln("UC4RB_OPS_TOKEN_ID ::=" + CommonUtil.encrypt(authToken.toString()));
    }

}

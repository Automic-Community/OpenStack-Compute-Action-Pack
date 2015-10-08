/**
 * 
 */
package com.automic.openstack.actions;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
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
public class GetTokenAction extends AbstractAction {

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
    /**
     * Preventing password to be logged
     * */
    protected List<String> noLogging() {

        return Arrays.asList(Constants.PASSWORD);
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
    protected void execute() throws AutomicException {

        ClientResponse response = null;

        WebResource webResource = client.resource(baseUrl).path("tokens");

        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.accept(MediaType.APPLICATION_JSON)
                .entity(getAuthenticationJson(username, password, tenantName).toString(), MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * This method prepare the output xml by converting the json response into xml which is then written to the file at
     * the path provided
     */

    private void prepareOutput(ClientResponse response) throws AutomicException {

        JSONObject jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());

        JSONObject tokenJson = jsonObj.getJSONObject("access").getJSONObject("token");

        LOGGER.info("Token Json " + tokenJson.toString());

        if (tokenJson.has("tenant")) {
            JSONObject tenantJson = tokenJson.getJSONObject("tenant");
            ConsoleWriter.writeln("UC4RB_OPS_TENANT_ID ::=" + tenantJson.get("id").toString());
        }

        ConsoleWriter.writeln("UC4RB_OPS_TOKEN_ID ::=" + CommonUtil.encrypt(tokenJson.get("id").toString()));
        ConsoleWriter.writeln("UC4RB_OPS_TOKEN_EXPIRY  ::=" + tokenJson.get("expires").toString());

    }

    private JSONObject getAuthenticationJson(String username, String password, String tenantName) {

        JSONObject passwordCreds = new JSONObject();
        passwordCreds.put("username", username);
        passwordCreds.put("password", password);

        JSONObject auth = new JSONObject();

        auth.put("passwordCredentials", passwordCreds);
        if (tenantName != null && !tenantName.isEmpty()) {
            auth.put("tenantName", tenantName);
        }

        JSONObject json = new JSONObject();
        json.put("auth", auth);

        return json;

    }

}

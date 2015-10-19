package com.automic.openstack.actions;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.service.ResponseProcessService;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CreateServerAction extends AbstractHttpAction{

    private static final Logger LOGGER = LogManager.getLogger(CreateServerAction.class);

    private String paramterFilePath;
    private String tokenId;
    private String tenantId;
    private final String SERVER_ID="id";
    private final String RESERVATION_ID ="reservation_id";
    private final String SERVER_KEY="server";
    private final String SERVERS_KEY="servers";

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
        paramterFilePath = getOptionValue("filepath");

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
        if (!Validator.checkFileExists(paramterFilePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_FILE, paramterFilePath);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     * Authenticates and Create Servers by  http://baseUrl/v2/​{tenant_id}​/servers
     * */
    protected void executeSpecific() throws AutomicException {

        ClientResponse response = null;

        tokenId = AESEncryptDecrypt.decrypt(tokenId);

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers");

        LOGGER.info("Calling url " + webResource.getURI());
      

        response = webResource.entity(new File(paramterFilePath), MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .post(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * This method prepare the output xml by converting the json response into xml which is then written to the file at
     * the path provided
     */

    private void prepareOutput(ClientResponse response) throws AutomicException {    	
    	 JSONObject jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());
    	 
    	 if(jsonObj != null && jsonObj.has(SERVER_KEY)){
    		 JSONObject  server = jsonObj.getJSONObject(SERVER_KEY);
    		 ConsoleWriter.writeln("UC4RB_OPS_SERVER_IDS ::=" + server.getString(SERVER_ID));
    		 
    	 }else if(jsonObj != null && jsonObj.has(RESERVATION_ID)){    		 
    		 ResponseProcessService listServerFilter =  ResponseProcessService.getResponseProcessService(client);
    		 jsonObj = listServerFilter.executeListServerFilter(baseUrl, tenantId, tokenId, jsonObj.getString(RESERVATION_ID));		
    		 
    		 if(jsonObj != null && jsonObj.has(SERVERS_KEY)){    			 
    		 JSONArray servers = jsonObj.getJSONArray(SERVERS_KEY);
    		 
    		 if(servers!=null && servers.length()>0){
                 for (int i = 0; i < servers.length(); i++) {
                	 JSONObject  server = servers.getJSONObject(i);
                	 ConsoleWriter.writeln("UC4RB_OPS_SERVER_IDS["+i+"] ::=" + server.getString(SERVER_ID));
                 }
             }
    	 }else{
    		 LOGGER.info(" Unable to find Json key[servers] in json object :" + jsonObj); 
    	 }
    		 
    	 }else{
    		 LOGGER.info(" Unable to find Json keys[server, reservation_id] in json object :" + jsonObj);    	      
    	 }

    }

}

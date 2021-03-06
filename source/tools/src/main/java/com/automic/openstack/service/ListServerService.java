package com.automic.openstack.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.exception.AutomicRuntimeException;
import com.automic.openstack.util.CommonUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is used to execute List Service request.
 * 
 * @author Anurag Upadhyay
 */
public class ListServerService {
    private static final Logger LOGGER = LogManager.getLogger(ListServerService.class);

    private final Client client;

    /**
     * Initializes a newly created {@code ListServerService} object.
     * 
     * @param client
     */
    private ListServerService(Client client) {

        this.client = client;
    }

    /**
     * This method create the instance of {@code ListServerService} with provided client.
     * 
     * @param client
     * @return Initialize instance of {@code ListServerService}
     */
    public static ListServerService getListServerService(Client client) {

        return new ListServerService(client);

    }

    /**
     * Get server details using reservation id by calling http://baseUrl/{tenant_id}​/servers/detail.
     * 
     * @param baseUrl
     * @param tenantId
     * @param tokenId
     * @param queryParamMap
     * @return {@code JSONObject} object
     * @throws AutomicException
     */
    public JSONObject executeListServerService(String baseUrl, String tenantId, String tokenId,
            MultivaluedMap<String, String> queryParamMap) throws AutomicException {

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path("detail")
                .queryParams(queryParamMap);

        return executeRequest(webResource, tokenId);

    }

    /**
     * Get server details by calling http://baseUrl/{tenant_id}​/servers/detail.
     * 
     * @param baseUrl
     * @param tenantId
     * @param tokenId
     * @return {@code JSONObject} object
     * @throws AutomicException
     */
    public JSONObject executeListServerService(String baseUrl, String tenantId, String tokenId) throws AutomicException {

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("servers").path("detail");

        return executeRequest(webResource, tokenId);

    }

    /**
     * This method execute request by provided {@code WebResource} object
     * 
     * @param webResource
     * @param tokenId
     * @return
     * @throws AutomicException
     * @throws AutomicRuntimeException
     */
    private JSONObject executeRequest(WebResource webResource, String tokenId) throws AutomicRuntimeException,
            AutomicException {

        ClientResponse response = null;
        JSONObject jsonObj = null;
        LOGGER.info("List servers Calling url " + webResource.getURI());
        response = webResource.accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .get(ClientResponse.class);
        jsonObj = CommonUtil.jsonResponse(response.getEntityInputStream());

        return jsonObj;

    }

}

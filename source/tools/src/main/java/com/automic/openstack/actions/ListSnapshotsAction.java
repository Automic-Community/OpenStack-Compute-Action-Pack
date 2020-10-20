package com.automic.openstack.actions;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.automic.openstack.util.Validator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author Shruti Nambiar
 * 
 *         This class is used to list all the images and its details that are present. The result for the same is stored
 *         in an XML file at the specified location.
 * 
 */

public class ListSnapshotsAction extends AbstractHttpAction {

    private static final Logger LOGGER = LogManager.getLogger(ListSnapshotsAction.class);

    private String filePath;
    private String tokenId;
    private String tenantId;
    private String queryParam;

    public ListSnapshotsAction() {
        addOption("computeurl", true, "Compute service endpoint");
        addOption("tokenid", true, "Token Id for authentication");
        addOption("tenantid", true, "Tenant/Project name");
        addOption("filepath", true, "Xml file path");
        addOption("queryparam", false, "Query parameter in comma separated format");
    }

    @Override
    protected void initialize() {
        baseUrl = getOptionValue("computeurl");
        tokenId = getOptionValue("tokenid");
        tenantId = getOptionValue("tenantid");
        filePath = getOptionValue("filepath");
        queryParam = getOptionValue("queryparam");

    }

    @Override
    protected void validate() throws AutomicException {
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
        if (!Validator.checkFileDirectoryExists(filePath)) {
            String errMsg = String.format(ExceptionConstants.INVALID_DIRECTORY, filePath);
            LOGGER.error(errMsg);
            throw new AutomicException(errMsg);
        }

    }

    @Override
    /**
     * Retrieves snapshot details by calling http://baseUrl/{tenantId}/images
     */
    protected void executeSpecific() throws AutomicException {
        ClientResponse response = null;

        WebResource webResource = client.resource(baseUrl).path(tenantId).path("images");

        if (Validator.checkNotEmpty(queryParam)) {
            webResource = webResource.queryParams(prepareValidQueryParamsMap(queryParam));
        }

        LOGGER.info("Calling url " + webResource.getURI());

        response = webResource.accept(MediaType.APPLICATION_JSON).header(Constants.X_AUTH_TOKEN, tokenId)
                .get(ClientResponse.class);

        prepareOutput(response);

    }

    /**
     * Method to prepare a multi-valued map of valid query parameters. It checks for the valid query param using switch
     * statement
     * 
     * @param queryParamMap
     *            Query Parameters as a key value in a map
     * @return params (A multi valued map)
     */

    private MultivaluedMap<String, String> prepareValidQueryParamsMap(final String queryArgs) {

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        for (Map.Entry<String, String> entry : CommonUtil.prepareQueryParamsMap(queryArgs).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key.toLowerCase()) {
                case "changes-since":
                case "server":
                case "name":
                case "status":
                case "type":
                case "limit":
                case "marker":
                    params.put(key, Arrays.asList(value));
                    break;
                default:
                    LOGGER.info("Invalid query parameter : " + key + "=" + value);
            }
        }
        return params;
    }

    /**
     * Method to prepare output based on Response of an HTTP request to client.
     * 
     * @param response
     *            instance of {@link ClientResponse}
     * @throws AutomicException
     */
    private void prepareOutput(final ClientResponse response) throws AutomicException {

        CommonUtil.jsonResponse2xml(response.getEntityInputStream(), filePath, "ListSnapshots");
        ConsoleWriter.writeln("UC4RB_OPS_SNAPSHOTS_XML_PATH ::=" + filePath);

    }

}

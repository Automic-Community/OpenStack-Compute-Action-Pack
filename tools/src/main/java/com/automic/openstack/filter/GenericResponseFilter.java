package com.automic.openstack.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.exception.AutomicRuntimeException;
import com.automic.openstack.filter.ErrorResponse;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the response to validate it.
 *
 */

public class GenericResponseFilter extends ClientFilter {

    private static final int HTTP_SUCCESS_START = 200;
    private static final int HTTP_SUCCESS_END = 299;

    private static final Logger LOGGER = LogManager.getLogger(GenericResponseFilter.class);

    private Class<? extends ErrorResponse> errorHandler;

    public GenericResponseFilter(Class<? extends ErrorResponse> errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public ClientResponse handle(ClientRequest arg0) {
        ClientResponse response = getNext().handle(arg0);
        if (!(response.getStatus() >= HTTP_SUCCESS_START && response.getStatus() <= HTTP_SUCCESS_END)) {
            LOGGER.error("Response code for " + arg0.getURI() + " is " + response.getStatus());
            String errorMsg = response.getEntity(errorHandler).toString();
            LOGGER.error(errorMsg);
            throw new AutomicRuntimeException(errorMsg);
        }
        return response;
    }

}

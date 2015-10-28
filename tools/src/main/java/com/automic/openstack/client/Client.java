/**
 *
 */
package com.automic.openstack.client;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.exception.util.ExceptionHandler;
import com.automic.openstack.util.ConsoleWriter;

/**
 * Main Class is the insertion point of openstack interaction api when called from AE implementation. It delegates the
 * parameters to appropriate action and returns a response code based on output of action.
 *
 * Following response code are returned by java program 0 - Successful response from OpenStack API 1 - An exception
 * occurred/Error in response from OpenStack API 2 - Connection timeout while calling OpenStack API
 *
 */
public final class Client {

    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    private static final int RESPONSE_OK = 0;

    private Client() {
    }

    /**
     * Main method which will start the execution of an action on Open Stack. This method will call the ClientHelper
     * class which will trigger the execution of specific action and then if action fails this main method will handle
     * the failed scenario and print the error message and system will exit with the respective response code.
     *
     * @param args
     *            array of Arguments
     */
    public static void main(String[] args) {
    	    	
        int responseCode = RESPONSE_OK;
        try {
            ClientHelper.executeAction(args);
        } catch (Exception e) {
            responseCode = ExceptionHandler.handleException(e);
        } finally {
            ConsoleWriter.flush();
        }
        LOGGER.info("@@@@@@@ Execution ends for action  with response code : " + responseCode);
        System.exit(responseCode);
    }

}

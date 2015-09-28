/**
 *
 */
package com.automic.openstack.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.actions.AbstractAction;
import com.automic.openstack.actions.ActionFactory;
import com.automic.openstack.cli.Cli;
import com.automic.openstack.cli.CliOptions;
import com.automic.openstack.constants.Action;
import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;

/**
 * Helper class to delegate request to specific Action based on input arguments .
 * */
public final class ClientHelper {

    private static final Logger LOGGER = LogManager.getLogger(ClientHelper.class);

    private ClientHelper() {
    }

    /**
     * Method to delegate parameters to an instance of {@link AbstractAction} based on the value of Action parameter.
     *
     * @param map
     *            of options with key as option name and value is option value
     * @throws AutomicException
     */
    public static void executeAction(String[] args) throws AutomicException {
        String action = new Cli(new CliOptions(), args).getOptionValue(Constants.ACTION).toUpperCase();
        LOGGER.info("Execution starts for action [" + action + "]...");
        AbstractAction useraction = ActionFactory.getAction(Action.valueOf(action));
        useraction.executeAction(args);
    }
}

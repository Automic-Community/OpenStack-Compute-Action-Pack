/**
 *
 */
package com.automic.openstack.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.config.HttpClientConfig;
import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.filter.AuthenticationFilter;
import com.automic.openstack.filter.GenericResponseFilter;
import com.automic.openstack.util.CommonUtil;
import com.sun.jersey.api.client.Client;

/**
 * An abstract action which parses the command line parameters using apache cli and further calls the execute method.
 * The implementation of execute method will be provided by the subclass of this class. This class also provides the
 * method to retrieve the arguments which can be used inside execute method.
 */
public abstract class AbstractHttpAction extends AbstractAction {

    private static final Logger LOGGER = LogManager.getLogger(AbstractHttpAction.class);

    /**
     * Service end point
     */
    protected String baseUrl;
    protected Client client;
    
    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeOut;

    /**
     * Read timeout in milliseconds
     */
    private int readTimeOut;
    
    private String currentAEDate;
    private int timeoutCriteria;

    public AbstractHttpAction() {
    	addOption(Constants.READ_TIMEOUT, true, "Read timeout");
        addOption(Constants.CONNECTION_TIMEOUT, true, "connection timeout");
        addOption(Constants.CURRENT_AE_DATE, true, "Current Automation engine date");
        addOption(Constants.TIMEOUT_CRITERIA, true, "Specify timeout criteria");
    }

    /**
     * This method initializes the arguments and calls the execute method.
     * 
     * @throws AutomicException
     *             exception while executing an action
     */
    public final void execute() throws AutomicException {
        try {
            initializeCommonInputs();
            initialize();
            validate();
            client = getClient();
            client.addFilter(new GenericResponseFilter());
            client.addFilter(new AuthenticationFilter(currentAEDate, client, timeoutCriteria));
            executeSpecific();
        } finally {
            if (client != null) {
                client.destroy();
            }
        }
    }

    private void initializeCommonInputs() {
        this.connectionTimeOut = CommonUtil.getAndCheckUnsignedValue(getOptionValue(Constants.CONNECTION_TIMEOUT));
        this.readTimeOut = CommonUtil.getAndCheckUnsignedValue(getOptionValue(Constants.READ_TIMEOUT));
        this.currentAEDate = getOptionValue(Constants.CURRENT_AE_DATE);
        this.timeoutCriteria =  Integer.valueOf(getOptionValue(Constants.TIMEOUT_CRITERIA));
    }

    private Client getClient() throws AutomicException {
        try {
            return HttpClientConfig.getClient(new URL(baseUrl).getProtocol(),this.connectionTimeOut, this.readTimeOut);
        } catch (MalformedURLException ex) {
            String msg = String.format(ExceptionConstants.INVALID_BASE_URL, baseUrl);
            LOGGER.error(msg, ex);
            throw new AutomicException(msg);
        }
    }

    protected abstract void initialize();

    protected abstract void validate() throws AutomicException;

    /**
     * Method to execute the action.
     * 
     * @throws AutomicException
     */
    protected abstract void executeSpecific() throws AutomicException;

}

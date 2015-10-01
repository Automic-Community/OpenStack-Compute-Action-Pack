/**
 *
 */
package com.automic.openstack.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.cli.Cli;
import com.automic.openstack.cli.CliOptions;
import com.automic.openstack.config.HttpClientConfig;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.sun.jersey.api.client.Client;

/**
 * An abstract action which parses the command line parameters using apache cli
 * and further calls the execute method. The implementation of execute method
 * will be provided by the subclass of this class. This class also provides the
 * method to retrieve the arguments which can be used inside execute method.
 */
public abstract class AbstractAction {

	private static final Logger LOGGER = LogManager
			.getLogger(AbstractAction.class);

	private CliOptions actionOptions;
	private Cli cli;

	/**
	 * Service endpoint
	 */
	protected String baseUrl;

	/**
	 * Connection timeout in milliseconds
	 */
	private int connectionTimeOut;

	/**
	 * Read timeout in milliseconds
	 */
	private int readTimeOut;

	protected Client client;

	protected String certFilePath;

	public AbstractAction() {
		actionOptions = new CliOptions();
	}

	/**
	 * This method is used to add the argument.
	 * 
	 * @param optionName
	 *            argument key used to identify the argument.
	 * @param isRequired
	 *            true/false. True means argument is mandatory otherwise it is
	 *            optional.
	 * @param description
	 *            represents argument description.
	 */
	public final void addOption(String optionName, boolean isRequired,
			String description) {
		actionOptions.addOption(optionName, isRequired, description);
	}

	/**
	 * This method is used to retrieve the value of specified argument.
	 * 
	 * @param arg
	 *            argument key for which you want to get the value.
	 * @return argument value for the specified argument key.
	 */
	public final String getOptionValue(String arg) {
		return cli.getOptionValue(arg);
	}

	/**
	 * This method initializes the arguments and calls the execute method.
	 * 
	 * @throws AutomicException
	 *             exception while executing an action
	 */
	public final void executeAction(String[] commandLineArgs)
			throws AutomicException {
		try {
			cli = new Cli(actionOptions, commandLineArgs);
			cli.log(noLogging());
			initialize();
			validate();
			client = getClient();
			execute();
		} finally {
			if (client != null) {
				client.destroy();
			}
		}
	}

	private Client getClient() throws AutomicException {
		try {
			return HttpClientConfig.getClient(new URL(baseUrl).getProtocol(),
					this.certFilePath, connectionTimeOut, readTimeOut);
		} catch (MalformedURLException ex) {
			String msg = String.format(ExceptionConstants.INVALID_BASE_URL,
					baseUrl);
			LOGGER.error(msg, ex);
			throw new AutomicException(msg);
		}
	}

	/**
	 * Method to retrieve the argument keys that we don't need to log.
	 */
	protected abstract List<String> noLogging();

	protected abstract void initialize();

	protected abstract void validate() throws AutomicException;

	/**
	 * Method to execute the action.
	 * 
	 * @throws AutomicException
	 */
	protected abstract void execute() throws AutomicException;

}

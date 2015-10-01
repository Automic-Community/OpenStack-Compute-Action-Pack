/**
 * 
 */
package com.automic.openstack.actions;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.Validator;

/**
 * @author sumitsamson
 * 
 */
public class GetTokenAction extends AbstractAction {

	private static final Logger LOGGER = LogManager
			.getLogger(GetTokenAction.class);

	private String username;
	private String password;
	private String tenantName;

	GetTokenAction() {

		addOption("baseurl", true, "Identity service endpoint");
		addOption("username", true, "Username for openstack");
		addOption("password", true, "password for openstack");
		addOption("tenantname", true, "Tenant/Project name");

	}

	@Override
	protected List<String> noLogging() {

		return Arrays.asList(new String[] { Constants.PASSWORD });
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
			throw new AutomicException(
					ExceptionConstants.EMPTY_SERVICE_ENDPOINT);
		}
		if (!Validator.checkNotEmpty(this.username)) {
			LOGGER.error(ExceptionConstants.EMPTY_USERNAME);
			throw new AutomicException(ExceptionConstants.EMPTY_USERNAME);
		}
		if (!Validator.checkNotEmpty(this.password)) {
			LOGGER.error(ExceptionConstants.EMPTY_PASSWORD);
			throw new AutomicException(ExceptionConstants.EMPTY_PASSWORD);
		}
		if (!Validator.checkNotEmpty(tenantName)) {
			LOGGER.error(ExceptionConstants.EMPTY_TENANT_NAME);
			throw new AutomicException(ExceptionConstants.EMPTY_TENANT_NAME);
		}
	}

	@Override
	protected void execute() throws AutomicException {
		initialize();
		validate();

	}

}

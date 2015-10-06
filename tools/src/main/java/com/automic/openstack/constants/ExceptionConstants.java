package com.automic.openstack.constants;




/**
 * Constant class containing messages to describe Exception Scenarios.
 *
 */
public final class ExceptionConstants {

    // General Errors
    public static final String GENERIC_ERROR_MSG = "System Error occured.";

    // URL/Http Errors
    public static final String INVALID_CONNECTION_TIMEOUT = "Connection timeout should be positive value";
    public static final String INVALID_READ_TIMEOUT = "Read timeout should be positive value";

    public static final String INVALID_KEYSTORE = "Invalid KeyStore.";
    public static final String SSLCONTEXT_ERROR = "Unable to build secured context.";

    public static final String INVALID_FILE = " File [%s] is invalid. Possibly file does not exist ";

    public static final String UNABLE_TO_WRITEFILE = "Error writing file ";

    public static final String UNABLE_TO_CLOSE_STREAM = "Error while closing stream";
    public static final String UNABLE_TO_FLUSH_STREAM = "Error while flushing stream";
    public static final String UNABLE_TO_COPY_DATA = "Error while copy data on file [%s]";

    public static final String OPTION_VALUE_MISSING = "Value for option %s [%s]is missing";
    public static final String INVALID_ARGS = "Improper Args. Possible cause : %s";

	public static final String EMPTY_SERVICE_ENDPOINT = "Service endpoint must not be empty";

	public static final String  EMPTY_USERNAME = "Username must not be empty";
	public static final String  EMPTY_PASSWORD = "Password must not be empty";
	public static final String  EMPTY_TENANT_NAME = "Tenant/Project must not be empty";

	public static final String INVALID_BASE_URL = "Invalid service endpont [ %s ]";

	public static final String UNABLE_TO_WRITE_FILE = "Unable to write into file [%s]";
	

    private ExceptionConstants() {
    }

}

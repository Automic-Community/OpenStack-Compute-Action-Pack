package com.automic.openstack.constants;

/**
 * Class contains all the constants used in Azure java application.
 * 
 */
public final class Constants {

    /**
     * int constant for IO Buffer used to buffer the data.
     */
    public static final int IO_BUFFER_SIZE = 4 * 1024;
    public static final String HELP = "help";
    public static final String CONNECTION_TIMEOUT = "connectiontimeout";
    public static final String READ_TIMEOUT = "readtimeout";
    public static final String ACTION = "action";
    public static final String PASSWORD = "password";
    public static final String HTTPS = "https";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";
    public static final String REQUEST_TOKENID_KEY = "X-Compute-Request-Id";
    public static final String CURRENT_AE_DATE = "currentdate";
    public static final String TIMEOUT_CRITERIA = "timeoutcriteria";
    public static final String DATE_FORMAT = "dateformat";
    
    public static final String QUERY_DELIMETER = ",";
    public static final String VAL_DELIMETER = "=";
    public static final long ONE_MINUTE_IN_MILLIS = 60000;
    public static final String AE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssX";
 
    public static final String EXPIRES = "expires";
    public static final String ISSUED_AT = "issued_at";
	public static final String ACCESS = "access";
	public static final String TOKEN = "token";
	public static final String ID = "id";

    private Constants() {
    }

}

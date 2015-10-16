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

    private Constants() {
    }

}

package com.automic.openstack.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;

/**
 * OpenStack utility class
 *
 */
public final class CommonUtil {

    private static final String YES = "YES";
    private static final String TRUE = "TRUE";
    private static final String ONE = "1";

    private static final Logger LOGGER = LogManager.getLogger(CommonUtil.class);

    private static final String RESPONSE_ERROR = "ERROR";

    private CommonUtil() {
    }

    /**
     * Method to append type to message in format "type | message"
     *
     * @param type
     * @param message
     * @return
     */
    public static String formatErrorMessage(final String message) {
        final StringBuilder sb = new StringBuilder();
        sb.append(RESPONSE_ERROR).append(" | ").append(message);
        return sb.toString();
    }

    /**
     *
     * Method to get unsigned integer value if presented by a string literal.
     *
     * @param value
     * @return
     */
    public static int getAndCheckUnsignedValue(final String value) {
        int i = -1;
        if (Validator.checkNotEmpty(value)) {
            try {
                i = Integer.parseInt(value);
            } catch (final NumberFormatException nfe) {
                i = -1;
            }
        }
        return i;
    }

    /**
     * Method to convert YES/NO values to boolean true or false
     *
     * @param value
     * @return true if YES, 1
     */
    public static boolean convert2Bool(final String value) {
        boolean ret = false;
        if (Validator.checkNotEmpty(value)) {
            final String upperCaseValue = value.toUpperCase();
            ret = YES.equals(upperCaseValue) || TRUE.equals(upperCaseValue) || ONE.equals(upperCaseValue);
        }
        return ret;
    }

    /**
     * Method to copy contents of an {@link InputStream} to a {@link OutputStream}
     *
     * @param source
     *            {@link InputStream} to read from
     * @param dest
     *            {@link OutputStream} to write to
     * @throws AutomicException
     */
    public static void copyData(final InputStream source, final OutputStream dest) throws AutomicException {
        final byte[] buffer = new byte[Constants.IO_BUFFER_SIZE];
        int length;
        try {
            while ((length = source.read(buffer)) > 0) {
                dest.write(buffer, 0, length);
                dest.flush();
            }

        } catch (final IOException e) {
            final String msg = String.format(ExceptionConstants.UNABLE_TO_COPY_DATA, source);
            LOGGER.error(msg, e);
            throw new AutomicException(msg);
        } finally {

            try {
                source.close();
                dest.close();
            } catch (final IOException e) {
                final String msg = String.format(ExceptionConstants.UNABLE_TO_COPY_DATA, source);
                LOGGER.error(msg, e);
                throw new AutomicException(msg);
            }

        }
    }

}

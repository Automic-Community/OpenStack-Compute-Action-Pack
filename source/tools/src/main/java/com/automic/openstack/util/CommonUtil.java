package com.automic.openstack.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.automic.openstack.actions.ListSnapshotsAction;
import com.automic.openstack.constants.Constants;
import com.automic.openstack.constants.ExceptionConstants;
import com.automic.openstack.exception.AutomicException;

/**
 * OpenStack utility class
 * 
 */
public final class CommonUtil {

	private static final Logger LOGGER = LogManager.getLogger(CommonUtil.class);

	private static final String RESPONSE_ERROR = "ERROR";
	private static final String YES = "YES";
	private static final String TRUE = "TRUE";
	private static final String ONE = "1";
	private static final String UTC = "UTC";

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

	/**
	 * Method to create file at location filePath. If some error occurs it will
	 * delete the file
	 * 
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	public static void createFile(String filePath, String content) throws AutomicException {

		File file = new File(filePath);
		boolean success = true;
		try (FileWriter fr = new FileWriter(file)) {
			fr.write(content);
		} catch (IOException e) {
			success = false;
			LOGGER.error("Error while writing file ", e);
			throw new AutomicException(String.format(ExceptionConstants.UNABLE_TO_WRITE_FILE, filePath));
		} finally {
			if (!success && !file.delete()) {
				LOGGER.error("Error deleting file " + file.getName());
			}
		}
	}

	/**
	 * Method to convert a stream into Json object
	 * 
	 * @param is
	 *            input stream
	 * @return JSONObject
	 */
	public static JSONObject jsonResponse(InputStream is) {
		return new JSONObject(new JSONTokener(is));

	}

	/**
	 * Method to convert a json to xml and then write it to a File specified. It
	 * also appends a Root tag to xml.
	 * 
	 * @param json
	 * @param filePath
	 * @param rootTag
	 * @throws DockerException
	 */
	public static void json2xml(JSONObject json, String filePath, String rootTag) throws AutomicException {
		createFile(filePath, org.json.XML.toString(JSON2XMLAdapter.adoptJsonToXml(json), rootTag));
	}

	/**
	 * Method to convert a stream to xml and then write it to a File specified. It
	 * also appends a Root tag to xml.
	 * 
	 * @param is
	 * @param filePath
	 * @param rootTag
	 * @throws DockerException
	 */
	public static void jsonResponse2xml(InputStream is, String filePath, String rootTag) throws AutomicException {
		json2xml(jsonResponse(is), filePath, rootTag);
	}

	public static void jsonResponse2xml(InputStream is, String filePath) throws AutomicException {
		json2xml(jsonResponse(is), filePath, null);
	}

	public static String encrypt(String input) throws AutomicException {

		try {
			input = AESEncryptDecrypt.encrypt(input);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {

			LOGGER.error("Error while encryption ", e);
			throw new AutomicException(ExceptionConstants.GENERIC_ERROR_MSG);

		}

		return input;
	}

	public static String decrypt(String input) throws AutomicException {
		try {
			input = AESEncryptDecrypt.decrypt(input);
		} catch (Exception e) {
			LOGGER.error("Error while decryption ", e);
			throw new AutomicException(ExceptionConstants.INVALID_AUTH_TOKEN);
		}
		return input;
	}

	/**
	 * This method is useful for reading small files. It create {@code JSONObject}
	 * from file content
	 * 
	 * @param parameterFilePath
	 * @return
	 * @throws AutomicException
	 */
	public static JSONObject getJSONObjectByFilePath(String parameterFilePath) throws AutomicException {
		Path path = Paths.get(parameterFilePath);
		JSONObject jsonObj = null;
		try {
			String jsonString = new String(Files.readAllBytes(path));
			jsonObj = new JSONObject(jsonString);
		} catch (IOException e) {
			LOGGER.error(e);
			throw new AutomicException(e.getMessage());
		}

		return jsonObj;
	}

	/**
	 * Method to prepare a map of query parameters. It splits the string using two
	 * delimiters {@link ListSnapshotsAction#QUERY_DELIMETER} and
	 * {@link ListSnapshotsAction#VAL_DELIMETER} and creates the key-value pair.
	 *
	 * @param queryArgs
	 *            Query arguments as one string
	 * @return map
	 */
	public static Map<String, String> prepareQueryParamsMap(final String queryArgs) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		String[] splitArgs = queryArgs.split(Constants.QUERY_DELIMETER);
		if (splitArgs != null && splitArgs.length > 0) {
			for (String str : splitArgs) {
				String[] queryParam = str.split(Constants.VAL_DELIMETER);
				if (queryParam != null && queryParam.length == 2) {
					String key = queryParam[0].trim();
					String value = queryParam[1].trim();
					if (Validator.checkNotEmpty(key) && Validator.checkNotEmpty(value)) {
						params.put(key, value);
					}
				}
			}
		}
		return params;
	}

	/**
	 * This method convert provided string to date object according to given format
	 * .
	 * 
	 * @param givenDate
	 * @param format
	 * @return date object
	 * @throws AutomicException
	 */
	public static Date convert2date(String givenDate, String format) throws AutomicException {
		Date date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone(UTC));
		try {
			date = (Date) dateFormat.parse(givenDate);
		} catch (ParseException e) {
			LOGGER.error("Error while parsing String to produce a Date", e);
			throw new AutomicException(ExceptionConstants.INVALID_DATE);
		}
		return date;
	}

	/**
	 * This method calculate expiry token using provided arguments and return
	 * calculated tokentime in Long
	 * 
	 * @param expiry
	 * @param issuedAt
	 * @param currentAEDate
	 * @return expiry token time in {@link Long}
	 * @throws AutomicException
	 */
	public static Long calcTokenExpiryTime(String expiry, String issuedAt, String currentAEDate)
			throws AutomicException {

		Long tokenexpireTime = null;
		if (!"null".equals(expiry)) {
			long opsTokenExpiryTime = convert2date(expiry, getSystemProperty(Constants.ENV_OPS_TOKEN_EXPIRE_DATE_FORMAT,
					Constants.OPS_TOKEN_EXPIRE_DATE_FORMAT_DEFAULT)).getTime();
			long opsTokenIssueTime = convert2date(issuedAt, getSystemProperty(Constants.ENV_OPS_TOKEN_ISSUE_DATE_FORMAT,
					Constants.OPS_TOKEN_ISSUE_DATE_FORMAT_DEFAULT)).getTime();

			long timeStamp = opsTokenExpiryTime - opsTokenIssueTime;
			long aeTime = CommonUtil.convert2date(currentAEDate, Constants.AE_DATE_FORMAT).getTime();
			tokenexpireTime = aeTime + timeStamp;
		}
		return tokenexpireTime;
	}

	private static String getSystemProperty(String property, String defaultValue) {

		String propertyValue = System.getenv(property);
		if (propertyValue == null || propertyValue.isEmpty()) {
			propertyValue = defaultValue;
		}
		return propertyValue;
	}

}

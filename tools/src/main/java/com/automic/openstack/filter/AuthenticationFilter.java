package com.automic.openstack.filter;

import static com.automic.openstack.util.CommonUtil.calcTokenExpiryTime;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.automic.openstack.constants.Constants;
import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.exception.AutomicRuntimeException;
import com.automic.openstack.model.AuthenticationToken;
import com.automic.openstack.service.AuthenticationTokenSevice;
import com.automic.openstack.util.AESEncryptDecrypt;
import com.automic.openstack.util.CommonUtil;
import com.automic.openstack.util.ConsoleWriter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * This class acts as a filter and intercept the request to validate its
 * provided token id. If token id expire then generate new token id by
 * {@code AuthenticationTokenSevice} service and update request token id.
 * 
 */

public class AuthenticationFilter extends ClientFilter {

	private static boolean flag = true;

	private String currentAEDate;
	private Client client;
	private int timeoutCriteria;

	public AuthenticationFilter(String currentDate, Client client,
			int timeoutCriteria) {
		this.currentAEDate = currentDate;
		this.client = client;
		this.timeoutCriteria = timeoutCriteria;
	}

	private static final Logger LOGGER = LogManager
			.getLogger(AuthenticationFilter.class);

	/**
	 * {@inheritDoc} This method validate token id from request and validate
	 * its. If token id expire generate new token token id and update request
	 */
	@Override
	public ClientResponse handle(ClientRequest request) {
		JSONObject jsonObj = null;
		String authDetails = (String) request.getHeaders().getFirst(
				Constants.X_AUTH_TOKEN);

		if (authDetails != null) {

			try {

				AuthenticationToken authToken = new AuthenticationToken(
						AESEncryptDecrypt.decrypt(authDetails));
				Long currentAETime = CommonUtil.convert2date(currentAEDate,
						Constants.AE_DATE_FORMAT).getTime();
				publishDate(currentAETime, authToken.getTokenExpiry());
				// if token id is expired
				if (isExpired(authToken.getTokenExpiry(), currentAETime,
						timeoutCriteria)) {
					LOGGER.info("Renewing Token...");
					AuthenticationTokenSevice ats = AuthenticationTokenSevice
							.getAuthenticationTokenSevice(client);
					jsonObj = ats.executeAuthenticationTokenSevice(
							authToken.getBaseurl(), authToken.getUserName(),
							authToken.getPassword(), authToken.getTenantName());
					JSONObject tokenJson = jsonObj.getJSONObject(Constants.ACCESS)
							.getJSONObject(Constants.TOKEN);
					Long expiryTokenTime = calcTokenExpiryTime(
							tokenJson.getString(Constants.EXPIRES),
							tokenJson.getString(Constants.ISSUED_AT),
							currentAEDate);
					authToken = new AuthenticationToken(authToken.getBaseurl(),
							authToken.getUserName(), authToken.getPassword(),
							authToken.getTenantName(), tokenJson.getString(Constants.ID),
							expiryTokenTime);

					ConsoleWriter.writeln("UC4RB_OPS_AUTH_TOKEN ::="
							+ CommonUtil.encrypt(authToken.toString()));

				}
				request.getHeaders().putSingle(Constants.X_AUTH_TOKEN,
						authToken.getTokenId());
				if (authToken.getTenantName() == null) {
					ConsoleWriter.writeln("Tenant name is missing");
				}

			} catch (AutomicException e) {
				LOGGER.error(e);
				throw new AutomicRuntimeException(e.getMessage());
			}
		}
		return getNext().handle(request);
	}

	/**
	 * This method compare OpenStack Token expire TimeStamp with AE current
	 * TimeStamp
	 * 
	 * @param expiresDate
	 * @param currentAETime
	 * @param timeoutCriteria
	 * @return true or false
	 */
	private boolean isExpired(Long expiresTime, Long currentAETime,
			int timeoutCriteria) throws AutomicException {

		if (expiresTime == null)
			return false;

		currentAETime = currentAETime + timeoutCriteria
				* Constants.ONE_MINUTE_IN_MILLIS;

		return expiresTime.compareTo(currentAETime) < 0 ? true : false;
	}

	/**
	 * This method print AE and OpenStack TimeStamp id flag is true
	 * 
	 * @param currentAETime
	 * @param expiresDate
	 */
	private void publishDate(Long currentAETime, Long expiresTime) {

		if (flag) {
			ConsoleWriter.writeln("AE Current TimeStamp : "
					+ new Date(currentAETime));
			ConsoleWriter.writeln("OpenStack Token Expire TimeStamp : "
					+ (expiresTime == null ? null : new Date(expiresTime)));
			flag = false;
		}

	}

}

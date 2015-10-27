package com.automic.openstack.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	private String currentAEDate;
	private Client client;
	private int timeoutCriteria;
    private static boolean flag = true;
    
	public AuthenticationFilter(String currentDate, Client client,
			int timeoutCriteria) {
		this.currentAEDate = currentDate;
		this.client = client;
		this.timeoutCriteria = timeoutCriteria;
	}

	private static final Logger LOGGER = LogManager
			.getLogger(AuthenticationFilter.class);
	private static final SimpleDateFormat AE_DATETIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ssX");
	private static final SimpleDateFormat OPENSTACK_DATETIME_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssX");

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
				// if token id is expired
				if (isExpired(authToken.getTokenExpiry(), currentAEDate,
						timeoutCriteria)) {
					AuthenticationTokenSevice ats = AuthenticationTokenSevice
							.getAuthenticationTokenSevice(client);
					jsonObj = ats.executeAuthenticationTokenSevice(
							authToken.getBaseurl(), authToken.getUserName(),
							authToken.getPassword(), authToken.getTenantName());
					JSONObject tokenJson = jsonObj.getJSONObject("access")
							.getJSONObject("token");
					authToken = new AuthenticationToken(authToken.getBaseurl(),
							authToken.getUserName(), authToken.getPassword(),
							authToken.getTenantName(),
							tokenJson.getString("id"),
							tokenJson.getString("expires"),
							tokenJson.getString("issued_at"));					
					ConsoleWriter.writeln("UC4RB_OPS_TOKEN_ID ::="
							+ CommonUtil.encrypt(authToken.toString()));
					
				}
				request.getHeaders().putSingle(Constants.X_AUTH_TOKEN,
						authToken.getTokenId());
				if(authToken.getTenantName() == null){
					ConsoleWriter.writeln("Tenant name is missing");
				}

			} catch (AutomicException e) {
				LOGGER.error(e);
				throw new AutomicRuntimeException(e.getMessage());
			}
		}
		return getNext().handle(request);
	}

	private boolean isExpired(String expiresDate, String currentAEDate,
			int timeoutCriteria) {

		if (expiresDate == null || "null".equals(expiresDate))
			return false;

		long currentAETime = 0;
		long expirationTime = 0;
		final long ONE_MINUTE_IN_MILLIS = 60000;// millisecond
		try {
			
			Date automationEngineDate = AE_DATETIME_FORMAT.parse(currentAEDate);
			Date  openStackdate =  OPENSTACK_DATETIME_FORMAT.parse(expiresDate);

			currentAETime = automationEngineDate.getTime();
			currentAETime = currentAETime + timeoutCriteria
					* ONE_MINUTE_IN_MILLIS;
			expirationTime = openStackdate.getTime();
			
			publishDate(automationEngineDate, openStackdate);
			
		} catch (ParseException e) {
			LOGGER.error(e);
			throw new AutomicRuntimeException(e.getMessage());
		}
		return expirationTime <= currentAETime;
	}
	
	private void publishDate(Date currentAEDate, Date expiresDate){
		
		if(flag){
			ConsoleWriter.writeln("AE TimeStamp : "+currentAEDate);
			ConsoleWriter.writeln("OpenStack TimeStamp : "+expiresDate);
			flag = false;
		}
		
	}

}

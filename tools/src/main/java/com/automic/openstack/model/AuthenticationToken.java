package com.automic.openstack.model;

import org.json.JSONObject;

import com.automic.openstack.exception.AutomicException;

public class AuthenticationToken {

	private final JSONObject token;
	private static final String TOKEN_EXPIRY = "tokenExpiry";
	private static final String TOKEN_ID = "tokenId";
	private static final String TENANT_NAME = "tenantName";
	private static final String PASSWORD = "password";
	private static final String USERNAME = "userName";
	private static final String BASEURL = "baseURL";

	private AuthenticationToken() {
		token = new JSONObject();
	}

	public AuthenticationToken(String authToken) throws AutomicException {
		token = new JSONObject(authToken);

	}

	public String toString() {

		return token.toString();

	}

	public AuthenticationToken(String baseurl, String username, String passw,
			String tenantname, String tokenid, Long tokenexpiry) {
		this();
		setBaseurl(baseurl);
		setUserName(username);
		setPassword(passw);
		setTenantName(tenantname);
		setTokenId(tokenid);
		setTokenExpiry(tokenexpiry);

	}

	private void setTokenExpiry(Long tokenexpiry) {
		token.put(TOKEN_EXPIRY, tokenexpiry);
	}

	public Long getTokenExpiry() {
		return token.has(TOKEN_EXPIRY)?token.getLong(TOKEN_EXPIRY):null;
	}

	private void setTokenId(String tokenid) {
		token.put(TOKEN_ID, tokenid);
	}

	public String getTokenId() {
		return token.getString(TOKEN_ID);
	}

	private void setTenantName(String tenantname) {
		token.put(TENANT_NAME, tenantname);
	}

	public String getTenantName() {
		return token.has(TENANT_NAME) ? !""
				.equals(token.getString(TENANT_NAME)) ? token
				.getString(TENANT_NAME) : null : null;
	}

	private void setPassword(String passw) {
		token.put(PASSWORD, passw);
	}

	public String getPassword() {
		return token.getString(PASSWORD);
	}

	private void setUserName(String username) {
		token.put(USERNAME, username);
	}

	public String getUserName() {
		return token.getString(USERNAME);
	}

	private void setBaseurl(String baseurl) {
		token.put(BASEURL, baseurl);
	}

	public String getBaseurl() {
		return token.getString(BASEURL);
	}
}

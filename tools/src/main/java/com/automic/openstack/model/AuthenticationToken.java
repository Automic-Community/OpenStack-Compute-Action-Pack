package com.automic.openstack.model;

import org.json.JSONObject;

import com.automic.openstack.exception.AutomicException;
import com.automic.openstack.util.CommonUtil;

public class AuthenticationToken {
	
	private JSONObject token;
	
	public AuthenticationToken() {
		token = new JSONObject();
	}
	
	public AuthenticationToken(String authToken) throws AutomicException {
		token = new JSONObject(authToken);
		CommonUtil.decrypt(token.toString());
	}
	
	public AuthenticationToken(String baseurl, String username,
			String passw, String tenantname, String tokenid,
			String tokenexpiry, String tokenissue) {
		this();
		setBaseurl(baseurl);
		setUserName(username);
		setPassword(passw);
		setTenantName(tenantname);
		setTokenId(tokenid);
		setTokenExpiry(tokenexpiry);
		setTokenIssue(tokenissue);
	}

	private void setTokenIssue(String tokenissue) {
		token.put("tokenIssue", tokenissue);
	}
	
	private String getTokenIssue() {
		return token.getJSONObject("tokenIssue").toString();
	}

	private void setTokenExpiry(String tokenexpiry) {
		token.put("tokenExpiry", tokenexpiry);
	}

	private String getTokenExpiry() {
		return token.getJSONObject("tokenExpiry").toString();
	}
	
	private void setTokenId(String tokenid) {
		token.put("tokenId", tokenid);
	}
	
	private String getTokenId() {
		return token.getJSONObject("tokenId").toString();
	}

	private void setTenantName(String tenantname) {
		token.put("tenantName", tenantname);
	}
	
	private String getTenantName() {
		return token.getJSONObject("tenantName").toString();
	}

	private void setPassword(String passw) {
		token.put("password", passw);
	}
	
	private String getPassword() {
		return token.getJSONObject("password").toString();
	}

	private void setUserName(String username) {
		token.put("userName", username);
	}

	private String getUserName() {
		return token.getJSONObject("userName").toString();
	}
	
	private void setBaseurl(String baseurl) {
		token.put("baseURL", baseurl);
	}

	private String getBaseurl() {
		return token.getJSONObject("baseURL").toString();
	}
}

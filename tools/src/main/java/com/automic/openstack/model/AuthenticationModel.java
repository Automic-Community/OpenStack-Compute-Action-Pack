/**
 * 
 */
package com.automic.openstack.model;


/**
 * @author sumitsamson
 * 
 */

public class AuthenticationModel {

	
	private Authentication authentication;

	public AuthenticationModel(String username, String password,
			String tenantName) {

		this.authentication = new Authentication(username, password, tenantName);

	}

	/**
	 * @return the auth
	 */
	public Authentication getAuth() {
		return authentication;
	}

}

class Authentication {
	
	private String tenantName;
	
	private PasswordCredentials passwordCredentials;

	public Authentication(String username, String password, String tenantName) {
		this.tenantName = tenantName;
		this.passwordCredentials = new PasswordCredentials(username, password);

	}

	public String getTenantName() {
		return tenantName;
	}

	public PasswordCredentials getPasswordCredentials() {
		return passwordCredentials;
	}
}

class PasswordCredentials {

	
	private String username;

	
	private String password;

	public PasswordCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
/**
 * 
 */
package com.automic.openstack.model;

/**
 * @author sumitsamson
 * 
 */
public class AuthenticationModel {

	private String tenantName;

	private PasswordCredentials passwordCredentials;

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

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
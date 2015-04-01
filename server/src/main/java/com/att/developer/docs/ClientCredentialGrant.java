package com.att.developer.docs;

import com.mangofactory.swagger.models.dto.GrantType;
import com.mangofactory.swagger.models.dto.TokenEndpoint;

public class ClientCredentialGrant extends GrantType {

	private final TokenEndpoint tokenEndpoint;

	public ClientCredentialGrant(TokenEndpoint tokenEndpoint) {
		super("client_credentials");
		this.tokenEndpoint = tokenEndpoint;
	}

	public TokenEndpoint getTokenEndpoint() {
		return tokenEndpoint;
	}

}

package org.koushik.javabrains.messenger.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("secured")
public class SecuredResouce {
	
	@GET
	@Path("message")
	@Produces(MediaType.TEXT_PLAIN)
	public String securedMethod() {
		return "This API is secured";
	}
}

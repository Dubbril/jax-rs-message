package org.koushik.javabrains.messenger.resources;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.koushik.javabrains.messenger.model.Message;
import org.koushik.javabrains.messenger.resources.beans.MessageFilterBean;
import org.koushik.javabrains.messenger.service.MessageService;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

	MessageService messageService = new MessageService();

	@GET
	public List<Message> getMessage(@BeanParam MessageFilterBean filterBean) {
		if (filterBean.getYear() > 0) {
			return messageService.getAllMessageForYear(filterBean.getYear());
		}

		if (filterBean.getSize() > 0 && filterBean.getStart() > 0) {
			return messageService.getAllMessagePaginated(filterBean.getStart(), filterBean.getSize());
		}

		return messageService.getAllMessages();
	}

	@PUT
	@Path("/{messageId}")

	public Message updateMessage(@PathParam("messageId") long id, Message message) {
		message.setId(id);
		return messageService.updateMessage(message);
	}

	@DELETE
	@Path("/{messageId}")
	public void deleteMessage(@PathParam("messageId") long id) {
		messageService.removeMessage(id);
	}

	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		Message newMessage = messageService.addMessage(message);
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri).entity(newMessage).build();
	}

	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long id, @Context UriInfo uriInfo) {
		Message message = messageService.getMessage(id);
		message.addLink(getUriForSelf(uriInfo, message), "self");
		message.addLink(getUriForProfile(uriInfo, message), "profile");
		message.addLink(getUriForComments(uriInfo, message), "comments");
		return message;
	}

	private String getUriForComments(UriInfo uriInfo, Message message) {
		return uriInfo.getBaseUriBuilder().
				path(MessageResource.class).
				path(MessageResource.class,"getCommentResouces").
				path(CommentResources.class)
				.resolveTemplate("messageId", message.getId())
				.build().toString();
	}

	private String getUriForProfile(UriInfo uriInfo, Message message) {
		return uriInfo.getBaseUriBuilder().path(ProfileResource.class).path(message.getAuthor()).build().toString();
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		return uriInfo.getBaseUriBuilder().path(MessageResource.class).path(Long.toString(message.getId())).build()
				.toString();
	}

	@Path("/{messageId}/comments")
	public CommentResources getCommentResouces() {
		return new CommentResources();
	}
}

package com.bsds.amala.assignment1.client;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by amala on 19/09/17.
 */
public class WebClient {

    private final WebTarget webTarget;

    public WebClient(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    public Response postText(Object requestEntity) throws ClientErrorException {
        return webTarget
                .request(MediaType.TEXT_PLAIN)
                .post(Entity.entity(requestEntity, MediaType.TEXT_PLAIN), Response.class);
    }

    public Response getStatus() throws ClientErrorException {
        return webTarget
                .request(MediaType.TEXT_PLAIN)
                .get(Response.class);
    }
}

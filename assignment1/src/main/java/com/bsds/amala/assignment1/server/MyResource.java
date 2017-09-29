package com.bsds.amala.assignment1.server;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("myresource")
public class MyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return "alive";
    }
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public int postText(String content) {
        return content.length();
    }
}

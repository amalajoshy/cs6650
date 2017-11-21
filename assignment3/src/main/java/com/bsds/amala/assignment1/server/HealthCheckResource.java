package com.bsds.amala.assignment1.server;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
@Singleton
public class HealthCheckResource {

    @GET
    public Response ping() {
        return Response.ok().build();
    }
}

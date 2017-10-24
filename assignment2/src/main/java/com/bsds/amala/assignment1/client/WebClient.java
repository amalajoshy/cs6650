package com.bsds.amala.assignment1.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class WebClient {

    private final WebTarget webTarget;
    private final Executor executor;
    private final Map<Long, Long> latencyMap;

    public Response post(Object requestEntity) throws ClientErrorException {
        long startTime = System.currentTimeMillis();
        Response response = webTarget
                .request()
                .post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), Response.class);
        long endTime = System.currentTimeMillis();
        executor.execute(() -> latencyMap.put(endTime, endTime - startTime));
        return response;
    }

    public Response get() throws ClientErrorException {
        long startTime = System.currentTimeMillis();
        Response response = webTarget
                .request()
                .get(Response.class);
        long endTime = System.currentTimeMillis();
        executor.execute(() -> latencyMap.put(endTime, endTime - startTime));
        return response;
    }

    public Response delete() {
        return webTarget
                .request()
                .delete();
    }
}

package org.example.API;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("ping")
public class Ping {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ping(String json) {
        System.out.println("Received: " + json);
        return Response.ok("Cool").build();
    }
}

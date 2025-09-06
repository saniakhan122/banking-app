package com.oracle.controller;

import java.util.Map;

import com.oracle.beans.AdminUser;
import com.oracle.business.AdminUserService;
import com.oracle.business.util.ServiceFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/admin")
public class AdminRestController {

    private AdminUserService adminService = ServiceFactory.getAdminUserService();

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Map<String, Object> jsonBody) {
        if (jsonBody == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Missing request body"))
                           .build();
        }

        String username = (String) jsonBody.get("username");
        String password = (String) jsonBody.get("password");

        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of("error", "Missing credentials"))
                           .build();
        }

        boolean isAuthenticated = adminService.authenticate(username, password);

        if (isAuthenticated) {
            return Response.ok(Map.of(
                "success", true,
                "message", "Login successful",
                "username", username
            )).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity(Map.of("success", false, "error", "Invalid username or password"))
                           .build();
        }
    }


    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdmin(@PathParam("username") String username) {
        AdminUser admin = adminService.getAdminByUsername(username);
        return Response.status(200).entity(admin).build();
    }
}

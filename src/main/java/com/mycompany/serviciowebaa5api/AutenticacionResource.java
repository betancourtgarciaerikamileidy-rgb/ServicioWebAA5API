
package com.mycompany.serviciowebaa5api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("autenticacion")
public class AutenticacionResource {

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response autenticar(Usuario usuario) {

        if (usuario != null
                && "erika".equals(usuario.usuario)
                && "12345".equals(usuario.contrasena)) {

            return Response.ok(
                "{\"mensaje\":\"Autenticación satisfactoria\"}"
            ).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"mensaje\":\"Error en la autenticación\"}")
                .build();
    }

    public static class Usuario {
        public String usuario;
        public String contrasena;
    }
}

    


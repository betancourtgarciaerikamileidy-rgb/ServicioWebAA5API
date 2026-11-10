package com.mycompany.serviciowebaa5api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @GET
    public Response consultarClientes() {

        String sql = "SELECT id_cliente, nombre, apellido, telefono, correo, direccion FROM Clientes";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            StringBuilder json = new StringBuilder("[");
            boolean primero = true;

            while (resultado.next()) {

                if (!primero) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"id_cliente\":").append(resultado.getInt("id_cliente")).append(",")
                    .append("\"nombre\":\"").append(escapar(resultado.getString("nombre"))).append("\",")
                    .append("\"apellido\":\"").append(escapar(resultado.getString("apellido"))).append("\",")
                    .append("\"telefono\":\"").append(escapar(resultado.getString("telefono"))).append("\",")
                    .append("\"correo\":\"").append(escapar(resultado.getString("correo"))).append("\",")
                    .append("\"direccion\":\"").append(escapar(resultado.getString("direccion"))).append("\"")
                    .append("}");

                primero = false;
            }

            json.append("]");

            return Response.ok(json.toString()).build();

        } catch (SQLException e) {

            // Mostrar el error real en la consola de NetBeans
            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No fue posible consultar los clientes\"}")
                    .build();
        }
    }

    @POST
    public Response registrarCliente(Cliente cliente) {

        if (cliente == null
                || cliente.nombre == null || cliente.nombre.isBlank()
                || cliente.apellido == null || cliente.apellido.isBlank()
                || cliente.telefono == null || cliente.telefono.isBlank()
                || cliente.correo == null || cliente.correo.isBlank()
                || cliente.direccion == null || cliente.direccion.isBlank()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Todos los campos son obligatorios\"}")
                    .build();
        }

        String sql = "INSERT INTO Clientes (nombre, apellido, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement insertar = conexion.prepareStatement(sql)) {

            insertar.setString(1, cliente.nombre);
            insertar.setString(2, cliente.apellido);
            insertar.setString(3, cliente.telefono);
            insertar.setString(4, cliente.correo);
            insertar.setString(5, cliente.direccion);

            insertar.executeUpdate();

            return Response.status(Response.Status.CREATED)
                    .entity("{\"mensaje\":\"Cliente registrado correctamente\"}")
                    .build();

        } catch (SQLException e) {

            e.printStackTrace();

            if (e.getErrorCode() == 1062) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"El correo ya está registrado\"}")
                        .build();
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No fue posible registrar el cliente\"}")
                    .build();
        }
    }

    private String escapar(String texto) {

        if (texto == null) {
            return "";
        }

        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    public static class Cliente {

        public String nombre;
        public String apellido;
        public String telefono;
        public String correo;
        public String direccion;
    }
}


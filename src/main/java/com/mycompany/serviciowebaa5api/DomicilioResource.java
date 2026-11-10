package com.mycompany.serviciowebaa5api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("domicilios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DomicilioResource {

    @GET
    public Response listar() {

        String sql = "SELECT id_domicilio, id_pedido, direccion_entrega, "
                + "ciudad, telefono_contacto, estado FROM Domicilios";

        List<String> domicilios = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                domicilios.add("{"
                        + "\"id_domicilio\":" + rs.getInt("id_domicilio") + ","
                        + "\"id_pedido\":" + rs.getInt("id_pedido") + ","
                        + "\"direccion_entrega\":\"" + escapar(rs.getString("direccion_entrega")) + "\","
                        + "\"ciudad\":\"" + escapar(rs.getString("ciudad")) + "\","
                        + "\"telefono_contacto\":\"" + escapar(rs.getString("telefono_contacto")) + "\","
                        + "\"estado\":\"" + escapar(rs.getString("estado")) + "\""
                        + "}");
            }

            return Response.ok("[" + String.join(",", domicilios) + "]").build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"mensaje\":\"Error al consultar los domicilios\"}")
                    .build();
        }
    }

    @POST
    public Response registrar(Domicilio domicilio) {

        if (domicilio == null
                || domicilio.id_pedido <= 0
                || domicilio.direccion_entrega == null
                || domicilio.direccion_entrega.trim().isEmpty()
                || domicilio.ciudad == null
                || domicilio.ciudad.trim().isEmpty()
                || domicilio.telefono_contacto == null
                || domicilio.telefono_contacto.trim().isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\":\"Todos los datos son obligatorios\"}")
                    .build();
        }

        String sql = "INSERT INTO Domicilios "
                + "(id_pedido, direccion_entrega, ciudad, telefono_contacto, estado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, domicilio.id_pedido);
            ps.setString(2, domicilio.direccion_entrega);
            ps.setString(3, domicilio.ciudad);
            ps.setString(4, domicilio.telefono_contacto);

            String estado = domicilio.estado == null
                    || domicilio.estado.trim().isEmpty()
                    ? "Pendiente" : domicilio.estado;

            ps.setString(5, estado);

            ps.executeUpdate();

            return Response.status(Response.Status.CREATED)
                    .entity("{\"mensaje\":\"Domicilio registrado correctamente\"}")
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"mensaje\":\"Error al registrar el domicilio. Verifique el pedido\"}")
                    .build();
        }
    }

    private String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    public static class Domicilio {
        public int id_pedido;
        public String direccion_entrega;
        public String ciudad;
        public String telefono_contacto;
        public String estado;
    }
}
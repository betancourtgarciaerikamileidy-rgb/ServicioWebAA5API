package com.mycompany.serviciowebaa5api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Path("detalles-pedido")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DetallePedidoResource {

    @GET
    public Response listar() {
        String sql = "SELECT id_detalle, id_pedido, id_producto, "
                + "cantidad, precio_unitario FROM Detalle_pedido";

        List<String> detalles = new ArrayList<>();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                detalles.add("{"
                        + "\"id_detalle\":" + rs.getInt("id_detalle") + ","
                        + "\"id_pedido\":" + rs.getInt("id_pedido") + ","
                        + "\"id_producto\":" + rs.getInt("id_producto") + ","
                        + "\"cantidad\":" + rs.getInt("cantidad") + ","
                        + "\"precio_unitario\":" + rs.getBigDecimal("precio_unitario")
                        + "}");
            }

            return Response.ok("[" + String.join(",", detalles) + "]").build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"mensaje\":\"Error al consultar los detalles\"}")
                    .build();
        }
    }

    @POST
    public Response registrar(Detalle detalle) {

        if (detalle == null
                || detalle.id_pedido <= 0
                || detalle.id_producto <= 0
                || detalle.cantidad <= 0
                || detalle.precio_unitario < 0) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\":\"Datos del detalle no válidos\"}")
                    .build();
        }

        String sql = "INSERT INTO Detalle_pedido "
                + "(id_pedido, id_producto, cantidad, precio_unitario) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.id_pedido);
            ps.setInt(2, detalle.id_producto);
            ps.setInt(3, detalle.cantidad);
            ps.setDouble(4, detalle.precio_unitario);

            ps.executeUpdate();

            return Response.status(Response.Status.CREATED)
                    .entity("{\"mensaje\":\"Detalle de pedido registrado correctamente\"}")
                    .build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"mensaje\":\"Error al registrar el detalle. Verifique los identificadores\"}")
                    .build();
        }
    }

    public static class Detalle {
        public int id_pedido;
        public int id_producto;
        public int cantidad;
        public double precio_unitario;
    }
}
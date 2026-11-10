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

@Path("pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidosResource {

    @GET
    public Response consultarPedidos() {

        String sql = "SELECT id_pedido, id_cliente, fecha_pedido, estado, total FROM Pedidos";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder json = new StringBuilder("[");
            boolean primero = true;

            while (rs.next()) {

                if (!primero) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"id_pedido\":").append(rs.getInt("id_pedido")).append(",")
                    .append("\"id_cliente\":").append(rs.getInt("id_cliente")).append(",")
                    .append("\"fecha_pedido\":\"").append(rs.getDate("fecha_pedido")).append("\",")
                    .append("\"estado\":\"").append(rs.getString("estado")).append("\",")
                    .append("\"total\":").append(rs.getBigDecimal("total"))
                    .append("}");

                primero = false;
            }

            json.append("]");

            return Response.ok(json.toString()).build();

        } catch (SQLException e) {
            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No fue posible consultar los pedidos\"}")
                    .build();
        }
    }

    @POST
    public Response registrarPedido(Pedido pedido) {

        if (pedido == null
                || pedido.id_cliente <= 0
                || pedido.fecha_pedido == null
                || pedido.fecha_pedido.trim().isEmpty()
                || pedido.total < 0) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Datos del pedido incompletos o inválidos\"}")
                    .build();
        }

        String sql = "INSERT INTO Pedidos (id_cliente, fecha_pedido, estado, total) VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, pedido.id_cliente);
            ps.setDate(2, java.sql.Date.valueOf(pedido.fecha_pedido));
            ps.setString(3, pedido.estado == null || pedido.estado.trim().isEmpty()
                    ? "Pendiente" : pedido.estado);
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(pedido.total));

            int filas = ps.executeUpdate();

            if (filas > 0) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Pedido registrado correctamente\"}")
                        .build();
            }

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No fue posible registrar el pedido\"}")
                    .build();

        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"No fue posible registrar el pedido. Verifique los datos y el cliente\"}")
                    .build();
        }
    }

    public static class Pedido {
        public int id_cliente;
        public String fecha_pedido;
        public String estado;
        public double total;
    }
}


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

@Path("productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {

    @GET
    public Response consultarProductos() {

        String sql = "SELECT id_producto, nombre, descripcion, categoria, precio, stock FROM Productos";

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
                    .append("\"id_producto\":").append(resultado.getInt("id_producto")).append(",")
                    .append("\"nombre\":\"").append(escapar(resultado.getString("nombre"))).append("\",")
                    .append("\"descripcion\":");

                String descripcion = resultado.getString("descripcion");

                if (descripcion == null) {
                    json.append("null");
                } else {
                    json.append("\"").append(escapar(descripcion)).append("\"");
                }

                json.append(",")
                    .append("\"categoria\":\"").append(escapar(resultado.getString("categoria"))).append("\",")
                    .append("\"precio\":").append(resultado.getBigDecimal("precio")).append(",")
                    .append("\"stock\":").append(resultado.getInt("stock"))
                    .append("}");

                primero = false;
            }

            json.append("]");

            return Response.ok(json.toString()).build();

        } catch (SQLException e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No fue posible consultar los productos\"}")
                    .build();
        }
    }

    @POST
    public Response registrarProducto(Producto producto) {

        if (producto == null
                || producto.nombre == null || producto.nombre.isBlank()
                || producto.categoria == null || producto.categoria.isBlank()
                || producto.precio == null || producto.precio < 0
                || producto.stock == null || producto.stock < 0) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Datos del producto incompletos o incorrectos\"}")
                    .build();
        }

        String sql = "INSERT INTO Productos (nombre, descripcion, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement insertar = conexion.prepareStatement(sql)) {

            insertar.setString(1, producto.nombre);
            insertar.setString(2, producto.descripcion);
            insertar.setString(3, producto.categoria);
            insertar.setBigDecimal(4, java.math.BigDecimal.valueOf(producto.precio));
            insertar.setInt(5, producto.stock);

            insertar.executeUpdate();

            return Response.status(Response.Status.CREATED)
                    .entity("{\"mensaje\":\"Producto registrado correctamente\"}")
                    .build();

        } catch (SQLException e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No fue posible registrar el producto\"}")
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

    public static class Producto {
        public String nombre;
        public String descripcion;
        public String categoria;
        public Double precio;
        public Integer stock;
    }
}


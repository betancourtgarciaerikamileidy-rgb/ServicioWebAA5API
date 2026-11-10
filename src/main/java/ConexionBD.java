package com.mycompany.serviciowebaa5api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bits_sublimation";

    private static final String USUARIO =
            System.getenv("BITS_DB_USER");

    private static final String CONTRASENA =
            System.getenv("BITS_DB_PASSWORD");

    public static Connection conectar() throws SQLException {

        System.out.println("=== PRUEBA DE CONEXION MYSQL ===");
        System.out.println("Usuario configurado: " + USUARIO);
        System.out.println("Contraseña configurada: "
                + (CONTRASENA != null && !CONTRASENA.isBlank()));

        if (USUARIO == null || CONTRASENA == null) {
            throw new SQLException(
                    "Faltan las variables de conexión a la base de datos."
            );
        }

        Connection conexion = DriverManager.getConnection(
                URL,
                USUARIO,
                CONTRASENA
        );

        System.out.println("Conexion a MySQL exitosa.");

        return conexion;
    }
}
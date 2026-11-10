package com.mycompany.serviciowebaa5api;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        try (Connection conexion = ConexionBD.conectar()) {

            System.out.println("CONEXION EXITOSA");
            System.out.println("Base de datos: "
                    + conexion.getCatalog());

        } catch (Exception e) {

            System.out.println("ERROR DE CONEXION");
            e.printStackTrace();
        }
    }
}


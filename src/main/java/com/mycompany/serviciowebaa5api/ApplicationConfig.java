package com.mycompany.serviciowebaa5api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("resources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> recursos = new HashSet<>();

        recursos.add(AutenticacionResource.class);
        recursos.add(ClienteResource.class);
        recursos.add(ProductoResource.class);
        recursos.add(PedidosResource.class);
        recursos.add(DetallePedidoResource.class);
        recursos.add(DomicilioResource.class);

        return recursos;
    }
}
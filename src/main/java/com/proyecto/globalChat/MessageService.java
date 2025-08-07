package com.proyecto.globalChat;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageService {
    private final List<Mensaje> mensajes = new CopyOnWriteArrayList<>();

    public List<Mensaje> getMensajes() {
        return Collections.unmodifiableList(mensajes);
    }

    public void agregar(Mensaje mensaje) {
        Objects.requireNonNull(mensaje, "El mensaje no puede ser nulo");
        mensajes.add(mensaje);
    }

    public boolean eliminar(String usuario, String contenido) {
        return mensajes.removeIf(m -> 
            m.getUsuario().equals(usuario) && 
            m.getMensaje().equals(contenido)
        );
    }
}
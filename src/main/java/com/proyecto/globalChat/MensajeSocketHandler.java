package com.proyecto.globalChat;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class MensajeSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sesiones = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sesiones.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sesiones.remove(session);
    }
    
    public void broadcastNuevoMensaje(Mensaje mensaje) {
        enviarATodos("nuevo", mensaje);
    }

    public void broadcastEliminarMensaje(Mensaje mensaje) {
        enviarATodos("eliminar", mensaje);
    }

    private void enviarATodos(String tipo, Mensaje contenido) {
        String json = String.format(
            "{\"tipo\": \"%s\", \"usuario\": \"%s\", \"mensaje\": \"%s\"}",
            tipo,
            escapeJson(contenido.getUsuario()),
            escapeJson(contenido.getMensaje())
        );

        sesiones.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            } catch (Exception e) {
            }
        });
    }

    private static String escapeJson(String text) {
        return text == null ? "" : text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
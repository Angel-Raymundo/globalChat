package com.proyecto.globalChat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class FormServlet extends HttpServlet {
    private MessageService messageService;
    private MensajeSocketHandler socketHandler;

    @Override
    public void init() {
        var context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        this.messageService = context.getBean(MessageService.class);
        this.socketHandler = context.getBean(MensajeSocketHandler.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if (req.getContentType() != null && req.getContentType().startsWith("multipart/form-data")) {
                Part filePart = req.getPart("archivo");
                String usuario = req.getParameter("usuario");
                
                if (filePart == null || usuario == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Archivo y usuario son requeridos");
                    return;
                }

                InputStream fileContent = filePart.getInputStream();
                byte[] fileBytes = fileContent.readAllBytes();
                String base64File = Base64.getEncoder().encodeToString(fileBytes);
                String fileType = filePart.getContentType();
                String fileName = filePart.getSubmittedFileName();
                
                String mensajeTexto;
                if (fileType.startsWith("image/")) {
                    mensajeTexto = "IMG:" + base64File + ":" + fileType;
                } else if (fileType.startsWith("video/")) {
                    mensajeTexto = "VID:" + base64File + ":" + fileType;
                } else if (fileType.startsWith("audio/")) {
                    mensajeTexto = "AUD:" + base64File + ":" + fileType;
                } else {
                    mensajeTexto = "FILE:" + fileName + ":" + fileType;
                }
                
                Mensaje nuevoMensaje = new Mensaje(usuario, mensajeTexto);
                messageService.agregar(nuevoMensaje);
                socketHandler.broadcastNuevoMensaje(nuevoMensaje);
                
                resp.setStatus(HttpServletResponse.SC_CREATED);
                return;
            }

            String usuario = req.getParameter("usuario");
            String mensaje = req.getParameter("mensaje");

            if (usuario == null || mensaje == null || mensaje.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Usuario y mensaje son requeridos");
                return;
            }

            Mensaje nuevoMensaje = new Mensaje(usuario, mensaje);
            messageService.agregar(nuevoMensaje);
            socketHandler.broadcastNuevoMensaje(nuevoMensaje);

            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
            e.printStackTrace();
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String usuario = req.getParameter("usuario");
        String mensaje = req.getParameter("mensaje");

        if (usuario == null || mensaje == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Usuario y mensaje son requeridos");
            return;
        }

        messageService.eliminar(usuario, mensaje);
        socketHandler.broadcastEliminarMensaje(new Mensaje(usuario, mensaje));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        new ObjectMapper().writeValue(resp.getWriter(), messageService.getMensajes());
    }
}
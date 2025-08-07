package com.proyecto.globalChat;

public class Mensaje {
    private String usuario;
    private String mensaje;

    public Mensaje(String usuario, String mensaje) {
        this.usuario = usuario;
        this.mensaje = mensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mensaje)) return false;
        Mensaje otro = (Mensaje) o;
        return usuario.equals(otro.usuario) && mensaje.equals(otro.mensaje);
    }

    @Override
    public int hashCode() {
        return usuario.hashCode() + mensaje.hashCode();
    }
}
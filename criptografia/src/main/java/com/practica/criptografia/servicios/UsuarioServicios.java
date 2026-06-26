package com.practica.criptografia.servicios;

import com.practica.criptografia.modelo.Usuario;
import com.practica.criptografia.repositorio.UsuarioRepositorio;
import com.practica.criptografia.seguridad.SeguridadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UsuarioServicios {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailServicio emailServicio;

    public String registrarUsuario(Usuario nuevoUsuario) {

        if (nuevoUsuario.getCorreo() == null || nuevoUsuario.getCorreo().trim().isEmpty()) {
            return "CORREO_VACIO";
        }

        if (usuarioRepositorio.existsById(nuevoUsuario.getId())) {
            return "ID_EXISTE";
        }

        if (usuarioRepositorio.countByCorreo(nuevoUsuario.getCorreo()) >= 2) {
            System.out.println(
                    "[!] ------> Registro rechazado. El correo " + nuevoUsuario.getCorreo() + " alcanzó el límite.");
            return "CORREO_LIMITE";
        }

        String hash = SeguridadUtil.aplicarSHA256(nuevoUsuario.getPwdHash());
        nuevoUsuario.setPwdHash(hash); // Sobrescribimos el texto plano con el hash

        String codigoGenerado = String.format("%06d", new Random().nextInt(999999));
        nuevoUsuario.setCodigoVerificacion(codigoGenerado);
        usuarioRepositorio.save(nuevoUsuario);
        System.out.println("[!] ------> Usuario registrado en SQLite: " + nuevoUsuario.getId());

        emailServicio.enviarCodigoVerificacion(nuevoUsuario.getCorreo(), codigoGenerado);

        return "OK";
    }

    public boolean verificarCodigo(String id, String codigoIngresado) {
        Optional<Usuario> usuarioGuardado = usuarioRepositorio.findById(id);
        if (usuarioGuardado.isPresent()) {
            Usuario usuario = usuarioGuardado.get();
            if (usuario.getCodigoVerificacion().equals(codigoIngresado)) {
                usuario.setVerificado(true);
                usuario.setCodigoVerificacion(null);
                usuarioRepositorio.save(usuario);
                System.out.println("[!] ------> Usuario verificado: " + id);
                return true;
            }
        }
        return false;
    }

    public boolean validarlogin(String id, String intentoHash) {
        Optional<Usuario> usuarioGuardado = usuarioRepositorio.findById(id);
        if (usuarioGuardado.isPresent())
            return usuarioGuardado.get().getPwdHash().equals(intentoHash);
        return false;
    }

    public boolean solicitarRecuperacion(String id) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String codigoRecuperacion = String.format("%06d", new Random().nextInt(999999));
            usuario.setCodigoVerificacion(codigoRecuperacion);
            usuarioRepositorio.save(usuario);
            emailServicio.enviarCodigoVerificacion(usuario.getCorreo(), codigoRecuperacion);
            System.out.println("[!] ------> Código de recuperación enviado para usuario: " + id);
            return true;
        }
        return false;
    }

    public boolean cambiarContraseña(String id, String codigoIngresado, String nuevaPwdPlana) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getCodigoVerificacion() != null && usuario.getCodigoVerificacion().equals(codigoIngresado)) {
                String nuevoHash = SeguridadUtil.aplicarSHA256(nuevaPwdPlana);
                usuario.setPwdHash(nuevoHash);
                usuario.setCodigoVerificacion(null);
                usuarioRepositorio.save(usuario);
                System.out.println("[!] ------> Contraseña cambiada para usuario: " + id);
                return true;
            }
        }
        return false;
    }

    @Scheduled(fixedRate = 60000)
    public void limpiarUsuariosNoVerificados() {
        usuarioRepositorio.deleteByVerificadoFalse();
        System.out.println("[!] ------> Usuarios no verificados eliminados");
    }
}

package com.practica.criptografia.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

import com.practica.criptografia.modelo.Usuario;
import com.practica.criptografia.seguridad.SeguridadUtil;
import com.practica.criptografia.servicios.UsuarioServicios;

@RestController
@RequestMapping("/api")
public class UsuarioControlador {
    @Autowired
    private UsuarioServicios usuarioServicios;

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        String resultado = usuarioServicios.registrarUsuario(nuevoUsuario);

        if (resultado.equals("OK")) {
            return ResponseEntity.ok("Registro exitoso. Revisa tu correo para el código de activación.");
        }
        if (resultado.equals("CORREO_VACIO")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error crítico: El servidor no recibió el correo electrónico. Revisa la conexión del formulario.");
        }

        if (resultado.equals("ID_EXISTE")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El ID de usuario ya se encuentra registrado. Intenta con otro.");
        }

        if (resultado.equals("CORREO_LIMITE")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Este correo electrónico ya está asociado al máximo de 2 cuentas permitidas.");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocurrió un error inesperado en el servidor.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestBody Map<String, String> datos) {
        String id = datos.get("id");
        String pwdPlana = datos.get("pwd");
        String pwdHash = SeguridadUtil.aplicarSHA256(pwdPlana);

        System.out.println("Intento de LOGIN dado por ID: " + id + ", Contraseña: " + pwdPlana);
        System.out.println("Contraseña hasheada (SHA-256): " + pwdHash);

        boolean acceso = usuarioServicios.validarlogin(id, pwdHash);
        if (acceso)
            return ResponseEntity.ok("Login exitoso");
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");

    }

    @PostMapping("/verificar")
    public ResponseEntity<String> verificarCuenta(@RequestBody Map<String, String> datos) {
        String id = datos.get("id");
        String codigo = datos.get("codigo");
        System.out.println("Intento de verificacion para ID " + id + " con codigo: " + codigo);
        boolean verificado = usuarioServicios.verificarCodigo(id, codigo);
        if (verificado)
            return ResponseEntity.ok("Cuenta verificada exitosamente");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Código de verificación incorrecto o usuario no encontrado");
    }

    @PostMapping("/solicitar-recuperacion")
    public ResponseEntity<String> solicitarRecuperacion(@RequestBody Map<String, String> datos) {
        String id = datos.get("id");

        boolean enviado = usuarioServicios.solicitarRecuperacion(id);
        if (enviado)
            return ResponseEntity.ok("Código de recuperación enviado al correo registrado");
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Usuario no encontrado");
    }

    @PostMapping("/cambiar-pwd")
    public ResponseEntity<String> cambiarPwd(@RequestBody Map<String, String> datos) {
        String id = datos.get("id");
        String codigo = datos.get("codigo");
        String nuevaPwd = datos.get("nuevaPwd");

        boolean actualizado = usuarioServicios.cambiarContraseña(id, codigo, nuevaPwd);
        if (actualizado)
            return ResponseEntity.ok("Contraseña cambiada exitosamente");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: Código de recuperación incorrecto o usuario no encontrado");
    }
}

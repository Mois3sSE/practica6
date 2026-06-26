package com.practica.criptografia.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServicio {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigoVerificacion(String destinatario, String codigo) {
        try {
            System.out.println("=========================================");
            System.out.println("[INTENTO] Preparando correo para: [" + destinatario + "]");
            
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject("Código de Verificación - Sistema de Criptografía");
            mensaje.setText("Tu código de verificación es: " + codigo);
            
            mailSender.send(mensaje);
            System.out.println("[ÉXITO] El correo fue entregado a Google correctamente.");
            System.out.println("=========================================");
            
        } catch (Exception e) {
            System.err.println("=========================================");
            System.err.println("[ERROR CRÍTICO] Falló el envío del correo a: " + destinatario);
            System.err.println("Motivo exacto del fallo:");
            e.printStackTrace();
            System.err.println("=========================================");
            throw new RuntimeException("Error de red al conectar con Gmail");
        }
    }
}
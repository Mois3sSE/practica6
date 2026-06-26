package com.practica.criptografia.repositorio;

import com.practica.criptografia.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    long countByCorreo(String correo);

    @Transactional
    void deleteByVerificadoFalse();
}

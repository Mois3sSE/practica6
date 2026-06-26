const formLogin = document.getElementById("formLogin");
const formRegistro = document.getElementById("formRegistro");
const formVerificar = document.getElementById("formVerificar");
const formRecuperar = document.getElementById("formRecuperar");
const formCambiarPwd = document.getElementById("formCambiarPwd");

window.addEventListener('pageshow', function () {
    document.querySelectorAll('form').forEach(formulario => formulario.reset());
})


if (formRegistro) {
    formRegistro.addEventListener("submit", async function (evento) {
        evento.preventDefault();
        // Captura de valores de id's
        const idUsuario = document.getElementById("id").value;
        const pwd = document.getElementById("pwd").value;
        const pwd2 = document.getElementById("pwd2").value;
        const correo = document.getElementById("email").value;

        if (pwd != pwd2) {
            alert("Las contraseñas no coinciden. Verificalas por favor");
            return;
        }
        // Creacion del paquete a enviar 
        console.log("los datos son:", idUsuario, pwd, correo);
        const datosRegistro = {
            id: idUsuario,
            pwdHash: pwd,
            correo: correo
        };
        // POST al SpringBoot 
        console.log("Enviando datos de registro:", JSON.stringify(datosRegistro));
        try {
            const respuesta = await fetch("/api/registro", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(datosRegistro)
            });
            if (respuesta.ok) {
                alert("Registro exitoso.");
                localStorage.setItem("idPendiente", idUsuario);
                window.location.href = "./verificar.html";
            } else {
                const mensajeError = await respuesta.text();
                alert(mensajeError);
            }
        } catch (error) {
            console.error("Error de conexion:", error);
            alert("No se pudo conectar con el servidor. Intenta nuevamente mas tarde.");
        }
    });

}

if (formLogin) {
    formLogin.addEventListener('submit', async function (evento) {
        evento.preventDefault();
        const idUsuario = document.getElementById("id").value;
        const pwd = document.getElementById("pwd").value;

        const credenciales = {
            id: idUsuario,
            pwd: pwd
        };
        try {
            const respuesta = await fetch("/api/login", {
                method: 'POST',
                headers: {
                    "Content-Type": 'application/json'
                },
                body: JSON.stringify(credenciales)
            });

            if (respuesta.ok) {
                window.location.href = "./inicio.html";
            } else {
                alert("Credenciales incorrectas. Intenta nuevamente.");
            }
        } catch (error) {
            console.error("Error de conexion:", error);
            alert("No se pudo conectar con el servidor. Intenta nuevamente mas tarde.");
        }
    });
}

if (formVerificar) {
    const idUsuario = localStorage.getItem("idPendiente");
    if (idUsuario) document.getElementById("usuarioMostrado").innerText = idUsuario;
    else {
        alert("Error: No se encontró ningun registro en proceso");
        window.location.href = "./registro.html";
    }

    formVerificar.addEventListener("submit", async function (evento) {
        evento.preventDefault();
        const codigo = document.getElementById("codigo").value;
        const datosVerificacion = {
            id: idUsuario,
            codigo: codigo
        };
        try {
            const respuesta = await fetch("/api/verificar", {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosVerificacion)
            });
            if (respuesta.ok) {
                alert("Verificacion exitosa. Ya puedes iniciar sesion.");
                localStorage.removeItem("idPendiente");
                window.location.href = "./index.html";
            } else {
                alert("Código de verificación incorrecto. Intenta nuevamente.");
            }
        } catch (error) {
            console.error("Error de conexion:", error);
            alert("No se pudo conectar con el servidor. Intenta nuevamente mas tarde.");
        }
    });
}

if (formRecuperar) {
    formRecuperar.addEventListener("submit", async function (evento) {
        evento.preventDefault();
        const idUsuario = document.getElementById("idUsuario").value;
        const datosRecuperacion = {
            id: idUsuario
        };
        try {
            const respuesta = await fetch("/api/solicitar-recuperacion", {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosRecuperacion)
            });
            if (respuesta.ok) {
                alert("Solicitud de recuperación enviada.");
                localStorage.setItem("idRecuperacion", idUsuario);
                window.location.href = "./cambiar-pwd.html";
            } else {
                alert("Error al solicitar recuperación. Verifica el usuario e intenta nuevamente.");
            }
        } catch (error) {
            console.error("Error de conexion:", error);
            alert("No se pudo conectar con el servidor. Intenta nuevamente mas tarde.");
        }
    });
}

if (formCambiarPwd) {
    const idUsuario = localStorage.getItem("idRecuperacion");
    if (idUsuario) document.getElementById("usuarioRecuperacion").innerText = idUsuario;
    else {
        alert("Error: No hay solicitud de recuperación");
        window.location.href = "./recuperar.html";
    }
    formCambiarPwd.addEventListener("submit", async function (evento) {
        evento.preventDefault();
        const codigo = document.getElementById("codigo").value;
        const nuevaPwd = document.getElementById("nuevaPwd").value;
        const nuevaPwd2 = document.getElementById("nuevaPwd2").value;

        if (nuevaPwd != nuevaPwd2) {
            alert("Las contraseñas no coinciden. Verificalas por favor");
            return;
        }

        const datosCamio = {
            id: idUsuario,
            codigo: codigo,
            nuevaPwd: nuevaPwd
        };

        try {
            const respuesta = await fetch("/api/cambiar-pwd", {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datosCamio)
            });
            if (respuesta.ok) {
                alert("Contraseña cambiada exitosamente. Ya puedes iniciar sesión.");
                localStorage.removeItem("idRecuperacion");
                window.location.href = "./index.html";
            } else {
                alert("Error al cambiar contraseña. Verifica el código e intenta nuevamente.");
            }
        } catch (error) {
            console.error("Error de conexion:", error);
            alert("No se pudo conectar con el servidor. Intenta nuevamente mas tarde.");
        }

    });
}

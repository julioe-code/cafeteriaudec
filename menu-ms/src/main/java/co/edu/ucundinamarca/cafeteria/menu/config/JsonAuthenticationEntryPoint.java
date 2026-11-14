package co.edu.ucundinamarca.cafeteria.menu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Responde 401 en JSON sin la cabecera WWW-Authenticate, para que el navegador
 * no muestre su ventana de usuario/contraseña y el login lo maneje la página admin.
 * El mensaje es el mismo si falla el usuario o la contraseña (no revela cuál existe).
 */
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"Usuario o contraseña incorrectos\"}");
    }
}

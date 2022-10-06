package CatBank.Security.JasonWebToken;


import CatBank.Security.Configuration.CustomUserDetails;
import CatBank.Security.DTO.MensajeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;


/**
 * Clase que genera el token y valida que este bien fornamdo y no este expirado
 */
@Component
public class JwtProvider {

    // Implementamos un logger para ver cual metodo da error en caso de falla
    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    //Valores que tenemos en el aplicattion.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return Jwts.builder().setSubject(customUserDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    //subject --> Nombre del usuario
    public String getUserNameFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Token mal formado");
            new ResponseEntity<>(new MensajeDTO("El Token está mal formado o contiene errores"), HttpStatus.BAD_REQUEST);
        }catch (UnsupportedJwtException e){
            logger.error("Token no soportado");
            new ResponseEntity<>(new MensajeDTO("El Token no soportado"), HttpStatus.BAD_REQUEST);
        }catch (ExpiredJwtException e){
            logger.error("Token expirado");
            new ResponseEntity<>(new MensajeDTO("El Token ha expirado"), HttpStatus.BAD_REQUEST);
        }catch (IllegalArgumentException e){
            logger.error("Token vacio");
            new ResponseEntity<>(new MensajeDTO("El Token está vacío"), HttpStatus.BAD_REQUEST);
        }catch (SignatureException e){
            logger.error("Fallo con la firma");
            new ResponseEntity<>(new MensajeDTO("Error en la firma del Token"), HttpStatus.BAD_REQUEST);
        }
        return false;
    }



}

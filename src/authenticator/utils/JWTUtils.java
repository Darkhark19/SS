package authenticator.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

class JWTSecrets{
    public static final String PASSPHRASE = "p54xoyMev6MH3jJuf85HjuXMB401tUqV9PoF048u";
    public static final String SUBJECT = "JSON Web Token for SegSoft 2022/2023";
    public static final int VALIDITY = 1000 * 60 * 10 ; // 10 minutes in milliseconds

}
public class JWTUtils {

    public static byte[] getPassphraseEncoded(){
        return DatatypeConverter.parseBase64Binary(JWTSecrets.PASSPHRASE);
    }
    public static String createJWT(String id, String issuer) {
        //The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //sign JWT with ApiKey secret
        byte[] apiKeySecretBytes = getPassphraseEncoded();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //Set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setClaims(null)
                .setSubject(JWTSecrets.SUBJECT)
                .setIssuer(issuer)
                .setExpiration(new Date(System.currentTimeMillis() + JWTSecrets.VALIDITY))
                .signWith(signatureAlgorithm, signingKey);
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    public static void parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(getPassphraseEncoded())
                .parseClaimsJws(jwt).getBody();
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
    }
}

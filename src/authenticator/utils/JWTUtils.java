package authenticator.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;

public class JWTUtils {

    private static final String PASSPHRASE = "322ffdcd16d50546568368e50a10110c7320448b3d59b23d27f1fd14e881d9f6";
    private static final String ISSUER = "authenticator-project.fct.unl.pt";
    private static final int VALIDITY = 1000 * 60 * 10 ; // 10 minutes in milliseconds
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    public static String createJWT(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY))
                .signWith(new SecretKeySpec(getPassphraseEncoded(), SIGNATURE_ALGORITHM.getJcaName()), SIGNATURE_ALGORITHM)
                .compact();
    }


    /**
     * Parses the JWT and returns the username
     * @param jwt the JWT
     * @return username or null if invalid token or expired
     */
    public static String parseJWT(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getPassphraseEncoded())
                    .build()
                    .parseClaimsJws(jwt).getBody();
            return claims.getSubject();  // returns the username
        }
        catch (JwtException e) {
            return null;   // invalid token or expired
        }
    }

    private static byte[] getPassphraseEncoded(){
        return DatatypeConverter.parseBase64Binary(PASSPHRASE);
    }
}

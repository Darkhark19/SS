package authenticator.utils;

import authorization.Capability;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.Operation;
import models.Resource;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

public class JWTUtils {

    private static final String PASSPHRASE = "322ffdcd16d50546568368e50a10110c7320448b3d59b23d27f1fd14e881d9f6";
    private static final String ISSUER = "authenticator-project.fct.unl.pt";
    public static final String SUBJECT = "JSON Web Token for SegSoft 2022/2023";
    public static final String SUBJECT_CAPABILITIES = "JSON Capabilities Token for SegSoft 2022/2023";
    private static final int VALIDITY = 1000 * 60 * 10 ; // 10 minutes in milliseconds
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private static final String CAPABILITY = "capability";
    private static final String USERNAME = "username";
    public static final String JWT ="jwt";
    public static String createJWT(String username,String id) {
        Map<String,Object> claims = new HashMap<>();
        claims.put(USERNAME, username);
        return Jwts.builder()
                .setId(id)
                .setSubject(SUBJECT)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY))
                .signWith(new SecretKeySpec(getPassphraseEncoded(), SIGNATURE_ALGORITHM.getJcaName()), SIGNATURE_ALGORITHM)
                .compact();
    }

    public static String createJWTPermissions(String username, String id, Capability cap){
        Map<String, Object> claims = new HashMap<>();
        claims.put(USERNAME, username);
        claims.put(CAPABILITY, cap);
        return Jwts.builder()
                .setId(id)
                .setSubject(SUBJECT_CAPABILITIES)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY))
                .signWith(new SecretKeySpec(getPassphraseEncoded(), SIGNATURE_ALGORITHM.getJcaName()), SIGNATURE_ALGORITHM)
                .compact();
    }

    /**
     * Parses the JWT and returns the username
     * @param jwt the JWT
     * @return username or null if invalid token or expired
     */
    public static String parseJWT(String jwt, String id) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getPassphraseEncoded())
                    .build()
                    .parseClaimsJws(jwt).getBody();
            Date exp = claims.getExpiration();
            if(exp == null || exp.before(new Date())) {
                System.out.println("Expired token");
                throw new JwtException("Expired JWT token");
            }else if(!claims.getIssuer().equals(ISSUER)) {
                System.out.println("Invalid issuer");
                throw new JwtException("Invalid JWT issuer");
            }else if(!claims.getId().equals(id)) {
                System.out.println("Invalid id");
                throw new JwtException("Invalid JWT id");
            } else {
                return claims.get("username").toString();  // returns the username
            }
        }
        catch (JwtException e) {
            e.printStackTrace();
            return null;// invalid token or expired
        }
    }
    public static Capability parseCapabilityJWT(String jwt, String id,String username) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getPassphraseEncoded())
                    .build()
                    .parseClaimsJws(jwt).getBody();
            Date exp = claims.getExpiration();
            if(exp == null || exp.before(new Date())) {
                System.out.println("Expired token");
                throw new JwtException("Expired JWT token");
            }else if(!claims.getIssuer().equals(ISSUER)) {
                System.out.println("Invalid issuer");
                throw new JwtException("Invalid JWT issuer");
            }else if(!claims.getId().equals(id)) {
                System.out.println("Invalid id");
                throw new JwtException("Invalid JWT id");
            } else if(!claims.get("username").toString().equals(username)){
                System.out.println("Invalid username");
                throw new JwtException("Invalid JWT username");
            }else{
                return mapToCapability( (LinkedHashMap<String,?>) claims.get(CAPABILITY));  // returns the capability
            }
        }
        catch (JwtException e) {
            e.printStackTrace();
            return null;// invalid token or expired
        }
    }

    private static Capability mapToCapability(LinkedHashMap<String,?> claim) {
        Operation operation = Operation.valueOf(claim.get("operation").toString());
        Resource res = Resource.valueOf(claim.get("resource").toString());
        Date expiration = new Date( (Long)claim.get("expireTime"));
        /*String resource = value.get("resource");
        List<String> operations = (List<String>) map.get("operations");
        Date expiration = (Date) map.get("expireTime");
        result.add(new Capability(Resource.valueOf(resource), mapToOperationList(operations), expiration))*/
        Capability cap = new Capability(claim.get("username").toString());
        cap.setOperation(operation);
        cap.setResource(res);
        cap.setExpireTime(expiration);
        return cap;
    }

    private static byte[] getPassphraseEncoded(){
        return DatatypeConverter.parseBase64Binary(PASSPHRASE);
    }
}

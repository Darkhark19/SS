package authenticator;

import authenticator.utils.PasswordUtils;
import database.DatabaseOperator;
import database.exceptions.AccountNotFountException;
import database.exceptions.NameAlreadyExists;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.Account;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.sql.SQLException;
import java.util.Date;

public class AuthenticatorClass implements Authenticator {


    private static final int VALIDITY = 1000 * 60 * 10;
    private final DatabaseOperator db;

    public AuthenticatorClass() {
        this.db = new DatabaseOperator();
    }

    @Override
    public void createAccount(String name, String pwd1, String pwd2) throws NameAlreadyExists, RuntimeException {
        if (!pwd1.equals(pwd2)) {
            throw new RuntimeException("Passwords do not match");
        }
        try {
            if (accountExists(name)) {
                throw new NameAlreadyExists();
            }
            db.createAccount(name, pwd1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAccount(String name) throws AccountNotFountException, RuntimeException {
        try {
            Account account = db.getAccount(name);
            if (account == null)
                throw new AccountNotFountException();
            if (account.isLoggedIn() || !account.isLocked())
                throw new RuntimeException("Cannot delete account");
            db.deleteAccount(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account getAccount(String username) throws AccountNotFountException {
        try {
            Account account = db.getAccount(username);
            if (account == null)
                throw new AccountNotFountException();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void changePwd(String name, String pwd1, String pwd2) {

    }

    @Override
    public Account login(String name, String pwd) {
        return null;
    }

    @Override
    public void logout(Account acc) {
    }

    @Override
    public Account login(HttpServletRequest req, HttpServletResponse resp) {
        return null;
    }

    // check if account with given name exists
    public boolean accountExists(String name) {
        try {
            Account account = db.getAccount(name);
            if (account != null) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    private String createJWT(String id, String issuer, String subject) {
        //The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //sign JWT with ApiKey secret
        byte[] apiKeySecretBytes =PasswordUtils.getPassphraseEncoded();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //Set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id).setSubject(subject).setIssuer(issuer)
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY))
                .signWith(signatureAlgorithm, signingKey);
        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    private void parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(PasswordUtils.getPassphraseEncoded())
                .parseClaimsJws(jwt).getBody();
        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
    }

}

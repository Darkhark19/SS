package authenticator;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class CustomRealm extends RealmBase {
    private String username;
    private String password;

    @Override
    public Principal authenticate(String username, String credentials) {

        this.username = username;
        this.password = credentials;
        /* authentication just check the username and password is same*/
        if (this.username.equals(this.password)) {
            return getPrincipal(username);
        }else{
            return null;
        }
    }

    @Override
    protected String getPassword(String username) {
        return password;
    }
    @Override
    protected Principal getPrincipal(String string) {
        List<String> roles = new ArrayList<String>();
        roles.add("TomcatAdmin");  // Adding role "TomcatAdmin" role to the user
        Principal principal = new GenericPrincipal(username, password,roles);
        return principal;
    }

}

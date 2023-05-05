package authenticator;

public interface LogManager {

    void authenticated(String operation, String account, String name);
}

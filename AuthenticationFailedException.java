/**
 * Thrown when login credentials do not match any staff record.
 */
public class AuthenticationFailedException extends HospitalException {
    public AuthenticationFailedException(String username) {
        super("Authentication failed for user: '" + username + "'. Invalid credentials.");
    }
}


package edu.pw_hashing;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean check(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}

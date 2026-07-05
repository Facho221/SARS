package sars;

import org.mindrot.jbcrypt.BCrypt;

public class GenerarHash {
    public static void main(String[] args) {
        System.out.println("sars2026:  " + BCrypt.hashpw("sars2026", BCrypt.gensalt()));
        System.out.println("admin2026: " + BCrypt.hashpw("admin2026", BCrypt.gensalt()));
    }
}
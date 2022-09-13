package com.example.cloudst.server.hash_password;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Hash {

    public String hash(char[] password, byte[] salt) {
        byte[] hash = pbkdf2(password,salt);;
        System.arraycopy(salt,0,hash,0,salt.length);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return enc.encodeToString(hash);
    }

    private byte[] pbkdf2(char[] password, byte[] salt) {
        KeySpec keySpec = new PBEKeySpec(password,salt,65636, 128);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            return secretKeyFactory.generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}

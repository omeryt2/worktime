package eu.vranckaert.worktime.security.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

import eu.vranckaert.worktime.security.exception.PasswordLenghtInvalidException;

public class Password {
	// The higher the number of iterations the more 
    // expensive computing the hash is for us
    // and also for a brute force attack.
    private static final int iterations = 10*1024;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;
    
    /**
     * Validate if the provided password is valid.
     * @param password The password.
     * @throws PasswordLenghtInvalidException If the password is less than six
     * characters or more then 30.
     */
	public static void validatePassword(String password) throws PasswordLenghtInvalidException {
		if (password.length() < 6 || password.length() > 30) {
			throw new PasswordLenghtInvalidException();
		}
	}
	
	/**
	 * Validate if the provided password is valid.
     * @param password The password.
     * @return {@link Boolean#FALSE} if the password is less than six characters
     * or more then 30. Otherwise {@link Boolean#TRUE}.
	 */
	public static boolean validatePasswordCheck(String password) {
		try {
			Password.validatePassword(password);
		} catch (PasswordLenghtInvalidException e) {
			return false;
		}
		
		return true;
	}

    /**
     * Computes a salted PBKDF2 hash of given plaintext password suitable for 
     * storing in a database.
     * @param password The password in plain text.
     * @return The hashed (and salted) password that can be stored in a DB.
     * @throws Exception An exception if anything goes wrong.
     */
    public static String getSaltedHash(String password) {
        byte[] salt;
		try {
			salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks whether given plaintext password corresponds to a stored salted 
     * hash of the password.
     * @param password The password in plain text. 
     * @param stored The stored hash.
     * @return {@link Boolean#TRUE} if the password matches with the hash. 
     * {@link Boolean#FALSE} otherwise.
     * @throws Exception An exception if anything goes wrong.
     */
    public static boolean check(String password, String stored) {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2)
            return false;
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private static String hash(String password, byte[] salt) {
        SecretKeyFactory f;
        SecretKey key = null;
		try {
			f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			key = f.generateSecret(new PBEKeySpec(
				    password.toCharArray(), salt, iterations, desiredKeyLen)
				);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (InvalidKeySpecException e) {
			return null;
		}
        return Base64.encodeBase64String(key.getEncoded());
    }
}

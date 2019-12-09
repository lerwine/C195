package scheduler;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Contains a password hashed intended to be stored (in string format) in the database.
 * @author Leonard T. Erwine
 */
public class PwHash {
    /**
     * The length of the password cipher hash as stored (minus salt).
     */
    public static final int HASH_LENGTH = 31;
    
    /**
    * Iteration count for generating the password cipher hash.
    * A higher number makes it less suseptible to brute force attacks.
    */
    public static final int CIPHER_ITERATION_COUNT = 1024;
    /**
     * The length of the salt sequence used in generating the password cipher.
     */
    public static final int SALT_LENGTH = 6;
    
    /**
     * The length of the salt sequence when base-64 encoded.
     */
    public static final int SALT_STRING_LENGTH = 8;
    
    /**
     * The length of the password hash when when base-64 encoded.
     */
    public static final int HASHED_STRING_LENGTH = 50;
    
    private byte[] salt;
    private byte[] hash;
    
    /**
     * Constructs a new password hash.
     * @param password      The password or a string containing the salt and password hash.
     * @param isRawPassword Indicates whether the password string is a new password or an encoded password hash string.
     * @throws InvalidArgumentException
     */
    public PwHash(String password, boolean isRawPassword) throws InvalidArgumentException {
        // It would make no sense to create a hash with zero bytes.
        if (password == null || password.length() == 0)
            throw new InvalidArgumentException("password", "Password cannot be empty.");
        if (isRawPassword) {
            try {
                // Create new random salt sequence.
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                salt = new byte[SALT_LENGTH];
                random.nextBytes(salt);
                // Generate new hash of the raw password.
                PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, CIPHER_ITERATION_COUNT, HASH_LENGTH * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                hash = skf.generateSecret(spec).getEncoded();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                // This should never occur unless there is a typo or invalid constant in the code.
                Logger.getLogger(PwHash.class.getName()).log(Level.SEVERE, null, ex);
                throw new InternalException("Unexpected error generating hash", ex);
            }
            return;
        }
        
        // Formatted password hash strings will always be twice the length of the salt and key lengths combined.
        if (password.length() != HASHED_STRING_LENGTH)
            throw new InvalidArgumentException("password", "Invalid salt and hash sequence.");
        
        Base64.Decoder dec = Base64.getDecoder();
        // Decode the salt bytes.
        salt = dec.decode(password.substring(0, SALT_STRING_LENGTH));
        // Decode the password hash bytes.
        hash = dec.decode(password.substring(SALT_STRING_LENGTH) + "==");
    }
    
    /**
     * Gets the salt and password hash as a sequence of hexadecimal character pairs.
     * @return The salt and password hash as a sequence of hexadecimal character pairs.
     */
    @Override
    public String toString() {
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(salt) + enc.encodeToString(hash).substring(0, 42);
    }
    
    /**
     * Determines whether the hash for the specified password matches the current password hash.
     * This uses the current salt sequence to generate a hash from the specified password and then
     * compares the resulting bytes with the current hash.
     * @param password  The password to check.
     * @return  {@code true} if the hash of the specified password matches the current hash; otherwise, {@code false}.
     */
    public boolean test(String password) {
        if (password == null || password.length() == 0)
            return false;
        
        try {
            // Generate new hash of the raw password.
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, CIPHER_ITERATION_COUNT, HASH_LENGTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] h = skf.generateSecret(spec).getEncoded();
            if (h.length == hash.length) {
                for (int i = 0; i < h.length; i++) {
                    if (h[i] != hash[i])
                        return false;
                }
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // This should never occur unless there is a typo or invalid constant in the code.
            Logger.getLogger(PwHash.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalException("Unexpected error generating hash", ex);
        }
        return false;
    }
}

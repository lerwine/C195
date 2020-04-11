package scheduler.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import scheduler.observables.NonNullableStringProperty;

/**
 * Contains a password hashed intended to be stored (in string format) in the database.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class PwHash {

    private static final Logger LOG = Logger.getLogger(PwHash.class.getName());

    /**
     * The length of the password cipher hash as stored (minus salt).
     */
    public static final int HASH_LENGTH = 31;

    /**
     * Iteration count for generating the password cipher hash. A higher number makes it less suseptible to brute force attacks.
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

    private final NonNullableStringProperty encodedHash;

    public String getEncodedHash() {
        return encodedHash.get();
    }

    public void setEncodedHash(String value) {
        encodedHash.set(value);
    }

    public StringProperty encodedHashProperty() {
        return encodedHash;
    }

    private final ReadOnlyObjectWrapper<ObservableByteArrayList> salt;

    public ObservableByteArrayList getSalt() {
        return salt.get();
    }

    public ReadOnlyObjectProperty<ObservableByteArrayList> saltProperty() {
        return salt.getReadOnlyProperty();
    }

    private final ReadOnlyObjectWrapper<ObservableByteArrayList> hash;

    public ObservableByteArrayList getHash() {
        return hash.get();
    }

    public ReadOnlyObjectProperty<ObservableByteArrayList> hashProperty() {
        return hash.getReadOnlyProperty();
    }

    private final ReadOnlyBooleanWrapper valid;

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    private static boolean test(String password, byte[] salt, byte[] hash) {
        if (salt == null || hash == null || password == null || password.isEmpty()) {
            return false;
        }
        try {
            // Generate new hash of the raw password.
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, CIPHER_ITERATION_COUNT, HASH_LENGTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] h = skf.generateSecret(spec).getEncoded();
            if (h.length == hash.length) {
                for (int i = 0; i < h.length; i++) {
                    if (h[i] != hash[i]) {
                        return false;
                    }
                }
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // This should never occur unless there is a typo or invalid constant in the code.
            LOG.log(Level.SEVERE, "Unexpected failure", ex);
            throw new InternalException("Unexpected error generating hash", ex);
        }

        return false;
    }

    public void setFromRawPassword(String password) {
        if (password.isEmpty()) {
            salt.set(null);
            hash.set(null);
            encodedHash.set("");
            return;
        }
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            // Create new random salt sequence.
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte[] sb = new byte[SALT_LENGTH];
            random.nextBytes(sb);
            salt.set(new ObservableByteArrayList(sb));
            // Generate new hash of the raw password.
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sb, CIPHER_ITERATION_COUNT, HASH_LENGTH * 8);
            byte[] hb = skf.generateSecret(spec).getEncoded();
            hash.set(new ObservableByteArrayList(hb));
            Base64.Encoder enc = Base64.getEncoder();
            encodedHash.set((enc.encodeToString(sb) + enc.encodeToString(hb)).substring(0, HASHED_STRING_LENGTH));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // This should never occur unless there is a typo or invalid constant in the code.
            LOG.log(Level.SEVERE, "Unexpected failure", ex);
            throw new InternalException("Unexpected error generating hash", ex);
        }
    }

    /**
     * Constructs a new password hash.
     *
     * @param password The password or a string containing the salt and password hash.
     * @param isRawPassword Indicates whether the password string is a new password or an encoded password hash string.
     */
    public PwHash(String password, boolean isRawPassword) {
        valid = new ReadOnlyBooleanWrapper(false);
        encodedHash = new NonNullableStringProperty();
        salt = new ReadOnlyObjectWrapper<>(null);
        hash = new ReadOnlyObjectWrapper<>(null);
        encodedHash.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.length() == HASHED_STRING_LENGTH) {
                Base64.Decoder dec = Base64.getDecoder();
                try {
                    byte[] saltBytes = dec.decode(newValue.substring(0, SALT_STRING_LENGTH));
                    byte[] hashBytes = dec.decode(newValue.substring(SALT_STRING_LENGTH) + "==");
                    ObservableByteArrayList b = salt.get();
                    if (null != b) {
                        boolean notChanged = true;
                        for (int i = 0; i < SALT_LENGTH; i++) {
                            if (saltBytes[i] != b.get(i)) {
                                notChanged = false;
                                break;
                            }
                        }
                        if (notChanged) {
                            b = hash.get();
                            for (int i = 0; i < hashBytes.length; i++) {
                                if (hashBytes[i] != b.get(i)) {
                                    notChanged = false;
                                    break;
                                }
                            }
                            if (notChanged) {
                                return;
                            }
                        }
                    }
                    salt.set(new ObservableByteArrayList(saltBytes));
                    hash.set(new ObservableByteArrayList(hashBytes));
                    valid.set(true);
                    return;
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error decoding hash", ex);
                }
            }
            salt.set(null);
            hash.set(null);
            valid.set(false);
        });
        if (isRawPassword) {
            setFromRawPassword(password);
        } else {
            encodedHash.set(password);
        }
    }

    /**
     * Gets the salt and password hash as a sequence of hexadecimal character pairs.
     *
     * @return The salt and password hash as a sequence of hexadecimal character pairs.
     */
    @Override
    public String toString() {
        return encodedHash.get();
    }

    /**
     * Determines whether the hash for the specified password matches the current password hash. This uses the current salt sequence to generate a hash from the specified password
     * and then compares the resulting bytes with the current hash.
     *
     * @param password The password to check.
     * @return {@code true} if the hash of the specified password matches the current hash; otherwise, {@code false}.
     */
    public boolean test(String password) {
        ObservableByteArrayList s = salt.get();
        if (s == null) {
            return false;
        }
        ObservableByteArrayList h = hash.get();
        return h != null && test(password, s.bytes, h.bytes);
    }

    public class ObservableByteArrayList extends javafx.collections.ObservableListBase<Byte> {

        private final byte[] bytes;

        public ObservableByteArrayList(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public Byte get(int index) {
            return bytes[index];
        }

        @Override
        public int size() {
            return bytes.length;
        }
    }
}

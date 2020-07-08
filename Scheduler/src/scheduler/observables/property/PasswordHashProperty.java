package scheduler.observables.property;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import scheduler.util.InternalException;

/**
 * A property containing the {@link Base64}-encoded bytes of a password salt and cryptographic hash.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class PasswordHashProperty extends SimpleStringProperty {

    private static final Logger LOG = Logger.getLogger(PasswordHashProperty.class.getName());

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
     * The length of the password hash when base-64 encoded.
     */
    public static final int HASHED_STRING_LENGTH = 50;

    public static boolean test(String password, byte[] salt, List<Byte> hash) {
        if (hash == null || hash.isEmpty()) {
            return (null == password || password.isEmpty()) && (null == salt || salt.length == 0 || salt.length == SALT_LENGTH);
        }
        if (null == password || password.isEmpty() || hash.size() != HASH_LENGTH || salt.length != SALT_LENGTH) {
            return false;
        }
        try {
            // Generate new hash of the raw password.
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, CIPHER_ITERATION_COUNT, HASH_LENGTH * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] h = skf.generateSecret(spec).getEncoded();
            if (h.length == HASH_LENGTH) {
                Iterator<Byte> iterator = hash.iterator();
                for (int i = 0; i < HASH_LENGTH; i++) {
                    if (h[i] != iterator.next()) {
                        return false;
                    }
                }
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // This should never occur unless there is a typo or invalid constant in the code.
            LOG.log(Level.SEVERE, "Unexpected failure", ex);
        }

        return false;
    }

    private final ObservableList<Byte> backingSaltBytes;
    private final ReadOnlyListWrapper<Byte> saltBytes;
    private final ObservableList<Byte> backingHashBytes;
    private final ReadOnlyListWrapper<Byte> hashBytes;
    private final ReadOnlyBooleanWrapper valid;

    public PasswordHashProperty() {
        this("");
    }

    public PasswordHashProperty(String initialValue) {
        this(null, "", initialValue.trim());

    }

    public PasswordHashProperty(Object bean, String name) {
        this(bean, name, "");
    }

    public PasswordHashProperty(Object bean, String name, String initialValue) {
        super(bean, name, (null == initialValue) ? "" : initialValue.trim());
        backingSaltBytes = FXCollections.<Byte>observableArrayList();
        saltBytes = new ReadOnlyListWrapper<>(FXCollections.unmodifiableObservableList(backingSaltBytes));
        backingHashBytes = FXCollections.<Byte>observableArrayList();
        hashBytes = new ReadOnlyListWrapper<>(FXCollections.unmodifiableObservableList(backingHashBytes));
        valid = new ReadOnlyBooleanWrapper();

        super.addListener((observable, oldValue, newValue) -> {
            if (null == oldValue || !oldValue.equals(newValue)) {
                onEncodedDataChanged(newValue);
            }
        });
        onEncodedDataChanged(super.get());
    }

    public ObservableList<Byte> getSaltBytes() {
        return saltBytes.get();
    }

    public ReadOnlyListProperty<Byte> saltBytesProperty() {
        return saltBytes.getReadOnlyProperty();
    }

    public ObservableList<Byte> getHashBytes() {
        return hashBytes.get();
    }

    public ReadOnlyListProperty<Byte> hashBytesProperty() {
        return hashBytes.getReadOnlyProperty();
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyProperty();
    }

    @Override
    public void set(String newValue) {
        super.set((null == newValue) ? "" : newValue.trim());
    }

    private void onEncodedDataChanged(String data) {
        if (data.isEmpty()) {
            if (!backingHashBytes.isEmpty()) {
                backingHashBytes.clear();
            }
            valid.set(true);
            return;
        }
        if (data.length() == HASHED_STRING_LENGTH) {
            Base64.Decoder dec = Base64.getDecoder();
            try {
                byte[] s = dec.decode(data.substring(0, SALT_STRING_LENGTH));
                byte[] h = dec.decode(data.substring(SALT_STRING_LENGTH) + "==");
                if (backingSaltBytes.isEmpty()) {
                    for (byte b : s) {
                        backingSaltBytes.add(b);
                    }
                } else {
                    for (int i = 0; i < SALT_LENGTH; i++) {
                        backingSaltBytes.set(i, s[i]);
                    }
                    if (!backingHashBytes.isEmpty()) {
                        for (int i = 0; i < HASH_LENGTH; i++) {
                            backingHashBytes.set(i, h[i]);
                        }
                        valid.set(true);
                        return;
                    }
                }
                for (byte b : h) {
                    backingHashBytes.add(b);
                }
                valid.set(true);
                return;
            } catch (IllegalArgumentException ex) {
                LOG.log(Level.WARNING, "Error decoding hash", ex);
            }
        }
        backingHashBytes.clear();
        valid.set(false);
    }

    public void setFromRawPassword(String password, boolean useSameSalt) {
        if (null == password || password.isEmpty()) {
            set("");
        } else {
            try {
                byte[] sb = new byte[SALT_LENGTH];
                if (useSameSalt && !saltBytes.isEmpty()) {
                    for (int i = 0; i < SALT_LENGTH; i++) {
                        sb[i] = saltBytes.get(i);
                    }
                } else {
                    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                    random.nextBytes(sb);
                }
                // Generate new hash of the raw password.
                PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sb, CIPHER_ITERATION_COUNT, HASH_LENGTH * 8);
                SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] hb = skf.generateSecret(spec).getEncoded();
                Base64.Encoder enc = Base64.getEncoder();
                set((enc.encodeToString(sb) + enc.encodeToString(hb)).substring(0, HASHED_STRING_LENGTH));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                // This should never occur unless there is a typo or invalid constant in the code.
                LOG.log(Level.SEVERE, "Unexpected failure", ex);
                throw new InternalException("Unexpected error generating hash", ex);
            }
        }
    }

    /**
     * Determines whether the hash for the specified raw password matches the current password hash. This uses the current salt sequence to generate a
     * hash from the specified password and then compares the resulting bytes with the current hash.
     *
     * @param password The raw password to check.
     * @return {@code true} if the hash of the specified password matches the current hash; otherwise, {@code false}.
     */
    public boolean test(String password) {
        if (null == password || password.isEmpty()) {
            return backingHashBytes.isEmpty();
        }
        if (backingHashBytes.isEmpty()) {
            return false;
        }

        byte[] s = new byte[SALT_LENGTH];
        for (int i = 0; i < SALT_LENGTH; i++) {
            s[i] = backingSaltBytes.get(i);
        }
        return test(password, s, backingHashBytes);
    }

}

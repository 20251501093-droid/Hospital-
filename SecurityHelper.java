import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * RSA-2048 Encryption Helper.
 *
 * Fixed from original:
 * - FileInputStream now properly closed via try-with-resources (resource leak fix)
 * - Uses StandardCharsets.UTF_8 for portable encoding
 * - Static utility class (all methods static) — consistent with original design
 */
public class SecurityHelper {
    private static final String ALGORITHM = "RSA";
    private static final String PUBLIC_KEY_FILE = "public.key";
    private static final String PRIVATE_KEY_FILE = "private.key";

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    // Static initializer — executes once when the class is first loaded
    static {
        try {
            if (new File(PUBLIC_KEY_FILE).exists() && new File(PRIVATE_KEY_FILE).exists()) {
                loadKeys();
            } else {
                generateAndSaveKeys();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RSA security: " + e.getMessage(), e);
        }
    }

    private static void generateAndSaveKeys() throws Exception {
        System.out.println("[Security] First boot detected. Generating RSA-2048 key pair...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();

        try (FileOutputStream fos = new FileOutputStream(PUBLIC_KEY_FILE)) {
            fos.write(publicKey.getEncoded());
        }
        try (FileOutputStream fos = new FileOutputStream(PRIVATE_KEY_FILE)) {
            fos.write(privateKey.getEncoded());
        }
        System.out.println("[Security] RSA keys saved successfully.\n");
    }

    // FIXED: FileInputStream properly closed with try-with-resources
    private static void loadKeys() throws Exception {
        byte[] pubKeyBytes;
        try (FileInputStream fis = new FileInputStream(PUBLIC_KEY_FILE)) {
            pubKeyBytes = fis.readAllBytes();
        }
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
        publicKey = kf.generatePublic(new X509EncodedKeySpec(pubKeyBytes));

        byte[] privKeyBytes;
        try (FileInputStream fis = new FileInputStream(PRIVATE_KEY_FILE)) {
            privKeyBytes = fis.readAllBytes();
        }
        privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));
    }

    // FIXED: Uses StandardCharsets.UTF_8 for portable encoding
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }
}
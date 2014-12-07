package imag.quizz.common.tool;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 *
 */
public final class Hash {

    /**
     * Computes a md5 hash of the provided String.
     *
     * @param source the String to hash
     *
     * @return a md5 of the provided String
     */
    public static String md5(final String source) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            return new String(digest.digest(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Fatal error: can't even md5!");
        }
    }

    /**
     * Encodes a String in base64.
     *
     * @param source the String to encode
     *
     * @return an base64 encoded version of the provided String
     */
    public static String encodeBase64(final String source) {
        return Base64.getEncoder().encodeToString(source.getBytes());
    }

    /**
     * Decodes a String in base64.
     *
     * @param source the base64 encoded String
     *
     * @return the original String
     */
    public static String decodeBase64(final String source) {
        return new String(Base64.getDecoder().decode(source));
    }

    /**
     * Never instantiate this tool class.
     */
    private Hash() {
    }
}

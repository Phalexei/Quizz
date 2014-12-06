package imag.quizz.common.tool;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public final class Hash {

    /**
     * Computes a md5 hash of the provided String.
     * @param source the String to hash
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
     * Never instantiate this tool class.
     */
    private Hash() {
    }
}

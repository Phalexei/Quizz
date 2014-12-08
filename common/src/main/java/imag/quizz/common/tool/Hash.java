package imag.quizz.common.tool;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public final class Hash {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

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
            return new String(digest.digest(source.getBytes(Hash.CHARSET)), Hash.CHARSET);
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
        return new String(Base64.encodeBase64(source.getBytes(Hash.CHARSET)), Hash.CHARSET);
    }

    /**
     * Decodes a String in base64.
     *
     * @param source the base64 encoded String
     *
     * @return the original String
     */
    public static String decodeBase64(final String source) {
        return new String(Base64.decodeBase64(source), Hash.CHARSET);
    }

    /**
     * Never instantiate this tool class.
     */
    private Hash() {
    }
}

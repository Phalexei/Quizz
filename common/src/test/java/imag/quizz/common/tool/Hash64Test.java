package imag.quizz.common.tool;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Ribesg.
 */

@RunWith(Parameterized.class)
public class Hash64Test {

    private final String sourceString;

    public Hash64Test(final String sourceString) {
        this.sourceString = sourceString;
    }

    @Test
    public void testBase64() {
        final String encodedText = Hash.encodeBase64(this.sourceString);
        final String decodedText = Hash.decodeBase64(encodedText);
        Assert.assertEquals(this.sourceString, decodedText);
    }

    @Test
    public void testMd5Base64() {
        final String md5 = Hash.md5(this.sourceString);
        final String encodedText = Hash.encodeBase64(md5);
        final String decodedText = Hash.decodeBase64(encodedText);
        Assert.assertEquals(md5, decodedText);
    }

    @Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> data = new LinkedList<>();
        for (final String sourceString : new String[]{
                "Test",
                "Tom",
                "abcdefghijklmnopqrstuvwxyz0123456789",
                "KEK",
                "42",
                "ENSIMAG"
        }) {
            data.add(new Object[]{
                    sourceString
            });
        }
        return data;
    }
}

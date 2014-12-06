package imag.quizz.common.protocol;

import org.apache.commons.lang3.Validate;

/**
 * Separators used to separate different groups/records/units in protocol
 * messages. Default separator is {@link #LEVEL_1}.
 */
public final class Separator {

    /**
     * File Separator ASCII code
     */
    public static final char LEVEL_1 = 0x1C;

    /**
     * String representation of {@link #LEVEL_1}.
     */
    public static final String LEVEL_1S = Character.toString(Separator.LEVEL_1);

    /**
     * Group Separator ASCII code
     */
    public static final char LEVEL_2 = 0x1D;

    /**
     * String representation of {@link #LEVEL_2}.
     */
    public static final String LEVEL_2S = Character.toString(Separator.LEVEL_2);

    /**
     * Record Separator ASCII code
     */
    public static final char LEVEL_3 = 0x1E;

    /**
     * String representation of {@link #LEVEL_3}.
     */
    public static final String LEVEL_3S = Character.toString(Separator.LEVEL_3);

    /**
     * Unit Separator ASCII code
     */
    public static final char LEVEL_4 = 0x1F;

    /**
     * String representation of {@link #LEVEL_4}.
     */
    public static final String LEVEL_4S = Character.toString(Separator.LEVEL_4);

    /**
     * Gets a char separator based on its level.
     *
     * @param level the level
     *
     * @return the char separator
     */
    public static char getChar(final int level) {
        Validate.inclusiveBetween(1, 4, level, "Invalid level");
        switch (level) {
            case 1:
                return Separator.LEVEL_1;
            case 2:
                return Separator.LEVEL_2;
            case 3:
                return Separator.LEVEL_3;
            case 4:
                return Separator.LEVEL_4;
            default:
                // Impossible
                return '!';
        }
    }

    /**
     * Gets a String separator based on its level.
     *
     * @param level the level
     *
     * @return the String separator
     */
    public static String getString(final int level) {
        Validate.inclusiveBetween(1, 4, level, "Invalid level");
        switch (level) {
            case 1:
                return Separator.LEVEL_1S;
            case 2:
                return Separator.LEVEL_2S;
            case 3:
                return Separator.LEVEL_3S;
            case 4:
                return Separator.LEVEL_4S;
            default:
                // Impossible
                return null;
        }
    }

    /**
     * Never instantiate this tool class.
     */
    private Separator() {
    }
}

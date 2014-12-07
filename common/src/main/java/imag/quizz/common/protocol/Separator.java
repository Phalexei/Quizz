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
    private static final char FILE_SEPARATOR = 0x1C;

    /**
     * Group Separator ASCII code
     */
    private static final char GROUP_SEPARATOR = 0x1D;

    /**
     * Record Separator ASCII code
     */
    private static final char RECORD_SEPARATOR = 0x1E;

    /**
     * Unit Separator ASCII code
     */
    private static final char UNIT_SEPARATOR = 0x1F;

    /**
     * Level 1 separator
     */
    public static final String LEVEL_1 = "" + Separator.FILE_SEPARATOR + Separator.FILE_SEPARATOR;

    /**
     * Level 2 separator
     */
    public static final String LEVEL_2 = "" + Separator.GROUP_SEPARATOR + Separator.GROUP_SEPARATOR;

    /**
     * Level 3 separator
     */
    public static final String LEVEL_3 = "" + Separator.RECORD_SEPARATOR + Separator.RECORD_SEPARATOR;

    /**
     * Level 4 separator
     */
    public static final String LEVEL_4 = "" + Separator.UNIT_SEPARATOR + Separator.UNIT_SEPARATOR;

    /**
     * Level 5 separator
     */
    public static final String LEVEL_5 = "" + Separator.FILE_SEPARATOR;

    /**
     * Level 6 separator
     */
    public static final String LEVEL_6 = "" + Separator.GROUP_SEPARATOR;

    /**
     * Level 7 separator
     */
    public static final String LEVEL_7 = "" + Separator.RECORD_SEPARATOR;

    /**
     * Level 8 separator
     */
    public static final String LEVEL_8 = "" + Separator.UNIT_SEPARATOR;

    /**
     * All separators
     */
    private static final String[] SEPARATORS = new String[]{
            Separator.LEVEL_1,
            Separator.LEVEL_2,
            Separator.LEVEL_3,
            Separator.LEVEL_4,
            Separator.LEVEL_5,
            Separator.LEVEL_6,
            Separator.LEVEL_7,
            Separator.LEVEL_8
    };

    public static final int AMOUNT = Separator.SEPARATORS.length;

    /**
     * Gets a separator based on its level.
     *
     * @param level the level
     *
     * @return the String separator
     */
    public static String get(final int level) {
        Validate.inclusiveBetween(1, Separator.AMOUNT, level, "Invalid level");
        return Separator.SEPARATORS[level - 1];
    }

    /**
     * Replaces every separator in a String by spaces.
     *
     * @param string a String
     *
     * @return the same String without separators
     */
    public static String clean(final String string) {
        String result = string;
        int i = 1;
        for (final String separator : Separator.SEPARATORS) {
            result = result.replace(separator, " |" + i++ + "| ");
        }
        return result;
    }

    /**
     * Never instantiate this tool class.
     */
    private Separator() {
    }
}

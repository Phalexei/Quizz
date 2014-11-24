package imag.quizz.common.protocol;

/**
 * Separators used to separate different groups/records/units in protocol
 * messages. Default separator is {@link #LEVEL_1}.
 */
public interface Separator {

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
}

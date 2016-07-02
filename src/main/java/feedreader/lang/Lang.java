package feedreader.lang;

public class Lang
{
    /** Available languages. */
    public enum Type { en_US, es_AR, de_DE };

    /** Default selected language. */
    public static Type langTypeDef = Type.en_US;

    /** Selected language. */
    public static Type selected = Type.en_US;

    public static String selectedModule = "default";

    /**
     * Gets the language string's representation for the given key. I.e, get("LOREM_20", "Generic") will return a
     * 20 words lorem ipsum paragraph taken from the generic translation map.
     *
     * @param languageKey
     * @param module
     * @return
     *
     */
    public static String get(String languageKey, String module)
    {
        return languageKey;
    }

    public static String get(String languageKey)
    {
        return get(languageKey, selectedModule);
    }

}

package info.datamuse.onesky;

/**
 * File format enumeration.
 */
public enum FileFormat {

    /**
     * {@code IOS_STRINGS} format.
     */
    IOS_STRINGS,

    /**
     * {@code IOS_STRINGSDICT_XML} format.
     */
    IOS_STRINGSDICT_XML,

    /**
     * {@code GNU_PO} format.
     */
    GNU_PO,

    /**
     * {@code ANDROID_XML} format.
     */
    ANDROID_XML,

    /**
     * {@code ANDROID_JSON} format.
     */
    ANDROID_JSON,

    /**
     * {@code JAVA_PROPERTIES} format.
     */
    JAVA_PROPERTIES,

    /**
     * {@code RUBY_YML} format.
     */
    RUBY_YML,

    /**
     * {@code RUBY_YAML} format.
     */
    RUBY_YAML,

    /**
     * {@code FLASH_XML} format.
     */
    FLASH_XML,

    /**
     * {@code GNU_POT} format.
     */
    GNU_POT,

    /**
     * {@code RRC} format.
     */
    RRC,

    /**
     * {@code RESX} format.
     */
    RESX,

    /**
     * {@code HIERARCHICAL_JSON} format.
     */
    HIERARCHICAL_JSON,

    /**
     * {@code PHP} format.
     */
    PHP,

    /**
     * {@code PHP_SHORT_ARRAY} format.
     */
    PHP_SHORT_ARRAY,

    /**
     * {@code PHP_VARIABLES} format.
     */
    PHP_VARIABLES,

    /**
     * {@code HTML} format.
     */
    HTML,

    /**
     * {@code RESW} format.
     */
    RESW,

    /**
     * {@code YML} format.
     */
    YML,

    /**
     * {@code YAML} format.
     */
    YAML,

    /**
     * {@code ADEMPIERE_XML} format.
     */
    ADEMPIERE_XML,

    /**
     * {@code IDEMPIERE_XML} format.
     */
    IDEMPIERE_XML,

    /**
     * {@code QT_TS_XML} format.
     */
    QT_TS_XML,

    /**
     * {@code XLIFF} format.
     */
    XLIFF,

    /**
     * {@code RESJSON} format.
     */
    RESJSON,

    /**
     * {@code TMX} format.
     */
    TMX,

    /**
     * {@code L10N} format.
     */
    L10N,

    /**
     * {@code INI} format.
     */
    INI,

    /**
     * {@code REQUIREJS} format.
     */
    REQUIREJS,

    ;

    /**
     * Returns file format name as a string.
     *
     * @return file format name string
     */
    public String getFormatString() {
        return name();
    }

}

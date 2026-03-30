package alCarmel

/**
 * Key/value configuration. Keys are defined in {@link SettingKey}; values are edited in the DB.
 */
class Setting {

    String key
    String value
    static constraints = {
        key   unique: true, blank: false
        value nullable: false, blank: true
    }

    static mapping = {
        table 'setting'
        key column: 'setting_key'
        value column: 'setting_value'
    }

    Integer toInteger() { value?.toInteger() }
    Boolean toBoolean() { value?.toBoolean() }

    String toString() { "$key = $value" }
}

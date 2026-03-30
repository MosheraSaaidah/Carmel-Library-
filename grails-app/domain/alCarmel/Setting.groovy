package alCarmel

<<<<<<< HEAD
/**
 * Key/value configuration. Keys are defined in {@link SettingKey}; values are edited in the DB.
 */
=======
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
class Setting {

    String key
    String value
    static constraints = {
<<<<<<< HEAD
        key   unique: true, blank: false
        value nullable: false, blank: true
=======
        key    unique: true, blank: false
        value  blank: false
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
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

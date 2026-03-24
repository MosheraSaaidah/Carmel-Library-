package alCarmel

class Setting {

    String key
    String value
    static constraints = {
        key    unique: true, blank: false
        value  blank: false
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

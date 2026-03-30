package alCarmel

import grails.gorm.transactions.Transactional


@Transactional
class SettingService {

    static final String SMTP_HOST = 'smtp.gmail.com'
    static final int SMTP_PORT = 587

    @Transactional(readOnly = true)
    String get(String key) {
        Setting.findByKey(key)?.value
    }

    @Transactional(readOnly = true)
    Integer getInt(String key) {
        Setting.findByKey(key)?.toInteger()
    }

    @Transactional(readOnly = true)
    Boolean getBool(String key) {
        Setting.findByKey(key)?.toBoolean()
    }

    void set(String key, String value) {
        Setting setting = Setting.findByKey(key) ?: new Setting(key: key)
        setting.value = value
        setting.save(flush: true, failOnError: true)
    }

    /** Inserts default rows only when a key is missing (e.g. first run after {@code create-drop}). */
    void initDefaults() {
        [
                (SettingKey.BORROW_DAYS_UNTIL_DUE)        : '14',
                (SettingKey.NOTIFICATION_REMINDER_DAYS)   : '1',
                (SettingKey.NOTIFICATION_DAILY_ENABLED)  : 'true',
                (SettingKey.NOTIFICATION_DAILY_CRON)      : '0 0 20 * * ?',
                (SettingKey.MAIL_USERNAME)                : 'mushera667@gmail.com',
                (SettingKey.MAIL_PASSWORD)                : 'ltxaaepnxnnxjeih',
        ].each { String mapKey, String mapValue ->
            String key = mapKey
            String value = mapValue != null ? mapValue.toString() : ''
            if (!key) {
                return
            }
            if (!Setting.findByKey(key)) {
                new Setting(key: key, value: value).save(flush: true, failOnError: true)
            }
        }
    }
}

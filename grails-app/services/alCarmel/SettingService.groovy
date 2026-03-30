package alCarmel

import grails.gorm.transactions.Transactional

<<<<<<< HEAD

@Transactional
class SettingService {

    static final String SMTP_HOST = 'smtp.gmail.com'
    static final int SMTP_PORT = 587
=======
@Transactional
class SettingService {

    static final String BORROW_DAY_UNIT_DUE = 'borrow.daysUntilDue'
    static final String NOTIF_REMINDER_DAYS_BEFORE_DUE = 'notification.reminderDaysBeforeDue'
    static final String NOTIF_DAILY_ENABLED            = 'notification.dailyScheduleEnabled'
    static final String NOTIF_DAILY_CRON               = 'notification.dailyCron'
    static final String MAIL_HOST                      = 'mail.host'
    static final String MAIL_PORT                      = 'mail.port'
    static final String MAIL_USERNAME                  = 'mail.username'
    static final String MAIL_PASSWORD                  = 'mail.password'
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e

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

<<<<<<< HEAD
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
=======
   
    void initDefaults(){
        [
                (BORROW_DAY_UNIT_DUE)           : '14' ,
                (NOTIF_REMINDER_DAYS_BEFORE_DUE): '1',
                (NOTIF_DAILY_ENABLED)           : 'true',
                (NOTIF_DAILY_CRON)              : '0 0 20 * * ?',
                (MAIL_HOST)                     : 'smtp.gmail.com',
                (MAIL_PORT)                     : '587',
                (MAIL_USERNAME)                 : 'mushera667@gmail.com',
                (MAIL_PASSWORD)                 : 'ltxaaepnxnnxjeih',
        ].each {key ,defaultValue ->
            if(!Setting.findByKey(key)){
                new Setting(key: key , value: defaultValue)
                        .save(flush: true, failOnError: true)
            }
        }
        
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
    }
}

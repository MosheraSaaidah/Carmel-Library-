package alCarmel


final class SettingKey {

    private SettingKey() {}

    static final String BORROW_DAYS_UNTIL_DUE           = 'borrow.daysUntilDue'
    static final String NOTIFICATION_REMINDER_DAYS        = 'notification.reminderDaysBeforeDue'
    static final String NOTIFICATION_DAILY_ENABLED      = 'notification.dailyScheduleEnabled'
    static final String NOTIFICATION_DAILY_CRON         = 'notification.dailyCron'

    static final String MAIL_USERNAME                   = 'mail.username'
    static final String MAIL_PASSWORD                   = 'mail.password'
}

package alCarmel

class DailyNotificationSchedulerService {

    BorrowService borrowService
    NotificationService notificationService
    SettingService settingService

    void runDailyBorrowNotifications() {
        Boolean enabled = settingService.getBool(SettingKey.NOTIFICATION_DAILY_ENABLED)
        if (enabled == Boolean.FALSE) {
            return
        }
        borrowService.updateLateBorrows()
        notificationService.sendDueDateReminders()
        notificationService.sendLateNotices()
    }
}

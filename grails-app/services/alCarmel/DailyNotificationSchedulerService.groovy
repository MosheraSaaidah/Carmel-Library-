package alCarmel

class DailyNotificationSchedulerService {

    BorrowService borrowService
    NotificationService notificationService
    SettingService settingService

    void runDailyBorrowNotifications() {
<<<<<<< HEAD
        Boolean enabled = settingService.getBool(SettingKey.NOTIFICATION_DAILY_ENABLED)
=======
        Boolean enabled = settingService.getBool(SettingService.NOTIF_DAILY_ENABLED)
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
        if (enabled == Boolean.FALSE) {
            return
        }
        borrowService.updateLateBorrows()
        notificationService.sendDueDateReminders()
        notificationService.sendLateNotices()
    }
}

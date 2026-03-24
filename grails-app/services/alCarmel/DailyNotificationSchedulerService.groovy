package alCarmel

import org.springframework.scheduling.annotation.Scheduled

/**
 * Runs due-date reminders and late notices on a fixed schedule (see application.yml:
 * carmel.notification.dailyCron, dailyScheduleEnabled).
 */
class DailyNotificationSchedulerService {

    BorrowService borrowService
    NotificationService notificationService
    NotificationClockService notificationClockService
    def grailsApplication

    @Scheduled(cron = '${carmel.notification.dailyCron:0 0 20 * * ?}')
    void runDailyBorrowNotifications() {
        Boolean enabled = grailsApplication?.config?.getProperty(
                'carmel.notification.dailyScheduleEnabled', Boolean, true)
        if (Boolean.FALSE == enabled) {
            return
        }
        // PRODUCTION (لا يوجد تاريخ افتراضي في الجلسة):
        // borrowService.updateLateBorrows()
        // notificationService.sendDueDateReminders()
        // notificationService.sendLateNotices()
        // TESTING: نفس اليوم الافتراضي للجدولة (fixedEffectiveDate) يُمرَّر صراحةً
        Date effectiveToday = notificationClockService.resolveEffectiveToday(null)
        borrowService.updateLateBorrows(null)
        notificationService.sendDueDateReminders(effectiveToday)
        notificationService.sendLateNotices(effectiveToday)
    }
}

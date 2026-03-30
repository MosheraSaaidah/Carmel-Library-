package alCarmel

import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component

import java.util.concurrent.ScheduledFuture


@Slf4j
@Component
class DailyNotificationScheduleRegistrar {

    private final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler()
    private ScheduledFuture<?> scheduledFuture

    private final DailyNotificationSchedulerService dailyNotificationSchedulerService
    private final SettingService settingService

    DailyNotificationScheduleRegistrar(
            DailyNotificationSchedulerService dailyNotificationSchedulerService,
            SettingService settingService) {
        this.dailyNotificationSchedulerService = dailyNotificationSchedulerService
        this.settingService = settingService
    }

    @PostConstruct
    void initScheduler() {
        scheduler.poolSize = 1
        scheduler.threadNamePrefix = 'carmel-daily-notif-'
        scheduler.initialize()
    }

    @PreDestroy
    void shutdown() {
        cancelJob()
        scheduler.shutdown()
    }

    @EventListener(ApplicationReadyEvent)
    void onApplicationReady() {
        reschedule()
    }

    /**
     * Call after changing {@code notification.dailyCron} or {@code notification.dailyScheduleEnabled} in the DB
     * if you want the new values without restarting the app.
     */
    void reschedule() {
        cancelJob()
<<<<<<< HEAD
        Boolean enabled = settingService.getBool(SettingKey.NOTIFICATION_DAILY_ENABLED)
=======
        Boolean enabled = settingService.getBool(SettingService.NOTIF_DAILY_ENABLED)
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
        if (enabled == Boolean.FALSE) {
            log.info('Daily notification schedule is disabled (notification.dailyScheduleEnabled).')
            return
        }
<<<<<<< HEAD
        String cron = settingService.get(SettingKey.NOTIFICATION_DAILY_CRON) ?: '0 0 20 * * ?'
=======
        String cron = settingService.get(SettingService.NOTIF_DAILY_CRON) ?: '0 0 20 * * ?'
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
        try {
            scheduledFuture = scheduler.schedule(
                    { dailyNotificationSchedulerService.runDailyBorrowNotifications() },
                    new CronTrigger(cron))
            log.info('Scheduled daily borrow notifications with cron: {}', cron)
        } catch (Exception e) {
            log.error('Invalid notification.dailyCron: {}', cron, e)
        }
    }

    private void cancelJob() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false)
            scheduledFuture = null
        }
    }
}

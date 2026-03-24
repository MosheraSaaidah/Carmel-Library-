package alCarmel

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * للإنتاج: يُرجع دائماً new Date().
 * للتجربة (carmel.notification.testing.enabled: true): تاريخ من الجلسة بعد تسجيل الدخول
 * أو من fixedEffectiveDate عند المهام بدون HTTP (مثل الجدولة).
 */
class NotificationClockService {

    static final String SESSION_NOTIFICATION_TEST_DATE = 'notificationTestDate'

    def grailsApplication

    static def currentHttpSession() {
        def ra = RequestContextHolder.getRequestAttributes()
        if (ra instanceof ServletRequestAttributes) {
            return ra.request.getSession(false)
        }
        null
    }

    /**
     * @param httpSession جلسة الطلب (مثلاً من الكنترولر)، أو null للمهام الخلفية
     */
    Date resolveEffectiveToday(def httpSession) {
        boolean testing = grailsApplication?.config?.getProperty(
                'carmel.notification.testing.enabled', Boolean, false) ?: false
        if (!testing) {
            return new Date()
        }

        if (httpSession) {
            def attr = httpSession.getAttribute(SESSION_NOTIFICATION_TEST_DATE)
            if (attr instanceof Date) {
                return (Date) attr
            }
            if (attr instanceof Long) {
                return new Date((long) attr)
            }
            if (attr instanceof String) {
                try {
                    return Date.parse('yyyy-MM-dd', attr)
                } catch (ignored) {
                }
            }
        }

        String fixed = grailsApplication?.config?.getProperty(
                'carmel.notification.testing.fixedEffectiveDate', String, null)
        if (fixed) {
            try {
                return Date.parse('yyyy-MM-dd', fixed)
            } catch (ignored) {
            }
        }

        return new Date()
    }
}

package alCarmel

import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.support.RequestContextUtils


class LocaleInterceptor {

    LocaleInterceptor() {
        matchAll()
    }

    int order = -50

    boolean before() {
        def lang = session?.uiLang ?: 'en'
        Locale locale = (lang == 'ar') ? new Locale('ar') : Locale.ENGLISH
        LocaleResolver resolver = RequestContextUtils.getLocaleResolver(request)
        resolver?.setLocale(request, response, locale)
        true
    }
}

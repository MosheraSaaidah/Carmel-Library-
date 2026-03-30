// Place your Spring DSL code here
import org.springframework.web.servlet.i18n.SessionLocaleResolver

beans = {
    localeResolver(SessionLocaleResolver) {
        defaultLocale = java.util.Locale.ENGLISH
    }
}

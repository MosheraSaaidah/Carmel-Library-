package alCarmel

class LocaleController {

    SecurityService securityService
    def switchLang() {
        if (!securityService?.isLoggedIn(session)) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def lang = params.lang?.toString()?.toLowerCase()
        if (lang in ['en', 'ar']) {
            session.uiLang = lang
        }
        def r = params.r?.toString()
        if (r && r.startsWith('/') && !r.startsWith('//') && !r.contains('://')) {
            redirect(uri: r)
            return
        }
        redirect(uri: '/')
    }
}

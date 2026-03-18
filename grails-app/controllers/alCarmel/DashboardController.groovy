package alCarmel

class DashboardController {

    DashboardService dashboardService
    SecurityService securityService

    def index() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def stats = dashboardService.getStats()
        def activity = dashboardService.getBorrowingActivity()
        render(view: 'index' , model: [
                *:stats ,
                borrowingActivity : activity
        ])
    }
}

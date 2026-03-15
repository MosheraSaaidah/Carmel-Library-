package alCarmel

class DashboardController {

    DashboardService dashboardService
    def index() {
        def stats = dashboardService.getStats()
        def activity = dashboardService.getBorrowingActivity()
        render(view: 'index' , model: [
                *:stats ,
                borrowingActivity : activity
        ])
    }
}

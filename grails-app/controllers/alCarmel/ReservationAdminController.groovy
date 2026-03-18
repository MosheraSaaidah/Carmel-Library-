package alCarmel

class ReservationAdminController {

    SecurityService securityService

    def index() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def reservations = Reservation.list(sort: 'dateCreated', order: 'desc')
        [reservations: reservations]
    }
}


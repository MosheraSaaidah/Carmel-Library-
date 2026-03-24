package alCarmel

class MemberAreaController {
    SecurityService securityService
    BorrowService borrowService
    ReservationService reservationService

   
    def books() {
        if (!securityService.hasRole(session, "MEMBER")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def user = securityService.getCurrentUser(session)
        def member = Member.findByUser(user)
        if (!member) {
            render "No member profile linked to this user."
            return
        }

        // نعرض كل الكتب النشطة، حتى لو ما في نسخ متاحة (عشان يسمح بالحجز)
        def books = Book.findAllByActive(true, [sort: 'bookTitle'])

        // كتب عند العضو عليها حجز ACTIVE/NOTIFIED
        def memberReservations = Reservation.findAllByMemberAndStatusInList(
                member,
                ["ACTIVE", "NOTIFIED"]
        )
        Set<Long> reservedBookIds = memberReservations.collect { it.book?.id }.findAll { it } as Set<Long>

        [member: member, books: books, reservedBookIds: reservedBookIds]
    }

    
    def borrowHistory() {
        if (!securityService.hasRole(session, "MEMBER")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def user = securityService.getCurrentUser(session)
        def member = Member.findByUser(user)
        if (!member) {
            render "No member profile linked to this user."
            return
        }

        def borrows = Borrow.findAllByMember(member, [sort: 'borrowDate', order: 'desc'])
        [member: member, borrows: borrows]
    }
    
    def reservations() {
        if (!securityService.hasRole(session, "MEMBER")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def user = securityService.getCurrentUser(session)
        def member = Member.findByUser(user)
        if (!member) {
            render "No member profile linked to this user."
            return
        }

        def reservations = Reservation.findAllByMember(member, [sort: 'dateCreated', order: 'desc'])
        [member: member, reservations: reservations]
    }

   
    def borrow(Long bookId) {
        if (!securityService.hasRole(session, "MEMBER")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def user = securityService.getCurrentUser(session)
        def member = Member.findByUser(user)
        if (!member) {
            flash.error = "No member profile linked to this user."
            redirect(action: 'books')
            return
        }

        try {
            borrowService.borrow(bookId, member.id)
            flash.success = "Book borrowed successfully."
        } catch (Exception e) {
            flash.error = e.message ?: "Unable to borrow this book."
        }
        redirect(action: 'books')
    }
   
    def reserve(Long bookId) {
        if (!securityService.hasRole(session, "MEMBER")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def user = securityService.getCurrentUser(session)
        def member = Member.findByUser(user)
        if (!member) {
            flash.error = "No member profile linked to this user."
            redirect(action: 'books')
            return
        }

        def result = reservationService.createReservation(bookId, member)
        if (result.success) {
            flash.success = "Reservation created successfully. You will receive an email when the book becomes available."
        } else {
            flash.error = result.errorMessage
        }
        redirect(action: 'books')
    }

   
    def cancelReservation(Long id) {
        if (!securityService.hasRole(session, "MEMBER")) {
            redirect(controller: 'auth', action: 'login')
            return
        }

        def user = securityService.getCurrentUser(session)
        def member = Member.findByUser(user)
        if (!member) {
            flash.error = "No member profile linked to this user."
            redirect(action: 'reservations')
            return
        }

        def result = reservationService.cancelReservation(id, member)
        if (result.success) {
            flash.success = "Reservation cancelled."
        } else {
            flash.error = result.errorMessage
        }
        redirect(action: 'reservations')
    }
}



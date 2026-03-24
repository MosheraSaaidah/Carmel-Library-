package alCarmel

class BorrowController {

    BorrowService borrowService
    SecurityService securityService

    // Borrowing System index: loads stats and latest borrow records.
    def index() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return 
        }
        borrowService.updateLateBorrows()
        [
                borrows      : borrowService.getBorrows(params.filter),
                currentFilter: params.filter ?: "ALL",
                members      : Member.findAllByActive(true, [sort: 'fullName']),
                books        : Book.findAllByActiveAndAvailableCopiesGreaterThan(true, 0, [sort: 'bookTitle'])
        ]
    }

    // Handles a new borrow request. Validates book/member IDs and
    // surfaces any error (e.g. "no copies available", null property) as a toast.
    def save() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def bookId   = params.long('bookId')
        def memberId = params.long('memberId')
        if (!bookId || !memberId) {
            flash.error = 'Please select both a member and a book.'
            redirect(action: 'index')
            return
        }
        try {
            borrowService.borrow(bookId, memberId)
            flash.success = 'Book borrowed successfully'
        } catch (Exception e) {
            def msg = e.message
            if (e.cause?.message) {
                msg = e.cause.message
            }
            flash.error = msg ?: 'Unable to borrow this book.'
        }
        redirect(action: 'index')
    }

    // Marks a borrow as returned and restores the available copy.
    def returnBook() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def lateDays = borrowService.returnBookService(params.id as Long)
        if (lateDays && lateDays > 0) {
            flash.success = "Book returned successfully. Late fee: \$${lateDays}"
        } else {
            flash.success = 'Book returned successfully. No late fees.'
        }
        redirect(action: 'index')
    }
}
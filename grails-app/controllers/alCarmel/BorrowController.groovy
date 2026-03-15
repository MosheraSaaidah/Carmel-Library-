package alCarmel

class BorrowController {

    BorrowService borrowService

    // Borrowing System index: loads stats and latest borrow records.
    def index() {
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
        borrowService.returnBookService(params.id as Long)
        flash.success = 'Book returned successfully'
        redirect(action: 'index')
    }

    // Borrowing records are never deleted; they are kept for history and statistics.
}
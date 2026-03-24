package alCarmel

import grails.gorm.transactions.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Transactional
class BorrowService {

    NotificationService notificationService
    def grailsApplication

    /** عدد أيام الاستعارة حتى موعد الإرجاع (من application.yml: carmel.borrow.daysUntilDue، افتراضي 14) */
    int getDaysUntilDue() {
        grailsApplication?.config?.getProperty('carmel.borrow.daysUntilDue', Integer, 14) ?: 14
    }
    // Returns all borrows or a subset filtered by status for the
    def getBorrows(String filter) {
        if (!filter || filter == "ALL") {
            return Borrow.list()
        }
        Borrow.findAllByStatus(filter)
    }

    // Creates a new borrow record while enforcing key business rules:
    // - Book and member must exist.
    // - Book must have at least one available copy.
    // - Due date is 14 days from the borrow date.
    def borrow(Long bookId, Long memberId) {
        def book   = Book.get(bookId)
        def member = Member.get(memberId)

        if (!book || !member) throw new Exception("Book or Member not found")
        if (!book.active) throw new Exception("This book is no longer available (archived).")
        if (!member.active) throw new Exception("This member is archived and cannot borrow.")
        if (book.availableCopies < 1) throw new Exception("Out of Stock")
        if (!book.category) throw new Exception("Book must have a category. Please edit the book and choose a category.")

        // Prevent the same member from borrowing the same book twice at the same time
        def activeDuplicateCount = Borrow.withCriteria {
            eq 'book', book
            eq 'member', member
            'in'('status', ['BORROWED', 'LATE'])
            projections { rowCount() }
        }[0] ?: 0
        if (activeDuplicateCount > 0) {
            throw new Exception("You cannot borrow the same book twice before returning it.")
        }

        int days = getDaysUntilDue()
        LocalDate localRef = LocalDate.now()
        Date borrowDate = Date.from(
                localRef.atStartOfDay(ZoneId.systemDefault()).toInstant())
        def due = Date.from(
                localRef.plusDays(days)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        )

        def borrow = new Borrow(
                book       : book,
                member     : member,
                borrowDate : borrowDate,
                dueDate    : due,
                returnDate : null,
                status     : "BORROWED"
        )

        // نتأكد من صحة البيانات قبل الحفظ
        if (!borrow.validate()) {
            def msg = borrow.errors.allErrors.collect { it.defaultMessage ?: it.code }.join(', ')
            throw new Exception(msg ?: "Unable to borrow this book.")
        }

        // حفظ الاستعارة (لو فشل يرمي استثناءً ويرجع السبب في التوست)
        borrow.save(flush: true, failOnError: true)

        book.availableCopies--
        book.save(flush: true, failOnError: true)

        borrow
    }

    /**
     * Marks a borrow as returned.
     */
    def returnBookService(Long borrowId) {
        def borrow = Borrow.get(borrowId)
        if (!borrow) throw new Exception("Borrow record not found")

        borrow.returnDate = new Date()
        int lateDays = 0
        if (borrow.dueDate) {
            // difference in whole days between return date and due date
            lateDays = (int) ((borrow.returnDate.time - borrow.dueDate.time) / (24 * 60 * 60 * 1000))
            if (lateDays < 0) {
                lateDays = 0
            }
        }

        // If the book is returned after its due date, mark as LATE, otherwise RETURNED
        borrow.status = lateDays > 0 ? "LATE" : "RETURNED"
        borrow.save()

        borrow.book.availableCopies++
        borrow.book.save()

        // Notify reservations if this book has become available again
        if (notificationService) {
            notificationService.notifyReservationsForBook(borrow.book)
        }

        // Late fee is $1 per late day; caller can use this value.
        return lateDays
    }


    def updateLateBorrows() {
        Date today = Date.from(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        Borrow.findAllByStatusAndDueDateLessThan("BORROWED", today).each {
            it.status = "LATE"
            it.save()
        }
    }

    // Borrowing records are never deleted; they are kept for history and statistics.
}
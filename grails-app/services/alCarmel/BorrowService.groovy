package alCarmel

import grails.gorm.transactions.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Transactional
class BorrowService {

    // Returns all borrows or a subset filtered by status for the
    // Borrowing System tabs on the UI.
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

        // تاريخ الاستعارة والموعد النهائي (14 يوم)
        def now = new Date()
        def due = Date.from(
                LocalDate.now().plusDays(14)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        )

        def borrow = new Borrow(
                book       : book,
                member     : member,
                borrowDate : now,
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

    def returnBookService(Long borrowId) {
        def borrow = Borrow.get(borrowId)
        if (!borrow) throw new Exception("Borrow record not found")

        borrow.returnDate = new Date()
        borrow.status     = "RETURNED"
        borrow.save()

        borrow.book.availableCopies++
        borrow.book.save()
    }

    def updateLateBorrows() {
        def today = Date.from(
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        )
        Borrow.findAllByStatusAndDueDateLessThan("BORROWED", today).each {
            it.status = "LATE"
            it.save()
        }
    }

    // Borrowing records are never deleted; they are kept for history and statistics.
}
package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class ReservationService {

    /**
     * Creates a reservation for a member on a book (when no copies available).
     */
    Map<String, Object> createReservation(Long bookId, Member member) {
        def book = Book.get(bookId)
        if (!book || !book.active) {
            return [success: false, errorMessage: "Book not found."]
        }
        // If the member already has this book borrowed, reserving it makes no sense.
        def activeBorrowCount = Borrow.withCriteria {
            eq 'book', book
            eq 'member', member
            'in'('status', ['BORROWED', 'LATE'])
            projections { rowCount() }
        }[0] ?: 0
        if (activeBorrowCount > 0) {
            return [success: false, errorMessage: "You already borrowed this book. Return it first before reserving again."]
        }
        if (book.availableCopies > 0) {
            return [success: false, errorMessage: "This book is available. You can borrow it directly instead of reserving."]
        }
        def existing = Reservation.findByBookAndMemberAndStatusInList(
                book,
                member,
                ["ACTIVE", "NOTIFIED"]
        )
        if (existing) {
            return [success: false, errorMessage: "You already have a reservation for this book."]
        }
        def reservation = new Reservation(
                book  : book,
                member: member,
                status: "ACTIVE"
        )
        if (!reservation.save(flush: true)) {
            return [success: false, errorMessage: "Unable to create reservation."]
        }
        return [success: true, errorMessage: null]
    }

    /**
     * Cancels a reservation if it belongs to the given member.
     * Returns [success: boolean, errorMessage: String or null]
     */
    Map<String, Object> cancelReservation(Long reservationId, Member member) {
        def reservation = Reservation.get(reservationId)
        if (!reservation || reservation.member?.id != member?.id) {
            return [success: false, errorMessage: "Reservation not found."]
        }
        reservation.status = "CANCELLED"
        reservation.save(flush: true)
        return [success: true, errorMessage: null]
    }
}

package alCarmel

import grails.gorm.transactions.Transactional
import java.time.LocalDate
import java.time.ZoneId

@Transactional
class DashboardService {

    def getStats() {
        def today = toDate(LocalDate.now())
        [
                members         : Member.countByActive(true),
                archivedMembers : Member.countByActive(false),
                totalBooks      : Book.executeQuery("SELECT COALESCE(SUM(b.totalCopies), 0) FROM Book b WHERE b.active = true")[0] ?: 0,
                totalBooksEver   : Book.executeQuery("SELECT COALESCE(SUM(b.totalCopies), 0) FROM Book b")[0] ?: 0,
                activeBookTitles : Book.countByActive(true),
                archivedBookTitles: Book.countByActive(false),
                categories      : Category.count(),
                activeLoans     : Borrow.countByStatus("BORROWED"),
                lateReturns     : Borrow.countByStatus("LATE"),
                borrowedToday   : Borrow.countByBorrowDateGreaterThanEquals(today)
        ]
    }

    def getBorrowingActivity() {
        (6..0).collect { daysAgo ->
            def localDay = LocalDate.now().minusDays(daysAgo)
            def day      = toDate(localDay)
            def nextDay  = toDate(localDay.plusDays(1))
            [
                    date : localDay.dayOfWeek.toString()[0..2].toLowerCase().capitalize(),
                    count: Borrow.countByBorrowDateBetween(day, nextDay)
            ]
        }
    }

    private Date toDate(LocalDate localDate) {
        Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
}
package alCarmel

import java.time.LocalDate
import java.time.ZoneId

class Borrow {
    Book book
    Member member
    Date borrowDate = new Date()
    Date dueDate
    Date returnDate
    String status = "BORROWED"
    Date dateCreated
    Date lastUpdated

    static belongsTo = [member: Member, book: Book]

    static constraints = {
        returnDate nullable: true
        dueDate    nullable: true
        status inList: ["BORROWED", "RETURNED", "LATE"]
    }

    static mapping = {
        autoTimestamp true
    }

    def beforeInsert() {
        if (!borrowDate) {
            borrowDate = new Date()
        }
        if (!dueDate) {
            dueDate = Date.from(
                    LocalDate.now().plusDays(14)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()
            )
        }
      
    }

    boolean isLate() {
        status == "BORROWED" && dueDate && new Date() > dueDate
    }
}
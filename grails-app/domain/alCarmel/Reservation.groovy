package alCarmel

class Reservation {

    Book book
    Member member

    String status = "ACTIVE"   // ACTIVE, NOTIFIED, CANCELLED, COMPLETED

    Date dateCreated
    Date lastUpdated

    static belongsTo = [book: Book, member: Member]

    static constraints = {
        status inList: ["ACTIVE", "NOTIFIED", "CANCELLED", "COMPLETED"]
    }

    static mapping = {
        autoTimestamp true
    }
}


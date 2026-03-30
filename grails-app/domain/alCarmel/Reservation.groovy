package alCarmel

class Reservation {

    Book book
    Member member

    String status = "ACTIVE"   // ACTIVE, NOTIFIED, CANCELLED

    Date dateCreated
    Date lastUpdated

    static belongsTo = [book: Book, member: Member]

    static constraints = {
        status inList: ["ACTIVE", "NOTIFIED", "CANCELLED"]
    }

    static mapping = {
        autoTimestamp true
    }
}


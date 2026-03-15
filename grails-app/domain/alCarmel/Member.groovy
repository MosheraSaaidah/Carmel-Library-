package alCarmel

class Member {
    // Standardized list of Palestinian cities (West Bank) for registration.
    public static final List<String> CITY_OPTIONS = [
            'Ramallah',
            'Nablus',
            'Hebron',
            'Bethlehem',
            'Jenin',
            'Tulkarm',
            'Qalqilya',
            'Jericho',
            'Salfit',
            'Tubas'
    ]

    String fullName
    String email
    String phoneNumber
    String address
    Date membershipDate = new Date()

    /** Soft delete: false = archived, not shown in normal lists */
    Boolean active = true
    Date dateCreated
    Date lastUpdated
    Date archivedAt
    Member archivedBy

    static hasMany = [borrows: Borrow]

    static constraints = {
        fullName    blank: false
        email       email: true, unique: true
        phoneNumber nullable: false, validator: { val, obj ->
            def v = val?.toString()?.trim() ?: ''
            if (!v) return false
            // Only digits (ASCII 0-9); length 10–12
            if (!v.matches(/^\d{10,12}$/)) return 'invalidLength'
            return true
        }
        // We reuse `address` as a standardized city field selected from CITY_OPTIONS.
        address     nullable: false, inList: CITY_OPTIONS
        archivedAt  nullable: true
        archivedBy  nullable: true
    }

    static mapping = {
        autoTimestamp true
    }

    String getFirstChar() {
        fullName ? fullName[0].toUpperCase() : "?"
    }

    String getMemberSinceYear() {
        if (!membershipDate) return ""
        new java.text.SimpleDateFormat("yyyy").format(membershipDate)
    }
}
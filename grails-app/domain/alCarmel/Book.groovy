package alCarmel

class Book {
    String bookTitle
    String authorName
    String description
    Integer totalCopies = 1
    Integer availableCopies = 1
    Integer publishYear
    Category category

    /** Soft delete: false = archived, not shown in normal lists */
    Boolean active = true
    Date dateCreated
    Date lastUpdated
    Date archivedAt
    Member archivedBy

    static belongsTo = [category: Category]
    static hasMany = [borrow: Borrow]
    // Business rules / validation:
    // - `bookTitle` must be present and unique per `authorName`
    //   so the same author cannot have two books with the same title.
    // - `publishYear` cannot be in the future (greater than current year).
    // - Other fields are optional where marked `nullable`.
    static constraints = {
        bookTitle blank: false ,unique: ['authorName']
        authorName blank: false
        description nullable: true
        publishYear nullable: false, validator: { Integer val, Book obj ->
            if (!val) {
                return false
            }
            int currentYear = Calendar.instance.get(Calendar.YEAR)
             if (val> currentYear) {
                // Custom error code used in the controller to show
                // a clear message to the user.
                return 'futureYear'
            }
            return true
        }
        archivedAt nullable: true
        archivedBy nullable: true
    }

    static mapping = {
        autoTimestamp true
    }



}
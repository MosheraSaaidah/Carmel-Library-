package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class BookService {
    
    def getBooks(String query) {
        if (query) {
            return Book.executeQuery("""
                FROM Book b
                WHERE b.active = true
                AND (lower(b.bookTitle) LIKE :q
                     OR lower(b.authorName) LIKE :q
                     OR lower(b.category.categoryName) LIKE :q)
            """, [q: "%${query.toLowerCase()}%"])
        }
        Book.findAllByActive(true, [sort: 'bookTitle', order: 'asc'])
    }

<<<<<<< HEAD
=======
    /** List only archived books for admin/archived view. */
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
    def getArchivedBooks(String query = null) {
        if (query) {
            return Book.executeQuery("""
                FROM Book b
                WHERE b.active = false
                AND (lower(b.bookTitle) LIKE :q
                     OR lower(b.authorName) LIKE :q
                     OR lower(b.category.categoryName) LIKE :q)
            """, [q: "%${query.toLowerCase()}%"])
        }
        Book.findAllByActive(false, [sort: 'archivedAt', order: 'desc'])
    }

    def saveBook(Map params) {
        def book = new Book(
                bookTitle     : params.bookTitle,
                authorName    : params.authorName,
<<<<<<< HEAD
                description   : params.description,
=======
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                totalCopies   : params.totalCopies as Integer ?: 1,
                availableCopies: params.totalCopies as Integer ?: 1,
                publishYear   : params.publishYear as Integer,
                category      : Category.get(params.categoryId as Long),
                active        : true
        )
        book.save()
        book
    }

    def updateBook(Long id, Map params) {
        def book = Book.get(id)
        if (!book) return null
<<<<<<< HEAD

        def onLoan = Math.max(0, (book.totalCopies ?: 0) - (book.availableCopies ?: 0))

        def tcStr = params.totalCopies?.toString()?.trim()
        if (!tcStr || !tcStr.isInteger()) {
            book.errors.rejectValue('totalCopies', 'invalid', 'Total copies must be a valid number.')
            return book
        }
        int newTotal = tcStr.toInteger()
        if (newTotal < onLoan) {
            book.errors.rejectValue('totalCopies', 'tooFewCopies',
                    "Total copies cannot be less than the number currently borrowed (${onLoan}).")
            return book
        }

        def pubStr = params.publishYear?.toString()?.trim()
        if (!pubStr || !pubStr.isInteger()) {
            book.errors.rejectValue('publishYear', 'invalid', 'Publish year must be a valid number.')
            return book
        }

        def catStr = params.categoryId?.toString()?.trim()
        if (!catStr || !catStr.isLong()) {
            book.errors.rejectValue('category', 'nullable', 'Please select a category.')
            return book
        }
        def category = Category.get(catStr.toLong())
        if (!category) {
            book.errors.rejectValue('category', 'notFound', 'Category not found.')
            return book
        }

        book.bookTitle     = params.bookTitle
        book.authorName    = params.authorName
        book.description   = params.description
        book.publishYear   = pubStr.toInteger()
        book.category      = category
        book.totalCopies   = newTotal
        book.availableCopies = newTotal - onLoan
=======
        book.bookTitle     = params.bookTitle
        book.authorName    = params.authorName
        book.description   = params.description
        book.publishYear   = params.publishYear as Integer
        book.category      = Category.get(params.categoryId as Long)

        def diff = params.totalCopies as Integer - book.totalCopies
        book.totalCopies   = params.totalCopies as Integer
        book.availableCopies = book.availableCopies + diff
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
        book.save()
        book
    }

<<<<<<< HEAD
    
=======
    /**
     * Archive book (soft delete). Cannot archive if it is currently borrowed (BORROWED or LATE).
     * @param archivedByMemberId optional id of the member/admin performing the archive (nullable)
     */
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
    def archiveBook(Long id, Long archivedByMemberId = null) {
        def book = Book.get(id)
        if (!book) return null
        if (!book.active) return book

        def activeCount = Borrow.withCriteria {
            eq('book', book)
            'in'('status', ['BORROWED', 'LATE'])
            projections { rowCount() }
        }[0] ?: 0
        if (activeCount > 0) {
            throw new IllegalStateException("Cannot archive book: it is currently borrowed (${activeCount} active loan(s)). Please ensure the book is returned first.")
        }
        book.active = false
        book.archivedAt = new Date()
        book.archivedBy = archivedByMemberId ? Member.get(archivedByMemberId) : null
        book.save()
        book
    }

    /** Restore an archived book. */
    def restoreBook(Long id) {
        def book = Book.get(id)
        if (!book) return null
        book.active = true
        book.archivedAt = null
        book.archivedBy = null
        book.save()
        book
    }

    /** Total copies of active books only (current inventory). */
    def sumActiveBooksTotalCopies() {
        Book.executeQuery("SELECT COALESCE(SUM(b.totalCopies), 0) FROM Book b WHERE b.active = true")[0] ?: 0
    }

    /** Total books ever added (active + archived) for reporting. */
    def countAllBooks() {
        Book.count()
    }

    /** Total active books (count of titles). */
    def countActiveBooks() {
        Book.countByActive(true)
    }

    /** Total archived books (count of titles). */
    def countArchivedBooks() {
        Book.countByActive(false)
    }
}

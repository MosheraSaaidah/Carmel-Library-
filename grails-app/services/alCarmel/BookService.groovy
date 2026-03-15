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

    /** List only archived books for admin/archived view. */
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
        book.bookTitle     = params.bookTitle
        book.authorName    = params.authorName
        book.description   = params.description
        book.publishYear   = params.publishYear as Integer
        book.category      = Category.get(params.categoryId as Long)

        def diff = params.totalCopies as Integer - book.totalCopies
        book.totalCopies   = params.totalCopies as Integer
        book.availableCopies = book.availableCopies + diff
        book.save()
        book
    }

    /**
     * Archive book (soft delete). Cannot archive if it is currently borrowed (BORROWED or LATE).
     * @param archivedByMemberId optional id of the member/admin performing the archive (nullable)
     */
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

package alCarmel

<<<<<<< HEAD
import groovy.json.JsonOutput

=======
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
class BookController {

    BookService bookService
    SecurityService securityService
<<<<<<< HEAD

    /**
     * JSON payload for the edit-book modal (single source of truth; avoids fragile data-* attributes).
     */
    def edit(Long id) {
        if (!securityService.hasRole(session, 'ADMIN')) {
            response.status = 403
            render(contentType: 'application/json; charset=UTF-8', text: '{}')
            return
        }
        def book = Book.get(id)
        if (!book) {
            response.status = 404
            render(contentType: 'application/json; charset=UTF-8', text: '{"error":"not found"}')
            return
        }
        def data = [
                id           : book.id,
                bookTitle    : book.bookTitle,
                authorName   : book.authorName,
                description  : book.description ?: '',
                totalCopies  : book.totalCopies,
                publishYear  : book.publishYear,
                categoryId   : book.category?.id,
                categoryName : book.category?.categoryName ?: ''
        ]
        render(contentType: 'application/json; charset=UTF-8', text: JsonOutput.toJson(data))
    }
=======
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
    
    // Books listing page (active only)
    def index() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        
        def books = bookService.getBooks(params.p)
        def query = params.q
        if (request.xhr) {
            render(template: 'bookList', model: [books: books, query: query])
            return
        }
        [
                books     : books,
                query     : query,
                categories: Category.list(sort: 'categoryName')
        ]
    }

    // Archived books list (admin view) — supports AJAX search like index
    def archived() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def books = bookService.getArchivedBooks(params.q)
        def query = params.q
        if (request.xhr) {
            render(template: 'archivedBookList', model: [books: books, query: query])
            return
        }
        [
                books     : books,
                query     : query,
                categories: Category.list()
        ]
    }

    def save() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def book = bookService.saveBook(params)

        if (book.hasErrors()) {
            def errorMsg = 'Please check the book details and try again.'
            def titleError = book.errors.getFieldError('bookTitle')
            if (titleError?.code == 'unique') {
                errorMsg = 'A book with this title already exists for the same author.'
            } else {
                def yearError = book.errors.getFieldError('publishYear')
                if (yearError?.code == 'futureYear') {
                    errorMsg = 'Publish year cannot be in the future.'
                }
            }
            flash.error = errorMsg
            redirect(action: 'index', params: [q: params.q])
        } else {
            flash.success = 'Book added successfully'
            redirect(action: 'index')
        }
    }

    def update() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
<<<<<<< HEAD
        def idStr = params.id?.toString()?.trim()
        if (!idStr || !idStr.isLong()) {
            flash.error = 'Book not found.'
            redirect(action: 'index', params: [q: params.q])
            return
        }
        def book = bookService.updateBook(idStr.toLong(), params)
=======
        def book = bookService.updateBook(params.id as Long, params)
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
        if (!book) {
            flash.error = 'Book not found.'
            redirect(action: 'index')
            return
        }
        if (book.hasErrors()) {
            def errorMsg = 'Please check the book details and try again.'
            def titleError = book.errors.getFieldError('bookTitle')
<<<<<<< HEAD
            def copiesError = book.errors.getFieldError('totalCopies')
            def yearError = book.errors.getFieldError('publishYear')
            def catError = book.errors.getFieldError('category')
            if (titleError?.code == 'unique') {
                errorMsg = 'Another book by this author already uses this title.'
            } else if (copiesError?.code == 'tooFewCopies') {
                errorMsg = copiesError.defaultMessage ?: errorMsg
            } else if (copiesError?.code == 'invalid' || yearError?.code == 'invalid' || catError?.code in ['nullable', 'notFound']) {
                errorMsg = copiesError?.defaultMessage ?: yearError?.defaultMessage ?: catError?.defaultMessage ?: errorMsg
            } else if (yearError?.code == 'futureYear') {
                errorMsg = 'Publish year cannot be in the future.'
=======
            if (titleError?.code == 'unique') {
                errorMsg = 'Another book by this author already uses this title.'
            } else {
                def yearError = book.errors.getFieldError('publishYear')
                if (yearError?.code == 'futureYear') {
                    errorMsg = 'Publish year cannot be in the future.'
                }
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
            }
            flash.error = errorMsg
            redirect(action: 'index', params: [q: params.q])
        } else {
            flash.success = 'Book updated successfully'
            redirect(action: 'index')
        }
    }

    // Soft delete: archive book instead of permanent delete
    def archive() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        try {
            bookService.archiveBook(params.long('id'))
            flash.success = 'Book archived successfully. It no longer appears in the active list but remains in the database for history.'
        } catch (Exception e) {
            def msg = e.cause?.message ?: e.message
            flash.error = msg ?: 'Cannot archive this book while it is borrowed.'
        }
        redirect(action: 'index')
    }

    def restore() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        try {
            bookService.restoreBook(params.long('id'))
            flash.success = 'Book restored successfully. It is active again.'
        } catch (Exception e) {
            flash.error = "Restore failed: ${e.message}"
        }
        redirect(action: 'archived')
    }
}

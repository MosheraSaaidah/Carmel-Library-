package alCarmel

class BookController {

    BookService bookService
    SecurityService securityService
    
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
        def book = bookService.updateBook(params.id as Long, params)
        if (!book) {
            flash.error = 'Book not found.'
            redirect(action: 'index')
            return
        }
        if (book.hasErrors()) {
            def errorMsg = 'Please check the book details and try again.'
            def titleError = book.errors.getFieldError('bookTitle')
            if (titleError?.code == 'unique') {
                errorMsg = 'Another book by this author already uses this title.'
            } else {
                def yearError = book.errors.getFieldError('publishYear')
                if (yearError?.code == 'futureYear') {
                    errorMsg = 'Publish year cannot be in the future.'
                }
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

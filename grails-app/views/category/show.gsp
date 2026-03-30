<meta name="layout" content="main"/>

%{--
  Books-by-category view.
  When a user clicks a category card on the Categories page, they
  are taken here to see only the books in that category, displayed
  using the same card design as the main Books Collection page.
--}%

<!-- Header -->
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Books in ${category?.categoryName}</div>
        <div class="page-subtitle">
            All titles currently assigned to this category.
        </div>
    </div>
    <g:link controller="book" action="index" class="btn-green">
        <i class="bi bi-arrow-left"></i> Back to All Books
    </g:link>
</div>

<!-- Books Grid -->
<div class="books-grid">
    <g:each in="${books}" var="book">
        <div class="book-card">

            <!-- Top -->
            <div class="book-card-top">
                <span class="category-badge">${book.category?.categoryName?.toUpperCase()}</span>
                <span class="copies-badge ${book.availableCopies > 0 ? 'copies-available' : 'copies-none'}">
                    ${book.availableCopies} COPIES LEFT
                </span>
            </div>

            <!-- Info -->
            <div class="book-title">${book.bookTitle}</div>
            <div class="book-author">by ${book.authorName}</div>
            <div class="book-description">${book.description}</div>

            <!-- Footer -->
            <div class="book-footer">
                <span><i class="bi bi-book"></i> ${book.totalCopies} TOTAL</span>
                <span><i class="bi bi-calendar3"></i> ${book.publishYear}</span>
            </div>

        </div>
    </g:each>

    <!-- Empty State -->
    <g:if test="${!books}">
        <div class="empty-state">
            <i class="bi bi-book"></i>
            <p>No books in this category yet</p>
        </div>
    </g:if>
</div>


<meta name="layout" content="main"/>

<!-- Header -->
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Archived Books</div>
        <div class="page-subtitle">Books removed from circulation. They remain in the database for reporting and history. Restore to make them active again.</div>
    </div>
    <g:link controller="book" action="index" class="btn btn-outline-primary">
        <i class="bi bi-book"></i> Back to Active Books
    </g:link>
</div>

<!-- Search (نفس تصميم Books Collection: بحث كبير + AJAX) -->
<div class="card-white mb-4">
    <g:form action="archived" method="get">
        <div class="search-wrapper">
            <i class="bi bi-search search-icon"></i>
            <input type="text" name="q" value="${query}"
                   class="search-input"
                   placeholder="Search archived by title, author, or category..."/>
        </div>
    </g:form>
</div>

<!-- Archived Books Grid (يُحدَّث عبر AJAX عند الكتابة في البحث) -->
<div class="books-grid" id="booksGrid">
    <g:render template="archivedBookList" model="[books: books, query: query]"/>
</div>

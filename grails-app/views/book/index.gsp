<meta name="layout" content="main"/>

<!-- Header -->
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Books Collection</div>
        <div class="page-subtitle">Manage your library's inventory and availability. (Active only)</div>
    </div>
    <div class="d-flex gap-2">
        <g:link controller="book" action="archived" class="btn btn-outline-secondary">
            <i class="bi bi-archive"></i> Archived Books
        </g:link>
        <button class="btn-green" data-bs-toggle="modal" data-bs-target="#addBookModal">
            <i class="bi bi-plus-lg"></i> Add New Book
        </button>
    </div>
</div>

<!-- Search -->
<div class="card-white mb-4">
    <g:form action="index" method="get">
        <div class="search-wrapper">
            <i class="bi bi-search search-icon"></i>
            <input type="text" name="q" value="${query}"
                   class="search-input"
                   placeholder="Search by title, author, or category..."/>
        </div>
    </g:form>
</div>

<!-- Books Grid (يُحدَّث عبر AJAX عند الكتابة في البحث دون الخروج من الصندوق) -->
<div class="books-grid" id="booksGrid">
    <g:render template="bookList" model="[books: books, query: query]"/>
</div>
<g:render template="modals"/>
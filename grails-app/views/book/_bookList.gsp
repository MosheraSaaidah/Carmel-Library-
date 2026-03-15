%{-- قائمة الكتب (كروت + حالة فارغة). يُستخدم في التحميل الأول وفي البحث عبر AJAX. --}%
<g:each in="${books}" var="book">
    <div class="book-card">
        <div class="book-card-top">
            <span class="category-badge">${book.category?.categoryName?.toUpperCase()}</span>
            <span class="copies-badge ${book.availableCopies > 0 ? 'copies-available' : 'copies-none'}">
                ${book.availableCopies} COPIES LEFT
            </span>
        </div>
        <div class="book-title">${book.bookTitle}</div>
        <div class="book-author">by ${book.authorName}</div>
        <div class="book-description">${book.description}</div>
        <div class="book-footer">
            <span><i class="bi bi-book"></i> ${book.totalCopies} TOTAL</span>
            <span><i class="bi bi-calendar3"></i> ${book.publishYear}</span>
        </div>
        <div class="book-actions">
            <button type="button" class="btn-edit btn-edit-book"
                    data-id="${book.id}"
                    data-title="${book.bookTitle}"
                    data-author="${book.authorName}"
                    data-description="${book.description ?: ''}"
                    data-total-copies="${book.totalCopies}"
                    data-publish-year="${book.publishYear ?: 0}"
                    data-category-id="${book.category?.id ?: 0}">
                <i class="bi bi-pencil"></i> Edit
            </button>
            <g:form action="archive" method="post" style="display:inline"
                    onsubmit="return confirm('Archive this book? It will not appear in the active list but remain in the database for history.');">
                <input type="hidden" name="id" value="${book.id}"/>
                <button type="submit" class="btn-delete" title="Archive book">
                    <i class="bi bi-archive"></i>
                </button>
            </g:form>
        </div>
    </div>
</g:each>
<g:if test="${!books}">
    <div class="empty-state">
        <i class="bi bi-book"></i>
        <p>No books found</p>
    </div>
</g:if>

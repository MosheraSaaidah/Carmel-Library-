%{-- قائمة الكتب المؤرشفة (كروت) — يُحدَّث عبر AJAX مثل bookList --}%
<g:each in="${books}" var="book">
    <div class="book-card">
        <div class="book-card-top">
            <span class="category-badge">${book.category?.categoryName?.toUpperCase() ?: '—'}</span>
            <span class="copies-badge copies-none">ARCHIVED</span>
        </div>
        <div class="book-title">${book.bookTitle}</div>
        <div class="book-author">by ${book.authorName}</div>
        <g:if test="${book.description}">
            <div class="book-description">${book.description}</div>
        </g:if>
        <div class="book-footer">
            <span><i class="bi bi-book"></i> ${book.totalCopies} TOTAL</span>
            <g:if test="${book.archivedAt}">
                <span><i class="bi bi-archive"></i> <g:formatDate date="${book.archivedAt}" format="MMM d, yyyy"/></span>
            </g:if>
        </div>
        <div class="book-actions">
            <g:form action="restore" method="post" style="display:inline; flex:1">
                <input type="hidden" name="id" value="${book.id}"/>
                <button type="submit" class="btn-restore">
                    <i class="bi bi-arrow-counterclockwise"></i> Restore
                </button>
            </g:form>
        </div>
    </div>
</g:each>
<g:if test="${!books}">
    <div class="empty-state">
        <i class="bi bi-archive"></i>
        <p>No archived books found</p>
    </div>
</g:if>

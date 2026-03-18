<meta name="layout" content="main"/>

<div class="card-white">
    <h2>Available Books</h2>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>
    <g:if test="${flash.success}">
        <div class="alert alert-success">${flash.success}</div>
    </g:if>

    <table class="table">
        <thead>
        <tr>
            <th>Title</th>
            <th>Author</th>
            <th>Available Copies</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${books}" var="book">
            <tr>
                <td>${book.bookTitle}</td>
                <td>${book.authorName}</td>
                <td>${book.availableCopies}</td>
                <td>
                    <g:if test="${book.availableCopies > 0}">
                        <g:form controller="memberArea" action="borrow" method="post" style="display:inline">
                            <input type="hidden" name="bookId" value="${book.id}"/>
                            <button type="submit" class="btn btn-sm btn-success">Borrow</button>
                        </g:form>
                    </g:if>
                    <g:else>
                        <g:if test="${reservedBookIds?.contains(book.id)}">
                            <span class="badge bg-secondary">Reserved</span>
                        </g:if>
                        <g:else>
                            <g:form controller="memberArea" action="reserve" method="post" style="display:inline">
                                <input type="hidden" name="bookId" value="${book.id}"/>
                                <button type="submit" class="btn btn-sm btn-outline-primary">Reserve</button>
                            </g:form>
                        </g:else>
                    </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

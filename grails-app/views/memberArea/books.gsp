<meta name="layout" content="main"/>

<div class="card-white">
<<<<<<< HEAD
    <h2><g:message code="member.books.title"/></h2>
=======
    <h2>Available Books</h2>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>
    <g:if test="${flash.success}">
        <div class="alert alert-success">${flash.success}</div>
    </g:if>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e

    <table class="table">
        <thead>
        <tr>
<<<<<<< HEAD
            <th><g:message code="member.books.col.title"/></th>
            <th><g:message code="member.books.col.author"/></th>
            <th><g:message code="member.books.col.copies"/></th>
            <th><g:message code="member.books.col.action"/></th>
=======
            <th>Title</th>
            <th>Author</th>
            <th>Available Copies</th>
            <th>Action</th>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
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
<<<<<<< HEAD
                            <button type="submit" class="btn btn-sm btn-success"><g:message code="member.books.borrow"/></button>
=======
                            <button type="submit" class="btn btn-sm btn-success">Borrow</button>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                        </g:form>
                    </g:if>
                    <g:else>
                        <g:if test="${reservedBookIds?.contains(book.id)}">
<<<<<<< HEAD
                            <span class="badge bg-secondary"><g:message code="member.books.reserved"/></span>
=======
                            <span class="badge bg-secondary">Reserved</span>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                        </g:if>
                        <g:else>
                            <g:form controller="memberArea" action="reserve" method="post" style="display:inline">
                                <input type="hidden" name="bookId" value="${book.id}"/>
<<<<<<< HEAD
                                <button type="submit" class="btn btn-sm btn-outline-primary"><g:message code="member.books.reserve"/></button>
=======
                                <button type="submit" class="btn btn-sm btn-outline-primary">Reserve</button>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                            </g:form>
                        </g:else>
                    </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<meta name="layout" content="main"/>

<div class="card-white">
    <h2><g:message code="member.books.title"/></h2>

    <table class="table">
        <thead>
        <tr>
            <th><g:message code="member.books.col.title"/></th>
            <th><g:message code="member.books.col.author"/></th>
            <th><g:message code="member.books.col.copies"/></th>
            <th><g:message code="member.books.col.action"/></th>
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
                            <button type="submit" class="btn btn-sm btn-success"><g:message code="member.books.borrow"/></button>
                        </g:form>
                    </g:if>
                    <g:else>
                        <g:if test="${reservedBookIds?.contains(book.id)}">
                            <span class="badge bg-secondary"><g:message code="member.books.reserved"/></span>
                        </g:if>
                        <g:else>
                            <g:form controller="memberArea" action="reserve" method="post" style="display:inline">
                                <input type="hidden" name="bookId" value="${book.id}"/>
                                <button type="submit" class="btn btn-sm btn-outline-primary"><g:message code="member.books.reserve"/></button>
                            </g:form>
                        </g:else>
                    </g:else>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

<meta name="layout" content="main"/>

<div class="card-white">
    <h2><g:message code="member.borrow.title"/></h2>

    <table class="table">
        <thead>
        <tr>
            <th><g:message code="member.borrow.col.book"/></th>
            <th><g:message code="member.borrow.col.borrowDate"/></th>
            <th><g:message code="member.borrow.col.dueDate"/></th>
            <th><g:message code="member.borrow.col.returnDate"/></th>
            <th><g:message code="member.borrow.col.status"/></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${borrows}" var="b">
            <tr>
                <td>${b.book?.bookTitle}</td>
                <td><g:formatDate date="${b.borrowDate}" format="yyyy-MM-dd"/></td>
                <td><g:formatDate date="${b.dueDate}" format="yyyy-MM-dd"/></td>
                <td><g:formatDate date="${b.returnDate}" format="yyyy-MM-dd"/></td>
                <td>${b.status}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

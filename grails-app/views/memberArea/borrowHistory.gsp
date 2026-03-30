<meta name="layout" content="main"/>

<div class="card-white">
<<<<<<< HEAD
    <h2><g:message code="member.borrow.title"/></h2>
=======
    <h2>My Borrow History</h2>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e

    <table class="table">
        <thead>
        <tr>
<<<<<<< HEAD
            <th><g:message code="member.borrow.col.book"/></th>
            <th><g:message code="member.borrow.col.borrowDate"/></th>
            <th><g:message code="member.borrow.col.dueDate"/></th>
            <th><g:message code="member.borrow.col.returnDate"/></th>
            <th><g:message code="member.borrow.col.status"/></th>
=======
            <th>Book</th>
            <th>Borrow Date</th>
            <th>Due Date</th>
            <th>Return Date</th>
            <th>Status</th>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
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
<<<<<<< HEAD
=======

>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e

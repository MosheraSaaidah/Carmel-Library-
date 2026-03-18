<meta name="layout" content="main"/>

<div class="card-white">
    <h2>My Borrow History</h2>

    <table class="table">
        <thead>
        <tr>
            <th>Book</th>
            <th>Borrow Date</th>
            <th>Due Date</th>
            <th>Return Date</th>
            <th>Status</th>
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


<meta name="layout" content="main"/>

<div class="card-white">
    <h2>All Reservations</h2>
    <p class="text-muted">View all reservations in the system (read‑only).</p>

    <table class="table table-sm align-middle">
        <thead>
        <tr>
            <th>#</th>
            <th>Book</th>
            <th>Member</th>
            <th>Status</th>
            <th>Created At</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${reservations}" var="r" status="i">
            <tr>
                <td>${i + 1}</td>
                <td>${r.book?.bookTitle}</td>
                <td>${r.member?.fullName}</td>
                <td>${r.status}</td>
                <td><g:formatDate date="${r.dateCreated}" format="yyyy-MM-dd HH:mm"/></td>
            </tr>
        </g:each>
        <g:if test="${!(reservations ?: [])}">
            <tr>
                <td colspan="5" class="text-center text-muted">No reservations found.</td>
            </tr>
        </g:if>
        </tbody>
    </table>
</div>


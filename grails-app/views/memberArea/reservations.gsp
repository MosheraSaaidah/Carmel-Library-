<meta name="layout" content="main"/>

<div class="card-white">
    <h2>My Reservations</h2>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>
    <g:if test="${flash.success}">
        <div class="alert alert-success">${flash.success}</div>
    </g:if>

    <table class="table">
        <thead>
        <tr>
            <th>Book</th>
            <th>Status</th>
            <th>Created At</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${reservations}" var="r">
            <tr>
                <td>${r.book?.bookTitle}</td>
                <td>${r.status}</td>
                <td><g:formatDate date="${r.dateCreated}" format="yyyy-MM-dd HH:mm"/></td>
                <td>
                    <g:if test="${r.status in ['ACTIVE','NOTIFIED']}">
                        <g:form controller="memberArea" action="cancelReservation" method="post" style="display:inline">
                            <input type="hidden" name="id" value="${r.id}"/>
                            <button type="submit" class="btn btn-sm btn-outline-danger">Cancel</button>
                        </g:form>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

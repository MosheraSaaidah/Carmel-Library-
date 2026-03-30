<meta name="layout" content="main"/>

<div class="card-white">
<<<<<<< HEAD
    <h2><g:message code="member.reservations.title"/></h2>
=======
    <h2>My Reservations</h2>

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
            <th><g:message code="member.reservations.col.book"/></th>
            <th><g:message code="member.reservations.col.status"/></th>
            <th><g:message code="member.reservations.col.created"/></th>
            <th><g:message code="member.reservations.col.action"/></th>
=======
            <th>Book</th>
            <th>Status</th>
            <th>Created At</th>
            <th>Action</th>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
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
<<<<<<< HEAD
                            <button type="submit" class="btn btn-sm btn-outline-danger"><g:message code="member.reservations.cancel"/></button>
=======
                            <button type="submit" class="btn btn-sm btn-outline-danger">Cancel</button>
>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
                        </g:form>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

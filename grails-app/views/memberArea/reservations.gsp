<meta name="layout" content="main"/>

<div class="card-white">
    <h2><g:message code="member.reservations.title"/></h2>

    <table class="table">
        <thead>
        <tr>
            <th><g:message code="member.reservations.col.book"/></th>
            <th><g:message code="member.reservations.col.status"/></th>
            <th><g:message code="member.reservations.col.created"/></th>
            <th><g:message code="member.reservations.col.action"/></th>
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
                            <button type="submit" class="btn btn-sm btn-outline-danger"><g:message code="member.reservations.cancel"/></button>
                        </g:form>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>

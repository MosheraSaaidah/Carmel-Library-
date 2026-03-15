<meta name="layout" content="main"/>

<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Archived Members</div>
        <div class="page-subtitle">Members marked as inactive. They remain in the database for reporting and history. Restore to make them active again.</div>
    </div>
    <g:link controller="member" action="index" class="btn btn-outline-primary">
        <i class="bi bi-people"></i> Back to Active Members
    </g:link>
</div>

<div class="card-white">
    <table class="members-table">
        <thead>
        <tr>
            <th>FULL NAME</th>
            <th>CONTACT INFO</th>
            <th>ARCHIVED AT</th>
            <th>ACTIONS</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${members}" var="member">
            <tr>
                <td>
                    <div class="d-flex align-items-center gap-3">
                        <div class="avatar">${member.getFirstChar()}</div>
                        <div>
                            <div class="member-name">${member.fullName}</div>
                            <div class="member-since">MEMBER SINCE ${member.memberSinceYear}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="member-email">${member.email}</div>
                    <div class="member-phone">${member.phoneNumber ?: '-'}</div>
                </td>
                <td>
                    <g:if test="${member.archivedAt}">
                        <g:formatDate date="${member.archivedAt}" format="MMM d, yyyy"/>
                        <g:if test="${member.archivedBy}">
                            <div class="text-muted" style="font-size:12px">by ${member.archivedBy.fullName}</div>
                        </g:if>
                    </g:if>
                    <g:else>—</g:else>
                </td>
                <td>
                    <g:form action="restore" method="post" style="display:inline">
                        <input type="hidden" name="id" value="${member.id}"/>
                        <button type="submit" class="btn btn-sm btn-success">
                            <i class="bi bi-arrow-counterclockwise"></i> Restore
                        </button>
                    </g:form>
                </td>
            </tr>
        </g:each>
        <g:if test="${!members}">
            <tr>
                <td colspan="4" class="text-center py-5 text-muted">
                    <i class="bi bi-archive" style="font-size:40px;display:block;margin-bottom:12px"></i>
                    No archived members
                </td>
            </tr>
        </g:if>
        </tbody>
    </table>
</div>

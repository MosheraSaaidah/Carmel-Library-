<meta name="layout" content="main"/>

<!-- Header -->
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Library Members</div>
        <div class="page-subtitle">Manage registered readers and their status. (Active only)</div>
    </div>
    <div class="d-flex gap-2">
        <g:link controller="member" action="archived" class="btn btn-outline-secondary">
            <i class="bi bi-archive"></i> Archived Members
        </g:link>
    </div>
</div>

<!-- Members Table -->
<div class="card-white">
    <table class="members-table">
        <thead>
        <tr>
            <th>FULL NAME</th>
            <th>CONTACT INFO</th>
            <th>CITY</th>
            <th>ACTIONS</th>
        </tr>
        </thead>
        <tbody id="membersTableBody">
        <g:each in="${members}" var="member">
            <g:set var="memberNameAttr" value="${(member.fullName ?: '').replaceAll(/\s+/, ' ').trim()}"/>
            <g:set var="memberAddrAttr" value="${(member.address ?: '').replaceAll(/\s+/, ' ').trim()}"/>
            <tr>
                <!-- Full Name + Avatar -->
                <td>
                    <div class="d-flex align-items-center gap-3">
                        <div class="avatar">${member.getFirstChar()}</div>
                        <div>
                            <div class="member-name">${member.fullName}</div>
                            <div class="member-since">
                                MEMBER SINCE ${member.memberSinceYear}
                            </div>
                        </div>
                    </div>
                </td>

                <!-- Contact -->
                <td>
                    <div class="member-email">${member.email}</div>
                    <div class="member-phone">${member.phoneNumber ?: '-'}</div>
                </td>

                <!-- City -->
                <td>
                    <div class="member-address">${member.address ?: '-'}</div>
                </td>

                <!-- Actions -->
                <td>
                    <div class="d-flex gap-2 justify-content-end">
                        <button type="button" class="btn-icon-edit btn-edit-member"
                                data-bs-toggle="modal"
                                data-bs-target="#editMemberModal"
                                data-member-id="${member.id}"
                                data-full-name="${memberNameAttr}"
                                data-email="${member.email}"
                                data-phone="${member.phoneNumber ?: ''}"
                                data-address="${memberAddrAttr}">
                            <i class="bi bi-pencil"></i>
                        </button>

                        <g:form action="archive" method="post" style="display:inline" class="archive-confirm-form"
                                data-archive-title="Archive this member?"
                                data-archive-text="They will not appear in the active list but remain in the database for history.">
                            <input type="hidden" name="id" value="${member.id}"/>
                            <button type="submit" class="btn-icon-delete" title="Archive member">
                                <i class="bi bi-archive"></i>
                            </button>
                        </g:form>
                    </div>
                </td>
            </tr>
        </g:each>

        <!-- Empty State -->
        <g:if test="${!members}">
            <tr>
                <td colspan="4" class="text-center py-5 text-muted">
                    <i class="bi bi-people" style="font-size:40px;display:block;margin-bottom:12px"></i>
                    No members registered yet
                </td>
            </tr>
        </g:if>
        </tbody>
    </table>
</div>
<g:render template="modals"/>
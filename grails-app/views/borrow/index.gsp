<meta name="layout" content="main"/>

<!-- Header -->
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <div class="page-title">Borrowing System</div>
        <div class="page-subtitle">Track book loans, due dates, and late returns.</div>
    </div>
    <div class="d-flex gap-2">
        <button class="btn-green" data-bs-toggle="modal" data-bs-target="#addBorrowModal">
            <i class="bi bi-plus-lg"></i> New Borrowing
        </button>
    </div>
</div>

<!-- Filter Tabs -->
<div class="filter-tabs">
    <g:link action="index" params="[filter: 'ALL']"
            class="filter-tab ${currentFilter == 'ALL' ? 'active' : ''}">
        ALL
    </g:link>
    <g:link action="index" params="[filter: 'BORROWED']"
            class="filter-tab ${currentFilter == 'BORROWED' ? 'active' : ''}">
        BORROWED
    </g:link>
    <g:link action="index" params="[filter: 'LATE']"
            class="filter-tab ${currentFilter == 'LATE' ? 'active' : ''}">
        LATE
    </g:link>
    <g:link action="index" params="[filter: 'RETURNED']"
            class="filter-tab ${currentFilter == 'RETURNED' ? 'active' : ''}">
        RETURNED
    </g:link>
</div>

<!-- Borrows Table -->
<div class="card-white">
    <table class="borrow-table">
        <thead>
        <tr>
            <th>BOOK & MEMBER</th>
            <th>DATES</th>
            <th>STATUS</th>
            <th>ACTION</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${borrows}" var="borrow">
            <tr>
                <td>
                    <div class="borrow-book-title">${borrow.book?.bookTitle}</div>
                    <div class="borrow-member-name">${borrow.member?.fullName}</div>
                </td>

                <td>
                    <div class="d-flex flex-column gap-1">
                        <div class="d-flex gap-2 align-items-center">
                            <span class="date-label">BORROW:</span>
                            <span class="date-value">
                                <g:formatDate date="${borrow.borrowDate}" format="M/d/yyyy"/>
                            </span>
                        </div>
                        <div class="d-flex gap-2 align-items-center">
                            <span class="date-label">DUE:</span>
                            <span class="date-value ${borrow.status == 'LATE' ? 'late' : ''}">
                                <g:formatDate date="${borrow.dueDate}" format="M/d/yyyy"/>
                            </span>
                        </div>
                        <g:if test="${borrow.returnDate}">
                            <div class="d-flex gap-2 align-items-center">
                                <span class="date-label" style="color:#16a34a">RETURN:</span>
                                <span class="date-value" style="color:#16a34a">
                                    <g:formatDate date="${borrow.returnDate}" format="M/d/yyyy"/>
                                </span>
                            </div>
                        </g:if>
                    </div>
                </td>

                <td>
                    <g:if test="${borrow.status == 'BORROWED'}">
                        <span class="badge-borrowed">BORROWED</span>
                    </g:if>
                    <g:elseif test="${borrow.status == 'LATE'}">
                        <span class="badge-late">LATE</span>
                    </g:elseif>
                    <g:else>
                        <span class="badge-returned">RETURNED</span>
                    </g:else>
                </td>

                <td>
                    <div class="d-flex gap-2 align-items-center justify-content-end">
                        <g:if test="${borrow.status == 'BORROWED' || borrow.status == 'LATE'}">
                            <g:form action="returnBook" method="post" style="display:inline">
                                <input type="hidden" name="id" value="${borrow.id}"/>
                                <button type="submit" class="btn-return">
                                    RETURN BOOK
                                </button>
                            </g:form>
                        </g:if>
                        %{-- Borrowing records are never deleted; they are kept for history and statistics. --}%
                    </div>
                </td>
            </tr>
        </g:each>

        <g:if test="${!borrows}">
            <tr>
                <td colspan="4" class="text-center py-5 text-muted">
                    <i class="bi bi-clock-history"
                       style="font-size:40px;display:block;margin-bottom:12px"></i>
                    No borrowings found
                </td>
            </tr>
        </g:if>
        </tbody>
    </table>
</div>
<g:render template="modals"/>
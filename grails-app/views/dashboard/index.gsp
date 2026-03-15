<meta name="layout" content="main"/>

<div class="d-flex justify-content-between align-items-start mb-4">
%{--    page header--}%
    <div>
        <div class="page-title">Library Overview</div>
        <div class="page-subtitle">Real-time statistics for Carmel Library management.</div>
    </div>
</div>

%{-- Stats chart--}%
<div class="stats-grid">

    <div class="stat-card">
        <div class="stat-icon icon-blue"><i class="bi bi-people"></i></div>
        <div class="stat-label">ACTIVE MEMBERS</div>
        <div class="stat-value">${members}</div>
    </div>

    <div class="stat-card">
        <div class="stat-icon icon-green"><i class="bi bi-book"></i></div>
        <div class="stat-label">ACTIVE BOOKS (COPIES)</div>
        <div class="stat-value">${totalBooks}</div>
    </div>

    <div class="stat-card">
        <div class="stat-icon icon-purple"><i class="bi bi-tag"></i></div>
        <div class="stat-label">CATEGORIES</div>
        <div class="stat-value">${categories}</div>
    </div>

    <div class="stat-card">
        <div class="stat-icon icon-orange"><i class="bi bi-journal-bookmark"></i></div>
        <div class="stat-label">ACTIVE BORROWS</div>
        <div class="stat-value">${activeLoans}</div>
    </div>

    <div class="stat-card">
        <div class="stat-icon icon-red"><i class="bi bi-exclamation-circle"></i></div>
        <div class="stat-label">LATE RETURNS</div>
        <div class="stat-value">${lateReturns}</div>
    </div>

    <div class="stat-card">
        <div class="stat-icon icon-teal"><i class="bi bi-calendar-check"></i></div>
        <div class="stat-label">BORROWED TODAY</div>
        <div class="stat-value">${borrowedToday}</div>
    </div>

    <g:link controller="member" action="archived" class="stat-card text-decoration-none text-dark">
        <div class="stat-icon icon-blue"><i class="bi bi-archive"></i></div>
        <div class="stat-label">ARCHIVED MEMBERS</div>
        <div class="stat-value">${archivedMembers ?: 0}</div>
    </g:link>

    <g:link controller="book" action="archived" class="stat-card text-decoration-none text-dark">
        <div class="stat-icon icon-green"><i class="bi bi-archive"></i></div>
        <div class="stat-label">ARCHIVED BOOKS</div>
        <div class="stat-value">${archivedBookTitles ?: 0}</div>
    </g:link>

</div>

%{-- BOTTOM SECTION  Bottom Section --}%
<div class="row mt-4">

%{-- Chart --}%
    <div class="col-8">
        <div class="card-white">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="fw-bold" style="font-size:16px">
                    Borrowing Activity (Last 7 Days)
                </div>
                <span class="text-muted" style="font-size:12px">TRENDS</span>
            </div>
            <canvas id="borrowingChart" height="100"
                    data-labels="[${borrowingActivity?.collect { '"' + it.date + '"' }?.join(',') ?: ''}]"
                    data-values="[${borrowingActivity?.collect { it.count }?.join(',') ?: ''}]">
            </canvas>
        </div>
    </div>

%{--   Quick Management --}%
    <div class="col-4">
        <div class="card-white">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div class="fw-bold" style="font-size:16px">Quick Management</div>
                <span class="text-muted" style="font-size:12px">SHORTCUTS</span>
            </div>

            <div class="quick-actions">
                <g:link controller="book" action="index" class="quick-action-item">
                    <div class="quick-icon"><i class="bi bi-plus-lg"></i></div>
                    <div>
                        <div class="quick-title">Add New Book</div>
                        <div class="quick-sub">UPDATE INVENTORY</div>
                    </div>
                </g:link>

                <g:link controller="member" action="index" class="quick-action-item">
                    <div class="quick-icon"><i class="bi bi-person-plus"></i></div>
                    <div>
                        <div class="quick-title">ADD Member</div>
                        <div class="quick-sub">NEW REGISTRATION</div>
                    </div>
                </g:link>

                <g:link controller="borrow" action="index" class="quick-action-item">
                    <div class="quick-icon"><i class="bi bi-arrow-left-right"></i></div>
                    <div>
                        <div class="quick-title">New Borrowing</div>
                        <div class="quick-sub">CHECK OUT BOOK</div>
                    </div>
                </g:link>
            </div>
        </div>
    </div>

</div>
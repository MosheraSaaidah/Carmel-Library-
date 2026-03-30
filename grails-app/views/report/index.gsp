<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="main" xmlns:g="http://www.w3.org/1999/xhtml"/>
<g:set var="selectedType" value="${reportType ?: 'BORROWING'}"/>
<!--Header-->
<div class="page-header">
    <div>
        <h1 class="page-title">Reports</h1>
        <p class="page-subtitle">Analyze library data over time with dynamic filters and charts.</p>
    </div>
</div>
<!--reports-filters-->
<div class="row g-3 reports-filters">
    <div class="col-12">
        <div class="card">
            <div class="card-body">
                <g:form controller="report" action="index" method="get" class="row g-3 align-items-end">
                    <div class="col-md-3">
                        <label class="form-label">Report Type</label>
                        <g:select name="reportType"
                                  from="${[
                                          'BORROWING'       : 'Borrowing Report',
                                          'BOOKS'           : 'Books Statistics',
                                          'USERS'           : 'Members Activity',
                                          'BOOK_HISTORY'    : 'Book History',
                                          'MEMBER_HISTORY'  : 'Member History',
                                          'POPULAR_BOOKS'   : 'Popular Books',
                                          'MONTHLY'         : 'Monthly Borrowing',
                                          'YEARLY'          : 'Yearly Statistics'
                                  ]}"
                                  optionKey="key"
                                  optionValue="value"
                                  value="${selectedType}"
                                  class="form-select"/>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">From Date</label>
                        <g:set var="fromVal" value="${params.fromDate instanceof Date ? params.fromDate : null}"/>
                        <input type="date" name="fromDate" class="form-control"
                               value="${fromVal ? g.formatDate(date: fromVal, format: 'yyyy-MM-dd') : ''}"/>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">To Date</label>
                        <g:set var="toVal" value="${params.toDate instanceof Date ? params.toDate : null}"/>
                        <input type="date" name="toDate" class="form-control"
                               value="${toVal ? g.formatDate(date: toVal, format: 'yyyy-MM-dd') : ''}"/>
                    </div>

                    <div class="col-md-2">
                        <label class="form-label">Year</label>
                        <input type="number" name="year" class="form-control"
                               min="2000" max="2026" value="${params.year ?: ''}"/>
                    </div>
                    <div class="col-md-2">
                        <label class="form-label">Month</label>
                        <g:select name="month"
                                  from="${(1..12).collect{ it.toString().padLeft(2,'0') }}"
                                  value="${params.month ?: ''}"
                                  noSelection="['':'All']"
                                  class="form-select"/>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Category</label>
                        <g:select name="categoryId"
                                  from="${allCategories}"
                                  optionKey="id"
                                  optionValue="categoryName"
                                  value="${params.long('categoryId')}"
                                  noSelection="['':'All categories']"
                                  class="form-select"/>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Member</label>
                        <g:select name="memberId"
                                  from="${allMembers}"
                                  optionKey="id"
                                  optionValue="fullName"
                                  value="${params.long('memberId')}"
                                  noSelection="['':'All members']"
                                  class="form-select"/>
                    </div>

                    <div class="col-md-3">
                        <label class="form-label">Book (for Book History)</label>
                        <g:select name="bookId"
                                  from="${allBooks}"
                                  optionKey="id"
                                  optionValue="bookTitle"
                                  value="${params.long('bookId')}"
                                  noSelection="['':'All books']"
                                  class="form-select"/>
                    </div>

                    <div class="col-md-12 d-flex justify-content-end gap-2 flex-wrap">
                        <div class="me-auto">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-funnel"></i> Apply Filters
                            </button>
                            <g:link controller="report" action="index" class="btn btn-outline-secondary ms-1">
                                Clear
                            </g:link>
                        </div>

                        <div>
                            <g:link controller="report" action="exportPdf"
                                    params="${params}"
                                    class="btn btn-outline-primary">
                                <i class="bi bi-file-earmark-pdf"></i> Download PDF
                            </g:link>
                            <g:link controller="report" action="exportExcel"
                                    params="${params}"
                                    class="btn btn-outline-success ms-1">
                                <i class="bi bi-file-earmark-excel"></i> Download Excel
                            </g:link>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
    </div>
</div>

<div class="row g-3 mt-1">

<!-- selectedType == 'BORROWING'   -->
    <g:if test="${selectedType == 'BORROWING'}">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Borrowing Report</h5>
                    <small class="text-muted">
                        From
                        <strong><g:if test="${borrowingReport.fromDate}"><g:formatDate date="${borrowingReport.fromDate}" format="yyyy-MM-dd"/></g:if></strong>
                        to
                        <strong><g:if test="${borrowingReport.toDate}"><g:formatDate date="${borrowingReport.toDate}" format="yyyy-MM-dd"/></g:if></strong>
                    </small>
                </div>
                <div class="card-body">
                    <div class="row mb-4">
                        <div class="col-md-4">
                            <div class="stat-card">
                                <div class="stat-label">Total Borrowings</div>
                                <div class="stat-value">${borrowingReport.totalBorrowings ?: 0}</div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <h6>Most Borrowed Books</h6>
                            <div class="table-responsive">
                                <table class="table table-sm align-middle">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Book</th>
                                        <th>Author</th>
                                        <th>Borrow Count</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${borrowingReport.mostBorrowedBooks ?: []}" var="row" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td>${row.book?.bookTitle}</td>
                                            <td>${row.book?.authorName}</td>
                                            <td>${row.count}</td>
                                        </tr>
                                    </g:each>
                                    <g:if test="${!(borrowingReport.mostBorrowedBooks ?: [])}">
                                        <tr>
                                            <td colspan="4" class="text-muted text-center">No data for this period.</td>
                                        </tr>
                                    </g:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!--   Most Active Member -->
                        <div class="col-md-6">
                            <h6>Most Active Member</h6>
                            <div class="table-responsive">
                                <table class="table table-sm align-middle">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Member</th>
                                        <th>Borrow Count</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${borrowingReport.mostActiveUsers ?: []}" var="row" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td>${row.member?.fullName}</td>
                                            <td>${row.count}</td>
                                        </tr>
                                    </g:each>
                                    <g:if test="${!(borrowingReport.mostActiveUsers ?: [])}">
                                        <tr>
                                            <td colspan="3" class="text-muted text-center">No data for this period.</td>
                                        </tr>
                                    </g:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:if>

<!-- selectedType == 'BOOKS'   -->
    <g:if test="${selectedType == 'BOOKS'}">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Books Statistics</h5>
                </div>
                <div class="card-body">
                    <div class="row g-3">
                        <div class="col-md-3">
                            <div class="stat-card">
                                <div class="stat-label">Total Copies</div>
                                <div class="stat-value">${booksStatistics.totalCopies ?: 0}</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card">
                                <div class="stat-label">Available Copies</div>
                                <div class="stat-value">${booksStatistics.availableCopies ?: 0}</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card">
                                <div class="stat-label">Borrowed Copies</div>
                                <div class="stat-value">${booksStatistics.borrowedCopies ?: 0}</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card">
                                <div class="stat-label">Active Titles</div>
                                <div class="stat-value">${booksStatistics.activeTitles ?: 0}</div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stat-card">
                                <div class="stat-label">Reserved Titles</div>
                                <div class="stat-value">${booksStatistics.reservedTitles ?: 0}</div>
                            </div>
                        </div>
                    </div>

                    <div class="row mt-4">
                        <div class="col-md-6">
                            <canvas id="booksStatusChart"
                                    data-active="${booksStatistics.activeTitles ?: 0}"
                                    data-archived="${booksStatistics.archivedTitles ?: 0}"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:if>

<!--   selectedType == 'USERS' -->
    <g:if test="${selectedType == 'USERS'}">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Members Activity</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="table-responsive">
                                <table class="table table-sm align-middle">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Member</th>
                                        <th>Borrow Count</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${memberActivity ?: []}" var="row" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td>${row.member?.fullName}</td>
                                            <td>${row.count}</td>
                                        </tr>
                                    </g:each>
                                    <g:if test="${!(memberActivity ?: [])}">
                                        <tr>
                                            <td colspan="3" class="text-muted text-center">No activity for this period.</td>
                                        </tr>
                                    </g:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="col-md-6">
                                    <canvas id="memberActivityChart"
                                            data-labels="${(memberActivity ?: []).collect{ it.member?.fullName } as grails.converters.JSON}"
                                            data-values="${(memberActivity ?: []).collect{ it.count } as grails.converters.JSON}"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:if>

<!--selectedType == 'POPULAR_BOOKS-->
    <g:if test="${selectedType == 'POPULAR_BOOKS'}">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">Popular Books</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="table-responsive">
                                <table class="table table-sm align-middle">
                                    <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Book</th>
                                        <th>Borrow Count</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${popularBooks ?: []}" var="row" status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td>${row.book?.bookTitle}</td>
                                            <td>${row.count}</td>
                                        </tr>
                                    </g:each>
                                    <g:if test="${!(popularBooks ?: [])}">
                                        <tr>
                                            <td colspan="3" class="text-muted text-center">No data for this period.</td>
                                        </tr>
                                    </g:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <canvas id="popularBooksChart"
                                    data-labels="${(popularBooks ?: []).collect{ it.book?.bookTitle } as grails.converters.JSON}"
                                    data-values="${(popularBooks ?: []).collect{ it.count } as grails.converters.JSON}"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:if>

<!--selectedType == 'BOOK_HISTORY-->
    <g:if test="${selectedType == 'BOOK_HISTORY'}">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Book Borrow History</h5>
                </div>
                <div class="card-body">
                    <g:if test="${bookHistory?.book}">
                        <h6>Book: ${bookHistory.book.bookTitle}</h6>
                        <p class="text-muted">
                            Total Borrowings: ${bookHistory.totalBorrowings ?: 0} |
                            Late Borrows: ${bookHistory.lateBorrows ?: 0}
                        </p>
                        <div class="table-responsive">
                            <table class="table table-sm align-middle">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Member</th>
                                    <th>Borrow Date</th>
                                    <th>Due Date</th>
                                    <th>Return Date</th>
                                    <th>Status</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${bookHistory.borrows ?: []}" var="b" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td>${b.member?.fullName}</td>
                                        <td><g:formatDate date="${b.borrowDate}" format="yyyy-MM-dd"/></td>
                                        <td><g:formatDate date="${b.dueDate}" format="yyyy-MM-dd"/></td>
                                        <td><g:if test="${b.returnDate}"><g:formatDate date="${b.returnDate}" format="yyyy-MM-dd"/></g:if></td>
                                        <td>${b.status}</td>
                                    </tr>
                                </g:each>
                                <g:if test="${!(bookHistory.borrows ?: [])}">
                                    <tr>
                                        <td colspan="6" class="text-muted text-center">No borrow history for this book.</td>
                                    </tr>
                                </g:if>
                                </tbody>
                            </table>
                        </div>
                    </g:if>
                    <g:else>
                        <p class="text-muted">Please choose a book from the filters above and click Apply Filters.</p>
                    </g:else>
                </div>
            </div>
        </div>
    </g:if>

<!--selectedType == 'MEMBER_HISTORY'-->
    <g:if test="${selectedType == 'MEMBER_HISTORY'}">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Member Borrow History</h5>
                </div>
                <div class="card-body">
                    <g:if test="${memberHistory?.member}">
                        <h6>Member: ${memberHistory.member.fullName}</h6>
                        <p class="text-muted">
                            Total Borrowings: ${memberHistory.totalBorrowings ?: 0} |
                            Late Borrows: ${memberHistory.lateBorrows ?: 0} |
                            Total Late Days: ${memberHistory.totalFees ?: 0}
                        </p>
                        <div class="table-responsive">
                            <table class="table table-sm align-middle">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Book</th>
                                    <th>Borrow Date</th>
                                    <th>Due Date</th>
                                    <th>Return Date</th>
                                    <th>Status</th>
                                </tr>
                                </thead>
                                <tbody>
                                <g:each in="${memberHistory.borrows ?: []}" var="b" status="i">
                                    <tr>
                                        <td>${i + 1}</td>
                                        <td>${b.book?.bookTitle}</td>
                                        <td><g:formatDate date="${b.borrowDate}" format="yyyy-MM-dd"/></td>
                                        <td><g:formatDate date="${b.dueDate}" format="yyyy-MM-dd"/></td>
                                        <td><g:if test="${b.returnDate}"><g:formatDate date="${b.returnDate}" format="yyyy-MM-dd"/></g:if></td>
                                        <td>${b.status}</td>
                                    </tr>
                                </g:each>
                                <g:if test="${!(memberHistory.borrows ?: [])}">
                                    <tr>
                                        <td colspan="6" class="text-muted text-center">No borrow history for this member.</td>
                                    </tr>
                                </g:if>
                                </tbody>
                            </table>
                        </div>
                    </g:if>
                    <g:else>
                        <p class="text-muted">Please choose a member from the filters above and click Apply Filters.</p>
                    </g:else>
                </div>
            </div>
        </div>
    </g:if>

<!--    selectedType in ['MONTHLY', 'YEARLY ']-->
    <g:if test="${selectedType in ['MONTHLY', 'YEARLY']}">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Borrowing Over Time</h5>
                    <small class="text-muted">Monthly and Yearly statistics</small>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-8">
                            <canvas id="borrowingOverTimeChart"
                                    data-month-labels="${(monthlyBorrowing ?: []).collect{ it.month } as grails.converters.JSON}"
                                    data-month-values="${(monthlyBorrowing ?: []).collect{ it.count } as grails.converters.JSON}"
                                    data-year-labels="${(yearlyBorrowing ?: []).collect{ it.year } as grails.converters.JSON}"
                                    data-year-values="${(yearlyBorrowing ?: []).collect{ it.count } as grails.converters.JSON}"></canvas>
                        </div>
                        <div class="col-md-4">
                            <h6>Yearly Summary</h6>
                            <div class="table-responsive">
                                <table class="table table-sm align-middle">
                                    <thead>
                                    <tr>
                                        <th>Year</th>
                                        <th>Borrowings</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <g:each in="${yearlyBorrowing ?: []}" var="row">
                                        <tr>
                                            <td>${row.year}</td>
                                            <td>${row.count}</td>
                                        </tr>
                                    </g:each>
                                    <g:if test="${!(yearlyBorrowing ?: [])}">
                                        <tr>
                                            <td colspan="2" class="text-muted text-center">No data available.</td>
                                        </tr>
                                    </g:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:if>
</div>


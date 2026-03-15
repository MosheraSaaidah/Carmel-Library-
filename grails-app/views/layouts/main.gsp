<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title>Carmel Library</title>
    <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 32 32'%3E%3Crect width='32' height='32' rx='6' fill='%2316a34a'/%3E%3Cpath d='M8 10v12c0 .5.4 1 1 1h4V9H9c-.6 0-1 .4-1 1z' fill='white' opacity='.9'/%3E%3Cpath d='M24 10v12c0 .5-.4 1-1 1h-4V9h4c.6 0 1 .4 1 1z' fill='white' opacity='.9'/%3E%3Cpath d='M16 9v14h-3V9h3zm0 0v14h3V9h-3z' fill='white'/%3E%3C/svg%3E"/>

%{-- Bootstrap 5--}%
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet"/>

%{-- Bootstrap Icons--}%
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css"
          rel="stylesheet"/>

%{--  SweetAlert2--}%
    <link href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css"
          rel="stylesheet"/>

%{--    CSS--}%
    <asset:stylesheet src="application.css"/>

    %{-- CSS خاص بكل صفحة --}%
    <g:if test="${controllerName == 'book'}">
        <asset:stylesheet src="book.css"/>
    </g:if>
    <g:if test="${controllerName == 'member'}">
        <asset:stylesheet src="members.css"/>
    </g:if>
    <g:if test="${controllerName == 'borrow'}">
        <asset:stylesheet src="borrow.css"/>
    </g:if>
    <g:if test="${controllerName == 'category'}">
        <asset:stylesheet src="categories.css"/>
        %{-- صفحة عرض التصنيف تستخدم كروت الكتب --}%
        <asset:stylesheet src="book.css"/>
    </g:if>
    <g:if test="${controllerName == 'report'}">
        <asset:stylesheet src="reports.css"/>
    </g:if>

      <g:layoutHead/>
</head>

<body data-success="${flash.success}" data-error="${flash.error}">

<div class="app-wrapper">

    %{--   Sidebar   --}%
    <aside class="sidebar">
    %{--Logo--}%
        <div class="sidebar-logo">
            <div class="logo-icon">
                <i class="bi bi-bar-chart-fill"></i>
            </div>
            <div class="logo-text-wrap">
                <div class="logo-text-ar">مكتبة الكرمل</div>
                <div class="logo-text-en">CARMEL LIBRARY</div>
            </div>
        </div>
     %{-- Nav--}%
    <div class="sidebar-nav">
            <g:link controller="dashboard" action="index" class="nav-item ${controllerName == 'dashboard' ? 'active' : ''}">
                <i class="bi bi-grid-1x2"></i>
                <span>Dashboard</span>
            </g:link>

            <g:link controller="book" action="index" class="nav-item ${controllerName == 'book' ? 'active' : ''}">
                <i class="bi bi-book"></i>
                <span>Books Inventory</span>
            </g:link>

            <g:link controller="category" action="index" class="nav-item ${controllerName == 'category' ? 'active' : ''}">
                <i class="bi bi-tag"></i>
                <span>Categories</span>
            </g:link>

            <g:link controller="borrow" action="index" class="nav-item ${controllerName == 'borrow' ? 'active' : ''}">
                <i class="bi bi-clock-history"></i>
                <span>Borrowing System</span>
            </g:link>

            <g:link controller="member" action="index" class="nav-item ${controllerName == 'member' ? 'active' : ''}">
                <i class="bi bi-people"></i>
                <span>Members</span>
            </g:link>
            
            <g:link controller="report" action="index" class="nav-item ${controllerName == 'report' ? 'active' : ''}">
                <i class="bi bi-bar-chart-line"></i>
                <span>Reports</span>
            </g:link>
    </div>


    </aside>

    %{--   Main Content    --}%
    <main class="main-content">
        <g:layoutBody/>
</main>
</div>



%{--JS--}%

%{-- Bootstrap 5--}%
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

%{-- SweetAlert2--}%
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>

%{-- Chart.js--}%
<g:if test="${controllerName in ['dashboard', 'report']}">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</g:if>

<g:if test="${controllerName == 'dashboard'}">
    <asset:javascript src="dashboard.js"/>
</g:if>

<g:if test="${controllerName == 'report'}">
    <asset:javascript src="reports.js"/>
</g:if>

%{-- JS خاص بكل صفحة--}%
<g:if test="${controllerName == 'book'}">
    <asset:javascript src="book.js"/>
</g:if>
<g:if test="${controllerName == 'member'}">
    <asset:javascript src="members.js"/>
</g:if>

%{-- application.js — عام لكل الصفحات (Toast) --}%
<asset:javascript src="application.js"/>

</body>
</html>

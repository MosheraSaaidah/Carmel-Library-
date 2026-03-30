<!doctype html>
<g:set var="uiLang" value="${session?.uiLang ?: 'en'}"/>
<g:set var="isRtl" value="${uiLang == 'ar'}"/>
<g:set var="nextLang" value="${isRtl ? 'en' : 'ar'}"/>
<g:set var="returnUri" value="${request.forwardURI + (request.queryString ? '?' + request.queryString : '')}"/>
<html lang="${uiLang}" dir="${isRtl ? 'rtl' : 'ltr'}">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><g:message code="app.title"/></title>
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

<body class="${controllerName == 'auth' ? 'auth-page' : ''} ${isRtl ? 'lang-ar' : 'lang-en'}">

<g:set bean="securityService" var="securityService"/>

<div class="app-wrapper">

    %{--   Sidebar   --}%
    <g:if test="${securityService?.isLoggedIn(session)}">
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

            %{-- Nav --}%
            <div class="sidebar-nav">

                %{-- روابط ADMIN --}%
                <g:if test="${session.role == 'ADMIN'}">
                    <g:link controller="dashboard" action="index"
                            class="nav-item ${controllerName == 'dashboard' ? 'active' : ''}">
                        <i class="bi bi-grid-1x2"></i>
                        <span><g:message code="nav.dashboard"/></span>
                    </g:link>

                    <g:link controller="book" action="index"
                            class="nav-item ${controllerName == 'book' ? 'active' : ''}">
                        <i class="bi bi-book"></i>
                        <span><g:message code="nav.books"/></span>
                    </g:link>

                    <g:link controller="category" action="index"
                            class="nav-item ${controllerName == 'category' ? 'active' : ''}">
                        <i class="bi bi-tag"></i>
                        <span><g:message code="nav.categories"/></span>
                    </g:link>

                    <g:link controller="borrow" action="index"
                            class="nav-item ${controllerName == 'borrow' ? 'active' : ''}">
                        <i class="bi bi-clock-history"></i>
                        <span><g:message code="nav.borrowing"/></span>
                    </g:link>

                    <g:link controller="member" action="index"
                            class="nav-item ${controllerName == 'member' ? 'active' : ''}">
                        <i class="bi bi-people"></i>
                        <span><g:message code="nav.members"/></span>
                    </g:link>

                    <g:link controller="report" action="index"
                            class="nav-item ${controllerName == 'report' ? 'active' : ''}">
                        <i class="bi bi-bar-chart-line"></i>
                        <span><g:message code="nav.reports"/></span>
                    </g:link>

                    <g:link controller="reservationAdmin" action="index"
                            class="nav-item ${controllerName == 'reservationAdmin' ? 'active' : ''}">
                        <i class="bi bi-bookmark"></i>
                        <span><g:message code="nav.reservations"/></span>
                    </g:link>
                </g:if>

                %{-- روابط MEMBER --}%
                <g:if test="${session.role == 'MEMBER'}">
                    <g:link controller="memberArea" action="books"
                            class="nav-item ${controllerName == 'memberArea' && actionName == 'books' ? 'active' : ''}">
                        <i class="bi bi-book"></i>
                        <span><g:message code="nav.member.books"/></span>
                    </g:link>

                    <g:link controller="memberArea" action="borrowHistory"
                            class="nav-item ${controllerName == 'memberArea' && actionName == 'borrowHistory' ? 'active' : ''}">
                        <i class="bi bi-clock-history"></i>
                        <span><g:message code="nav.member.borrowHistory"/></span>
                    </g:link>

                    <g:link controller="memberArea" action="reservations"
                            class="nav-item ${controllerName == 'memberArea' && actionName == 'reservations' ? 'active' : ''}">
                        <i class="bi bi-bookmark"></i>
                        <span><g:message code="nav.member.reservations"/></span>
                    </g:link>
                </g:if>
            </div>

            %{-- Language + logout (footer) --}%
            <div class="sidebar-footer">
                <g:link controller="locale" action="switchLang"
                        params="[lang: nextLang, r: returnUri]"
                        class="nav-item nav-lang-toggle"
                        title="${message(code: 'lang.switch.aria')}">
                    <i class="bi bi-globe2" aria-hidden="true"></i>
                    <span>
                        <g:if test="${isRtl}"><g:message code="lang.switch.toEnglish"/></g:if>
                        <g:else><g:message code="lang.switch.toArabic"/></g:else>
                    </span>
                </g:link>
                <g:link controller="auth" action="logout"
                        class="nav-item">
                    <i class="bi bi-box-arrow-right"></i>
                    <span><g:message code="nav.logout"/></span>
                </g:link>
            </div>

        </aside>
    </g:if>

    %{--   Main Content    --}%
    <main class="main-content">
        %{-- Auth: login/register card centered; flash toasts fixed bottom-right of the viewport --}%
        <g:if test="${controllerName == 'auth'}">
            <div class="auth-page-inner">
                <g:layoutBody/>
                <div class="auth-messages">
                    <g:if test="${flash.success}">
                        <div class="alert alert-success alert-dismissible fade show mb-0 flash-banner auth-flash-banner" role="alert">
                            <span class="auth-flash-body">${flash.success}</span>
                            <button type="button" class="btn-close auth-flash-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </g:if>
                    <g:if test="${flash.error}">
                        <div class="alert alert-danger alert-dismissible fade show mb-0 flash-banner auth-flash-banner" role="alert">
                            <span class="auth-flash-body">${flash.error}</span>
                            <button type="button" class="btn-close auth-flash-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </g:if>
                </div>
            </div>
        </g:if>
        <g:else>
            %{-- Flash: server-rendered (non-auth pages: above page body) --}%
            <g:if test="${flash.success}">
                <div class="alert alert-success alert-dismissible fade show mb-4 flash-banner" role="alert">
                    ${flash.success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </g:if>
            <g:if test="${flash.error}">
                <div class="alert alert-danger alert-dismissible fade show mb-4 flash-banner" role="alert">
                    ${flash.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </g:if>
            <g:layoutBody/>
        </g:else>
</main>
</div>



%{--JS--}%

%{-- Bootstrap 5--}%
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.flash-banner').forEach(function (el) {
            setTimeout(function () {
                try {
                    bootstrap.Alert.getOrCreateInstance(el).close();
                } catch (e) { /* no-op */ }
            }, 3000);
        });
    });
</script>

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

%{-- Shared UI (archive confirm, category edit modal, etc.) — after SweetAlert2 --}%
<asset:javascript src="application.js"/>

</body>
</html>

<meta name="layout" content="main"/>

<div class="card-white" style="max-width:460px;width:100%; text-align:center;">
    <div style="margin-bottom:20px;">
        <div style="font-size:13px;color:#6b7280;letter-spacing:1px;text-transform:uppercase;">
            Welcome to
        </div>
        <div style="font-size:22px;font-weight:700;color:#111827;">
            AlCarmel Library
        </div>
        <div style="font-size:14px;color:#6b7280;margin-top:4px;">
            Create a new member account
        </div>
    </div>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>
    <g:if test="${flash.success}">
        <div class="alert alert-success">${flash.success}</div>
    </g:if>

    <g:form action="registerMember" method="post" style="text-align:left;">
        <div class="mb-3">
            <label>Full name</label>
            <input type="text"
                   name="fullName"
                   class="form-control"
                   value="${values?.fullName ?: ''}"
                   required/>
            <g:if test="${errors?.fullName}">
                <div class="text-danger small">${errors.fullName}</div>
            </g:if>
        </div>
        <div class="mb-3">
            <label>Email</label>
            <input type="email"
                   name="email"
                   class="form-control"
                   value="${values?.email ?: ''}"
                   required/>
            <g:if test="${errors?.email}">
                <div class="text-danger small">${errors.email}</div>
            </g:if>
        </div>
        <div class="mb-3">
            <label>Phone number</label>
            <input type="text" name="phoneNumber" class="form-control"
                   required
                   minlength="10"
                   maxlength="12"
                   value="${values?.phoneNumber ?: ''}"/>
            <g:if test="${errors?.phoneNumber}">
                <div class="text-danger small">${errors.phoneNumber}</div>
            </g:if>
        </div>
        <div class="mb-3">
            <label>City</label>
            <g:select name="address"
                      from="${alCarmel.Member.CITY_OPTIONS}"
                      class="form-select"
                      required="true"
                      value="${values?.address}"/>
            <g:if test="${errors?.address}">
                <div class="text-danger small">${errors.address}</div>
            </g:if>
        </div>
        <div class="mb-3">
            <label>Password</label>
            <input type="password"
                   name="password"
                   class="form-control"
                   required/>
            <g:if test="${errors?.password}">
                <div class="text-danger small">${errors.password}</div>
            </g:if>
        </div>
        <button type="submit" class="btn-green" style="width: 100%;">Register</button>
    </g:form>

    <div class="d-grid gap-2" style="margin-top:15px;">
        <g:link controller="auth" action="login"
                class="btn btn-outline-secondary"
                style="width:100%;text-align:center;">
            Back to login
        </g:link>
    </div>
</div>
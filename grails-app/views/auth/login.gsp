<meta name="layout" content="main"/>

<div class="card-white" style="max-width:420px;width:100%; text-align:center;">
    <div style="margin-bottom:20px;">
        <div style="font-size:13px;color:#6b7280;letter-spacing:1px;text-transform:uppercase;">
            Welcome to
        </div>
        <div style="font-size:22px;font-weight:700;color:#111827;">
            AlCarmel Library
        </div>
        <div style="font-size:14px;color:#6b7280;margin-top:4px;">
            Member & Admin Login
        </div>
    </div>

    <!-- Login form -->
    <g:form action="login" method="post" style="text-align:left;">
        <div class="mb-3">
            <label>Username or Email</label>
            <input type="text" name="username" class="form-control" required/>
        </div>
        <div class="mb-3">
            <label>Password</label>
            <input type="password" name="password" class="form-control" required/>
        </div>
        <button type="submit" class="btn-green" style="width: 100%;">Login</button>
    </g:form>

    <!-- Extra options -->
    <div class="d-grid gap-2" style="margin-top:20px;">
        <g:link controller="auth" action="registerMember"
                class="btn btn-outline-secondary"
                style="margin-bottom:10px;width:100%;text-align:center;">
            Create new account
        </g:link>
    </div>
</div>
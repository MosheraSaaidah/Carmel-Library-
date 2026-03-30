<meta name="layout" content="main"/>

<div class="card-white" style="max-width:400px;margin:40px auto;">
    <h2>Confirm Email</h2>

    <p>Please enter the email you used for registration and the confirmation code sent to you.</p>

    <g:form action="confirmCode" method="post">
        <div class="mb-3">
            <label>Email</label>
            <input type="email" name="email" class="form-control" required/>
        </div>

        <div class="mb-3">
            <label>Confirmation Code</label>
            <input type="text" name="code" class="form-control" required/>
        </div>

        <button type="submit" class="btn-green">Confirm</button>
    </g:form>

    <div class="mt-3">
        <g:link controller="auth" action="login">Back to login</g:link>
    </div>
</div>


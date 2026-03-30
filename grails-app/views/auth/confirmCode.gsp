<meta name="layout" content="main"/>

<div class="card-white" style="max-width:400px;margin:40px auto;">
    <h2>Confirm Email</h2>

<<<<<<< HEAD
=======
    <g:if test="${flash.error}">
        <div class="alert alert-danger">${flash.error}</div>
    </g:if>
    <g:if test="${flash.success}">
        <div class="alert alert-success">${flash.success}</div>
    </g:if>

>>>>>>> e66bcc56455ff1dabd506a74f52d86e3e725c50e
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


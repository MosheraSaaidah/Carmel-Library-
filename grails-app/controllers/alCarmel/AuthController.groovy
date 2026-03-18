package alCarmel

import grails.gorm.transactions.Transactional

class AuthController {

    EmailService emailService
    SecurityService securityService

    /**
     * Login screen + POST handler.
     * GET: renders login.gsp
     * POST: authenticates and redirects to dashboard.
     */
    def login() {
        // لو المستخدم مسجل دخول أصلًا، نعيد توجيهه حسب دوره
        if (securityService.isLoggedIn(session)) {
            def currentUser = securityService.getCurrentUser(session)
            if (currentUser?.role == 'ADMIN') {
                redirect(controller: 'dashboard', action: 'index')
            } else if (currentUser?.role == 'MEMBER') {
                redirect(controller: 'memberArea', action: 'books')
            }
            return
        }

        if (request.method == 'GET') {
            return
        }

        String usernameOrEmail = params.username
        String password = params.password

        User user = User.findByUsername(usernameOrEmail) ?: User.findByEmail(usernameOrEmail)

        if (!user) {
            flash.error = "Invalid credentials."
            return
        }
        if (!securityService.passwordsMatch(password, user.passwordHash)) {
            flash.error = "Invalid credentials."
            return
        }
        if (!user.emailConfirmed) {
            flash.error = "Please confirm your email before logging in."
            return
        }
        if (!user.enabled) {
            flash.error = "Your account is disabled."
            return
        }

        session.userId = user.id
        session.role = user.role
        if(user.role == 'ADMIN')
        {
            redirect(controller: 'dashboard', action: 'index')
        }else if(user.role == "MEMBER"){
            redirect(controller: 'memberArea', action: 'books')
        } else  {
            redirect(controller: 'auth', action: 'login')
        }

    }

    def logout() {
        session.invalidate()
        redirect(action: 'login')
    }

    /**
     * Member self‑registration with email confirmation.
     */
    @Transactional
    def registerMember() {
        // لو المستخدم مسجل دخول، لا نسمح له بالتسجيل مرة أخرى
        if (securityService.isLoggedIn(session)) {
            def currentUser = securityService.getCurrentUser(session)
            if (currentUser?.role == 'ADMIN') {
                redirect(controller: 'dashboard', action: 'index')
            } else if (currentUser?.role == 'MEMBER') {
                redirect(controller: 'memberArea', action: 'books')
            }
            return
        }

        if (request.method == 'GET') {
            // clear any old login errors when opening registration
            flash.error = null
            return
        }

        String fullName = params.fullName?.trim()
        String email = params.email?.trim()
        String password = params.password
        String city = params.address   // uses Member.address as city

        // Simple per-field validation
        Map<String, String> fieldErrors = [:]

        if (!fullName) {
            fieldErrors.fullName = "Full name is required."
        }

        if (!email) {
            fieldErrors.email = "Email is required."
        } else if (!email.matches(/^[^@\s]+@[^@\s]+\.[^@\s]+$/)) {
            fieldErrors.email = "Please enter a valid email address."
        } else if (User.findByEmail(email)) {
            fieldErrors.email = "This email is already registered."
        }

        if (!params.phoneNumber) {
            fieldErrors.phoneNumber = "Phone number is required."
        }

        if (!city) {
            fieldErrors.address = "City is required."
        }

        if (!password) {
            fieldErrors.password = "Password is required."
        } else if (password.size() < 6) {
            fieldErrors.password = "Password must be at least 6 characters."
        }

        if (!fieldErrors.isEmpty()) {
            flash.error = "Please fix the highlighted fields."
            render(view: 'registerMember', model: [
                    errors: fieldErrors,
                    values: [
                            fullName   : fullName,
                            email      : email,
                            phoneNumber: params.phoneNumber,
                            address    : city
                    ]
            ])
            return
        }

        // Generate confirmation token (URL-based confirmation)
        String token = UUID.randomUUID().toString()

        // Create User (initially disabled until email is confirmed via link)
        def user = new User(
                username         : email,
                email            : email,
                passwordHash     : securityService.encodePassword(password),
                role             : "MEMBER",
                enabled          : false,
                emailConfirmed   : false,
                confirmationToken: token,
                confirmationCode : null
        )

        if (!user.save(flush: true)) {
            flash.error = "Registration failed. Please check your data."
            return
        }

        // Create Member linked to this User
        def member = new Member(
                fullName      : fullName,
                email         : email,
                phoneNumber   : params.phoneNumber,
                address       : city,
                membershipDate: new Date(),
                active        : true,
                user          : user
        )

        if (!member.save(flush: true)) {
            user.delete(flush: true)
            flash.error = "Registration failed. Please check member info."
            return
        }

        String subject = "Confirm your Carmel Library account"
        String confirmationLink = "http://localhost:8080/auth/confirmEmail?token=${token}"
        String body = """Hello ${fullName},

Please Confirm link:
${confirmationLink}

Thank you.
"""

        emailService.sendEmail(email, subject, body)
        flash.success = "Registration successful. Please check your email and click the confirmation link to activate your account."
        redirect(action: 'login')
    }

    /**
     * Email confirmation using token from email link (preferred).
     */
    @Transactional
    def confirmEmail() {
        String token = params.token
        if (!token) {
            flash.error = "Invalid confirmation link."
            redirect(action: 'login')
            return
        }

        User user = User.findByConfirmationToken(token)
        if (!user) {
            flash.error = "Invalid or expired confirmation link."
            redirect(action: 'login')
            return
        }

        user.emailConfirmed = true
        user.enabled = true
        user.confirmationToken = null
        user.save(flush: true)

        flash.success = "Your email has been confirmed. You can now log in."
        redirect(action: 'login')
    }
}

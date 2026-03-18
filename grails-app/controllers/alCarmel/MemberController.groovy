package alCarmel

class MemberController {

    MemberService memberService
    SecurityService securityService

    // Members listing page (active only)
    def index() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        [members: memberService.getMembers()]
    }

    // Archived members list (admin view)
    def archived() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        [members: memberService.getArchivedMembers()]
    }

    def save() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        def member = memberService.saveMember(params)

        if (member.hasErrors()) {
            def errorMsg = 'Please check the member details and try again.'
            def phoneError = member.errors.getFieldError('phoneNumber')
            if (phoneError?.code == 'invalidLength') {
                errorMsg = 'Phone number must be between 10 and 12 digits.'
            } else {
                def emailError = member.errors.getFieldError('email')
                if (emailError?.code == 'unique') {
                    errorMsg = 'This email is already registered.'
                }
            }
            flash.error = errorMsg
            redirect(action: 'index')
        } else {
            flash.success = 'Member registered successfully'
            redirect(action: 'index')
        }
    }

    def update() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        try {
            def member = memberService.updateMember(params.id as Long, params)

            if (member?.hasErrors()) {
                def errorMsg = 'Please check the member details and try again.'
                def phoneError = member.errors.getFieldError('phoneNumber')
                if (phoneError?.code == 'invalidLength') {
                    errorMsg = 'Phone number must be between 10 and 12 digits.'
                } else {
                    def emailError = member.errors.getFieldError('email')
                    if (emailError?.code == 'unique') {
                        errorMsg = 'This email is already registered.'
                    }
                }
                flash.error = errorMsg
                redirect(action: 'index')
            } else {
                flash.success = 'Member updated successfully'
                redirect(action: 'index')
            }
        } catch (Exception e) {
            flash.error = "Update failed: ${e.message}"
            redirect(action: 'index')
        }
    }

    // Soft delete: archive member instead of permanent delete
    def archive() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        try {
            memberService.archiveMember(params.long('id'))
            flash.success = 'Member archived successfully. They no longer appear in the active list but remain in the database for history.'
        } catch (Exception e) {
            def msg = e.cause?.message ?: e.message
            flash.error = msg ?: 'Cannot archive this member while they have active loans.'
        }
        redirect(action: 'index')
    }

    def restore() {
        if (!securityService.hasRole(session, "ADMIN")) {
            redirect(controller: 'auth', action: 'login')
            return
        }
        try {
            memberService.restoreMember(params.long('id'))
            flash.success = 'Member restored successfully. They are active again.'
        } catch (Exception e) {
            flash.error = "Restore failed: ${e.message}"
        }
        redirect(action: 'archived')
    }
}

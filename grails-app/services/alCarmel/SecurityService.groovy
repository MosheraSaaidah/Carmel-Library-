package alCarmel

import grails.gorm.transactions.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Transactional
class SecurityService {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder()

    String encodePassword(String rawPassword) {
        encoder.encode(rawPassword)
    }

    boolean passwordsMatch(String rawPassword, String encodedPassword) {
        encoder.matches(rawPassword, encodedPassword)
    }

    User getCurrentUser(def session) {
        def id = session.userId as Long
        id ? User.get(id) : null
    }

    boolean isLoggedIn(def session) {
        getCurrentUser(session) != null
    }

    boolean hasRole(def session, String role) {
        def user = getCurrentUser(session)
        return user &&
                user.role == role &&
                user.enabled &&
                user.emailConfirmed
    }
}

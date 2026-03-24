package alCarmel

import groovy.util.logging.Slf4j
import jakarta.mail.AuthenticationFailedException
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl

/**
 * Mail settings come from {@link Setting} (not application.yml). Not transactional so callers
 * control whether SMTP failures affect their transaction.
 */
@Slf4j
class EmailService {

    static transactional = false

    SettingService settingService

    /**
     * @return {@code true} if SMTP accepted the message; {@code false} if mail is not configured or send failed
     */
    boolean sendEmail(String to, String subject, String body) {
        String host = settingService.get(SettingService.MAIL_HOST)?.trim()
        String username = settingService.get(SettingService.MAIL_USERNAME)?.trim()
        // Gmail app passwords are often pasted with spaces; SMTP expects a single token.
        String password = (settingService.get(SettingService.MAIL_PASSWORD) ?: '').replaceAll(/\s+/, '')
        int port = settingService.getInt(SettingService.MAIL_PORT) ?: 587
        if (!host || !username) {
            log.warn('Mail not sent: mail.host or mail.username missing in settings.')
            return false
        }

        JavaMailSenderImpl sender = new JavaMailSenderImpl()
        sender.host = host
        sender.port = port
        sender.username = username
        sender.password = password ?: ''
        sender.javaMailProperties = [
                'mail.transport.protocol'              : 'smtp',
                'mail.smtp.auth'                       : 'true',
                'mail.smtp.starttls.enable'            : 'true',
                'mail.smtp.starttls.required'          : 'true',
        ] as Properties

        SimpleMailMessage message = new SimpleMailMessage()
        message.from = username
        message.to = to
        message.subject = subject
        message.text = body
        try {
            sender.send(message)
            return true
        } catch (MailException e) {
            if (e instanceof MailAuthenticationException || e.cause instanceof AuthenticationFailedException) {
                log.warn(
                        'SMTP login rejected for user {} (host {}). For Gmail use an App Password and mail.username = full email. Details: {}',
                        username, host, e.cause?.message ?: e.message)
                log.debug('SMTP auth failure', e)
            } else {
                log.error('Failed to send email to {}', to, e)
            }
            return false
        }
    }
}

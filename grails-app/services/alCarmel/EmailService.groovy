package alCarmel

import groovy.util.logging.Slf4j
import jakarta.mail.AuthenticationFailedException
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.MailException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper

/**
 * SMTP host/port: {@link SettingService#SMTP_HOST} / {@link SettingService#SMTP_PORT}.
 * Credentials: {@link SettingKey#MAIL_USERNAME}, {@link SettingKey#MAIL_PASSWORD} in {@link Setting}. Not transactional.
 */
@Slf4j
class EmailService {

    static transactional = false

    SettingService settingService

    /**
     * @return {@code true} if SMTP accepted the message; {@code false} if mail is not configured or send failed
     */
    boolean sendEmail(String to, String subject, String body) {
        String username = settingService.get(SettingKey.MAIL_USERNAME)?.trim()
        // Gmail app passwords are often pasted with spaces; SMTP expects a single token.
        String password = (settingService.get(SettingKey.MAIL_PASSWORD) ?: '').replaceAll(/\s+/, '')
        if (!username) {
            log.warn('Mail not sent: mail.username missing in settings.')
            return false
        }

        JavaMailSenderImpl sender = new JavaMailSenderImpl()
        sender.host = SettingService.SMTP_HOST
        sender.port = SettingService.SMTP_PORT
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
                        username, SettingService.SMTP_HOST, e.cause?.message ?: e.message)
                log.debug('SMTP auth failure', e)
            } else {
                log.error('Failed to send email to {}', to, e)
            }
            return false
        }
    }
    boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        String username = settingService.get(SettingKey.MAIL_USERNAME)?.trim()
        String password = (settingService.get(SettingKey.MAIL_PASSWORD) ?: '').replaceAll(/\s+/, '')
        if (!username) {
            log.warn('Mail not sent: mail.username missing in settings.')
            return false
        }

        JavaMailSenderImpl sender = new JavaMailSenderImpl()
        sender.host = SettingService.SMTP_HOST
        sender.port = SettingService.SMTP_PORT
        sender.username = username
        sender.password = password ?: ''
        sender.javaMailProperties = [
                'mail.transport.protocol'              : 'smtp',
                'mail.smtp.auth'                       : 'true',
                'mail.smtp.starttls.enable'            : 'true',
                'mail.smtp.starttls.required'          : 'true',
        ] as Properties

        try {
            def mimeMessage = sender.createMimeMessage()
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, 'UTF-8')
            helper.setFrom(username)
            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(htmlBody, true)
            sender.send(mimeMessage)
            return true
        } catch (MailException e) {
            if (e instanceof MailAuthenticationException || e.cause instanceof AuthenticationFailedException) {
                log.warn(
                        'SMTP login rejected for user {} (host {}). For Gmail use an App Password and mail.username = full email. Details: {}',
                        username, SettingService.SMTP_HOST, e.cause?.message ?: e.message)
                log.debug('SMTP auth failure', e)
            } else {
                log.error('Failed to send email to {}', to, e)
            }
            return false
        }
    }
}

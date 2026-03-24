package alCarmel

import grails.gorm.transactions.Transactional
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender


@Transactional
class EmailService {

    JavaMailSender mailSender
    void sendEmail(String to , String subject ,String body){

       if (!mailSender) {
           return
       }


       SimpleMailMessage message = new SimpleMailMessage()
       message.setTo(to)
       message.setSubject(subject)
       message.setText(body)
       mailSender.send(message)
   }

}

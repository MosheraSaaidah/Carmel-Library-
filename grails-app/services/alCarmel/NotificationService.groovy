package alCarmel

import grails.gorm.transactions.Transactional

@Transactional
class NotificationService {

    EmailService emailService
    SettingService settingService

    int getReminderDaysBeforeDue() {
        settingService.getInt(SettingKey.NOTIFICATION_REMINDER_DAYS) ?: 1
    }

    
    void sendDueDateReminders(Date explicitToday = null) {
        if (!emailService) {
            return
        }

        Date today = explicitToday ?: new Date()

        int daysBefore = getReminderDaysBeforeDue()
        Calendar cal = Calendar.getInstance()
        cal.setTime(today)
        
        // start of today (midnight)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_MONTH, daysBefore)
        Date startOfReminderDay = cal.getTime()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        Date endOfReminderDay = cal.getTime()

        Borrow.findAllByStatusAndDueDateBetween(
                "BORROWED",
                startOfReminderDay,
                endOfReminderDay
        ).each { Borrow b ->
            if (!b.member?.email) {
                return
            }
            String dayText = daysBefore == 1 ? "tomorrow" : "in ${daysBefore} days"
            String subject = "Due date reminder - Carmel Library"
            String body = """Hello ${b.member.fullName},

        This is a friendly reminder that the book "${b.book.bookTitle}" is due ${dayText}.
        
        Due date: ${b.dueDate}
        
        Please return the book on time to avoid late fees.
        """
            emailService.sendEmail(b.member.email, subject, body)
        }
    }

  
    void sendLateNotices(Date explicitToday = null) {
        if (!emailService) {
            return
        }

        Date today = explicitToday ?: new Date()

        Calendar cal = Calendar.getInstance()
        cal.setTime(today)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        Date startOfToday = cal.getTime()

        // All borrows that are overdue today (BORROWED or LATE)
        Borrow.findAllByStatusInListAndDueDateLessThan(
                ["BORROWED", "LATE"],
                startOfToday
        ).each { Borrow b ->
            if (!b.member?.email || !b.dueDate) {
                return
            }

            int daysLate = (int) ((startOfToday.time - b.dueDate.time) / (24 * 60 * 60 * 1000))
            if (daysLate <= 0) {
                return
            }

            int fee = daysLate // $1 per day

            String subject = "Late notice - Carmel Library"
            String body = """Hello ${b.member.fullName},

        You are ${daysLate} day(s) late in returning the book "${b.book.bookTitle}".
        Current late fee: \$${fee}.
        
        Please return the book as soon as possible to avoid increasing the fee.
        """
            emailService.sendEmail(b.member.email, subject, body)
        }
    }

   
    void notifyReservationsForBook(Book book) {
        if (!emailService || !book) {
            return
        }

        if (book.availableCopies <= 0) {
            return
        }

        List<Reservation> reservations = Reservation.findAllByBookAndStatus(
                book,
                "ACTIVE",
                [sort: 'dateCreated', order: 'asc']
        )

        reservations.each { Reservation r ->
            if (!r.member?.email) {
                return
            }

            String subject = "Reserved book now available - Carmel Library"
            String body = """Hello ${r.member.fullName},

            The book you reserved "${book.bookTitle}" is now available.
            
            Please visit the library soon to borrow it.
            """
            emailService.sendEmail(r.member.email, subject, body)

            r.status = "NOTIFIED"
            r.save()
        }
    }
}


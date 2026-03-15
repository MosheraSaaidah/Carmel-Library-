package alCarmel

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class BorrowServiceSpec extends Specification {

    BorrowService borrowService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Borrow(...).save(flush: true, failOnError: true)
        //new Borrow(...).save(flush: true, failOnError: true)
        //Borrow borrow = new Borrow(...).save(flush: true, failOnError: true)
        //new Borrow(...).save(flush: true, failOnError: true)
        //new Borrow(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //borrow.id
    }

    void "test get"() {
        setupData()

        expect:
        borrowService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Borrow> borrowList = borrowService.list(max: 2, offset: 2)

        then:
        borrowList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        borrowService.count() == 5
    }

    void "test delete"() {
        Long borrowId = setupData()

        expect:
        borrowService.count() == 5

        when:
        borrowService.delete(borrowId)
        sessionFactory.currentSession.flush()

        then:
        borrowService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Borrow borrow = new Borrow()
        borrowService.save(borrow)

        then:
        borrow.id != null
    }
}

package alCarmel

import grails.gorm.transactions.Transactional
import javax.sql.DataSource
@Transactional
class BootStrap {

    DataSource dataSource
    SecurityService securityService
    SettingService settingService


    def init = { servletContext ->

        settingService.initDefaults()

        // Ensure there is always a valid ADMIN user with known credentials.
        def admin = User.findByUsername('admin') ?: new User(username: 'admin')
        admin.email = 'admin@gmail.com'
        admin.passwordHash = securityService.encodePassword('admin123')
        admin.role = 'ADMIN'
        admin.enabled = true
        admin.emailConfirmed = true
        admin.confirmationCode = null
        admin.save(failOnError: true)

        addSoftDeleteColumnsIfMissing()

        def defaultCategories = [
                "Action", "Adventure", "Biography", "Children",
                "Drama", "Educational", "Fantasy", "Historical",
                "Horror", "Mystery", "Philosophy", "Religious",
                "Romantic", "Science Fiction", "Self Development",
                "Technology"
        ]
        defaultCategories.each { categoryName ->
            if (!Category.findByCategoryName(categoryName)) {
                new Category(categoryName: categoryName).save()
            }
        }

        Member.list().each { m ->
            if (m.active == null) {
                m.active = true
                m.save(flush: true)
            }
        }
        Book.list().each { b ->
            if (b.active == null) {
                b.active = true
                b.save(flush: true)
            }
        }
    }

    private void addSoftDeleteColumnsIfMissing() {
        def sql = new groovy.sql.Sql(dataSource)
        [
                [table: 'member', column: 'active', type: 'BOOLEAN DEFAULT TRUE'],
                [table: 'member', column: 'date_created', type: 'TIMESTAMP'],
                [table: 'member', column: 'last_updated', type: 'TIMESTAMP'],
                [table: 'member', column: 'archived_at', type: 'TIMESTAMP'],
                [table: 'member', column: 'archived_by_id', type: 'BIGINT'],
                [table: 'book', column: 'active', type: 'BOOLEAN DEFAULT TRUE'],
                [table: 'book', column: 'date_created', type: 'TIMESTAMP'],
                [table: 'book', column: 'last_updated', type: 'TIMESTAMP'],
                [table: 'book', column: 'archived_at', type: 'TIMESTAMP'],
                [table: 'book', column: 'archived_by_id', type: 'BIGINT'],
                [table: 'borrow', column: 'date_created', type: 'TIMESTAMP'],
                [table: 'borrow', column: 'last_updated', type: 'TIMESTAMP']
        ].each { def it ->
            try {
                sql.execute("ALTER TABLE ${it.table} ADD COLUMN ${it.column} ${it.type}")
            } catch (Exception e) {
                // Column already exists or DB dialect differs - ignore
            }
        }
        sql.close()
    }

    def destroy = {
    }

}

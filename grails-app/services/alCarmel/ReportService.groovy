package alCarmel

import grails.gorm.transactions.Transactional
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.lowagie.text.Document
import com.lowagie.text.Paragraph
import com.lowagie.text.PageSize
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter

@Transactional(readOnly = true)
class ReportService {

    private Date toDate(LocalDate localDate) {
        Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    /** Converts Date to LocalDate using getTime() only; avoids java.sql.Date.toInstant() which throws. */
    private static LocalDate toLocalDate(Date d) {
        if (d == null) return null
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Builds the same model map used by ReportController.index so it can be reused
     * for HTML rendering and for export (PDF / Excel) with identical filters.
     */
    Map<String, Object> buildReportModel(String reportType, def params) {
        def borrowingReport = [:]
        def booksStatistics = [:]
        def popularBooks = []
        def userActivity = []
        def monthlyBorrowing = []
        def yearlyBorrowing = []

        switch (reportType) {
            case "BORROWING":
                borrowingReport = getBorrowingReport(params)
                break
            case "BOOKS":
                booksStatistics = getBooksStatistics()
                break
            case "USERS":
                userActivity = getUserActivity(params)
                break
            case "POPULAR_BOOKS":
                popularBooks = getPopularBooks(params)
                break
            case "MONTHLY":
                Integer year = params.int('year') ?: LocalDate.now().year
                monthlyBorrowing = getMonthlyBorrowingReport(year, params)
                yearlyBorrowing = getYearlyBorrowingReport(params)
                break
            case "YEARLY":
                yearlyBorrowing = getYearlyBorrowingReport(params)
                break
            default:
                borrowingReport = getBorrowingReport(params)
                reportType = "BORROWING"
        }

        [
                reportType      : reportType,
                borrowingReport : borrowingReport,
                booksStatistics : booksStatistics,
                popularBooks    : popularBooks,
                userActivity    : userActivity,
                monthlyBorrowing: monthlyBorrowing,
                yearlyBorrowing : yearlyBorrowing
        ]
    }

    Map<String, Object> getBorrowingReport(Map params) {
        def range = resolveDateRange(params)
        Date from = range.from
        Date to = range.to

        Long categoryId = params.long('categoryId') ?: null
        Long memberId = params.long('memberId') ?: null

        String baseWhere = "b.borrowDate between :from and :to"
        Map queryParams = [from: from, to: to]

        if (categoryId) {
            baseWhere += " and b.book.category.id = :categoryId"
            queryParams.categoryId = categoryId
        }
        if (memberId) {
            baseWhere += " and b.member.id = :memberId"
            queryParams.memberId = memberId
        }

        String countHql = "select count(b.id) from Borrow b where " + baseWhere
        Long totalBorrowings = Borrow.executeQuery(
                countHql,
                queryParams
        )[0] as Long ?: 0L

        String mostBooksHql = """select b.book, count(b.id) 
                   from Borrow b 
                   where """ + baseWhere + """ 
                   group by b.book 
                   order by count(b.id) desc"""
        List mostBorrowedBooks = Borrow.executeQuery(
                mostBooksHql,
                queryParams,
                [max: 10]
        ).collect { [book: it[0] as Book, count: it[1] as Long] }

        String mostUsersHql = """select b.member, count(b.id) 
                   from Borrow b 
                   where """ + baseWhere + """ 
                   group by b.member 
                   order by count(b.id) desc"""
        List mostActiveUsers = Borrow.executeQuery(
                mostUsersHql,
                queryParams,
                [max: 10]
        ).collect { [member: it[0] as Member, count: it[1] as Long] }

        [
                fromDate         : from,
                toDate           : to,
                totalBorrowings  : totalBorrowings,
                mostBorrowedBooks: mostBorrowedBooks,
                mostActiveUsers  : mostActiveUsers
        ]
    }

    /**
     * Export the selected report as CSV (Excel-friendly).
     */
    void exportReportToExcel(String reportType, Map params, OutputStream out) {
        Map model = buildReportModel(reportType, params)
        StringBuilder sb = new StringBuilder()

        switch (model.reportType) {
            case "BORROWING":
                sb.append("User Name,Book Title,Borrow Date,Return Date,Category,City\n")
                Borrow.withCriteria {
                    // reuse same filters as getBorrowingReport
                    def range = resolveDateRange(params)
                    between 'borrowDate', range.from, range.to
                    if (params.long('categoryId')) {
                        book {
                            eq 'category.id', params.long('categoryId')
                        }
                    }
                    if (params.long('memberId')) {
                        member {
                            eq 'id', params.long('memberId')
                        }
                    }
                }.each { Borrow b ->
                    def df = new java.text.SimpleDateFormat("yyyy-MM-dd")
                    sb.append("\"${b.member?.fullName ?: ''}\",")
                    sb.append("\"${b.book?.bookTitle ?: ''}\",")
                    sb.append("${b.borrowDate ? df.format(b.borrowDate) : ''},")
                    sb.append("${b.returnDate ? df.format(b.returnDate) : ''},")
                    sb.append("\"${b.book?.category?.categoryName ?: ''}\",")
                    sb.append("\"${b.member?.address ?: ''}\"\n")
                }
                break

            case "USERS":
                sb.append("User Name,Borrow Count\n")
                (model.userActivity ?: []).each { row ->
                    sb.append("\"${row.member?.fullName ?: ''}\",")
                    sb.append("${row.count ?: 0}\n")
                }
                break

            case "POPULAR_BOOKS":
                sb.append("Book Title,Borrow Count\n")
                (model.popularBooks ?: []).each { row ->
                    sb.append("\"${row.book?.bookTitle ?: ''}\",")
                    sb.append("${row.count ?: 0}\n")
                }
                break

            case "BOOKS":
                sb.append("Metric,Value\n")
                sb.append("Total Copies,${model.booksStatistics.totalCopies ?: 0}\n")
                sb.append("Available Copies,${model.booksStatistics.availableCopies ?: 0}\n")
                sb.append("Borrowed Copies,${model.booksStatistics.borrowedCopies ?: 0}\n")
                sb.append("Active Titles,${model.booksStatistics.activeTitles ?: 0}\n")
                sb.append("Archived Titles,${model.booksStatistics.archivedTitles ?: 0}\n")
                break

            case "MONTHLY":
            case "YEARLY":
                sb.append("Year,Borrowings\n")
                (model.yearlyBorrowing ?: []).each { row ->
                    sb.append("${row.year},${row.count}\n")
                }
                break
        }

        out.write(sb.toString().getBytes("UTF-8"))
    }

    /**
     * Export the selected report as a simple PDF with a title, date, and table.
     */
    void exportReportToPDF(String reportType, Map params, OutputStream out) {
        Map model = buildReportModel(reportType, params)
        Document document = new Document(PageSize.A4.rotate())
        PdfWriter.getInstance(document, out)
        document.open()

        String title = "Library Report - " + model.reportType
        document.add(new Paragraph(title))
        document.add(new Paragraph("Generated at: " + new Date().toString()))
        document.add(new Paragraph("System: Carmel Library Management System"))
        document.add(new Paragraph(" "))

        PdfPTable table

        switch (model.reportType) {
            case "BORROWING":
                table = new PdfPTable(6)
                ["User Name", "Book Title", "Borrow Date", "Return Date", "Category", "City"].each { table.addCell(it) }
                Borrow.withCriteria {
                    def range = resolveDateRange(params)
                    between 'borrowDate', range.from, range.to
                    if (params.long('categoryId')) {
                        book {
                            eq 'category.id', params.long('categoryId')
                        }
                    }
                    if (params.long('memberId')) {
                        member {
                            eq 'id', params.long('memberId')
                        }
                    }
                }.each { Borrow b ->
                    table.addCell(b.member?.fullName ?: "")
                    table.addCell(b.book?.bookTitle ?: "")
                    table.addCell(b.borrowDate ? b.borrowDate.toString() : "")
                    table.addCell(b.returnDate ? b.returnDate.toString() : "")
                    table.addCell(b.book?.category?.categoryName ?: "")
                    table.addCell(b.member?.address ?: "")
                }
                break

            case "USERS":
                table = new PdfPTable(2)
                ["User Name", "Borrow Count"].each { table.addCell(it) }
                (model.userActivity ?: []).each { row ->
                    table.addCell(row.member?.fullName ?: "")
                    table.addCell((row.count ?: 0).toString())
                }
                break

            case "POPULAR_BOOKS":
                table = new PdfPTable(2)
                ["Book Title", "Borrow Count"].each { table.addCell(it) }
                (model.popularBooks ?: []).each { row ->
                    table.addCell(row.book?.bookTitle ?: "")
                    table.addCell((row.count ?: 0).toString())
                }
                break

            case "BOOKS":
                table = new PdfPTable(2)
                ["Metric", "Value"].each { table.addCell(it) }
                def stats = model.booksStatistics ?: [:]
                [["Total Copies", stats.totalCopies],
                 ["Available Copies", stats.availableCopies],
                 ["Borrowed Copies", stats.borrowedCopies],
                 ["Active Titles", stats.activeTitles],
                 ["Archived Titles", stats.archivedTitles]].each { row ->
                    table.addCell(row[0]?.toString() ?: "")
                    table.addCell((row[1] ?: 0).toString())
                }
                break

            case "MONTHLY":
            case "YEARLY":
                table = new PdfPTable(2)
                ["Year", "Borrowings"].each { table.addCell(it) }
                (model.yearlyBorrowing ?: []).each { row ->
                    table.addCell(row.year?.toString() ?: "")
                    table.addCell((row.count ?: 0).toString())
                }
                break

            default:
                table = new PdfPTable(1)
                table.addCell("No data for this report type.")
        }

        if (table) {
            document.add(table)
        }

        document.close()
    }

    Map<String, Object> getBooksStatistics() {
        Long totalBooks = Book.executeQuery(
                "select coalesce(sum(b.totalCopies), 0) from Book b"
        )[0] as Long ?: 0L

        Long totalActiveCopies = Book.executeQuery(
                "select coalesce(sum(b.totalCopies), 0) from Book b where b.active = true"
        )[0] as Long ?: 0L

        Long availableBooks = Book.executeQuery(
                "select coalesce(sum(b.availableCopies), 0) from Book b where b.active = true"
        )[0] as Long ?: 0L

        Long borrowedCopies = totalActiveCopies - availableBooks

        Long activeTitles = Book.countByActive(true)
        Long archivedTitles = Book.countByActive(false)

        [
                totalCopies     : totalBooks,
                availableCopies : availableBooks,
                borrowedCopies  : borrowedCopies,
                activeTitles    : activeTitles,
                archivedTitles  : archivedTitles
        ]
    }

    List<Map<String, Object>> getPopularBooks(Map params) {
        def range = resolveDateRange(params)
        Date from = range.from
        Date to = range.to

        Long categoryId = params.long('categoryId') ?: null
        Long memberId = params.long('memberId') ?: null

        String baseWhere = "b.borrowDate between :from and :to"
        Map queryParams = [from: from, to: to]

        if (categoryId) {
            baseWhere += " and b.book.category.id = :categoryId"
            queryParams.categoryId = categoryId
        }
        if (memberId) {
            baseWhere += " and b.member.id = :memberId"
            queryParams.memberId = memberId
        }

        String hql = """select b.book, count(b.id) 
                   from Borrow b 
                   where """ + baseWhere + """ 
                   group by b.book 
                   order by count(b.id) desc"""
        Borrow.executeQuery(
                hql,
                queryParams,
                [max: 10]
        ).collect { [book: it[0] as Book, count: it[1] as Long] }
    }

    List<Map<String, Object>> getUserActivity(Map params) {
        def range = resolveDateRange(params)
        Date from = range.from
        Date to = range.to

        Long categoryId = params.long('categoryId') ?: null
        Long memberId = params.long('memberId') ?: null

        String baseWhere = "b.borrowDate between :from and :to"
        Map queryParams = [from: from, to: to]

        if (categoryId) {
            baseWhere += " and b.book.category.id = :categoryId"
            queryParams.categoryId = categoryId
        }
        if (memberId) {
            baseWhere += " and b.member.id = :memberId"
            queryParams.memberId = memberId
        }

        String hql = """select b.member, count(b.id) 
                   from Borrow b 
                   where """ + baseWhere + """ 
                   group by b.member 
                   order by count(b.id) desc"""
        Borrow.executeQuery(
                hql,
                queryParams,
                [max: 20]
        ).collect { [member: it[0] as Member, count: it[1] as Long] }
    }

    List<Map<String, Object>> getMonthlyBorrowingReport(Integer year, Map params = [:]) {
        if (!year) {
            year = LocalDate.now().year
        }

        Long categoryId = params.long('categoryId') ?: null
        Long memberId = params.long('memberId') ?: null

        String baseWhere = "year(b.borrowDate) = :year"
        Map queryParams = [year: year]

        if (categoryId) {
            baseWhere += " and b.book.category.id = :categoryId"
            queryParams.categoryId = categoryId
        }
        if (memberId) {
            baseWhere += " and b.member.id = :memberId"
            queryParams.memberId = memberId
        }

        String hql = """select month(b.borrowDate), count(b.id) 
                   from Borrow b 
                   where """ + baseWhere + """ 
                   group by month(b.borrowDate) 
                   order by month(b.borrowDate)"""
        Borrow.executeQuery(
                hql,
                queryParams
        ).collect { [month: it[0] as Integer, count: it[1] as Long] }
    }

    List<Map<String, Object>> getYearlyBorrowingReport(Map params = [:]) {
        Long categoryId = params.long('categoryId') ?: null
        Long memberId = params.long('memberId') ?: null

        String baseWhere = "1=1"
        Map queryParams = [:]

        if (categoryId) {
            baseWhere += " and b.book.category.id = :categoryId"
            queryParams.categoryId = categoryId
        }
        if (memberId) {
            baseWhere += " and b.member.id = :memberId"
            queryParams.memberId = memberId
        }

        String hql = """select year(b.borrowDate), count(b.id) 
                   from Borrow b 
                   where """ + baseWhere + """
                   group by year(b.borrowDate) 
                   order by year(b.borrowDate)"""
        Borrow.executeQuery(
                hql,
                queryParams
        ).collect { [year: it[0] as Integer, count: it[1] as Long] }
    }

    Map<String, Date> resolveDateRange(Map params) {
        LocalDate now = LocalDate.now()
        LocalDate fromLocal
        LocalDate toLocal

        Integer year = params.int('year')
        Integer month = params.int('month')

        if (year && month) {
            fromLocal = LocalDate.of(year, month, 1)
            toLocal = fromLocal.plusMonths(1).minusDays(1)
        } else if (year) {
            fromLocal = LocalDate.of(year, 1, 1)
            toLocal = LocalDate.of(year, 12, 31)
        } else {
            fromLocal = now.minusMonths(1)
            toLocal = now
        }

        if (params.fromDate instanceof Date) {
            fromLocal = toLocalDate(params.fromDate)
        }
        if (params.toDate instanceof Date) {
            toLocal = toLocalDate(params.toDate)
        }

        [
                from: toDate(fromLocal),
                to  : toDate(toLocal.plusDays(1)) // inclusive end
        ]
    }
}


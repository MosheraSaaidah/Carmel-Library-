package alCarmel

class ReportController {

    ReportService reportService

    static allowedMethods = [
            index      : "GET",
            exportPdf  : "GET",
            exportExcel: "GET"
    ]

    def index() {
        String reportType = params.reportType ?: "BORROWING"

        // Parse optional date filters from params (yyyy-MM-dd)
        params.fromDate = parseDateParam(params.fromDate)
        params.toDate = parseDateParam(params.toDate)

        Map model = reportService.buildReportModel(reportType, params)
        // For history reports we want to be able to see archived items too,
        // so we provide full lists (active + archived) for the dropdowns.
        model.allMembers = Member.list(sort: 'fullName')
        model.allCategories = Category.list(sort: 'categoryName')
        model.allBooks = Book.list(sort: 'bookTitle')
        model
    }

    def exportPdf() {
        String reportType = params.reportType ?: "BORROWING"
        params.fromDate = parseDateParam(params.fromDate)
        params.toDate = parseDateParam(params.toDate)

        String fileName = "${reportType.toLowerCase()}-report-${System.currentTimeMillis()}.pdf"
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        reportService.exportReportToPDF(reportType, params, baos)
        render(file: baos.toByteArray(), contentType: "application/pdf", fileName: fileName)
    }

    def exportExcel() {
        String reportType = params.reportType ?: "BORROWING"
        params.fromDate = parseDateParam(params.fromDate)
        params.toDate = parseDateParam(params.toDate)

        String fileName = "${reportType.toLowerCase()}-report-${System.currentTimeMillis()}.csv"
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        reportService.exportReportToExcel(reportType, params, baos)
        render(file: baos.toByteArray(), contentType: "text/csv", fileName: fileName)
    }

    private Date parseDateParam(Object val) {
        if (!val) return null
        if (val instanceof Date) return (Date) val
        try {
            return java.sql.Date.valueOf(val.toString())
        } catch (Exception ignored) {
            return null
        }
    }
}


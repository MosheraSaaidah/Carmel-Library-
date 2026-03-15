## Carmel Library Management System – Technical Documentation (Grails)

This document explains **all major parts of the Carmel Library Management System**, built with **Grails**, and covers:
- System components (domains, controllers, services, views, reporting).
- How each business process flows from start to end (add book, register member, borrow, return, archive, reporting, etc.).
- Where each piece of logic lives in the project structure (`grails-app/...`).

The project follows the standard Grails layout:
- Domains: `grails-app/domain/alCarmel`
- Controllers: `grails-app/controllers/alCarmel`
- Services: `grails-app/services/alCarmel`
- Views (GSP): `grails-app/views`
- Frontend assets (CSS/JS): `grails-app/assets`
- Main layout: `grails-app/views/layouts/main.gsp`

---

## 1. Domain Model

### 1.1 `Book` – Library Books
- **Path**: `grails-app/domain/alCarmel/Book.groovy`
- **Purpose**: Represents a single book in the system (title, author, year, copies, state).

Key fields:
- `String bookTitle`: book title.
- `String authorName`: author name.
- `String description`: optional description.
- `Integer totalCopies`: total number of copies (default 1).
- `Integer availableCopies`: how many copies are currently available to borrow (default equals `totalCopies` on creation).
- `Integer publishYear`: publishing year (must not be in the future).
- `Category category`: the category this book belongs to.
- `Boolean active`: soft delete flag (true = active, false = archived).
- `Date dateCreated`, `lastUpdated`: standard Grails timestamps.
- `Date archivedAt`: when the book was archived (if any).
- `Member archivedBy`: which member/admin performed the archive (optional).

Relations:
- `static belongsTo = [category: Category]` – each book belongs to a category.
- `static hasMany = [borrow: Borrow]` – a book can have many borrow records.

Constraints:
- `bookTitle` is required and **unique together** with `authorName` (same author cannot have two books with the same title).
- `publishYear` cannot be greater than the current year – validated in `constraints` and uses the custom error code `futureYear`.
- Archive-related fields can be null.

Usage:
- Listed and managed in `BookController.index` and related views.
- Used by the borrowing system through the `Borrow` domain and `BorrowService`.
- Used heavily in reporting (most borrowed books, books statistics, monthly/yearly borrowing).

---

### 1.2 `Member` – Library Members / Users
- **Path**: `grails-app/domain/alCarmel/Member.groovy`
- **Purpose**: Represents a library member (person who borrows books).

Key fields:
- `String fullName`
- `String email` – unique and email-validated.
- `String phoneNumber` – must be 10–12 digits.
- `String address` – the **city**, constrained to a predefined list of Palestinian (West Bank) cities.
- `Date membershipDate` – date the member joined (defaults to now).
- `Boolean active` – soft delete for members.
- `Date archivedAt`, `Member archivedBy`
- `Date dateCreated`, `lastUpdated`

Relations:
- `static hasMany = [borrows: Borrow]` – a member can have many borrow records.

Constraints:
- `email` must be unique and a valid email.
- `phoneNumber` must contain only digits and length 10–12; otherwise the custom constraint returns `invalidLength`.
- `address` (city) is required and must belong to a static list of allowed city names to ensure standardized data (e.g. Ramallah, Nablus, Hebron, Bethlehem, Jenin, Tulkarm, Qalqilya, Jericho, Salfit, Tubas).

Helper methods:
- `getFirstChar()` – returns the first character of the member name (used in UI for avatar initials).
- `getMemberSinceYear()` – returns only the year in which the member joined.

Usage:
- Managed through `MemberController` and related views.
- Linked to `Borrow` for tracking who borrowed what and when.
- Used in reporting (most active users, users activity charts, city-based analysis).

---

### 1.3 `Category` – Book Categories
- **Path**: `grails-app/domain/alCarmel/Category.groovy`
- **Purpose**: Groups books under named categories (e.g. Novel, Science, Kids...).

Fields & relations:
- `String categoryName` – unique, required.
- `static hasMany = [books: Book]` – a category holds many books.

Usage:
- Managed through `CategoryController` (create, list, delete).
- Used when adding/updating books (each book must have a category).
- Used as a filter in the reporting system (filter borrowings by category).

---

### 1.4 `Borrow` – Borrowing Records
- **Path**: `grails-app/domain/alCarmel/Borrow.groovy`
- **Purpose**: Represents a single borrowing transaction of a book by a member.

Key fields:
- `Book book`
- `Member member`
- `Date borrowDate` – borrowing date (defaults to today).
- `Date dueDate` – due date (defaults to 14 days after borrowing).
- `Date returnDate` – actual return date (null if not yet returned).
- `String status` – one of `"BORROWED"`, `"RETURNED"`, `"LATE"`.
- `Date dateCreated`, `lastUpdated`

Relations:
- `static belongsTo = [member: Member, book: Book]`

Constraints:
- `status` must be one of the defined values.
- `returnDate` and `dueDate` can be null (they are set automatically when needed).

Lifecycle hook:
- `beforeInsert()`:
  - If `borrowDate` is null, set it to current date.
  - If `dueDate` is null, set it to current date + 14 days.

Helper:
- `boolean isLate()`:
  - Returns true if status is `BORROWED` and today is past `dueDate`.

Usage:
- Central historical table for all borrowing activity.
- Used in:
  - Borrowing UI (`BorrowController` + `BorrowService`).
  - Updating late loans.
  - All reporting (time-based, per book, per user, per category, monthly and yearly).

---

## 2. Services – Business Logic Layer

### 2.1 `DashboardService`
- **Path**: `grails-app/services/alCarmel/DashboardService.groovy`
- **Purpose**: Computes aggregate statistics and recent activity for the dashboard.

Key methods:
- `getStats()`:
  - Returns a map with:
    - `members` / `archivedMembers`
    - `totalBooks` (sum of copies of active books)
    - `totalBooksEver` (sum of all books ever)
    - `activeBookTitles` / `archivedBookTitles`
    - `categories`
    - `activeLoans` (BORROWED)
    - `lateReturns` (LATE)
    - `borrowedToday`
- `getBorrowingActivity()`:
  - Builds Last-7-Days data:
    - For each of the past 7 days: day label + count of borrows that happened that day.
  - Used in the dashboard line chart (Chart.js) in `dashboard.js`.

---

### 2.2 `BorrowService`
- **Path**: `grails-app/services/alCarmel/BorrowService.groovy`
- **Purpose**: Contains all borrowing/returning business rules.

Methods and full flow:

1. `getBorrows(String filter)`:
   - If `filter` is null or `"ALL"`, returns all borrows.
   - Otherwise filters by status (BORROWED, RETURNED, LATE).
   - Used by `BorrowController.index` to build the borrowing tabs UI.

2. `borrow(Long bookId, Long memberId)`:
   - **Full flow**:
     1. Called from `BorrowController.save` when the user submits the borrow form.
     2. Loads `Book` and `Member` entities.
     3. Enforces business rules:
        - Book and member must exist.
        - Book must be active.
        - Member must be active.
        - Book must have at least one available copy (`availableCopies > 0`).
        - Book must have a category.
        - **A member cannot borrow the same book twice at the same time**:
          - It checks for existing `Borrow` records for the same `(book, member)` with status `BORROWED` or `LATE`.
          - If any exist, it throws an exception with the message `"You cannot borrow the same book twice before returning it."`.
     4. Sets `borrowDate` (now) and `dueDate` (now + 14 days).
     5. Creates a new `Borrow` instance and calls `validate()`.
     6. If validation fails, collects errors and throws an exception.
     7. Saves the borrow (flush + failOnError).
     8. Decrements `book.availableCopies` and saves the book.
   - **Controller / UI**:
     - `BorrowController.save` wraps this in a `try/catch` and shows:
       - Success toast on success.
       - Detailed error message toast on failure (from exception).

3. `returnBookService(Long borrowId)`:
   - Full flow:
     1. Called from `BorrowController.returnBook`.
     2. Loads the `Borrow` record.
     3. Sets:
        - `returnDate = new Date()`
        - `status = "RETURNED"`
     4. Increments `borrow.book.availableCopies`.
   - The borrow record is kept for historical reporting; it is never deleted.

4. `updateLateBorrows()`:
   - Called at the start of `BorrowController.index`.
   - Finds all borrows with status `BORROWED` whose `dueDate` is before today and sets status to `LATE`.
   - Guarantees that late loans are up to date before the UI and reports read them.

---

### 2.3 `BookService`
- **Path**: `grails-app/services/alCarmel/BookService.groovy`
- **Purpose**: All book-related logic (search, save, update, archive/restore, counts).

Key methods:
- `getBooks(String query)`:
  - Returns **only active** books.
  - Optional text search on:
    - Title
    - Author
    - Category name
  - Uses HQL with `lower(...) LIKE :q`.

- `getArchivedBooks(String query)`:
  - Returns only archived books (`active = false`).
  - Same search behavior.

- `saveBook(Map params)`:
  - Called from `BookController.save`.
  - Creates a `Book` with:
    - `totalCopies` + `availableCopies` from form.
    - `publishYear`, `category`, etc.
  - Saves and returns the instance for the controller to inspect errors or success.

- `updateBook(Long id, Map params)`:
  - Updates existing book fields.
  - Computes the difference between the new and old `totalCopies` and adjusts `availableCopies` so on-loan copies are preserved.

- `archiveBook(Long id, Long archivedByMemberId = null)`:
  - **Full flow**:
    1. Called by `BookController.archive`.
    2. Ensures the book exists and is active.
    3. Uses `Borrow.withCriteria` to count active borrows (`BORROWED` or `LATE`) for this book.
    4. If there are active borrows, throws an exception with a clear message.
    5. Otherwise:
       - Sets `active = false`.
       - Sets `archivedAt = new Date()`.
       - Sets `archivedBy` if provided.
    6. Saves the book.
  - This is a **soft delete**, preserving history for reporting.

- `restoreBook(Long id)`:
  - Re-activates a previously archived book and clears archive metadata.

- Helper counts:
  - `sumActiveBooksTotalCopies()` – total copies of active books.
  - `countAllBooks()` – count of all books (active + archived).
  - `countActiveBooks()`, `countArchivedBooks()` – used in dashboard/reports.

---

### 2.4 `MemberService`
- **Path**: `grails-app/services/alCarmel/MemberService.groovy`
- **Purpose**: Member management and soft delete logic.

Key methods:
- `getMembers()` – lists active members sorted by full name.
- `getArchivedMembers()` – lists archived members sorted by archive date.

- `saveMember(Map params)` / `updateMember(Long id, Map params)`:
  - Creates/updates `Member` from form data.
  - Relies on domain constraints for validation (email uniqueness, phone length, etc.).

- `archiveMember(Long id, Long archivedByMemberId = null)`:
  - **Full flow**:
    1. Called from `MemberController.archive`.
    2. Ensures the member exists and is active.
    3. Counts active borrows for this member (BORROWED or LATE).
    4. If there are active loans, throws an exception to prevent archiving.
    5. Otherwise:
       - Sets `active = false`.
       - Fills `archivedAt` and `archivedBy`.

- `restoreMember(Long id)`:
  - Re-activates an archived member and clears archive metadata.

- `countActive()`, `countArchived()`:
  - Provide active/archived member counts for reporting or dashboard.

---

### 2.5 `CategoryService`
- **Path**: `grails-app/services/alCarmel/CategoryService.groovy`
- **Purpose**: Simple CRUD logic for categories.

Key methods:
- `getCategories()` – returns all categories ordered by name.
- `saveCategory(Map params)` – creates a new category, mapping from form field `name` to domain field `categoryName`.
- `deleteCategory(Long id)` – deletes a category (expected to have no dependent books).

---

### 2.6 `ReportService` – Professional Reporting Layer
- **Path**: `grails-app/services/alCarmel/ReportService.groovy`
- **Purpose**: Central, scalable backend for all reports.

Core ideas:
- All time-based reporting is anchored on `Borrow.borrowDate`.
- Filters are centralized and reused:
  - **Time filters**: from/to, year, month.
  - **Dimension filters**: category, member.
- Methods return simple maps/lists that are UI-agnostic, making them reusable for web, API, or other clients.

Key methods:

1. `resolveDateRange(Map params)`:
   - Interprets time filters:
     - `year` only → full year.
     - `year + month` → that month only.
     - `fromDate + toDate` → custom range.
     - No params → defaults to “last month”.
   - Returns a map with `from` and `to` dates used by all other report methods.

2. `getBorrowingReport(Map params)`:
   - Builds a dynamic `where` clause with:
     - Time range (from/to).
     - Optional `categoryId` filter (`b.book.category.id`).
     - Optional `memberId` filter (`b.member.id`).
   - Returns:
     - `totalBorrowings`
     - `mostBorrowedBooks` – list of `[book, count]`.
     - `mostActiveUsers` – list of `[member, count]`.

3. `getBooksStatistics()`:
   - Computes:
     - Total number of copies ever.
     - Available copies for active books.
     - Borrowed copies = total − available.
     - Count of active titles vs archived titles.

4. `getPopularBooks(Map params)`:
   - Returns the most borrowed books for the selected period, honoring `categoryId` and `memberId` filters.

5. `getUserActivity(Map params)`:
   - Returns the most active users for the selected period, again honoring category/member filters.

6. `getMonthlyBorrowingReport(Integer year, Map params)`:
   - Aggregates borrows by month for a given year, with optional `categoryId`/`memberId`.

7. `getYearlyBorrowingReport(Map params)`:
   - Aggregates borrows by year across the entire history, with optional filters.

**Export and shared model:**

- `buildReportModel(String reportType, Map params)`:
  - Builds the same model used by `ReportController.index` to render HTML reports.
  - Centralizes which report blocks are populated for a given `reportType`, so HTML and exports (PDF/Excel) share one source of truth.

- `exportReportToExcel(String reportType, Map params, OutputStream out)`:
  - Generates a CSV (Excel-friendly) file based on the selected `reportType` and filters (`from/to`, `year`, `month`, `categoryId`, `memberId`).
  - For example:
    - **BORROWING**: columns `User Name, Book Title, Borrow Date, Return Date, Category, City`.
    - **USERS**: `User Name, Borrow Count`.
    - **POPULAR_BOOKS**: `Book Title, Borrow Count`.
    - **BOOKS / MONTHLY / YEARLY**: appropriate metric tables.

- `exportReportToPDF(String reportType, Map params, OutputStream out)`:
  - Uses OpenPDF to generate a structured PDF that includes:
    - Report title (`Library Report - <REPORT_TYPE>`).
    - Generation timestamp.
    - System name (`Carmel Library Management System`).
    - A table matching the selected report type and filters (same data as on screen).

This design makes the reporting layer flexible and ready for future report types **and** export destinations without changing controller or view structure.

---

## 3. Controllers & Request Flow

### 3.1 `DashboardController`
- **Path**: `grails-app/controllers/alCarmel/DashboardController.groovy`
- `index()`:
  - Calls `dashboardService.getStats()` and `dashboardService.getBorrowingActivity()`.
  - Renders `views/dashboard/index.gsp` with these stats + activity data.

---

### 3.2 `BookController`
- **Path**: `grails-app/controllers/alCarmel/BookController.groovy`

Main actions:
- `index()`:
  - Reads optional search query `params.q`.
  - Fetches active books from `BookService.getBooks`.
  - For AJAX (`request.xhr`), renders only the `_bookList` template.

- `archived()`:
  - Same pattern for archived books using `BookService.getArchivedBooks`.

- `save()`:
  - Delegates to `BookService.saveBook`.
  - Handles validation errors:
    - Duplicate title per author (unique constraint).
    - Future publish year (`futureYear`).
  - Uses `flash.error` / `flash.success` + redirect.

- `update()`:
  - Uses `BookService.updateBook`.
  - Same error handling pattern as `save()`.

- `archive()`:
  - Calls `BookService.archiveBook`.
  - If book has active borrows, catches the thrown exception and shows a descriptive error message.

- `restore()`:
  - Calls `BookService.restoreBook` and shows a success toast.

---

### 3.3 `MemberController`
- **Path**: `grails-app/controllers/alCarmel/MemberController.groovy`

Main actions:
- `index()` – show active members (`MemberService.getMembers()`).
- `archived()` – show archived members (`MemberService.getArchivedMembers()`).

- `save()`:
  - Creates a new member via `MemberService.saveMember`.
  - Handles:
    - Invalid phone length (`invalidLength`).
    - Duplicate email (`unique`).

- `update()`:
  - Updates a member by calling `MemberService.updateMember`.
  - Same validation error mapping as in `save()`.

- `archive()`:
  - Calls `MemberService.archiveMember`.
  - Prevents archiving if the member has active loans.

- `restore()`:
  - Calls `MemberService.restoreMember` and reactivates the member.

---

### 3.4 `CategoryController`
- **Path**: `grails-app/controllers/alCarmel/CategoryController.groovy`

Main actions:
- `index()` – lists categories via `CategoryService.getCategories`.
- `show(Long id)`:
  - Loads a category and passes its `books` collection to the view to display books in this category.
- `save()` – adds a new category using `CategoryService.saveCategory`.
- `delete()` – deletes a category via `CategoryService.deleteCategory`.

---

### 3.5 `BorrowController`
- **Path**: `grails-app/controllers/alCarmel/BorrowController.groovy`

Main actions:

1. `index()`:
   - Invokes `borrowService.updateLateBorrows()` to keep statuses accurate.
   - Loads:
     - Borrow list via `getBorrows(params.filter)`.
     - Active members for selection in the borrow form.
     - Active, in-stock books for selection in the borrow form.

2. `save()`:
   - Reads `bookId` and `memberId` from params.
   - Validates presence of both; if missing, sets `flash.error`.
   - Calls `borrowService.borrow(bookId, memberId)` inside a `try/catch`.
   - On error, extracts and shows a meaningful error message; on success shows a success toast.

3. `returnBook()`:
   - Calls `borrowService.returnBookService`.
   - Marks the borrow as returned and increments the book’s `availableCopies`.

---

### 3.6 `ReportController`
- **Path**: `grails-app/controllers/alCarmel/ReportController.groovy`

Single main action: `index()`:
- Reads `params.reportType` (defaults to `"BORROWING"`).
- Parses `fromDate` / `toDate` into `Date` using `parseDateParam`.
- Based on `reportType`, calls the relevant `ReportService` methods:
  - `BORROWING` → `getBorrowingReport(params)`
  - `BOOKS` → `getBooksStatistics()`
  - `USERS` → `getUserActivity(params)`
  - `POPULAR_BOOKS` → `getPopularBooks(params)`
  - `MONTHLY` → `getMonthlyBorrowingReport(year, params)` + `getYearlyBorrowingReport(params)`
  - `YEARLY` → `getYearlyBorrowingReport(params)`
- Additionally supplies:
  - `allMembers` – active members to populate the User filter.
  - `allCategories` – all categories to populate the Category filter.
- Renders `views/report/index.gsp` with this complete model.

---

## 4. Views & User Experience

### 4.1 Main Layout – Navigation & Assets
- **Path**: `grails-app/views/layouts/main.gsp`

Responsibilities:
- Links shared CSS/JS:
  - Bootstrap, Bootstrap Icons, SweetAlert2.
  - Global CSS: `application.css`.
  - Page-specific CSS:
    - `book.css`, `members.css`, `borrow.css`, `categories.css`, `reports.css`.
  - JS:
    - Loads `Chart.js` only for `dashboard` and `report` controllers.
    - Page-specific JS: `dashboard.js`, `book.js`, `members.js`, `reports.js`, and `application.js`.

- Sidebar navigation:
  - `Dashboard` → `DashboardController.index`
  - `Books Inventory` → `BookController.index`
  - `Categories` → `CategoryController.index`
  - `Borrowing System` → `BorrowController.index`
  - `Members` → `MemberController.index`
  - `Reports` → `ReportController.index`

This layout ensures a consistent look and feel and minimizes duplication.

---

### 4.2 Reports Page – `report/index.gsp`
- **Path**: `grails-app/views/report/index.gsp`
- **Purpose**: Central, professional reporting hub with filters, tables, and charts.

Structure:
1. **Header**:
   - Page title `Reports`.
   - Subtitle explaining that it’s a time-based analytics page.

2. **Filters section** (top):
   - `Report Type` – choose which report to generate:
     - Borrowing, Books Statistics, Users Activity, Popular Books, Monthly Borrowing, Yearly Statistics.
   - `From Date` / `To Date` – free date range.
   - `Year` / `Month` – for monthly/yearly reports.
   - `Category` – dropdown of all categories (All categories by default).
   - `User` – dropdown of active members (All users by default).
   - Buttons:
     - **Apply Filters**
     - **Clear**

3. **Report content area**:
   - **Borrowing Report**:
     - Card with `Total Borrowings` for the chosen range.
     - Table of `Most Borrowed Books`.
     - Table of `Most Active Users`.
   - **Books Statistics**:
     - Stat cards: total copies, available copies, borrowed copies, active titles.
     - Pie chart for active vs archived titles.
   - **Users Activity**:
     - Table: member vs borrow count.
     - Horizontal bar chart for user activity.
   - **Popular Books**:
     - Table of top borrowed books.
     - Bar chart for popular books.
   - **Monthly / Yearly**:
     - Line chart combining monthly and yearly data.
     - Yearly summary table on the side.

---

### 4.3 Reports CSS & JS

- **CSS**: `grails-app/assets/stylesheets/reports.css`
  - Styles the reports header, filter card, and statistic cards to provide a clean, modern reporting UI aligned with the rest of the app.

- **JS**: `grails-app/assets/javascripts/reports.js`
  - Uses Chart.js to render:
    - Books status pie chart (active vs archived).
    - User activity bar chart.
    - Popular books bar chart.
    - Borrowing over time line chart (monthly + yearly curves).
  - Reads data from `data-*` attributes on `<canvas>` elements that are populated by the GSP.

---

## 5. End-to-End Process Examples

### 5.1 Adding a New Book
1. User opens **Books Inventory** page.
2. Fills in book form (title, author, year, total copies, category).
3. Form submits to `BookController.save`.
4. Controller calls `BookService.saveBook(params)`.
5. Service constructs a `Book` and calls `save()`:
   - Domain constraints enforce valid year and unique title-per-author.
6. On success:
   - `flash.success` is set and the book appears in the list.
7. On failure:
   - An appropriate message (duplicate, future year, etc.) is shown to the user.

### 5.2 Registering a New Member
1. User opens **Members** page.
2. Fills member registration form.
3. `MemberController.save` calls `MemberService.saveMember`.
4. Domain constraints validate:
   - Email format and uniqueness.
   - Phone number length.
5. Result is shown via toasts: either success or detailed error.

### 5.3 Borrowing a Book
1. From **Borrowing System**:
   - User selects an active member.
   - User selects an available book (active with `availableCopies > 0`).
2. Form posts to `BorrowController.save`.
3. Controller validates that both IDs are present; if not, shows an error.
4. Calls `BorrowService.borrow(bookId, memberId)`:
   - Performs all checks (existence, active flags, available copies, category).
   - Creates and saves `Borrow`.
   - Decrements `book.availableCopies`.
5. On success, a success toast is shown and the loan appears in the borrowing list.

### 5.4 Returning a Book
1. User clicks “Return” on a borrowing in the UI.
2. `BorrowController.returnBook` calls `BorrowService.returnBookService`.
3. Service:
   - Sets `returnDate` and `status = "RETURNED"`.
   - Increments the book’s `availableCopies`.
4. The record remains for history; only its status changes.

### 5.5 Archiving a Book or Member
1. From the Books/Members page, admin clicks “Archive”.
2. Controller calls:
   - `BookService.archiveBook` for books.
   - `MemberService.archiveMember` for members.
3. The service checks:
   - No active borrows refer to this book or member.
4. If checks pass:
   - Sets `active = false` and fills archive-related fields.
5. Archived entities disappear from normal lists but remain in the DB for reports.

### 5.6 Generating a Filtered Report
Example: Monthly borrowing report for year 2025, filtered by a specific category.
1. User opens **Reports**.
2. Chooses:
   - `Report Type = MONTHLY`
   - `Year = 2025`
   - `Category = <chosen category>`
3. Clicks **Apply Filters**.
4. `ReportController.index`:
   - Receives all filters.
   - Calls:
     - `ReportService.getMonthlyBorrowingReport(2025, params)`
     - `ReportService.getYearlyBorrowingReport(params)`
   - These methods apply:
     - `year` filter.
     - `categoryId` filter on `b.book.category.id`.
5. View:
   - Renders a line chart of monthly borrowing counts for that year & category.
   - Renders a yearly summary table limited to that category.

---

## 6. Design & Scalability Notes

- **Service-centric design**: All business rules live in services (`BookService`, `MemberService`, `BorrowService`, `ReportService`, etc.), keeping controllers thin and easy to maintain.
- **Soft delete strategy** (books and members):
  - Prevents data loss and keeps historical data intact for reports.
- **Reporting architecture**:
  - Centralized `ReportService` using `Borrow` as a historical fact table.
  - Unified filters: time, category, user.
  - Easy to extend with new dimensions (e.g. author, status) or new report types.
- **Modern, consistent UI**:
  - Uses Bootstrap and Chart.js with a shared layout, plus page-specific assets to stay clean and scalable.

With this documentation, a new engineer can quickly understand **how the entire system works**, where each responsibility lies, and how data flows from the UI through controllers and services down to domains and back—especially for the new, professional reporting system.


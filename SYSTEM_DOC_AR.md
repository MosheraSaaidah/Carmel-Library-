## توثيق نظام إدارة مكتبة الكرمل (Grails)

هذا المستند يشرح **كل أجزاء نظام مكتبة الكرمل** المبني على إطار العمل **Grails**، ويصف:
- مكوّنات النظام (الدومينات، الكنترولرز، الخدمات، الواجهات، التقارير).
- أماكن وجود كل جزء في المشروع (المسارات داخل `grails-app`).

المشروع مبني على بنية Grails القياسية:
- الدومينات داخل: `grails-app/domain/alCarmel`
- الكنترولرز داخل: `grails-app/controllers/alCarmel`
- الخدمات داخل: `grails-app/services/alCarmel`
- الواجهات (GSP) داخل: `grails-app/views`
- ملفات الواجهة العامة (CSS/JS) داخل: `grails-app/assets`
- الـ layout الرئيسي داخل: `grails-app/views/layouts/main.gsp`

---

## 1. نموذج البيانات (Domains)

### 1.1 الكيان Book (الكتب)
- **المسار**: `grails-app/domain/alCarmel/Book.groovy`
- **الغرض**: يمثل كتاباً واحداً في النظام (العنوان، المؤلف، السنة، النسخ، الحالة...).

الحقول الأساسية:
- `String bookTitle`: عنوان الكتاب.
- `String authorName`: اسم المؤلف.
- `String description`: وصف اختياري للكتاب.
- `Integer totalCopies`: عدد النسخ الكلي (افتراضياً 1).
- `Integer availableCopies`: عدد النسخ المتاحة للاستعارة (افتراضياً يساوي totalCopies).
- `Integer publishYear`: سنة النشر (يتم التحقق ألا تكون في المستقبل).
- `Category category`: التصنيف الذي ينتمي إليه الكتاب.
- `Boolean active`: لتطبيق **soft delete** (true = كتاب نشط، false = كتاب مؤرشف).
- `Date dateCreated`, `lastUpdated`: تواريخ الإنشاء والتعديل.
- `Date archivedAt`: تاريخ أرشفة الكتاب (إن وُجد).
- `Member archivedBy`: من قام بأرشفة الكتاب (اختياري).

العلاقات:
- `static belongsTo = [category: Category]`  
  كل كتاب ينتمي لتصنيف واحد.
- `static hasMany = [borrow: Borrow]`  
  كل كتاب يمكن أن يرتبط بعدة عمليات استعارة.

القيود (constraints):
- `bookTitle` مطلوب وفريد مع `authorName` (لا يمكن لنفس المؤلف أن يسجل كتابين بنفس العنوان).
- `publishYear` لا يمكن أن تكون في المستقبل: التحقق يتم في `constraints` ويعيد كود خطأ `futureYear` لو كانت السنة أكبر من السنة الحالية.
- حقول الأرشفة (`archivedAt`, `archivedBy`) قابلة لأن تكون null.

استخدام Book في النظام:
- تظهر الكتب في صفحة **Books Inventory** عبر `BookController.index`.
- تستخدم في الاستعارة عبر `Borrow` و `BorrowService`.
- تستخدم في التقارير (أكثر الكتب استعارة، إحصائيات الكتب، التقارير الشهرية/السنوية).

---

### 1.2 الكيان Member (الأعضاء)
- **المسار**: `grails-app/domain/alCarmel/Member.groovy`
- **الغرض**: يمثل عضواً في المكتبة (قارئ/مستخدم).

الحقول الأساسية:
- `String fullName`: الاسم الكامل.
- `String email`: البريد الإلكتروني (فريد ويُتحقق من صيغته).
- `String phoneNumber`: رقم الهاتف (يجب أن يكون 10–12 رقم).
- `String address`: المدينة (قيمة قياسية من قائمة مدن فلسطينية).
- `Date membershipDate`: تاريخ التسجيل في المكتبة (افتراضياً تاريخ اليوم).
- `Boolean active`: soft delete للأعضاء (true = نشط، false = مؤرشف).
- `Date archivedAt`, `Member archivedBy`: معلومات الأرشفة (اختيارية).
- `Date dateCreated`, `lastUpdated`: تواريخ الإنشاء والتعديل.

العلاقات:
- `static hasMany = [borrows: Borrow]`  
  كل عضو يمكن أن يملك عدة عمليات استعارة.

القيود:
- `email` فريد وصحيح الصيغة.
- `phoneNumber` يتم التحقق أنه رقم فقط وبطول بين 10 و 12 رقم، وفي حال الخطأ يعاد كود `invalidLength`.
- `address` (المدينة) غير قابل لأن يكون null ويجب أن يكون ضمن قائمة ثابتة من المدن (West Bank) لضمان توحيد البيانات.

توابع مساعدة:
- `getFirstChar()`: يعيد أول حرف من الاسم الكامل (يستخدم غالباً في الواجهة كأفاتار).
- `getMemberSinceYear()`: يعيد سنة الانضمام فقط بشكل نصي.

استخدام Member في النظام:
- يُعرض في صفحة **Members** عبر `MemberController.index`.
- يُستخدم في الاستعارة (كل Borrow مرتبط بعضو).
- يُستخدم في التقارير (أكثر الأعضاء نشاطاً، نشاط المستخدمين، توزيع الاستعارات حسب المدينة...).

---

### 1.3 الكيان Category (التصنيفات)
- **المسار**: `grails-app/domain/alCarmel/Category.groovy`
- **الغرض**: تنظيم الكتب حسب تصنيفات (رواية، علمي، أطفال...).

الحقول والعلاقات:
- `String categoryName`: اسم التصنيف (فريد وغير فارغ).
- `static hasMany = [books: Book]`: كل تصنيف يحتوي عدة كتب.

استخدام Category:
- في إدارة التصنيفات (الإضافة/الحذف) من خلال `CategoryController`.
- في صفحة عرض تصنيف معيّن لعرض كل الكتب التابعة له.
- كفلتر في نظام التقارير (لتصفية الاستعارات حسب تصنيف كتاب معيّن).

---

### 1.4 الكيان Borrow (الاستعارات)
- **المسار**: `grails-app/domain/alCarmel/Borrow.groovy`
- **الغرض**: يمثل عملية استعارة واحدة لكتاب من قبل عضو معيّن.

الحقول الأساسية:
- `Book book`: الكتاب المستعار.
- `Member member`: العضو المستعير.
- `Date borrowDate`: تاريخ الاستعارة (افتراضياً تاريخ اليوم).
- `Date dueDate`: موعد الإرجاع (افتراضياً بعد 14 يوماً من تاريخ الاستعارة).
- `Date returnDate`: تاريخ الإرجاع الفعلي (null إذا لم يُرجَع بعد).
- `String status`: حالة الاستعارة (`BORROWED`, `RETURNED`, `LATE`).
- `Date dateCreated`, `lastUpdated`: تواريخ الإنشاء والتعديل.

العلاقات:
- `static belongsTo = [member: Member, book: Book]`  
  الاستعارة مرتبطة بعضو وكتاب.

القيود:
- `status` يجب أن يكون واحداً من القيم المحددة.
- `returnDate` و `dueDate` يمكن أن تكون null (يتم ضبطها آلياً).

الهُوكس (beforeInsert):
- عند إنشاء Borrow، إن لم يُمرَّر `borrowDate` يُعين بتاريخ اليوم.
- إن لم يُمرَّر `dueDate` يُحسب تلقائياً بعد 14 يوماً باستخدام `LocalDate`.

توابع مساعدة:
- `boolean isLate()`:
  - يعيد true إذا كانت الحالة `BORROWED` وتاريخ `dueDate` قبل اليوم الحالي.

استخدام Borrow في النظام:
- هو المصدر الأساسي لتاريخ الاستعارات في النظام (logs).
- يُستخدم في:
  - صفحة **Borrowing System** لعرض كل الاستعارات وحالتها.
  - تحديث الحالات المتأخرة (LATE).
  - التقارير (الاستعارات ضمن فترة، أشهر/سنوات، الكتب الشعبية، نشاط الأعضاء...إلخ).

---

## 2. الخدمات (Services)

### 2.1 DashboardService
- **المسار**: `grails-app/services/alCarmel/DashboardService.groovy`
- **الغرض**: حساب الإحصائيات المعروضة في صفحة الـ Dashboard.

الدوال الأساسية:
- `getStats()`:
  - يحسب:
    - عدد الأعضاء النشطين والمؤرشفين.
    - مجموع النسخ للكتب النشطة فقط ولجميع الكتب.
    - عدد عناوين الكتب النشطة والمؤرشفة.
    - عدد التصنيفات.
    - عدد الاستعارات النشطة والـ LATE.
    - عدد الاستعارات التي تمت اليوم.
  - كلها مبنية على دوال جروفي/HQL بسيطة على الدومينات.

- `getBorrowingActivity()`:
  - يبني بيانات آخر 7 أيام:
    - لكل يوم: اسم اليوم (Mon, Tue...) والعدد الإجمالي للاستعارات (Borrow) في ذلك اليوم.
  - هذه البيانات تُمرّر لصفحة الـ Dashboard لرسم **line chart** بـ `Chart.js`.

---

### 2.2 BorrowService
- **المسار**: `grails-app/services/alCarmel/BorrowService.groovy`
- **الغرض**: يحتوي كل منطق العمل (Business Logic) لعمليات الاستعارة والإرجاع.

الدوال:

1. `getBorrows(String filter)`:
   - إن كان `filter` فارغاً أو يساوي `"ALL"` يعيد كل الاستعارات.
   - غير ذلك يعيد الاستعارات بحالة معينة (`BORROWED`, `RETURNED`, `LATE`).
   - يُستخدم في `BorrowController.index` لبناء التابات في واجهة الاستعارات.

2. `borrow(Long bookId, Long memberId)`:
   - **مسار العملية من البداية للنهاية**:
     1. يستدعيه `BorrowController.save` عند طلب استعارة كتاب من الواجهة.
     2. يجلب `Book` و `Member` من قاعدة البيانات.
     3. يطبق قواعد العمل:
        - الكتاب/العضو يجب أن يكونا موجودين.
        - الكتاب يجب أن يكون نشطاً `active = true`.
        - العضو يجب أن يكون نشطاً.
        - يجب أن يكون هناك على الأقل نسخة متاحة `availableCopies > 0`.
        - يجب أن يكون للكتاب تصنيف.
        - **لا يُسمح لنفس العضو أن يستعير نفس الكتاب أكثر من مرة في نفس الوقت**:
          - يتم فحص وجود سجل `Borrow` بنفس العضو/الكتاب بحالة `BORROWED` أو `LATE`.
          - إن وجد، تُرمى Exception برسالة: `"You cannot borrow the same book twice before returning it."`.
     4. يحدد `borrowDate` بتاريخ اليوم، و`dueDate` بعد 14 يوم.
     5. ينشئ كيان `Borrow` جديد ويعمل `validate()`:
        - لو فشل التحقق يجمع رسائل الأخطاء ويرمي Exception.
     6. يحفظ الاستعارة (`borrow.save(flush: true, failOnError: true)`).
     7. ينقص `availableCopies` للكتاب ويحفظ الكتاب.
     8. يعيد كيان `Borrow` المحفوظ للكنترولر.
   - **في الواجهة**:
     - `BorrowController.save` يلتقط أي Exception ويحوّل الرسالة إلى `flash.error` لعرضها كتوصت (Toast).
     - في حال النجاح يعرض `flash.success` ويعيد التوجيه لصفحة الاستعارات.

3. `returnBookService(Long borrowId)`:
   - خطوات العملية:
     1. يستدعيه `BorrowController.returnBook` عند ضغط زر إرجاع.
     2. يجلب كيان `Borrow` المطلوب.
     3. يضبط:
        - `returnDate = new Date()` (اليوم).
        - `status = "RETURNED"`.
     4. يزيد `availableCopies` للكتاب بمقدار 1.
   - يحافظ على سجل الاستعارة للأرشيف ولا يحذفه.

4. `updateLateBorrows()`:
   - يُستدعى في بداية `BorrowController.index`.
   - يبحث عن كل الاستعارات ذات الحالة `BORROWED` التي `dueDate < اليوم` ويحوّل حالتها إلى `LATE`.
   - هذا يضمن أن حالة التأخير محسوبة دائماً قبل عرض الصفحة أو قبل التقارير.

---

### 2.3 BookService
- **المسار**: `grails-app/services/alCarmel/BookService.groovy`
- **الغرض**: إدارة الكتب (بحث، حفظ، تحديث، أرشفة، استرجاع).

الدوال:
- `getBooks(String query)`:
  - يعيد قائمة الكتب النشطة فقط.
  - يدعم بحثاً نصياً على:
    - عنوان الكتاب.
    - اسم المؤلف.
    - اسم التصنيف.
  - يستخدم HQL مع `lower(...) like :q`.

- `getArchivedBooks(String query)`:
  - يعيد الكتب المؤرشفة فقط (active = false).
  - يدعم نفس منطق البحث.

- `saveBook(Map params)`:
  - يُستدعى من `BookController.save`.
  - يبني كيان Book من بيانات الفورم:
    - يعين `totalCopies` و `availableCopies` بنفس القيمة الابتدائية.
    - يربط الكتاب بتصنيف عن طريق `categoryId`.
  - يحفظ الكيان ويعيده للكنترولر للتعامل مع الأخطاء أو النجاح.

- `updateBook(Long id, Map params)`:
  - يجلب الكتاب، يحدّث الحقول (العنوان، المؤلف، الوصف، السنة، التصنيف).
  - يحسب الفرق في `totalCopies` ويعدل `availableCopies` بناءً عليه حتى لا تُفقد النسخ المستعارة.

- `archiveBook(Long id, Long archivedByMemberId = null)`:
  - **مسار عملية أرشفة كتاب**:
    1. يُستدعى من `BookController.archive`.
    2. يتحقق أن الكتاب موجود ونشط.
    3. يبحث عن أي استعارات نشطة (`BORROWED` أو `LATE`) لهذا الكتاب باستخدام `Borrow.withCriteria`.
    4. إن وُجدت استعارات نشطة، يرمي Exception يمنع الأرشفة مع رسالة واضحة.
    5. إن لم توجد، يُغير:
       - `active = false`
       - يضبط `archivedAt` بتاريخ اليوم.
       - يضبط `archivedBy` إن تم تمرير عضو.
    6. يحفظ الكتاب.
  - بهذه الطريقة لا يُحذف الكتاب نهائياً، ويبقى موجوداً للتقارير التاريخية.

- `restoreBook(Long id)`:
  - يعيد الكتاب المؤرشف إلى الحالة النشطة ويصفر معلومات الأرشفة.

- دوال مساعدة لإحصائيات الكتب:
  - `sumActiveBooksTotalCopies()`: مجموع النسخ للكتب النشطة.
  - `countAllBooks()`: عدد كل الكتب (نشطة + مؤرشفة).
  - `countActiveBooks()`, `countArchivedBooks()`: عدد العناوين حسب الحالة.

---

### 2.4 MemberService
- **المسار**: `grails-app/services/alCarmel/MemberService.groovy`
- **الغرض**: إدارة الأعضاء (تحصيل، إضافة، تعديل، أرشفة، استرجاع، إحصاء).

الدوال الأهم:
- `getMembers()`: يعيد الأعضاء النشطين فقط (مرتَّبين بالاسم).
- `getArchivedMembers()`: يعيد الأعضاء المؤرشفين فقط (مرتَّبين بتاريخ الأرشفة).

- `saveMember(Map params)` / `updateMember(Long id, Map params)`:
  - تبني/تحدث كيان Member من بيانات الفورم.
  - تعتمد قيود الدومين للتحقق من رقم الهاتف والبريد.

- `archiveMember(Long id, Long archivedByMemberId = null)`:
  - **مسار عملية أرشفة عضو**:
    1. يُستدعى من `MemberController.archive`.
    2. يتحقق من وجود العضو ونشاطه.
    3. يحسب عدد الاستعارات النشطة (BORROWED, LATE) لهذا العضو.
    4. إذا كان لديه استعارات نشطة، يرمي Exception برسالة تفيد بضرورة إرجاع الكتب أولاً.
    5. وإلا:
       - يضبط `active = false`.
       - يحدد `archivedAt` بتاريخ اليوم.
       - يحدد `archivedBy` إن وُجد.

- `restoreMember(Long id)`:
  - يعيد العضو المؤرشف إلى الحالة النشطة ويصفر بيانات الأرشفة.

- `countActive()`, `countArchived()`:
  - تُستخدم في التقارير أو الـ Dashboard لإحصاء الأعضاء حسب الحالة.

---

### 2.5 CategoryService
- **المسار**: `grails-app/services/alCarmel/CategoryService.groovy`
- **الغرض**: إدارة التصنيفات.

الدوال:
- `getCategories()`: يعيد كل التصنيفات مرتبةً بالاسم.
- `saveCategory(Map params)`:
  - يأخذ `name` من الفورم ويخزنه في `categoryName`.
  - يعتمد قيود الدومين للتحقق من التفرد.
- `deleteCategory(Long id)`:
  - يحذف التصنيف (عادة يُفترض ألا يحتوي كتباً، ويتم ضبط هذا من UI/business rules).

---

### 2.6 ReportService (نظام التقارير)
- **المسار**: `grails-app/services/alCarmel/ReportService.groovy`
- **الغرض**: كل منطق التقارير الاحترافي والقابل للتوسع.

الأفكار الأساسية:
- كل تقارير الوقت مبنية على `Borrow.borrowDate`.
- توجد فلاتر موحدة يعاد استخدامها في كل مكان:
  - زمنية: من/إلى، سنة، شهر.
  - أبعاد أخرى: تصنيف، عضو (User).
- النتائج تُرجع على شكل Maps/Lists عامة لتسهيل استخدامها من صفحات GSP أو أي API مستقبلاً.

الدوال الرئيسية:

1. `resolveDateRange(Map params)`:
   - يفسر الفلاتر الزمنية:
     - `year` فقط → من أول السنة لآخرها.
     - `year + month` → من أول الشهر لآخر الشهر.
     - `fromDate` و `toDate` → نطاق مخصص.
     - إن لم يمر شيء → آخر شهر افتراضيًا.
   - يعيد `from` و `to` كتواريخ تستخدم في كل استعلامات التقارير.

2. `getBorrowingReport(Map params)`:
   - يستخدم:
     - المدى الزمني من `resolveDateRange`.
     - فلاتر اختيارية:
       - `categoryId`: لتصفية الاستعارات حسب تصنيف الكتاب.
       - `memberId`: لتصفية الاستعارات حسب عضو معيّن.
   - النتائج:
     - `totalBorrowings`: عدد الاستعارات في المدى.
     - `mostBorrowedBooks`: قائمة (كتاب + عدد الاستعارات).
     - `mostActiveUsers`: قائمة (عضو + عدد الاستعارات).

3. `getBooksStatistics()`:
   - يحسب:
     - `totalCopies`: مجموع كل النسخ (نشطة ومؤرشفة).
     - `availableCopies`: مجموع النسخ المتاحة للكتب النشطة.
     - `borrowedCopies`: الفرق بينهما.
     - `activeTitles`: عدد عناوين الكتب النشطة.
     - `archivedTitles`: عدد عناوين الكتب المؤرشفة.

4. `getPopularBooks(Map params)`:
   - أكثر الكتب استعارةً في الفترة المحددة، مع إمكانية تصفية بـ `categoryId` أو `memberId`.

5. `getUserActivity(Map params)`:
   - يعيد نشاط الأعضاء (عدد الاستعارات لكل عضو) مع تطبيق نفس الفلاتر الزمنية والتصنيف/العضو.

6. `getMonthlyBorrowingReport(Integer year, Map params)`:
   - يحسب عدد الاستعارات لكل شهر في سنة معينة.
   - يحترم فلاتر `categoryId` و `memberId` لو تم تمريرها.

7. `getYearlyBorrowingReport(Map params)`:
   - يحسب عدد الاستعارات لكل سنة في النظام.
   - يدعم نفس فلاتر التصنيف والعضو.

8. `buildReportModel(String reportType, Map params)`:
   - يبني نفس الـ model الذي تستخدمه صفحة HTML للتقارير (`ReportController.index`).
   - يُستخدم أيضاً في التصدير إلى PDF وExcel حتى يكون نفس المنطق والفلاتر مشتركة.

9. `exportReportToExcel(String reportType, Map params, OutputStream out)`:
   - يولّد ملف CSV (متوافق مع Excel) بناءً على نوع التقرير والفلاتر:
     - تقرير الاستعارات: أعمدة مثل User Name, Book Title, Borrow Date, Return Date, Category, City.
     - تقارير المستخدمين/الكتب/الإحصائيات: جداول مناسبة لكل نوع.
   - يستخدم نفس الفلاتر الزمنية وتصنيف/عضو لضمان تطابق النتائج مع ما يظهر على الشاشة.

10. `exportReportToPDF(String reportType, Map params, OutputStream out)`:
    - يبني PDF احترافي باستخدام OpenPDF:
      - عنوان التقرير، تاريخ التوليد، اسم النظام.
      - جدول منظم حسب نوع التقرير بنفس الأعمدة المذكورة أعلاه.

بهذا، يكون نظام التقارير قابلاً للتوسع، وكل المنطق (الاستعلامات + التصدير) موجود في Service واحدة يمكن استدعاؤها من أي Controller أو API مستقبلاً.

---

## 3. الكنترولرز (Controllers) وتدفق العمليات

### 3.1 DashboardController
- **المسار**: `grails-app/controllers/alCarmel/DashboardController.groovy`
- **action index()**:
  - يستدعي:
    - `DashboardService.getStats()` → إحصائيات عامة.
    - `DashboardService.getBorrowingActivity()` → بيانات الرسم البياني.
  - يرسل الـ model لواجهة `grails-app/views/dashboard/index.gsp`.

---

### 3.2 BookController
- **المسار**: `grails-app/controllers/alCarmel/BookController.groovy`

العمليات:

1. `index()`:
   - يجلب الكتب النشطة من `BookService.getBooks(params.q)`.
   - يدعم طلبات AJAX:
     - لو `request.xhr` يعيد فقط الـ template `bookList`.
   - يمرّر أيضاً قائمة التصنيفات لاستخدامها كفلتر أو في النماذج.

2. `archived()`:
   - يعرض الكتب المؤرشفة باستخدام `BookService.getArchivedBooks`.
   - نفس فكرة الـ AJAX template لكن لقائمة المؤرشف.

3. `save()`:
   - يبني/يحفظ كتاب جديد عن طريق `BookService.saveBook`.
   - يتعامل مع الأخطاء:
     - عنوان مكرر لنفس المؤلف.
     - سنة نشر مستقبلية.
   - يعرض رسائل الخطأ/النجاح عبر `flash` ويعيد التوجيه للـ index.

4. `update()`:
   - يعدّل كتاباً موجوداً باستخدام `BookService.updateBook`.
   - يتعامل مع نفس أنواع الأخطاء ويعرض الرسائل المناسبة.

5. `archive()`:
   - يستدعي `BookService.archiveBook`.
   - في حال وجود استعارات نشطة لنفس الكتاب، يظهر رسالة خطأ واضحة.
   - في حال النجاح، يعرض رسالة نجاح ويعيد التوجيه.

6. `restore()`:
   - يعيد كتاباً مؤرشَفاً إلى قائمة الكتب النشطة.

---

### 3.3 MemberController
- **المسار**: `grails-app/controllers/alCarmel/MemberController.groovy`

العمليات:
- `index()`:
  - يعرض قائمة الأعضاء النشطين (`MemberService.getMembers()`).

- `archived()`:
  - يعرض الأعضاء المؤرشفين (`MemberService.getArchivedMembers()`).

- `save()`:
  - ينشئ عضواً جديداً عبر `MemberService.saveMember`.
  - يعالج أخطاء:
    - رقم هاتف غير صحيح الطول (`invalidLength`).
    - بريد إلكتروني مكرر (`unique`).

- `update()`:
  - يحدّث بيانات عضو ويدير الأخطاء بنفس المنطق.

- `archive()`:
  - يستدعي `MemberService.archiveMember`.
  - يمنع الأرشفة إن كان للعضو استعارات نشطة.

- `restore()`:
  - يعيد عضو مؤرشف إلى الحالة النشطة.

---

### 3.4 CategoryController
- **المسار**: `grails-app/controllers/alCarmel/CategoryController.groovy`

العمليات:
- `index()`:
  - يعرض كل التصنيفات باستخدام `CategoryService.getCategories()`.

- `show(Long id)`:
  - يعرض صفحة تصنيف واحد مع كل الكتب التابعة له.

- `save()`:
  - ينشئ تصنيفاً جديداً عبر `CategoryService.saveCategory`.
  - إن كان الاسم مكرراً، يُظهر رسالة خطأ.

- `delete()`:
  - يحذف تصنيفاً (عادةً بدون كتب متعلقة).

---

### 3.5 BorrowController
- **المسار**: `grails-app/controllers/alCarmel/BorrowController.groovy`

العمليات الأساسية:

1. `index()`:
   - يستدعي `BorrowService.updateLateBorrows()` لتحديث الحالات المتأخرة.
   - يجلب:
     - قائمة الاستعارات حسب الفلتر (`getBorrows(params.filter)`).
     - قائمة الأعضاء النشطين لاختيارهم في نموذج الاستعارة.
     - قائمة الكتب النشطة والمتاحة (`availableCopies > 0`) لاختيارها.
   - يمرر هذه البيانات لواجهة `borrow/index.gsp`.

2. `save()`:
   - يقرأ `bookId` و `memberId` من الـ params.
   - إن كانت البيانات ناقصة يعرض خطأ.
   - يستدعي `BorrowService.borrow(bookId, memberId)` داخل `try/catch`.
   - في حال Exception:
     - يأخذ رسالة الخطأ الأصلية ويعرضها في Toast.

3. `returnBook()`:
   - يستدعي `BorrowService.returnBookService`.
   - يعرض رسالة نجاح ويعيد التوجيه إلى index.

---

### 3.6 ReportController (نظام التقارير)
- **المسار**: `grails-app/controllers/alCarmel/ReportController.groovy`

العمليات:

- `index()`:
  - يقرأ نوع التقرير من `params.reportType` (افتراضياً `"BORROWING"`).
  - يحول `fromDate` و `toDate` من `String` إلى `Date` عن طريق `parseDateParam`.
  - يستدعي `reportService.buildReportModel(reportType, params)` للحصول على نفس الـ model لكل أنواع التقارير.
  - يضيف:
    - `allMembers`: الأعضاء النشطون لاستخدامهم كفلتر.
    - `allCategories`: كل التصنيفات لاستخدامها كفلتر.
  - يرسل الـ model بالكامل إلى `views/report/index.gsp`.

- `exportPdf()`:
  - يستقبل نفس الفلاتر (`fromDate`, `toDate`, `year`, `month`, `categoryId`, `memberId`, `reportType`).
  - يهيئ الـ response كملف PDF ويستدعي `reportService.exportReportToPDF`.

- `exportExcel()`:
  - يستقبل نفس الفلاتر أيضاً.
  - يهيئ الـ response كملف CSV (Excel) ويستدعي `reportService.exportReportToExcel`.

---

## 4. الواجهات (Views) وتجربة المستخدم

### 4.1 الـ Layout الرئيسي
- **المسار**: `grails-app/views/layouts/main.gsp`
- يحتوي:
  - تضمين Bootstrap, Bootstrap Icons, SweetAlert2.
  - Sidebar مع عناصر:
    - Dashboard
    - Books Inventory
    - Categories
    - Borrowing System
    - Members
    - Reports (الذي يفتح على `ReportController.index`)
  - تحميل ملفات CSS/JS حسب الـ controller:
    - `book.css`, `members.css`, `borrow.css`, `categories.css`, `reports.css`.
    - `dashboard.js`, `book.js`, `members.js`, `reports.js`, `application.js`.
  - تحميل `Chart.js` فقط عندما يكون `controllerName` هو `dashboard` أو `report`.

---

### 4.2 صفحة التقارير `report/index.gsp`
- **المسار**: `grails-app/views/report/index.gsp`
- الوظيفة:
  - صفحة مركزية تجمع كل أنواع التقارير في مكان واحد مع فلاتر موحدة.

مكونات الصفحة:
1. **الهيدر**:
   - العنوان `Reports` ووصف توضيحي.

2. **منطقة الفلاتر** (أعلى الصفحة):
   - `Report Type`: اختيار نوع التقرير.
   - `From Date` + `To Date`: نطاق زمني حر.
   - `Year` + `Month`: تقارير شهرية/سنوية.
   - `Category`: لتصفية النتائج حسب تصنيف معيّن.
   - `User`: لتصفية النتائج حسب عضو معيّن.
   - زر **Apply Filters** + زر **Clear**.

3. **منطقة النتائج**:
   - **Borrowing Report**:
     - كارت "Total Borrowings".
     - جدول "Most Borrowed Books".
     - جدول "Most Active Users".
   - **Books Statistics**:
     - كروت أرقام (Total/Available/Borrowed/Active Titles).
     - Pie chart لتوزيع العناوين (Active vs Archived).
   - **Users Activity**:
     - جدول بعدد الاستعارات لكل عضو.
     - Bar chart أفقي لنشاط الأعضاء.
   - **Popular Books**:
     - جدول لأكثر الكتب استعارة.
     - Bar chart للكتب الشعبية.
   - **Monthly/Yearly**:
     - Line chart للـ Monthly و Yearly في مخطط واحد.
     - جدول ملخّص سنوي Yearly Summary.

---

### 4.3 ملفات الستايل والجافاسكربت الخاصة بالتقارير

- **CSS**: `grails-app/assets/stylesheets/reports.css`
  - تنسيق الهيدر، كروت الإحصائيات، كروت الفلاتر، لتكون الواجهة حديثة ومنسجمة مع باقي النظام.

- **JS**: `grails-app/assets/javascripts/reports.js`
  - يبني الرسوم التالية باستخدام Chart.js:
    - Pie chart لحالة الكتب (active/archived).
    - Bar chart لنشاط الأعضاء.
    - Bar chart للكتب الشعبية.
    - Line chart للتقارير الشهرية/السنوية.
  - يعتمد على بيانات مغذاة في عناصر `<canvas>` عبر `data-*` attributes من GSP.

---

## 5. أمثلة لتدفق العمليات من A إلى Z

### 5.1 إضافة كتاب جديد
1. المستخدم يفتح صفحة **Books Inventory**.
2. يملأ نموذج إضافة كتاب (العنوان، المؤلف، السنة، عدد النسخ، التصنيف).
3. عند الإرسال يتم استدعاء `BookController.save`.
4. الكنترولر يستدعي `BookService.saveBook`.
5. BookService ينشئ كيان Book ويستدعي `save()`:
   - يتم التحقق تلقائياً من القيود (سنة النشر، العنوان الفريد).
6. في حال النجاح:
   - تظهر رسالة Toast بنجاح الإضافة.
   - يتم تحديث قائمة الكتب.
7. في حال الخطأ:
   - تُعرض رسالة خطأ مناسبة للمستخدم (سنة مستقبلية، عنوان مكرر...إلخ).

### 5.2 تسجيل عضو جديد
1. من صفحة **Members** يتم تعبئة نموذج التسجيل.
2. `MemberController.save` يستدعي `MemberService.saveMember`.
3. يتم حفظ العضو والتحقق من:
   - صيغة البريد الإلكتروني.
   - التفرد (عدم تكرار البريد).
   - طول رقم الهاتف.
4. النتائج/الأخطاء تظهر للمستخدم بتوستات.

### 5.3 استعارة كتاب
1. من صفحة **Borrowing System**:
   - يختار المستخدم عضواً من قائمة الأعضاء النشطين.
   - يختار كتاباً من قائمة الكتب المتاحة فقط (active + availableCopies > 0).
2. عند الإرسال:
   - `BorrowController.save` يستدعي `BorrowService.borrow(bookId, memberId)`.
3. الخدمة:
   - تتحقق من كل قواعد العمل (وجود الكتاب/العضو، توفر النسخ، حالة الكتاب/العضو).
   - تنشئ Borrow وتضبط `borrowDate` و `dueDate`.
   - تنقص `availableCopies` للكتاب.
4. عند النجاح:
   - تظهر رسالة نجاح.
   - يظهر السجل الجديد في قائمة الاستعارات.

### 5.4 إرجاع كتاب
1. من نفس صفحة الاستعارات، زر "Return".
2. `BorrowController.returnBook` يستدعي `BorrowService.returnBookService`.
3. الخدمة:
   - تضبط `returnDate` و `status = RETURNED`.
   - تزيد `availableCopies` للكتاب.
4. النتيجة:
   - الكتاب يصبح متاحاً للاستعارة من جديد.
   - السجل يبقى في النظام لأغراض التقارير والتاريخ.

### 5.5 أرشفة كتاب/عضو
1. من صفحة الكتب أو الأعضاء، هناك زر "Archive".
2. عند الضغط:
   - `BookController.archive` → `BookService.archiveBook`.
   - أو `MemberController.archive` → `MemberService.archiveMember`.
3. الخدمة تتحقق:
   - عدم وجود استعارات نشطة مرتبطة.
4. إن كان كل شيء صحيحاً:
   - يتم ضبط `active = false` وتعبئة حقول الأرشفة.
5. الأرشيف يُستخدم لاحقاً في التقارير التاريخية دون حذف البيانات.

### 5.6 إنشاء تقرير (مثال: تقرير الاستعارات الشهري لسنة معيّنة لتصنيف معيّن)
1. المستخدم يفتح صفحة **Reports**.
2. يختار:
   - `Report Type = MONTHLY`
   - `Year = 2025`
   - `Category = رواية` (مثلاً)
3. عند الضغط على **Apply Filters**:
   - `ReportController.index` يستقبل كل الفلاتر.
   - يستدعي:
     - `ReportService.getMonthlyBorrowingReport(2025, params)`
     - `ReportService.getYearlyBorrowingReport(params)`
   - هاتان الدالتان تطبّقان:
     - فلتر `categoryId` على حقل `b.book.category.id`.
3. النتائج:
   - Line chart يعرض عدد الاستعارات لكل شهر في سنة 2025 لتلك الفئة.
   - جدول Yearly Summary يظهر مقارنة سريعة لكل السنوات للـ Category المحدد فقط.

---

## 6. ملاحظات تصميمية (Scalability & Professionalism)

- تم عزل منطق العمل في Services (BookService, MemberService, BorrowService, ReportService...) ليبقى الـ Controllers رفيعة وخفيفة.
- تم استخدام **soft delete** في الكتب والأعضاء، حتى لا نفقد التاريخ ولا تتكسر التقارير.
- نظام التقارير مبني على `Borrow` كجدول تاريخي رئيسي، مع فلاتر:
  - نطاق زمني (من/إلى، سنة، شهر).
  - تصنيف.
  - عضو.
- تم استخدام HQL/Criteria قابلة للتوسع:
  - يسهل إضافة فلاتر إضافية لاحقاً (مثل: حالة الاستعارة، مؤلف معيّن... إلخ).
- واجهات المستخدم تعتمد على Bootstrap + Chart.js، مع تفريق بين CSS/JS العامة والمتخصصة لكل صفحة، مما يسهل الصيانة والتطوير المستقبلي.

بهذا المستند، يمكن لأي مطور جديد فهم **كيف يعمل النظام كاملاً**، وأين توجد كل وظيفة، وكيف تتدفق البيانات من الواجهة إلى الدومين والعكس، وكيف تم تصميم نظام التقارير ليكون احترافياً وقابلاً للتوسع مع نمو بيانات المكتبة.


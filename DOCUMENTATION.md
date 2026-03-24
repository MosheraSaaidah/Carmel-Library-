# توثيق نظام مكتبة الكرمل (Grails) — من الألف للياء

هذا الملف هو **المرجع الوحيد** لتوثيق المشروع. يشرح المكوّنات، الإعدادات، الإشعارات، والتدفقات من الواجهة حتى قاعدة البيانات.

**هيكل Grails القياسي:**

| الجزء | المسار |
|--------|--------|
| الدومينات | `grails-app/domain/alCarmel` |
| الكنترولرز | `grails-app/controllers/alCarmel` |
| الخدمات | `grails-app/services/alCarmel` |
| الواجهات (GSP) | `grails-app/views` |
| الأصول (CSS/JS) | `grails-app/assets` |
| التخطيط الرئيسي | `grails-app/views/layouts/main.gsp` |
| الإعدادات | `grails-app/conf/application.yml` |

---

## ملخص الميزات الأساسية (نقاط)

- **لوحة تحكم (Dashboard):** إحصائيات (أعضاء، كتب، تصنيفات، استعارات نشطة/متأخرة، استعارات اليوم) ورسم بياني لنشاط الاستعارة آخر 7 أيام (Chart.js).
- **إدارة الكتب:** إضافة، تعديل، بحث، أرشفة واسترجاع (soft delete)، نسخ متعددة (`totalCopies` / `availableCopies`)، ربط بتصنيف إلزامي، منع أرشفة الكتاب إن وُجدت استعارات نشطة.
- **إدارة التصنيفات:** CRUD، عرض كتب التصنيف.
- **إدارة الأعضاء (من الأدمن):** إضافة، تعديل، أرشفة واسترجاع، منع أرشفة العضو إن وُجدت استعارات نشطة، تحقق من البريد والهاتف والمدينة (قائمة مدن).
- **نظام الاستعارة (لوحة الأدمن):** استعارة لعضو + كتاب، إرجاع، تبويب حسب الحالة (ALL / BORROWED / RETURNED / LATE)، تحديث تلقائي **BORROWED → LATE** عند تجاوز الموعد، منع استعارة نفس الكتاب مرتين قبل الإرجاع، رسوم تأخير عند الإرجاع ($1 لكل يوم).
- **تسجيل المستخدمين والأدوار:** كيان `User` (جدول `app_user`) بأدوار **ADMIN** و **MEMBER**، تشفير كلمات المرور (BCrypt) عبر `SecurityService`.
- **تسجيل الدخول والتسجيل الذاتي:** `AuthController` — تسجيل عضو جديد، رابط تأكيد بالبريد (`confirmEmail`)، تفعيل الحساب بعد التأكيد، حماية الصفحات حسب الدور.
- **منطقة العضو (Member Area):** تصفح الكتب، استعارة ذاتية، تاريخ استعارات، **حجز** كتاب عند نفاد النسخ، إلغاء الحجز.
- **الحجوزات:** دومين `Reservation` (حالات ACTIVE / NOTIFIED / CANCELLED / COMPLETED)، `ReservationService`، صفحة أدمن لعرض كل الحجوزات (`ReservationAdminController`).
- **البريد الإلكتروني:** `EmailService` + إعدادات `spring.mail` في `application.yml` (مثلاً Gmail / App Password).
- **الإشعارات:** تذكير قبل موعد الإرجاع، إشعار تأخير مع الرسوم، إشعار عند توفر كتاب محجوز بعد الإرجاع — عبر `NotificationService` و`DailyNotificationSchedulerService` مع `@EnableScheduling` في `Application.groovy`.
- **ساعة منطقية للاختبار:** `NotificationClockService` + قسم `carmel.notification.testing` في YAML (تاريخ افتراضي في الجلسة، `fixedEffectiveDate` للجدولة، **`borrowStartDaysInPast`** لمحاكاة استعارة متأخرة فوراً).
- **التقارير:** صفحة مركزية بفلاتر (زمن، تصنيف، عضو، نوع التقرير)، جداول ورسوم Chart.js، تصدير **PDF** (OpenPDF) و**Excel/CSV** عبر `ReportService` و`ReportController`.

---

## 1. نموذج البيانات (Domains)

### 1.1 Book — `Book.groovy`
- حقول: عنوان، مؤلف، وصف، `totalCopies`، `availableCopies`، سنة نشر (لا تكون في المستقبل)، `Category`، `active`، أرشفة (`archivedAt`، `archivedBy`).
- العلاقات: `belongsTo` تصنيف، `hasMany` استعارات.
- قيود: عنوان فريد مع المؤلف؛ soft delete للأرشفة.

### 1.2 Member — `Member.groovy`
- حقول: اسم، بريد فريد، هاتف (10–12 رقم)، عنوان/مدينة من قائمة، `membershipDate`، `active`، أرشفة.
- علاقة: `hasMany` استعارات.
- دوال مساعدة: `getFirstChar()`، `getMemberSinceYear()`.

### 1.3 Category — `Category.groovy`
- `categoryName` فريد؛ `hasMany` كتب.

### 1.4 Borrow — `Borrow.groovy`
- حقول: كتاب، عضو، `borrowDate`، `dueDate`، `returnDate`، `status` ∈ {`BORROWED`, `RETURNED`, `LATE`}.
- `beforeInsert`: تعبئة تواريخ افتراضية إن لزم.
- `isLate()`: مساعد للواجهة/المنطق عند الحاجة.

### 1.5 User — `User.groovy` (جدول `app_user`)
- `username`، `email`، `passwordHash`، `role` ∈ {`MEMBER`,`ADMIN`}، `enabled`، `emailConfirmed`، `confirmationToken`، `confirmationCode`.

### 1.6 Reservation — `Reservation.groovy`
- كتاب، عضو، `status` ∈ {`ACTIVE`,`NOTIFIED`,`CANCELLED`,`COMPLETED`}، طوابع زمنية.

---

## 2. الخدمات (Services)

### 2.1 DashboardService
- `getStats()` — إحصائيات مجمّعة للوحة التحكم.
- `getBorrowingActivity()` — بيانات آخر 7 أيام للرسم البياني.

### 2.2 BorrowService
- `getDaysUntilDue()` — من `carmel.borrow.daysUntilDue` (افتراضي 14).
- `getBorrowStartDaysInPast()` — عند تفعيل وضع الاختبار، لقراءة `carmel.notification.testing.borrowStartDaysInPast`.
- `getBorrows(filter)` — فلترة حسب الحالة أو الكل.
- `borrow(bookId, memberId, httpSession)` — قواعد العمل: كتاب/عضو نشطان، نسخ متاحة، تصنيف موجود، منع تكرار الاستعارة النشطة لنفس الزوج؛ حساب `borrowDate`/`dueDate` مع **ساعة الاختبار** واختيياً **رجوع تاريخ الاستعارة** (`borrowStartDaysInPast`); تقليل `availableCopies`.
- `returnBookService(borrowId, httpSession)` — تاريخ إرجاع حسب ساعة الاختبار؛ حساب أيام التأخير؛ `status` = `LATE` إن كان متأخراً وإلا `RETURNED`؛ زيادة النسخ؛ استدعاء `notificationService.notifyReservationsForBook` عند توفر نسخ.
- `updateLateBorrows(httpSession)` — تحويل `BORROWED` ذات `dueDate` قبل بداية «اليوم الفعلي» إلى `LATE`.

### 2.3 BookService / MemberService / CategoryService
- بحث، حفظ، تحديث، أرشفة/استرجاع مع التحقق من عدم وجود استعارات نشطة؛ تعديل النسخ عند تغيير `totalCopies`.

### 2.4 ReportService
- `resolveDateRange` — تفسير سنة/شهر/من-إلى.
- تقارير: استعارات، إحصاءات كتب، نشاط مستخدمين، كتب شائعة، شهري، سنوي.
- `buildReportModel` — نموذج موحّد للعرض والتصدير.
- `exportReportToExcel` (CSV) و `exportReportToPDF` (OpenPDF).

### 2.5 SecurityService
- تشفير ومطابقة BCrypt؛ `getCurrentUser`، `isLoggedIn`، `hasRole` مع التحقق من `enabled` و`emailConfirmed` حيث ينطبق.

### 2.6 EmailService
- إرسال البريد عبر إعدادات Spring Mail.

### 2.7 NotificationService
- `getReminderDaysBeforeDue()` من YAML.
- `sendDueDateReminders(explicitToday)` — استعارات `BORROWED` بموعد استحقاق في يوم التذكير (حسب `reminderDaysBeforeDue`).
- `sendLateNotices(explicitToday)` — استعارات متأخرة غير مُرجَعة؛ نص ديناميكي بالأيام والرسوم ($1/يوم).
- `notifyReservationsForBook(book)` — بعد الإرجاع إن توفرت نسخ؛ إيميل للمحجوزين وتغيير الحالة إلى `NOTIFIED`.

### 2.8 NotificationClockService
- في الإنتاج: «اليوم» = التاريخ الحالي.
- في الاختبار (`carmel.notification.testing.enabled: true`): من جلسة `notificationTestDate` (يُضبط عند تسجيل الدخول من `virtualTodayOnLogin`) أو من `fixedEffectiveDate` للمهام بدون HTTP (الجدولة).

### 2.9 DailyNotificationSchedulerService
- `@Scheduled(cron = '${carmel.notification.dailyCron:...}')` — إن `dailyScheduleEnabled` مفعّل: `updateLateBorrows(null)` ثم `sendDueDateReminders` و`sendLateNotices` مع `resolveEffectiveToday(null)`.

### 2.10 ReservationService
- `createReservation`، `cancelReservation` مع قواعد التوفّر وتجنب التكرار.

---

## 3. الكنترولرز (ملخص)

| الكنترولر | الدور |
|-----------|--------|
| `DashboardController` | لوحة الأدمن |
| `BookController` | الكتب والأرشيف |
| `MemberController` | الأعضاء |
| `CategoryController` | التصنيفات |
| `BorrowController` | الاستعارة (يمرّر `session` لـ BorrowService) |
| `ReportController` | التقارير؛ `exportPdf` و`exportExcel` بنفس الفلاتر |
| `AuthController` | دخول، تسجيل، تأكيد بريد، ضبط تاريخ الاختبار في الجلسة عند التفعيل |
| `MemberAreaController` | واجهة العضو (كتب، استعارة، حجوزات) |
| `ReservationAdminController` | قائمة الحجوزات للأدمن |

---

## 4. الواجهات والتجربة

- **Layout `main.gsp`:** Bootstrap، أيقونات، SweetAlert2، شريط جانبي (Dashboard، Books، Categories، Borrowing، Members، Reports)، تحميل CSS/JS حسب الصفحة، Chart.js للـ dashboard والتقارير.
- **Auth:** `login.gsp`، `registerMember.gsp` — تنسيق `auth-page` في `application.css` (خلفية خضراء، كرت وسط).
- **Member Area:** `books.gsp`، `borrowHistory.gsp`، `reservations.gsp`.
- **التقارير:** `report/index.gsp` + `reports.css` + `reports.js`.

---

## 5. الإعدادات — `application.yml` (قسم carmel)

```yaml
carmel:
  borrow:
    daysUntilDue: 14
  notification:
    reminderDaysBeforeDue: 1
    dailyScheduleEnabled: true
    dailyCron: "0 0 20 * * ?"
    testing:
      enabled: false
      virtualTodayOnLogin: "yyyy-MM-dd"
      fixedEffectiveDate: "yyyy-MM-dd"
      borrowStartDaysInPast: 0
```

- **`daysUntilDue`:** مدة الاستعارة بالأيام من تاريخ بداية الاستعارة المحسوب.
- **`reminderDaysBeforeDue`:** قبل كم يوم من الاستحقاق يُرسل التذكير.
- **`dailyScheduleEnabled` / `dailyCron`:** تشغيل يومي: تحديث LATE ثم التذكير ثم إشعارات التأخير (توقيت السيرفر).
- **وضع الاختبار:**
  - `enabled: true` + `virtualTodayOnLogin`: عند تسجيل الدخول يُخزَّن التاريخ في الجلسة كـ «اليوم».
  - `fixedEffectiveDate`: نفس المعنى للجدولة (بدون جلسة).
  - `borrowStartDaysInPast`: مثلاً **15** مع `daysUntilDue: 14` يجعل موعد الإرجاع **قبل** «اليوم» بيوم واحد → تظهر **LATE** مباشرة بعد فتح صفحة الاستعارة.

**البريد:** إعداد `spring.mail` (مضيف، منفذ، مستخدم، كلمة مرور) في نفس الملف أو profile.

**بعد تعديل YAML:** إعادة تشغيل التطبيق.

---

## 6. التحقق من LATE والإيميل (اختبار يدوي)

1. **تحديث الحالة:** فتح **Borrowing System** يستدعي `updateLateBorrows(session)`؛ الجدولة اليومية تفعل ذلك أيضاً لأي استعارة `BORROWED` و`dueDate` قبل بداية اليوم.
2. **الإرجاع المتأخر:** زر الإرجاع يعرض رسوم التأخير؛ الحالة تُسجَّل `LATE` عند التأخير.
3. **التذكير:** استعارة `BORROWED` بموعد استحقاق يطابق منطق `reminderDaysBeforeDue`؛ انتظار `dailyCron` أو تغييره مؤقتاً للتجربة (مثلاً كل دقيقة).
4. **إشعار التأخير:** استعارة متأخرة غير مُرجَعة عند تشغيل الجدولة.
5. **فشل الإرسال:** مراجعة الـ console وإعدادات SMTP.

---

## 7. تدفقات من الألف للياء (أمثلة)

### إضافة كتاب
الواجهة → `BookController.save` → `BookService.saveBook` → تحقق الدومين → تحديث القائمة ورسالة نجاح/خطأ.

### تسجيل عضو (ذاتي)
`AuthController.registerMember` → إنشاء `User` + `Member` → إيميل تأكيد → `confirmEmail` يفعّل الحساب.

### استعارة (أدمن)
`BorrowController.save` → `BorrowService.borrow(..., session)` → حفظ الاستعارة وتقليل النسخ.

### إرجاع
`BorrowController.returnBook` → `returnBookService(..., session)` → تحديث الحالة والنسخ وإشعار الحجوزات إن انطبق.

### أرشفة كتاب/عضو
التحقق من عدم وجود استعارات نشطة → `active = false` وحقول الأرشفة.

### تقرير مفلتر
`ReportController.index` → `ReportService.buildReportModel` + نفس الفلاتر للتصدير PDF/Excel.

---

## 8. ملاحظات تصميم

- منطق الأعمال في الـ **Services**؛ الكنترولرز رفيعة.
- **Soft delete** للكتب والأعضاء للحفاظ على التاريخ والتقارير.
- التقارير مبنية على `Borrow` كجدول زمني مع فلاتر قابلة للتوسع.
- الجدولة عبر Spring (`@EnableScheduling` في `Application.groovy`).

---

*آخر تحديث للتوثيق: دمج المحتوى السابق من `SYSTEM_DOC_AR.md`، `SYSTEM_DOC_AR_EXTENDED.md`، `SYSTEM_DOC_EN.md`، و`LATE_AND_NOTIFICATIONS.md` في هذا الملف المرجعي الوحيد.*

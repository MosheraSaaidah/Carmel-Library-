## توثيق مكمل لنظام مكتبة الكرمل – الميزات الإضافية

هذا الملف يكمل `SYSTEM_DOC_AR.md` ويشرح الميزات التي أضفناها لاحقاً:

- نظام المستخدمين والأدوار (ADMIN / MEMBER).
- تسجيل الدخول وتسجيل الأعضاء مع تفعيل البريد الإلكتروني.
- منطقة العضو (Member Area): عرض الكتب، الاستعارة الذاتية، والحجوزات.
- نظام الحجوزات (Reservation) وإشعارات توفر الكتب.
- نظام تنبيهات البريد (تذكير بموعد الإرجاع + إشعار التأخير + late fee).
- إعدادات الأعمال في `application.yml`.
- صفحة إدارة الحجوزات للأدمن.
- تحسينات واجهات الـLogin / Register والخلفية.

> ملاحظة: هذا الملف لا يكرر ما ورد في `SYSTEM_DOC_AR.md` بل يركّز على الإضافات فقط.

---

### 1. نظام المستخدمين والأدوار (Users & Roles)

#### 1.1 دومين User

- **المسار**: `grails-app/domain/alCarmel/User.groovy`
- الحقول:
  - `String username`
  - `String email`
  - `String passwordHash`
  - `String role` ∈ {`ADMIN`, `MEMBER`}
  - `Boolean enabled = false`
  - `Boolean emailConfirmed = false`
  - `String confirmationToken`
  - `String confirmationCode` (لم نعد نستخدمه حالياً بعد الانتقال لنظام التوكن).
- القيود:
  - `username` و `email` فريدان وغير فارغين.
  - `role` يجب أن يكون من القائمة `"MEMBER", "ADMIN"`.

#### 1.2 SecurityService

- **المسار**: `grails-app/services/alCarmel/SecurityService.groovy`
- الوظائف:
  - `encodePassword(raw)` و `passwordsMatch(raw, encoded)` باستخدام `BCryptPasswordEncoder`.
  - `getCurrentUser(session)` لإرجاع كيان User من الـ session.
  - `isLoggedIn(session)` للتحقق إن كان هناك مستخدم في الجلسة.
  - `hasRole(session, role)`:
    - يعيد true فقط إذا:
      - هناك User في الجلسة.
      - `user.role == role`.
      - `enabled = true`.
      - `emailConfirmed = true`.
    - يُستخدم لحماية كل الكنترولرز الإدارية (Dashboard, Books, Members, Reports, Borrowing System) للأدمن فقط، ومنطقة العضو لـ MEMBER فقط.

---

### 2. تسجيل الدخول وتسجيل الأعضاء (AuthController)

#### 2.1 Login

- **المسار**: `grails-app/controllers/alCarmel/AuthController.groovy`, action `login`.
- GET:
  - لو المستخدم مسجّل دخول:
    - ADMIN → يوجَّه إلى Dashboard (`dashboard/index`).
    - MEMBER → يوجَّه إلى Member Area (`memberArea/books`).
  - غير ذلك يعرض صفحة `views/auth/login.gsp`.
- POST:
  - يستقبل `params.username` (يمكن أن يكون username أو email) و `params.password`.
  - يبحث عن المستخدم:
    - `User.findByUsername(usernameOrEmail) ?: User.findByEmail(usernameOrEmail)`.
  - يتحقق من كلمة السر عبر `securityService.passwordsMatch`.
  - يرفض الدخول مع رسالة `"Invalid credentials."` إن لم تتطابق البيانات.
  - يتأكد:
    - إن لم يكن `emailConfirmed` → رسالة `"Please confirm your email before logging in."`.
    - إن لم يكن `enabled` → رسالة `"Your account is disabled."`.
  - في حال النجاح:
    - يضع `session.userId = user.id` و `session.role = user.role`.
    - يوجّه حسب الدور (ADMIN أو MEMBER).

#### 2.2 واجهة login

- **المسار**: `grails-app/views/auth/login.gsp`
- مميزات:
  - خلفية خضراء فاتحة (class `auth-page` على الـ body).
  - كرت أبيض وسط الصفحة فيه:
    - `WELCOME TO` بخط صغير.
    - `AlCarmel Library` كعنوان رئيسي.
    - سطر فرعي: `Member & Admin Login`.
  - فورم:
    - حقل Username or Email.
    - حقل Password.
    - زر Login بلون أخضر متناسق مع الخلفية.
    - زر **Create new account** للانتقال إلى صفحة التسجيل.

#### 2.3 تسجيل عضو جديد (registerMember)

- **المسار**: `AuthController.registerMember`.
- GET:
  - إن كان المستخدم مسجّلاً (ADMIN أو MEMBER) → يعاد توجيهه لواجهته ولا يُسمح له بالتسجيل مرة أخرى.
  - غير ذلك يعرض `views/auth/registerMember.gsp`.
- POST:
  - يتحقق من:
    - `fullName` غير فارغ.
    - `email` غير فارغ، صيغة صحيحة، وغير مستخدم في User.
    - `phoneNumber` غير فارغ.
    - `address` (City) غير فارغة.
    - `password` غير فارغة وطولها ≥ 6.
  - في حال وجود أخطاء:
    - يبني Map `errors` لكل حقل مع رسالة مناسبة.
    - يعيد عرض `registerMember.gsp` مع `errors` + `values` لإعادة تعبئة الحقول.
  - في حال الصحة:
    - يولّد `token = UUID.randomUUID().toString()`.
    - ينشئ User:
      - `username = email`, `email = email`, `role = "MEMBER"`.
      - `passwordHash = securityService.encodePassword(password)`.
      - `enabled = false`, `emailConfirmed = false`, `confirmationToken = token`.
    - ينشئ Member مربوطاً بهذا الـUser.
    - يرسل إيميل تفعيل عبر `EmailService.sendEmail` يحتوي:
      - `Confirm link: http://localhost:8080/auth/confirmEmail?token=<token>`.
    - يعرض رسالة نجاح للمستخدم بأن عليه تأكيد بريده ثم يعيده للـLogin.

#### 2.4 تأكيد البريد (confirmEmail)

- **المسار**: `AuthController.confirmEmail`.
- يستقبل `params.token`.
- يجد المستخدم بـ `User.findByConfirmationToken(token)`.
- إن لم يُوجد:
  - يعرض رسالة `"Invalid or expired confirmation link."` ويعيد لصفحة login.
- إن وُجد:
  - يضبط:
    - `emailConfirmed = true`.
    - `enabled = true`.
    - `confirmationToken = null`.
  - يحفظ ويعرض رسالة `"Your email has been confirmed. You can now log in."`.

---

### 3. إعدادات الأعمال في application.yml (مدة الاستعارة والتذكير)

- تمت إضافة قسم جديد:

```yaml
carmel:
  borrow:
    daysUntilDue: 14          # مدة الاستعارة بالأيام
  notification:
    reminderDaysBeforeDue: 1  # قبل كم يوم نرسل تذكير بالاستحقاق
```

- **BorrowService**:
  - يقرأ `carmel.borrow.daysUntilDue` عبر:
    - `grailsApplication.config.getProperty('carmel.borrow.daysUntilDue', Integer, 14)`.
  - يحدد `dueDate = today + daysUntilDue`.
- **NotificationService**:
  - يقرأ `carmel.notification.reminderDaysBeforeDue` بنفس الطريقة.
  - يختار الاستعارات التي `dueDate` لها يساوي التاريخ المطلوب قبل N أيام من اليوم، ويرسل لها تذكيراً.

---

### 4. منطقة العضو (Member Area)

#### 4.1 MemberAreaController

- **المسار**: `grails-app/controllers/alCarmel/MemberAreaController.groovy`
- يعتمد على:
  - `SecurityService` للتحقق من الدور.
  - `BorrowService` للاستعارة.
  - `ReservationService` للحجوزات.

الدوال الرئيسية:

1. `books()`:
   - متاحة فقط لـ `MEMBER`.
   - تجلب العضو المرتبط بالمستخدم الحالي.
   - تعرض **كل** الكتب النشطة (حتى لو `availableCopies = 0`).
   - تجمع `reservedBookIds` لكل كتاب لدى العضو عليه حجز `ACTIVE` أو `NOTIFIED`.
   - ترسل `books` + `reservedBookIds` إلى `memberArea/books.gsp`.

2. `borrow(Long bookId)`:
   - تسمح للعضو باستعارة كتاب مباشرة إن كانت هناك نسخ متاحة.
   - تستدعي `borrowService.borrow(bookId, member.id)` وتعرض رسائل النجاح/الخطأ.

3. `reservations()`:
   - تعرض كل حجوزات العضو الحالي (مرتبة تنازلياً بالتاريخ) في `memberArea/reservations.gsp`.

4. `reserve(Long bookId)`:
   - يتحقق أن الكتاب موجود ونشط ولا توجد نسخ متاحة (`availableCopies = 0`).
   - يمنع تكرار الحجز لنفس الكتاب والعضو (`ACTIVE` أو `NOTIFIED`).
   - يستدعي `reservationService.createReservation(bookId, member)` ويعرض الرسائل.

5. `cancelReservation(Long id)`:
   - يتحقق أن الحجز يخص العضو الحالي.
   - يستدعي `reservationService.cancelReservation(id, member)` ويغيّر الحالة إلى `CANCELLED`.

#### 4.2 واجهات Member Area

- `views/memberArea/books.gsp`:
  - جدول كتب مع عمود Action:
    - زر **Borrow** عند توفر نسخ.
    - زر **Reserve** عند نفاد النسخ.
    - Badge `Reserved` إن كان العضو حاجزاً الكتاب مسبقاً.
- `views/memberArea/borrowHistory.gsp`:
  - يعرض تاريخ استعارات العضو: Book, Borrow Date, Due Date, Return Date, Status.
- `views/memberArea/reservations.gsp`:
  - يعرض حجوزات العضو مع زر **Cancel** عندما تكون الحالة `ACTIVE` أو `NOTIFIED`.

---

### 5. نظام الحجوزات (Reservation) وإدارة الحجوزات للأدمن

#### 5.1 دومين Reservation

- **المسار**: `grails-app/domain/alCarmel/Reservation.groovy`
- الحقول:
  - `Book book`
  - `Member member`
  - `String status` ∈ {`ACTIVE`, `NOTIFIED`, `CANCELLED`, `COMPLETED`}
  - `Date dateCreated`, `lastUpdated`
- العلاقات:
  - `static belongsTo = [book: Book, member: Member]`.

#### 5.2 ReservationService

- **المسار**: `grails-app/services/alCarmel/ReservationService.groovy`
- دالتان رئيسيتان:
  - `createReservation(Long bookId, Member member)`:
    - تتأكد من أن الكتاب نشط ولا توجد نسخ متاحة.
    - تمنع تكرار الحجز لنفس العضو والكتاب.
    - تنشئ Reservation بحالة `ACTIVE`.
  - `cancelReservation(Long reservationId, Member member)`:
    - تتأكد أن الحجز يخص العضو.
    - تغيّر الحالة إلى `CANCELLED`.

#### 5.3 واجهة إدارة الحجوزات للأدمن

- **ReservationAdminController**:
  - **المسار**: `grails-app/controllers/alCarmel/ReservationAdminController.groovy`.
  - `index()`:
    - محمي بدور `ADMIN`.
    - يجلب جميع الحجوزات (`Reservation.list(...)`) مرتبة تنازلياً بالتاريخ.
- **الواجهة**: `views/reservationAdmin/index.gsp`:
  - جدول read‑only فيه كل الحجوزات:
    - #، Book، Member، Status، Created At.
  - لا يسمح للأدمن بإلغاء حجز العضو (حسب متطلبات النظام، الإلغاء من حق العضو فقط).

---

### 6. إشعارات البريد (NotificationService)

- **المسار**: `grails-app/services/alCarmel/NotificationService.groovy`
- يعتمد على:
  - `EmailService` لإرسال الرسائل.
  - `grailsApplication` لقراءة إعدادات `carmel.notification.reminderDaysBeforeDue`.

#### 6.1 تذكير قبل موعد الإرجاع

- `sendDueDateReminders(Date today = new Date())`:
  - يحدد اليوم المستهدف بناءً على `reminderDaysBeforeDue`.
  - يبحث عن كل `Borrow` بحالة `BORROWED` و `dueDate` يطابق هذا اليوم.
  - يرسل لكل عضو رسالة تذكير مع:
    - اسم الكتاب.
    - تاريخ الاستحقاق.
    - عدد الأيام المتبقية (غداً / بعد N أيام).

#### 6.2 إشعارات التأخير + الرسوم

- `sendLateNotices(Date today = new Date())`:
  - يحسب `startOfToday`.
  - يبحث عن كل `Borrow` بحالة `BORROWED` أو `LATE` حيث `dueDate < startOfToday`.
  - لكل استعارة:
    - يحسب `daysLate` (عدد الأيام بعد الاستحقاق).
    - `fee = daysLate` (1$ لكل يوم).
    - يرسل رسالة تبين:
      - عدد أيام التأخير.
      - المبلغ الحالي المستحق.

#### 6.3 إشعار توفر كتاب محجوز

- `notifyReservationsForBook(Book book)`:
  - تُستدعى بعد إرجاع كتاب في `BorrowService.returnBookService`.
  - إن أصبح لدى الكتاب نسخ متاحة (`availableCopies > 0`):
    - تجلب كل `Reservation` بحالة `ACTIVE` لهذا الكتاب.
    - ترسل لكل عضو حاجز رسالة بأن الكتاب أصبح متاحاً.
    - تغيّر الحالة إلى `NOTIFIED`.

---

### 7. تحسين واجهات الـLogin / Register والخلفية

- تم تمييز صفحات الـAuth بكلاس `auth-page` على `<body>` (في layout الرئيسي).
- في `application.css`:
  - `.auth-page .main-content`:
    - خلفية بتدرج أخضر فاتح.
    - محاذاة الكرت في وسط الشاشة عمودياً وأفقياً.
  - `.auth-page .card-white`:
    - زوايا دائرية أكبر وظل أنعم.
  - `.auth-page .btn-green`:
    - تم ضبط ألوان الزر ليتناسق مع الخلفية (أخضر متوسط مع نص غامق).
- تم توحيد عناوين الـLogin/Register لتعرض:
  - Welcome to
  - AlCarmel Library
  - وصف قصير يوضح وظيفة الشاشة (Login أو Register).

بهذا الملحق، أصبح توثيق النظام يغطي أيضاً:
- التسجيل الذاتي للأعضاء مع تفعيل البريد.
- الأدوار والـMember Area.
- الحجوزات وإدارتها وإشعارات توفر الكتب.
- نظام التذكير بالتاريخ والـLate Fee.
- تحسينات تجربة المستخدم في شاشات الدخول والتسجيل.

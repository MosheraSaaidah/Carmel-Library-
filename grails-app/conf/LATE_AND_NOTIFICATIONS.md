# التحقق من حالة LATE والرسائل الإيميل + أين نتحكم بعدد الأيام

## 1) أين نتحكم بعدد أيام الإرجاع ووقت التذكير؟

في ملف **`grails-app/conf/application.yml`** في أعلى الملف (قسم `carmel`):

```yaml
carmel:
    borrow:
        daysUntilDue: 14          # مدة الاستعارة بالأيام (موعد الإرجاع = تاريخ الاستعارة + هذا العدد)
    notification:
        reminderDaysBeforeDue: 1  # قبل كم يوم من موعد الإرجاع نرسل تذكير (1 = يوم واحد قبل، 2 = يومين قبل، ...)
```

- **daysUntilDue:** عدد أيام الاستعارة. لو غيّرته إلى 7 يصير موعد الإرجاع بعد أسبوع.
- **reminderDaysBeforeDue:** قبل كم يوم من الموعد نرسل إيميل "تذكير موعد الإرجاع". 1 = يوم واحد قبل (غداً)، 2 = يومين قبل، وهكذا.

بعد أي تعديل على `application.yml` أعد تشغيل التطبيق.

---

## 2) كيف تتأكد أن LATE شغال صح؟

1. **تحديث الحالة تلقائياً (BORROWED → LATE):**
   - عند فتح صفحة **Borrowing System** (من قائمة الأدمن) يُستدعى `updateLateBorrows()`.
   - أي استعارة حالتها **BORROWED** وموعد إرجاعها **أصغر من اليوم** تُحدَّث إلى **LATE**.
   - **اختبار:** أنشئ استعارة وعدّل في قاعدة البيانات (أو انتظر) حتى يصير `due_date` في الماضي، ثم افتح Borrowing System وتأكد أن الحالة صارت LATE.

2. **عند الإرجاع (Return):**
   - عند ضغط **Return Book** يُحسب عدد أيام التأخير ويُعرض الرسوم ($1 لكل يوم).
   - إذا كان تاريخ الإرجاع بعد الموعد، الحالة تُحفظ **LATE** (للتاريخ) ويظهر في التقرير ورسالة النجاح عدد الأيام والرسوم.
   - **اختبار:** استعارة موعدها مثلاً 10 آذار وأرجعها 13 آذار → يتوقع رسالة نجاح فيها "Late fee: $3" والحالة LATE في السجل.

---

## 3) كيف تتأكد أن الرسائل (الإيميل) شغالة صح؟

1. **تذكير موعد الإرجاع (Due Date Reminder):**
   - يُرسل لمن عنده استعارة **BORROWED** وموعد الإرجاع يصادف **بعد N أيام** (N = `reminderDaysBeforeDue`).
   - **اختبار:** ضع `reminderDaysBeforeDue: 1`، أنشئ استعارة موعد إرجاعها غداً، ثم من واجهة Borrowing System اضغط **Send Email Notifications** → يفترض يصل إيميل "Due date reminder" للعضو.

2. **إشعار التأخير (Late Notice):**
   - يُرسل لكل استعارة **متأخرة** (موعد الإرجاع في الماضي) ولم تُرجَع بعد.
   - النص ديناميكي: "You are X day(s) late" و "Current late fee: $X".
   - **اختبار:** استعارة متأخرة يوم أو أكثر، اضغط **Send Email Notifications** → يفترض يصل إيميل "Late notice" بعدد الأيام والمبلغ.

3. **إعداد البريد:**
   - الإيميلات تخرج عبر `spring.mail` في `application.yml` (نفس إعداد Gmail/App Password).
   - لو الإيميل لا يصل، راجع الـ console لأي أخطاء وإعدادات البريد.

---

## 4) ملخص الأماكن في الكود

| المطلوب | الملف | الملاحظة |
|--------|-------|----------|
| عدد أيام الاستعارة | `application.yml` → `carmel.borrow.daysUntilDue` | يُقرأ في `BorrowService.getDaysUntilDue()` |
| قبل كم يوم نرسل التذكير | `application.yml` → `carmel.notification.reminderDaysBeforeDue` | يُقرأ في `NotificationService.getReminderDaysBeforeDue()` |
| تحديث BORROWED → LATE | `BorrowService.updateLateBorrows()` | يُستدعى من `BorrowController.index()` عند فتح صفحة الاستعارات |
| إرسال التذكير + Late Notice | زر "Send Email Notifications" في صفحة Borrowing System | يستدعي `BorrowController.sendNotifications()` ثم `NotificationService.sendDueDateReminders()` و `sendLateNotices()` |

---
name: "compose-desktop-window-tray-menu"
description: "مهارة وتوجيهات تطبيق Compose Desktop Window, Tray and Menu APIs في المشاريع. واجهات النوافذ وشريط القوائم والأيقونة في منطقة الإشعارات والإشعارات المحلية لتطبيقات سطح المكتب من Compose Desktop."
apt_id: "APT-191"
domain: "UI, State & Navigation"
source_file: "[[02 - UI, State & Navigation.md]]"
tags:
  - tool-skill
  - android-preferred-tools
  - apt-191
---

# Compose Desktop Window, Tray and Menu APIs (APT-191)

## 1. الهوية
- **الاسم:** Compose Desktop Window, Tray and Menu APIs
- **المعرّف المعماري:** `APT-191`
- **النوع:** platform-api
- **الدور الأساسي:** واجهات النوافذ وشريط القوائم والأيقونة في منطقة الإشعارات والإشعارات المحلية لتطبيقات سطح المكتب من Compose Desktop.

## 2. الموقع في المنظومة
- **النطاق المعماري:** UI, State & Navigation
- **سير العمل:** يُعتمد ضمن سياق `02 - UI, State & Navigation.md` لإدارة وتطوير طبقة (`desktopMain`).
- **معيار الاستخدام:**
  - **يُرشّح عندما:** تطبيق مكتبي يحتاج قوائم أو أيقونة نظام أو عدة نوافذ.
  - **يُستبعد عندما:** تطبيقات الهاتف والويب.

## 3. التوافق والاعتماديات
- **المتطلبات والبيئة:**
- طبقة التشغيل: `desktopMain`.
- المنصات المدعومة: macOS (✅), Windows (✅).
- مسار Android: غير منطبق: ليست أداة أندرويدية.
- مسار KMP متعدد المنصات: تُغلَّف خلف واجهة `DesktopShell` في `commonMain` وتُنفَّذ في `desktopMain`؛ الإشعارات المحلية تختلف في السلوك بين macOS و Windows ويلزم اختبار كل نظام.
- التوافق والتحقق: Tier 1 رسمي من JetBrains لواجهات النوافذ؛ سلوك الإشعارات والـ Tray مشروط بالنظام. (كُتب من معرفة المراجع دون تحقق شبكي؛ يلزم التحقق الخارجي قبل الاعتماد.)
- **البدائل والتعارضات:** واجهات Native (AppKit / Win32) عبر JNA/JNI عند الحاجة المتقدمة.
- **حدود المنصات:** Linux مستبعد صراحةً، مع تركيز التوافق على خماسي (Android, iOS, macOS, Windows, Web).

## 4. المرجع الرسمي
- **الوثائق الرسمية:** https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-desktop-components.html
- **المصدر الداخلي:** [[02 - UI, State & Navigation.md#APT-191|سجل الأداة في 02 - UI, State & Navigation.md]]

استخدم المرجع الرسمي والأدلة الداخلية للتحقق الدقيق من توافق الإصدارات وسلوكيات الأداء. هذا السجل مضاف في الإصدار 2 ولم يُتحقق منه شبكياً.

## 5. تعليمات الاستخدام
- التزم بنطاق المسؤولية المحدد: واجهات النوافذ وشريط القوائم والأيقونة في منطقة الإشعارات والإشعارات المحلية لتطبيقات سطح المكتب من Compose Desktop.
- في مسار KMP: راعِ حدود التجريد وضع الواجهات في `commonMain` وحصر التنفيذ في المنصات المستهدفة.
- راجع `13 - Platform Capability Matrix.md` لمعرفة المكافئ على بقية المنصات.

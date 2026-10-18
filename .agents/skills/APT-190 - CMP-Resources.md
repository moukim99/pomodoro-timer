---
name: "cmp-resources"
description: "مهارة وتوجيهات تطبيق Compose Multiplatform Resources في المشاريع. نظام الموارد الموحد في Compose Multiplatform (نصوص مترجمة، صور، خطوط، ألوان) المستخدم للتدويل عبر المنصات."
apt_id: "APT-190"
domain: "UI, State & Navigation"
source_file: "[[02 - UI, State & Navigation.md]]"
tags:
  - tool-skill
  - android-preferred-tools
  - apt-190
---

# Compose Multiplatform Resources (APT-190)

## 1. الهوية
- **الاسم:** Compose Multiplatform Resources
- **المعرّف المعماري:** `APT-190`
- **النوع:** resources-library
- **الدور الأساسي:** نظام الموارد الموحد في Compose Multiplatform (نصوص مترجمة، صور، خطوط، ألوان) المستخدم للتدويل عبر المنصات.

## 2. الموقع في المنظومة
- **النطاق المعماري:** UI, State & Navigation
- **سير العمل:** يُعتمد ضمن سياق `02 - UI, State & Navigation.md` لإدارة وتطوير طبقة (`commonMain`).
- **معيار الاستخدام:**
  - **يُرشّح عندما:** واجهات Compose مشتركة متعددة المنصات.
  - **يُستبعد عندما:** مشروع Android صرف بموارد `res/` (كافٍ).

## 3. التوافق والاعتماديات
- **المتطلبات والبيئة:**
- طبقة التشغيل: `commonMain`.
- المنصات المدعومة: Android (✅), iOS (✅), macOS (✅), Windows (✅).
- مسار Android: يحل محل `res/` التقليدي عند الانتقال إلى `commonMain` (يبقى `res/` متاحاً للأندرويد).
- مسار KMP متعدد المنصات: موارد مشتركة تُولَّد منها مراجع آمنة الأنواع؛ تُستخدم لدعم RTL والترجمة (APT-054/056).
- التوافق والتحقق: Tier 1 رسمي من JetBrains. (كُتب من معرفة المراجع دون تحقق شبكي؛ يلزم التحقق الخارجي قبل الاعتماد.)
- **البدائل والتعارضات:** Android Resources (Android فقط)، Moko Resources.
- **حدود المنصات:** Linux مستبعد صراحةً، مع تركيز التوافق على خماسي (Android, iOS, macOS, Windows, Web).

## 4. المرجع الرسمي
- **الوثائق الرسمية:** https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources.html
- **المصدر الداخلي:** [[02 - UI, State & Navigation.md#APT-190|سجل الأداة في 02 - UI, State & Navigation.md]]

استخدم المرجع الرسمي والأدلة الداخلية للتحقق الدقيق من توافق الإصدارات وسلوكيات الأداء. هذا السجل مضاف في الإصدار 2 ولم يُتحقق منه شبكياً.

## 5. تعليمات الاستخدام
- التزم بنطاق المسؤولية المحدد: نظام الموارد الموحد في Compose Multiplatform (نصوص مترجمة، صور، خطوط، ألوان) المستخدم للتدويل عبر المنصات.
- في مسار KMP: راعِ حدود التجريد وضع الواجهات في `commonMain` وحصر التنفيذ في المنصات المستهدفة.
- راجع `13 - Platform Capability Matrix.md` لمعرفة المكافئ على بقية المنصات.

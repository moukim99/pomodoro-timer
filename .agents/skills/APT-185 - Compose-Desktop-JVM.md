---
name: "compose-desktop-jvm"
description: "مهارة وتوجيهات تطبيق Compose Desktop (JVM Target) في المشاريع. هدف Compose Multiplatform لسطح المكتب فوق JVM، وهو المسار العملي المعتمد في هذا الدفتر لتطبيقات Windows و macOS من نفس كود KMP."
apt_id: "APT-185"
domain: "Platform & Base Architecture"
source_file: "[[01 - Platform & Base Architecture.md]]"
tags:
  - tool-skill
  - android-preferred-tools
  - apt-185
---

# Compose Desktop (JVM Target) (APT-185)

## 1. الهوية
- **الاسم:** Compose Desktop (JVM Target)
- **المعرّف المعماري:** `APT-185`
- **النوع:** ui-target
- **الدور الأساسي:** هدف Compose Multiplatform لسطح المكتب فوق JVM، وهو المسار العملي المعتمد في هذا الدفتر لتطبيقات Windows و macOS من نفس كود KMP.

## 2. الموقع في المنظومة
- **النطاق المعماري:** Platform & Base Architecture
- **سير العمل:** يُعتمد ضمن سياق `01 - Platform & Base Architecture.md` لإدارة وتطوير طبقة (هدف `desktopMain`/`jvm` + تحزيم أصلي).
- **معيار الاستخدام:**
  - **يُرشّح عندما:** تطبيق مكتبي من كود KMP مشترك على Windows و macOS.
  - **يُستبعد عندما:** تطبيقات تتطلب واجهة Native مكتبية بالكامل (WinUI / AppKit).

## 3. التوافق والاعتماديات
- **المتطلبات والبيئة:**
- طبقة التشغيل: هدف `desktopMain`/`jvm` + تحزيم أصلي.
- المنصات المدعومة: macOS (✅), Windows (✅) — عبر JVM (Linux خارج النطاق).
- مسار Android: غير منطبق: ليست أداة أندرويدية.
- مسار KMP متعدد المنصات: يوفر النوافذ والقوائم وشريط المهام (APT-191) والتحزيم إلى `.dmg`/`.pkg`/`.msi`/`.exe` عبر jpackage أو Conveyor؛ والمكتبات المشار إليها بـ «Desktop JVM» في الفهرس تعمل هنا.
- التوافق والتحقق: Tier 1 رسمي من JetBrains. (كُتب من معرفة المراجع دون تحقق شبكي؛ يلزم التحقق الخارجي قبل الاعتماد.) توضيح قراءة الجداول: عبارة «Windows/macOS» في الفهرس تعني هذا الهدف ما لم يُذكر خلاف ذلك.
- **البدائل والتعارضات:** SwiftUI لـ macOS، WinUI 3 لـ Windows، Electron/Tauri (خارج مسار Kotlin).
- **حدود المنصات:** Linux مستبعد صراحةً، مع تركيز التوافق على خماسي (Android, iOS, macOS, Windows, Web).

## 4. المرجع الرسمي
- **الوثائق الرسمية:** https://github.com/JetBrains/compose-multiplatform
- **المصدر الداخلي:** [[01 - Platform & Base Architecture.md#APT-185|سجل الأداة في 01 - Platform & Base Architecture.md]]

استخدم المرجع الرسمي والأدلة الداخلية للتحقق الدقيق من توافق الإصدارات وسلوكيات الأداء. هذا السجل مضاف في الإصدار 2 ولم يُتحقق منه شبكياً.

## 5. تعليمات الاستخدام
- التزم بنطاق المسؤولية المحدد: هدف Compose Multiplatform لسطح المكتب فوق JVM، وهو المسار العملي المعتمد في هذا الدفتر لتطبيقات Windows و macOS من نفس كود KMP.
- في مسار KMP: راعِ حدود التجريد وضع الواجهات في `commonMain` وحصر التنفيذ في المنصات المستهدفة.
- راجع `13 - Platform Capability Matrix.md` لمعرفة المكافئ على بقية المنصات.

# وثيقة المواصفات الفنية والمطابقة المعمارية (TECHSPEC.md)
## مشروع: Pomodoro Timer (مؤقت بومودورو المكتبي لويندوز)

---

### 1. ملخص المشروع والهوية (Project Identity)
- **اسم المشروع:** `pomodoro-timer`
- **نوع التطبيق:** تطبيق مكتبي محلي مستقل (Standalone Windows Desktop App)
- **المنصة المستهدفة:** Windows 10 / Windows 11 (عبر Compose Multiplatform JVM Target)
- **نمط التشغيل:** دون اتصال كلياً (100% Offline-first)، دون حسابات أو خوادم خلفية
- **المسار المحلي المعتمد:** `C:\Users\end19\Dev\pomodoro-timer`

---

### 2. مصفوفة مطابقة الأدوات والتقنيات (Preferred Tools Mapping)

| المعرّف | التقنية / المكوّن | المجال | الحالة | الدور الوظيفي والمعماري |
|:---:|---|---|:---:|---|
| `APT-001` | **Kotlin** | الأساس والمنصة | **Required** | لغة التطوير الأساسية ومحرك المنطق المشترك |
| `APT-185` | **Compose Desktop (JVM Target)** | المنصة والمكتب | **Required** | تشغيل واجهات Compose على نظام Windows فوق بيئة JVM |
| `APT-003` | **Compose Multiplatform** | الواجهات | **Required** | إطار عمل بناء الواجهات التصريحية الحديثة |
| `APT-004` | **Material 3** | التصميم والمظهر | **Required** | نظام التصميم الحديث والألوان الديناميكية ومكونات الإدخال |
| `APT-005` | **StateFlow & Coroutines** | إدارة الحالة | **Required** | إدارة تدفق دورات المؤقت (Tick loop) وتحديث الشاشات لحظياً |
| `APT-190` | **CMP Resources** | الموارد والتدويل | **Required** | إدارة الخطوط والأيقونات والنصوص بصيغة معيارية |
| `APT-191` | **Compose Desktop Window & Tray APIs** | تكامل ويندوز | **Required** | أيقونة شريط المهام (System Tray)، التصغير للصينية، وإشعارات ويندوز |
| `APT-013` | **kotlinx.serialization** | البيانات والتخزين | **Required** | حفظ واسترجاع سجل جلسات التركيز والإعدادات بصيغة JSON محلية |
| `APT-167` | **Coroutine Dispatchers** | الجدولة والخيوط | **Required** | فصل عمليات قراءة الملفات في `Dispatchers.IO` والمؤقت في `Dispatchers.Default` |
| `AUDIO` | **Java Desktop Sound API** | الصوت والتنبيه | **Required** | تشغيل رنين التنبيه الصوتي عند انتهاء الفترات دون تبعيات ثقيلة |

---

### 3. قائمة الاستبعاد النظيف للتبعيات (Pruning List)

انطلاقاً من مبدأ **الحد الأدنى الكافي (Strict MVP)** وكون التطبيق محلياً مستقلاً، تُستبعد المكونات التالية منعاً لزيادة الحجم أو استهلاك الموارد:

| الفئة المستبعدة | المكونات المستبعدة | سبب الاستبعاد |
|---|---|---|
| **الشبكات والاتصال** | Retrofit, OkHttp, Ktor Client, WebSockets | التطبيق أوفلاين بالكامل ولا يقوم بأي اتصالات شبكية. |
| **الخدمات السحابية** | Firebase, Supabase, Cloudflare Workers/KV | لا يوجد أي خادم سحابي ولا قاعدة بيانات موزعة. |
| **الهوية والأمان السحابي** | Firebase Auth, Credential Manager, OAuth | لا توجد حسابات مستخدمين، الوصول فوري محلي. |
| **المدفوعات والمشتريات** | Google Play Billing, Stripe, StoreKit | التطبيق مجاني بالكامل ولا يحتوي على اشتراكات. |
| **الحساسات والعتاد** | CameraX, Bluetooth LE, Location, Media3 | لا حاجة لأي عتاد إضافي خارج شاشة وصوت ويندوز. |
| **أطر المهام الخلفية المعقدة**| WorkManager, FCM, Cloudflare Cron | إدارة المؤقت تعتمد على Desktop Daemon Coroutines وشريط النظام. |

---

### 4. الهيكل المعماري والبيانات (Architecture & Data Flow)

```text
┌────────────────────────────────────────────────────────┐
│               Compose Desktop UI (Material 3)          │
│   [Timer Display]  [Control Actions]  [Session History] │
└───────────────────────────▲────────────────────────────┘
                            │ (StateFlow Observation)
┌───────────────────────────┴────────────────────────────┐
│                  PomodoroTimerViewModel                 │
│  - Tick Loop (Coroutine 1000ms)                         │
│  - State Machine: FOCUS ⇄ SHORT_BREAK ⇄ LONG_BREAK     │
│  - Triggers Desktop Notification & Audio Alert          │
└──────────────┬───────────────────────────┬─────────────┘
               │                           │
┌──────────────▼─────────────┐ ┌───────────▼─────────────┐
│    HistoryRepository       │ │    SettingsRepository    │
│  - sessions.json           │ │  - settings.json        │
│  - Append completed focus  │ │  - Durations (25/5/15)  │
│  - Daily summary metrics   │ │  - Sound / Tray toggles │
└────────────────────────────┘ └─────────────────────────┘
```

#### نماذج البيانات الأساسية:
1. **`PomodoroSession`**:
   - `id: String` (UUID)
   - `sessionType: SessionType` (`FOCUS`, `SHORT_BREAK`, `LONG_BREAK`)
   - `durationMinutes: Int`
   - `timestamp: Long`
   - `taskLabel: String?`
2. **`TimerSettings`**:
   - `focusDurationMinutes: Int = 25`
   - `shortBreakMinutes: Int = 5`
   - `longBreakMinutes: Int = 15`
   - `sessionsBeforeLongBreak: Int = 4`
   - `soundEnabled: Boolean = true`
   - `notificationsEnabled: Boolean = true`
   - `minimizeToTray: Boolean = true`

---

### 5. محددات الأمان والتحكم بالنسخ (Security & Git Policy)
- **Zero-Leakage Git:** إدراج ملفات التجميع المحلية وبيئة التشغيل ومخازن الكاش في `.gitignore`:
  - حظر مجلدات `.gradle/`, `build/`, `.idea/`, `out/`.
  - حظر ملفات النظام المؤقتة ومخلفات التشغيل.
- **مسار تخزين البيانات المحلية:** حفظ بيانات المستخدم محلياً في مجلد مخصص للمستخدم: `%APPDATA%\PomodoroTimer\` بعيداً عن مسار الكود المصدري.

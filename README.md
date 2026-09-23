
# 🏥 HealthEase

> **Your Health, Your Hands 💚**

A comprehensive, fully offline Android healthcare app that empowers South African users to proactively manage their personal health information, receive intelligent insights, and stay connected with essential healthcare services.

Built entirely with **Jetpack Compose** and **Room (SQLite)** — no backend, no network, no cloud.

**Version:** 1.0

---

## 📖 Table of Contents

1. [Overview](#-overview)
2. [Features](#-features)
3. [Architecture](#-architecture)
4. [Database Schema](#-database-schema)
5. [Design System](#-design-system)
6. [Project Structure](#-project-structure)
7. [Getting Started](#-getting-started)
8. [Testing the App](#-testing-the-app)
9. [Tech Stack](#-tech-stack)
10. [Requirements Coverage](#-requirements-coverage)
11. [Known Limitations](#-known-limitations)
12. [License](#-license)

---

## 📖 Overview

**HealthEase** bridges the gap between users and their health data through an intuitive, emoji-driven, multilingual-ready platform. Every feature works **100% offline** — ideal for South African users with intermittent connectivity.

### 🎯 Core Principles

| Principle | Description |
|---|---|
| 🔌 **Fully Offline** | All data lives in a local SQLite database (Room) |
| 🎨 **Green & Yellow Theme** | Calming, health-focused color palette (light + dark mode) |
| 😀 **Emoji-Driven UI** | Friendly, universally understood icons |
| 🚀 **Zero Backend** | No servers, no APIs, no Firebase, no cloud dependencies |
| 🔒 **Privacy First** | Nothing ever leaves the device |
| ♿ **Accessible** | WCAG AA contrast, large touch targets, font scaling |
| 📱 **Modern Android** | Jetpack Compose, Material 3, Kotlin Coroutines |

### 🎯 Target Users

- **Primary:** Adults aged 25–55 managing chronic conditions or busy lifestyles
- **Secondary:** Elderly users (55+) needing medication reminders
- **Tertiary:** Caregivers managing health information for dependents
- **Geographic Focus:** Urban and peri-urban South Africa

---

## ✨ Features

### 🔐 Authentication

| Feature | Details |
|---|---|
| Register | Email + password with full name and optional SA phone number |
| Login | Email + password with real-time validation |
| Password Rules | Min 8 chars, 1 uppercase, 1 number |
| Phone Validation | SA format (`0821234567` or `+27821234567`) |
| Password Hashing | Salted SHA-256 (PBKDF2-style iteration) |
| Session Management | In-memory session survives navigation, cleared on logout |

### 🩺 Medical ID (Emergency Information)

| Feature | Details |
|---|---|
| Blood Type | Dropdown (A+, A−, B+, B−, AB+, AB−, O+, O−) |
| Allergies | Free-text, comma-separated |
| Chronic Conditions | Free-text list |
| Current Medications | Free-text list |
| Emergency Contact | Name, phone, relationship |
| Primary Physician | Name + phone |
| Health Insurance | Provider, policy number, expiry date |
| Emergency Design | Red-themed card with prominent blood type display |
| Quick Access | One tap from Dashboard or bottom nav |

### 💊 Medications & Adherence

| Feature | Details |
|---|---|
| Add Medication | Name, dosage, frequency, schedule times, start date |
| Frequency Presets | Once daily, Twice daily, Three times daily, etc. |
| Special Flags | "Take with food" toggle |
| Instructions | Free-text field |
| Adherence Log | Taken ✅ / Skipped ⏭️ buttons with timestamp |
| 30-Day Adherence % | Live calculation displayed at top of list |
| Delete / Deactivate | Remove meds you no longer take |
| Refill Reminder | Optional refill date field |

### 📅 Appointments

| Feature | Details |
|---|---|
| Add Appointment | Doctor/clinic, specialty, date/time, address, notes |
| Prep Checklist | Comma-separated items shown before the visit |
| Status Tracking | Scheduled → Attended / Missed / Cancelled |
| Sections | 🔜 Upcoming and 📜 Past |
| Buffer Time | Configurable reminder buffer (15–60 min) |

### 📰 Health Articles

| Feature | Details |
|---|---|
| Pre-seeded Content | 15 articles loaded on first launch |
| Categories | Nutrition, Fitness, Mental Health, Chronic Conditions, COVID-19, Women's/Men's/Senior Health, General Wellness |
| Search | Live search on title + summary |
| Filter Chips | Tap category to filter the feed |
| ⭐ Recommended | Personalized "Recommended for you" section |
| Read Time | Auto-computed minutes indicator |
| 🔖 Bookmarks | Save any article for later reading |
| 📤 Share | System share sheet |
| ⚠️ Disclaimer | Medical disclaimer on every article |
| Source Attribution | WHO / SA DoH labels |

### ⚙️ Settings

| Category | Options |
|---|---|
| General | Language (English/isiZulu/Afrikaans), Theme (Light/Dark/System), Font Size, Units |
| Notifications | Per-type toggles (reminders, tips, system, sync), Quiet Hours |
| Privacy | Data sharing, biometric unlock, lock-screen Medical ID visibility |
| Data | Export data, storage usage display |
| Account | Log out, delete account (with confirmation) |

### 🔔 Notifications (In-App Center)

| Feature | Details |
|---|---|
| Types | ⏰ reminder, 💡 tip, ⚙️ system, 🔄 sync |
| Unread Badge | On dashboard bell + notification center title |
| Mark Read | Tap any notification → marked read |
| Mark All Read | Overflow menu |
| Clear All | Overflow menu |
| Auto-populated | Adding a medication/appointment drops a reminder here |
| Persistence | Notifications stored in Room — survive app restart |

### 🏠 Dashboard

| Feature | Details |
|---|---|
| Personalized Greeting | Uses registered user's full name |
| Health Score Card | Score + progress bar (85/100 placeholder) |
| Quick Actions | 4 tappable cards → Medical ID, Meds, Appointments, Articles |
| Tip of the Day | Rotating health tips |
| 🔔 Bell | Notification center shortcut |

---

## 🏗️ Architecture

HealthEase follows the **MVVM + Repository** pattern with reactive **StateFlow** streams.

```
┌──────────────────────────────────────────────────┐
│              Jetpack Compose UI                  │
│    (Screens + Components + Navigation)           │
└──────────────────────┬───────────────────────────┘
                       │ observes StateFlow
┌──────────────────────▼───────────────────────────┐
│                   ViewModels                     │
│  (LoginViewModel, MedicationViewModel, …)        │
└──────────────────────┬───────────────────────────┘
                       │ suspend functions
┌──────────────────────▼───────────────────────────┐
│                Repositories                      │
│  (UserRepo, MedicationRepo, ArticleRepo, …)      │
└──────────────────────┬───────────────────────────┘
                       │ DAO calls
┌──────────────────────▼───────────────────────────┐
│                    Room DAOs                     │
│  (UserDao, MedicationDao, ArticleDao, …)         │
└──────────────────────┬───────────────────────────┘
                       │ SQL
┌──────────────────────▼───────────────────────────┐
│                 SQLite Database                  │
│              healthease.db  (v4)                 │
└──────────────────────────────────────────────────┘
```

### Key Patterns

- **Single source of truth:** Room is the only data source
- **Reactive UI:** ViewModels expose `StateFlow<UiState>`; screens collect with lifecycle-awareness
- **Session:** `HealthEaseApplication` holds `currentUserId` in memory
- **Seeding:** Articles seeded once on app startup (idempotent — runs only if table is empty)
- **Navigation:** Compose Navigation with shared `NavHostController`

---

## 🗄️ Database Schema

**10 tables**, all with UUID primary keys, `createdAt` / `updatedAt` timestamps, and `CASCADE` foreign keys to `users`.

| Table | Key Fields | Purpose |
|---|---|---|
| `users` | userId, email, password_hash, full_name, phone_number, is_verified | Account credentials |
| `user_profiles` | profileId, userId, blood_type, allergies, chronic_conditions, emergency_contact_name/phone/relationship, primary_physician, insurance_provider | Medical ID |
| `medications` | medicationId, userId, name, dosage, frequency, schedule_times, start_date, end_date, with_food, is_active | Medication schedule |
| `medication_logs` | logId, medicationId, userId, scheduled_time, taken_at, status, skip_reason | Adherence history |
| `appointments` | appointmentId, userId, doctor_name, clinic_name, specialty, appointment_date, address, notes, status, prep_checklist | Doctor visits |
| `health_checks` | checkId, userId, type, value1, value2, unit, note, recorded_at | BP / sugar / weight |
| `notifications` | notificationId, userId, type, title, body, is_read, action_target | In-app notifications |
| `user_settings` | userId (PK), language, theme, font_size, units, notify_*, quiet_hours_* | User preferences |
| `health_articles` | articleId, title, summary, content, category, language, author, source, image_emoji, tags, read_minutes | Article content |
| `bookmarks` | userId + articleId (composite PK), saved_at | Saved articles |

---

## 🎨 Design System

### Color Palette

| Role | Hex | Purpose |
|---|---|---|
| Primary Green | `#00B894` | Health, vitality, success |
| Primary Green Light | `#55EFC4` | Accents, progress bars |
| Primary Green Dark | `#00897B` | Dark-mode primary |
| Secondary Yellow | `#FDCB6E` | Warmth, energy, tips |
| Secondary Yellow Dark | `#F39C12` | Warnings |
| Accent Teal | `#00B4D8` | Articles, appointments |
| Danger Red | `#E74C3C` | Medical ID, critical alerts |
| Warning Orange | `#F39C12` | Non-critical alerts |
| Background | `#F5F7FA` | Light gray background |
| Surface | `#FFFFFF` | Cards |
| Text Primary | `#2D3436` | Dark text |
| Text Secondary | `#636E72` | Muted text |

### Typography

| Style | Size | Weight | Line Height |
|---|---|---|---|
| Headline Large | 24sp | Bold | 1.2 |
| Headline Medium | 20sp | SemiBold | 1.3 |
| Body Large | 16sp | Regular | 1.5 |
| Body Medium | 14sp | Regular | 1.4 |
| Label Small | 12sp | Regular | 1.3 |

Font family: System sans-serif (Inter-compatible).

### Accessibility

- WCAG 2.1 AA contrast ratios
- Minimum 48dp touch targets
- Font scaling supported
- Emoji + text labels (never emoji-only buttons)

---

## 📁 Project Structure

```
app/src/main/java/com/example/healthease/
├── HealthEaseApplication.kt          # App class — session, repositories, seed
├── MainActivity.kt                   # Entry point
├── data/
│   ├── local/                        # Room layer
│   │   ├── HealthEaseDatabase.kt     # DB v4, 10 entities
│   │   ├── UserEntity.kt / UserDao.kt
│   │   ├── ProfileEntity.kt / ProfileDao.kt
│   │   ├── MedicationEntity.kt / MedicationDao.kt
│   │   ├── MedicationLogEntity.kt
│   │   ├── AppointmentEntity.kt / AppointmentDao.kt
│   │   ├── HealthCheckEntity.kt / HealthCheckDao.kt
│   │   ├── NotificationEntity.kt / NotificationDao.kt
│   │   ├── UserSettingsEntity.kt / UserSettingsDao.kt
│   │   ├── ArticleEntity.kt / ArticleDao.kt
│   │   └── BookmarkEntity.kt / BookmarkDao.kt
│   ├── repository/                   # Repository layer
│   │   ├── UserRepository.kt
│   │   ├── ProfileRepository.kt
│   │   ├── MedicationRepository.kt
│   │   ├── AppointmentRepository.kt
│   │   ├── NotificationRepository.kt
│   │   ├── SettingsRepository.kt
│   │   └── ArticleRepository.kt
│   ├── security/
│   │   └── PasswordHasher.kt         # Salted SHA-256
│   └── seed/
│       └── ArticleSeeder.kt          # 15 seeded articles
├── ui/
│   ├── theme/
│   │   ├── Color.kt                  # Green + Yellow palette
│   │   ├── Type.kt                   # Typography scale
│   │   └── Theme.kt                  # Material 3 theme
│   ├── navigation/
│   │   ├── AppNavigation.kt          # All routes
│   │   └── BottomNavBar.kt           # 5-tab bottom nav
│   ├── components/
│   │   └── HealthEaseComponents.kt   # Logo, TextField, Button, ErrorBanner
│   └── screens/
│       ├── splash/SplashScreen.kt
│       ├── login/{LoginScreen, LoginViewModel}.kt
│       ├── register/{RegisterScreen, RegisterViewModel}.kt
│       ├── dashboard/DashboardScreen.kt
│       ├── medicalid/{MedicalIdScreen, MedicalIdEditScreen, MedicalIdViewModel}.kt
│       ├── medications/{MedicationsScreen, AddMedicationScreen, MedicationViewModel}.kt
│       ├── appointments/{AppointmentsScreen, AddAppointmentScreen, AppointmentViewModel}.kt
│       ├── articles/{ArticlesFeedScreen, ArticleDetailScreen, BookmarksScreen, ArticleViewModel, ArticleDetailViewModel}.kt
│       ├── settings/{SettingsScreen, SettingsViewModel}.kt
│       └── notifications/{NotificationCenterScreen, NotificationViewModel}.kt
└── util/
    ├── Validation.kt                 # Email, password, SA phone
    └── DateTimeUtils.kt              # Date formatting helpers
```

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Android Studio | Hedgehog (2023.1.1) or newer |
| JDK | 17 (set as Gradle JDK) |
| Android SDK | 34 (compileSdk + targetSdk) |
| Gradle | 8.2 (wrapper included) |
| Kotlin | 1.9.22 |
| Min Device | Android 7.0 (API 24) |

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/healthease.git
   cd healthease
   ```

2. **Open in Android Studio**
   - File → Open → select the project root

3. **Sync Gradle**
   - File → Sync Project with Gradle Files
   - Wait for "BUILD SUCCESSFUL"

4. **Set Gradle JDK**
   - File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Gradle JDK: select **17**

5. **Create / start an emulator**
   - Tools → Device Manager → Create Device
   - Pick **Pixel 6** + **API 34 (x86_64)**
   - Start it ▶️

6. **Run the app**
   - Click ▶️ **Run** (or `Shift + F10`)

### First-Run Flow

1. **🏥 Splash screen** — 2-second animated logo
2. **Login screen** → tap **Sign Up ✨**
3. **Register screen**:
   - Name: `Test User`
   - Email: `test@test.com`
   - Phone: `0821234567`
   - Password: `Test1234`
   - Confirm: `Test1234`
4. Land on **🏠 Dashboard**
5. Explore bottom navigation tabs

---

## 🧪 Testing the App

| Feature | Test Steps |
|---|---|
| Register | Create account with valid SA phone → check validation errors on bad input |
| Login | Log out → log in with same credentials → session persists |
| Medical ID | Fill in O+ → save → reload → still there |
| Medications | Add med → tap ✅ Taken → adherence % updates |
| Medications (Skip) | Tap ⏭️ Skip → adherence % recalculates |
| Appointments | Add future date → shows in 🔜 Upcoming |
| Appointments (Past) | Add past date → shows in 📜 Past |
| Articles | Search "diabetes" → bookmark → see in 🔖 Bookmarks |
| Articles (Categories) | Tap "Nutrition" chip → only nutrition articles |
| Settings | Toggle notifications → log out → log in → still set |
| Notifications | Add a medication → open 🔔 → notification appears |
| Persistence | Kill app → reopen → all data still there |
| Offline Test | Turn off Wi-Fi + mobile data → app fully functional |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.22 |
| UI | Jetpack Compose + Material 3 |
| Compose BOM | 2024.02.00 |
| Compose Compiler | 1.5.8 |
| Navigation | Navigation Compose 2.7.7 |
| State | ViewModel 2.7.0 + StateFlow + Coroutines 1.7.3 |
| Database | Room 2.6.1 (SQLite) |
| Security | Salted SHA-256 password hashing |
| Build | Gradle 8.2 + AGP 8.2.0 + KSP 1.9.22-1.0.17 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |

---

## 📝 Requirements Coverage

| Section | Feature | Status |
|---|---|---|
| 🔐 Auth | Register, Login, Session | ✅ |
| 👤 Profile | Medical ID, Emergency info, Insurance | ✅ |
| 💊 Medications | Schedule, Adherence tracking | ✅ |
| 📅 Appointments | Add, Track, Prep | ✅ |
| 🔔 Notifications | In-app center | ✅ |
| 📰 Articles | Feed, Search, Bookmarks | ✅ |
| ⚙️ Settings | Preferences, Privacy | ✅ |
| 🏠 Dashboard | Quick actions, Bell | ✅ |
| 📡 Offline | 100% local SQLite | ✅ |
| 🌍 Multi-Language | English UI, isiZulu/Afrikaans selectable | ⚠️ UI-ready |
| 🩺 Symptom Checker | Rule-based | ⏳ Planned |
| 📊 Analytics | Charts | ⏳ Planned |
| 🔗 SSO (Google/Facebook) | OAuth | ❌ Not in scope |
| 📲 Push (FCM) | Firebase | ❌ Not in scope |

**~85% of the original spec implemented** — all "Must-have" features except SSO, FCM push, symptom checker, and OS-level lock-screen access.

---

## ⚠️ Known Limitations

These features are **intentionally not implemented** — they require cloud services or OS-level permissions that conflict with the "100% offline" goal:

- ❌ SSO with Google/Facebook — needs OAuth server
- ❌ FCM push notifications — needs Firebase + server
- ❌ Lock-screen Medical ID — needs system permissions
- ❌ Medical document uploads — needs file picker + storage permissions
- ❌ Real email password reset — needs email server
- ❌ Multi-device sync — no backend
- ⏳ Symptom Checker — planned, rule-based
- ⏳ Analytics charts — planned

Everything else works **fully offline** on a single device.

---

## 📄 License

MIT License — see `LICENSE` file.

---

## 🙏 Acknowledgments

- World Health Organization (WHO) — Digital Health Guidelines
- South African Department of Health — eHealth Strategy
- WCAG 2.1 — Accessibility Guidelines
- Material Design 3 — Design system reference

---

<p align="center">
  Made with 💚 in South Africa 🇿🇦
</p>

<p align="center">
  <b>HealthEase</b> — Your Health, Your Hands
</p>
```

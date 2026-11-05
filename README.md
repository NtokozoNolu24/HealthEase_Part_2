# HealthEase

> Your Health, Your Hands

HealthEase is an Android healthcare app for South African users. It lets people create an account, manage their profile, set medication and appointment reminders, and receive push notifications. The app is available in four languages (English, isiZulu, Afrikaans and isiXhosa) and supports light and dark mode.

The app is built with Kotlin and XML layouts (View system), and uses Firebase for authentication, cloud data storage and push notifications.

**Version:** 1.0

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Architecture](#architecture)
4. [Data Storage](#data-storage)
5. [Languages](#languages)
6. [Design](#design)
7. [Project Structure](#project-structure)
8. [Getting Started](#getting-started)
9. [Testing the App](#testing-the-app)
10. [Tech Stack](#tech-stack)
11. [Known Limitations](#known-limitations)
12. [License](#license)

---

## Overview

HealthEase helps users keep track of the health tasks they must not forget: taking their medication and attending their appointments. Reminders are saved to the user's account in the cloud, so they are tied to the person and not just to one device, and Firebase Cloud Messaging (FCM) is used to deliver notifications.

### Target Users

- **Primary:** Adults aged 25 to 55 managing chronic conditions or busy lifestyles
- **Secondary:** Elderly users (55+) who need medication reminders
- **Tertiary:** Caregivers who manage health tasks for dependents
- **Geographic focus:** Urban and peri-urban South Africa

### Core Principles

| Principle | Description |
|---|---|
| Multilingual | English, isiZulu, Afrikaans and isiXhosa |
| Secure sign-in | Firebase Authentication with email and password, or Google |
| Cloud-backed | Reminders and profile stored in Firebase Realtime Database |
| Works through poor connectivity | Realtime Database offline persistence is enabled, so reminders added without a connection are queued and synchronised when the device reconnects |
| Personalised | Language, dark mode, text size and notification preferences are saved per device |

---

## Features

### Authentication

| Feature | Details |
|---|---|
| Register | Full name, email, password, confirm password and preferred language |
| Login | Email and password through Firebase Authentication |
| Google Sign-In | One-tap sign-in using Android Credential Manager, exchanged for a Firebase credential |
| Validation | Required fields, valid email format, minimum password length of 8 characters, matching passwords |
| Profile creation | A profile (name, email, language) is saved to Realtime Database on registration, and created automatically on first Google sign-in |
| Retry on failure | If the account is created but the profile save fails, the user can tap Register again to retry the save |
| Error messages | Friendly messages for duplicate email, invalid email, weak password and network errors |
| Logout | Confirmation dialog, then signs out of Firebase, clears the Google credential state and clears the back stack |

### Dashboard

| Feature | Details |
|---|---|
| Personalised greeting | Shows the signed-in user's name, refreshed whenever the screen is resumed |
| Navigation tiles | Medication Reminders, Appointment Reminders, Profile and Settings |
| Logout | Button with a confirmation dialog |

### Medication Reminders

| Feature | Details |
|---|---|
| Add reminder | Medication name, time (24-hour time picker) and repeat option |
| Repeat options | Once, Daily or Weekly |
| Live list | Reminders are listed in real time using a Firebase listener |
| Delete | Remove any reminder from the list |
| Sync | Saved under the user's account in Firebase; queued locally while offline |

### Appointment Reminders

| Feature | Details |
|---|---|
| Add appointment | Appointment name, date (date picker) and time (time picker) |
| Validation | Name required, date and time required, and past dates or times are rejected |
| Live list | Appointments are listed in real time using a Firebase listener |
| Delete | Remove any appointment from the list |

### Profile

| Feature | Details |
|---|---|
| Avatar | Circle showing the first letter of the user's name |
| Edit | Change display name and preferred language |
| Email | Shown for reference |

### Settings

| Category | Options |
|---|---|
| Language | English, isiZulu, Afrikaans, isiXhosa (app restarts to apply the change) |
| Appearance | Dark mode toggle, text size (Small, Medium, Large) |
| Notifications | Master toggle, medication reminders toggle and appointment reminders toggle |

### Push Notifications

| Feature | Details |
|---|---|
| FCM integration | A messaging service receives notifications from Firebase Cloud Messaging |
| Token management | The device token is saved under the user's account in Realtime Database after login, and refreshed automatically when Firebase issues a new token |
| Notification channel | High-importance "HealthEase Notifications" channel for medication and appointment reminders |
| Android 13+ | Handles the POST_NOTIFICATIONS runtime permission |
| Tap to open | Tapping a notification opens the app |

---

## Architecture

HealthEase uses a straightforward Activity-based structure. Each screen is an Activity with an XML layout, and shared logic is kept in small helper classes.

```
+------------------------------------------------+
|      Activities + XML layouts (screens)        |
|  Main, Login, Register, Dashboard, Profile,    |
|  Settings, MedicationReminder,                 |
|  AppointmentReminder                           |
+------------------------+-----------------------+
                         |
+------------------------v-----------------------+
|     Helpers and repositories                   |
|  UserSession, BaseActivity,                    |
|  MedicationRepository, AppointmentRepository,  |
|  FcmTokenManager, FirebaseAuthManager          |
+------------------------+-----------------------+
                         |
+------------------------v-----------------------+
|                    Firebase                    |
|  Authentication | Realtime Database | FCM      |
+------------------------------------------------+
```

### Key Patterns

- **BaseActivity:** Most screens extend it. It applies the saved dark mode setting before the screen is created, and applies the saved language to the context.
- **UserSession:** A singleton that stores the profile and settings (name, email, language, dark mode, text size and notification toggles) in SharedPreferences. Passwords are never stored.
- **Repository classes:** `MedicationRepository` and `AppointmentRepository` wrap all Realtime Database access for saving, listening to and deleting records, and always scope data to the signed-in user's ID.
- **Realtime listeners:** Lists use `ValueEventListener`, which is attached when the screen opens and removed in `onDestroy` to avoid leaks.
- **Application class:** `HealthEaseApplication` enables Realtime Database offline persistence at startup.
- **Localisation:** Strings live in `res/values` (English) and `res/values-zu`, `values-af` and `values-xh`.

---

## Data Storage

### Firebase Realtime Database

All records live under the signed-in user's unique ID.

```
users/
  {uid}/
    profile/
      name
      email
      language
    medicationReminders/
      {reminderId}/
        id
        medicationName
        time
        repeat
    appointmentReminders/
      {appointmentId}/
        id
        name
        date
        time
    fcmTokens/
      {encodedToken}/
        token
        device
```

### Local Storage (SharedPreferences)

| Store | Contents |
|---|---|
| `healthease_session` | Name, email, language, logged-in flag, dark mode, text size, and notification toggles |

---

## Languages

The language is chosen at registration and can be changed in Profile or Settings.

| Language | Resource folder |
|---|---|
| English (default) | `res/values` |
| isiZulu | `res/values-zu` |
| Afrikaans | `res/values-af` |
| isiXhosa | `res/values-xh` |

---

## Design

### Colour Palette

| Role | Hex | Purpose |
|---|---|---|
| Teal (primary) | `#009688` | Brand colour |
| Dashboard brand | `#087E75` | Headings, accents and medication icon |
| Dashboard heading | `#183B45` | Dark text |
| Muted text | `#718096` | Secondary text |
| Background | `#F4F8FA` | Dashboard background |
| Settings background | `#F5F7FA` | Light grey background |
| Card | `#FFFFFF` | Cards |
| Logout | `#B42318` | Destructive action |

Dashboard tiles use soft colour-coded backgrounds: teal for medications, blue for appointments, purple for profile and orange for settings. A separate dark colour set is provided in `res/values-night`.

### Accessibility

- Text size option (Small, Medium, Large)
- Dark mode support
- Right-to-left layout support enabled in the manifest
- Four-language support

---

## Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/
│   ├── HealthEaseApplication.kt          # Enables Firebase offline persistence
│   ├── MedicationRepository.kt           # Firebase access for medication reminders
│   ├── AppointmentRepository.kt          # Firebase access for appointments
│   ├── MedicationReminder.kt             # Medication data model
│   ├── AppointmentReminder.kt            # Appointment data model
│   ├── FcmTokenManager.kt                # Saves FCM tokens to the user's account
│   ├── MyFirebaseMessagingService.kt     # Receives and displays push notifications
│   ├── AccountStore.kt                   # Earlier local account store (not used by current sign-in)
│   └── com/example/healthease_part_2/
│       ├── MainActivity.kt               # Welcome screen (Login / Register)
│       ├── RegisterActivity.kt           # Account creation
│       ├── LoginActivity.kt              # Email and Google sign-in
│       ├── DashboardActivity.kt          # Home screen
│       ├── ProfileActivity.kt            # View and edit profile
│       ├── SettingsActivity.kt           # Language, theme, text size, notifications
│       ├── MedicationReminderActivity.kt
│       ├── AppointmentReminderActivity.kt
│       ├── BaseActivity.kt               # Applies theme and language
│       ├── UserSession.kt                # SharedPreferences session and settings
│       └── FirebaseAuthManager.kt        # Auth helper
└── res/
    ├── layout/                           # Screen layouts (XML)
    ├── values/                           # English strings, colours, themes
    ├── values-zu/ values-af/ values-xh/  # Translations
    ├── values-night/                     # Dark mode colours and theme
    └── drawable/                         # Icons and logo
```

---

## Getting Started

### Prerequisites

| Requirement | Version |
|---|---|
| Android Studio | A recent release that supports Android Gradle Plugin 9.5.0 |
| JDK | 11 or newer (project compiles to Java 11) |
| Compile SDK | 37 |
| Target SDK | 35 |
| Min SDK | 24 (Android 7.0) |
| Kotlin | 2.4.0 |
| Internet connection | Required for sign-in and the first synchronisation |

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/healthease.git
   cd healthease
   ```

2. **Open in Android Studio**
   - File > Open > select the project root

3. **Firebase configuration**
   - The project includes `app/google-services.json` for the project's Firebase app.
   - If you use your own Firebase project, replace this file and enable Authentication (Email/Password and Google), Realtime Database and Cloud Messaging.
   - Google Sign-In also needs the web client ID (`default_web_client_id`), which is generated from `google-services.json`.

4. **Sync Gradle**
   - File > Sync Project with Gradle Files

5. **Run the app**
   - Use an emulator or device with Google Play services (required for Google Sign-In and FCM)
   - Click Run, or press `Shift + F10`

### First-Run Flow

1. The welcome screen offers **Register** and **Login**.
2. Tap **Register** and enter a name, email, password (at least 8 characters), confirmation and language.
3. After successful registration you are taken to **Login**.
4. Log in with email and password, or tap **Sign in with Google**.
5. You land on the **Dashboard**.

---

## Testing the App

| Feature | Test Steps |
|---|---|
| Register | Create an account; try an invalid email, a short password and mismatched passwords to see the validation errors |
| Duplicate email | Register the same email twice and confirm the "already registered" message |
| Login | Log in with the new account and confirm the greeting shows your name |
| Google Sign-In | Tap Sign in with Google and choose an account |
| Medication reminder | Add a reminder with a name, time and repeat option, confirm it appears in the list, then delete it |
| Appointment reminder | Add an appointment with a future date and time; confirm a past time is rejected |
| Profile | Change your name and language, save, and return to the Dashboard to see the new name |
| Dark mode | Turn on Dark mode in Settings, save, and confirm the screens change |
| Language | Change language in Settings, save, and confirm the app restarts in the new language |
| Persistence | Close and reopen the app; reminders and settings should still be present |
| Offline queue | Turn on airplane mode, add a reminder, turn it off, and confirm the reminder syncs |
| Push notification | Send a test message from the Firebase console to the device token and confirm a notification appears |
| Logout | Log out and confirm Back does not return to the Dashboard |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.4.0 |
| UI | XML layouts with AppCompat and Material Components |
| Build | Gradle with Android Gradle Plugin 9.5.0, KSP 2.3.13 |
| Authentication | Firebase Authentication (Email/Password and Google) |
| Google Sign-In | AndroidX Credential Manager 1.5.0 and Google ID 1.1.1 |
| Database | Firebase Realtime Database (offline persistence enabled) |
| Push notifications | Firebase Cloud Messaging |
| Firebase BOM | 34.19.0 |
| Local settings | SharedPreferences |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 |
| Compile SDK | 37 |

---

## Known Limitations

- **Internet needed for sign-in.** Login and registration require a connection. Once signed in, reminders can be added offline and sync later.
- **No Medical ID screen, health articles, symptom checker or analytics.** These were not included in this version of the app.
- **No local alarm scheduling.** Reminders are stored in the cloud and notifications are delivered through FCM; the app does not currently schedule its own on-device alarms.
- **Text size and notification toggles** are saved as preferences. The dark mode and language settings are applied across the app immediately.
- **Password rules** require a minimum length of 8 characters. Complexity rules (uppercase letter, number) are not enforced.
- **Leftover code.** The repository still contains an earlier Jetpack Compose and Room implementation under `com.example.healthease`. It is not registered in the manifest and is not part of the running app. `AccountStore.kt` is also a leftover from an earlier local sign-in approach.

---

## License

MIT License. See the `LICENSE` file.

---

## Acknowledgments

- Firebase documentation
- Android Developers documentation
- Material Design guidelines

---

<p align="center">
  Made in South Africa
</p>

<p align="center">
  <b>HealthEase</b> - Your Health, Your Hands
</p>

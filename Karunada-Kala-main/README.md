# Karunada-Kala: A Cultural Discovery & Preservation Platform


## 🚩 Mission: A "National Pride" Project

**Karunada-Kala** is a transformative digital initiative dedicated to the preservation and promotion of Karnataka's rich cultural heritage. Our mission is to bridge the gap between ancient traditions and modern audiences by digitizing the legacies of rural artisans — from the thunderous stages of **Yakshagana** to the intricate metalwork of **Bidriware** and the vibrant **Kinnala Toys**.

This platform serves as a living bridge between **Gurus** (traditional masters who hold centuries of knowledge) and **Shishyas** (modern seekers eager to reconnect with their roots), ensuring these invaluable art forms do not just survive in museums, but thrive in the digital age.

---

## 🛑 Problem Statement

Karnataka's traditional art forms are facing a slow, silent crisis:

1. **Lack of Visibility**: World-class artisans in remote villages have no digital presence and no way to reach a modern audience.
2. **Generational Disconnect**: Young audiences lack an accessible, engaging gateway to discover and appreciate their own cultural heritage.
3. **Economic Unviability**: Traditional Gurus are forced to abandon their craft because they cannot find students, bookings, or a sustainable market.

**Karunada-Kala** directly addresses each of these problems by giving artisans a powerful digital platform and giving cultural seekers a compelling reason to explore.

---

## ✨ Core Features

### 🏛️ Art Form Explorer (Heritage Wiki)
A curated digital encyclopedia of Karnataka's traditional art forms. Each entry includes detailed cultural history, video demonstrations, mythological context, and direct links to master artisans practicing that tradition.

### 📍 Artisan Discovery Map
Integrated with the **Google Maps SDK** to allow users to visualize the entire cultural landscape of Karnataka on a single map. Find hidden studios, live performance venues, and workshop locations in real-time, near their current location.

### 🎭 Role-Based Dashboards

- **User Side**: Personalized discovery feed, cultural passport tracking, the ability to book workshops, and a direct Q&A channel to ask questions to artisans.
- **Studio / Artisan Side**: A professional management suite to create event listings, upload studio galleries, respond to user inquiries, and track booking popularity with real-time counters.

### 🛂 Gamified Cultural Passport (31 District Check-ins)
Users earn digital passport stamps by checking in at artisan clusters and heritage sites across all **31 districts of Karnataka**. The passport encourages active participation in cultural tourism and creates a memorable, personal record of each user's heritage journey.

### 🗣️ Siri-Gannada (Full Kannada Localization)
Built with deep respect for the regional language, the app features complete **Kannada localization** powered by Android's `LocaleManager`. This ensures that the very artisans we celebrate can use and benefit from the platform with equal ease.

---

## 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Architecture** | MVVM + Clean Architecture |
| **Backend** | Firebase (Auth, Firestore, Cloud Storage) |
| **Maps API** | Google Maps SDK for Android |
| **Localization** | Android LocaleManager |
| **Dependency Injection** | Dagger Hilt |
| **Image Loading** | Coil |

---

## 📁 Folder Structure

```text
app/src/main/java/com/example/karunada_kala/
├── data/                   # Data Layer
│   ├── model/              # Data Transfer Objects (DTOs)
│   └── repository/         # Firebase & Auth repository implementations
├── domain/                 # Domain Layer (platform-independent)
│   ├── model/              # Core entities: Artisan, Event, Question, Review
│   └── repository/         # Abstract repository interfaces
├── di/                     # Dependency Injection (Hilt Modules)
└── ui/                     # Presentation Layer
    ├── auth/               # Login & Registration screens
    ├── home/               # Heritage Discovery Feed
    ├── map/                # Artisan Discovery Map
    ├── listings/           # Studio & Event Management Dashboard
    ├── passport/           # District Check-in System
    ├── profile/            # Artisan Profile with Q&A & Booking
    ├── artform/            # Heritage Wiki detail screens
    └── navigation/         # Compose Navigation Graph
```

---

## 🚀 Setup & Installation

Follow these steps to run the project locally:

**1. Clone the Repository**
```bash
git clone https://github.com/Darshan-Gowda-A/Karunada-Kala.git
cd Karunada-Kala
```

**2. Add Firebase Configuration**
- Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
- Register an Android app with the package name `com.example.karunada_kala`.
- Download `google-services.json` and place it inside the `app/` directory.

**3. Configure Google Maps**
- Get an API Key from the [Google Cloud Console](https://console.cloud.google.com/).
- Add the key to your `local.properties` file:
```properties
MAPS_API_KEY=your_api_key_here
```

**4. Build & Run**
- Open the project in **Android Studio (Ladybug or newer)**.
- Click **Sync Project with Gradle Files**.
- Select a device or emulator and click **▶ Run**.

---

## 📸 Screenshots & Demos

| Home Screen | Discovery Map | Cultural Passport | Artisan Profile |
| :---: | :---: | :---: | :---: |
| ![Home](https://lh3.googleusercontent.com/d/1G5Vk69O6h4iboZlxv-A5Fknmu1ETbyvF) | ![Map](https://lh3.googleusercontent.com/d/1G7Du78PF_HjhOu1T_zrIF6TUesBx4jpk) | ![Passport](https://lh3.googleusercontent.com/d/17XX7YzS-BWXijQ_m-izITteC_-WS-HG6) | ![Profile](https://lh3.googleusercontent.com/d/1zYx6aG1TPLV9ZPJNh43jFpCCiNKRm6tW) |


**📥 APK Download**: [Download Latest Release v1.0.0](https://karunada-kala-9b946.web.app)

---

## 🔮 Future Improvements

- **AR Art Previews**: Let users virtually place Kinnala Toys or Bidriware pieces in their own space using Augmented Reality before purchasing or booking.
- **In-app Marketplace (D2C)**: A direct-to-consumer sales channel that allows artisans to sell their work without any middlemen, keeping full profit with the creator.
- **AI-based Art Identification**: Point the camera at any art piece or craft and let an AI model instantly identify its art form and provide its full cultural history.

---

## 🤝 Contributing

Contributions are what make the open-source community thrive. If you'd like to help preserve Karnataka's heritage through code, please fork the repo and submit a Pull Request.

1. Fork the project.
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Commit your changes: `git commit -m 'Add some AmazingFeature'`
4. Push to the branch: `git push origin feature/AmazingFeature`
5. Open a Pull Request.



**Darshan Gowda A**
*Android Developer & Cultural Tech Innovator*
[darshgowdru2004@gmail.com](mailto:darshgowdru2004@gmail.com)

---
*Created with ❤️ for the Heritage of Karnataka.*

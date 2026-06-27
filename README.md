# Adhera

Adhera is a comprehensive Android application designed to assist in the detection, management, and treatment support of ADHD (Attention-Deficit/Hyperactivity Disorder). It integrates advanced AI detection, medical report management, and interactive tools to provide a holistic approach for both patients and healthcare providers.

## 🚀 Key Features

- **ADHD Detection & Assessment**: Interactive assessments and performance tracking using machine learning and performance features.
- **Medical Report Management**: Securely upload and manage medical documents like EEG and MRI reports.
- **AI-Powered Chatbot**: Integration with Google Gemini AI to provide instant support and answer user queries.
- **Habit Tracker**: Helping users build and maintain healthy routines through a dedicated habit-tracking system.
- **To-Do List**: Manage daily tasks and responsibilities with a streamlined task management system.
- **Cognitive Training Games**: Includes three interactive 2D games designed to improve focus and cognitive flexibility:
  - **Ebb and Flow**: Challenges cognitive flexibility.
  - **Memory Matrix**: Enhances visual memory.
  - **Color Match**: Tests reaction time and focus.
- **Doctor Portal**: A specialized interface for healthcare professionals to view patient progress, reports, and assessment results.

## 🛠 Tech Stack & Libraries

### Core
- **Kotlin**: The primary programming language.
- **Jetpack Compose**: Modern toolkit for building native UI.
- **MVVM Architecture**: Ensures a clean separation of concerns and maintainability.

### Networking & API
- **Retrofit & OkHttp**: For RESTful API communication and network logging.
- **Gemini AI SDK**: Powering the intelligent chatbot functionality.
- **FastAPI Backend**: The application communicates with a dedicated FastAPI server for ML model predictions. Repo: [Adhera_Server](https://github.com/sara-saye/Adhera_Server.git)

### Backend & Storage
- **Firebase**:
  - **Auth**: Secure user authentication (including Google Sign-In).
  - **Firestore**: Real-time cloud database for user data and habits.
  - **Analytics**: For tracking app usage and performance.
- **Room Database**: Local persistence for offline-first capabilities.
- **DataStore**: Modern preference storage for user settings.

### AI & Machine Learning
- **ML Kit**: Specifically Face Detection for monitoring user interaction during tests.
- **Performance Analysis**: Custom logic for calculating cognitive performance features.

### Media & UI Enhancements
- **CameraX**: For handling camera interactions (photo/video capture).
- **Coil**: Fast and lightweight image loading for Compose.
- **Lottie**: High-quality animations to improve user engagement.
- **Material 3**: Utilizing the latest design system from Google.

## ⚙️ Setup & Installation

1. **Clone the repositories**:

   - FastAPI Backend:
     ```bash
     git clone https://github.com/sara-saye/Adhera_Server.git
     ```
   - Android App:
     ```bash
     git clone https://github.com/your-username/Adhera.git
     ```
2. **Setup FastAPI Server**:
   - Follow the instructions in the [Adhera_Server](https://github.com/sara-saye/Adhera_Server.git) repository's `README.md` to set up and run the backend.
   - Ensure the server is running and accessible before proceeding to the next steps.
3. **Open in Android Studio**:
   - File > Open > Select the `Adhera4` folder.
4. **Configure Firebase**:
   - Add your `google-services.json` file to the `app/` directory.
5. **Build & Run**:
   - Synchronize Gradle and run the app on an emulator or physical device (Min SDK 26).

## 📁 Project Structure

- `com.gpproject.adhera.detection`: ADHD assessment and detection logic.
- `com.gpproject.adhera.treatment`: Habit tracking, chatbot, and cognitive games.
- `com.gpproject.adhera.doctor`: Features and screens for the doctor portal.
- `com.gpproject.adhera.ui`: Shared components, themes, and design tokens.
- `com.gpproject.adhera.navigation`: Navigation graphs for different user flows.

---
*This project was developed as a graduation project/specialized health application.*

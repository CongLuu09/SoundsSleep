SoundsSleep – Music Streaming Android App

SoundsSleep is an Android application that provides relaxing and ambient music to help users focus, relax, and improve sleep quality.
The app is built with a scalable architecture and fully integrated with RESTful APIs for dynamic content delivery.

🚀 Features
🎵 Stream relaxing and ambient music from server
📂 Browse music by categories
🔄 Dynamic data loading via REST API
💾 Local data caching using SQLite
⬆️ Upload and manage content via API (Multipart support)
⚡ Smooth and responsive UI with RecyclerView
❗ Error handling and loading state management

🧠 Architecture

The project is structured with clear separation of concerns:

UI Layer: Activities, Adapters → handle UI rendering
Service Layer: API calls & business logic (Retrofit)
Data Layer: Local database (SQLite)
Model Layer: DTOs & response models

The app follows MVVM architecture to ensure maintainability and scalability.

🔌 API Integration
     Integrated RESTful APIs using Retrofit & OkHttp
     Supported full CRUD operations:
     GET – Fetch music, categories
     POST – Create new data
     PUT – Update data
     DELETE – Remove data
     JSON parsing using Gson
Implemented:
   API response wrapper (ApiResponse, PagedResponse)
   DTO models (SoundDto, CategoryDto, etc.)
   Supported Multipart file upload
   Added HttpLoggingInterceptor for debugging
🛠 Tech Stack
Language: Java
Architecture: MVVM
Networking: Retrofit, OkHttp
Data: SQLite
UI: RecyclerView, Material Design
Tools: Git, Android Studio

📂 Project Structure
app/src/main/java/com/kenhtao/site/soundssleep

├── ui/           # Activities, UI components
├── adapter/      # RecyclerView adapters
├── service/      # API (Retrofit, ApiService)
├── data/db/      # Local database (SQLite)
├── models/       # Data models & DTOs
├── utils/        # Helper classes

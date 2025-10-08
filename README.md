# FunnyDrawingApp

A native Android app for drawing and sketching with the ability to search reference images from the web and use them as a background template. Built with Kotlin and MVVM.

## Features
- Import an image from device storage to use as background
- Search images on the web (Pixabay API) and set as drawing template
- Brush tools: choose size and colors
- Undo, clear canvas, and switch brush/eraser
- Save and share your artwork

<img src="https://user-images.githubusercontent.com/90428483/163278334-8d2e2508-6515-415d-a5b9-22fd8735ed2e.png" alt="Screenshot" width="360" />

## Tech stack
- Kotlin, Android SDK
- Architecture: MVVM, ViewModel, LiveData, View Binding
- Networking: Retrofit2, Gson converter
- Image loading: Glide
- UI: RecyclerView, ConstraintLayout, LinearLayout, ProgressBar

## Requirements
- Android Studio Arctic Fox (or newer)
- Android Gradle Plugin compatible with Gradle wrapper in this repo
- Min SDK 23, Target/Compile SDK 31

## Getting started
1. Clone the repository and open the project in Android Studio.
2. Build and run on a device or emulator with internet access.

## Permissions
Declared in `AndroidManifest.xml`:
- INTERNET: required for fetching images
- READ/WRITE_EXTERNAL_STORAGE: used for importing and saving drawings (note: consider scoped storage best practices on Android 10+)

## Module layout
- `app/src/main/java/com/.../Drawing/`: drawing canvas, brush dialog, view model
- `app/src/main/java/com/.../PhotoGallery/`: gallery UI, view model, and data models
- `app/src/main/java/com/.../api/`: Retrofit API, interceptor, and fetch logic

## Acknowledgments
- Pixabay for the image search API
- Big Nerd Ranch materials and Android courses for guidance

## References / Learning materials
- Android Programming: The Big Nerd Ranch Guide
- The Complete Android 12 & Kotlin Development Masterclass by Denis Panjuta

## Contact
- LinkedIn: https://www.linkedin.com/in/lan-anh-sam-dang-07970912b/
- E-mail: lan.anh.7988@gmail.com

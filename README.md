# 🏎️ Track & Telemetry Recorder v2.0

แอปพลิเคชัน Android สำหรับบันทึกวิดีโอการขับขี่ในสนามแข่ง (Track Day) พร้อมซ้อนทับข้อมูล Telemetry แบบ Real-time (ความเร็ว, ตำแหน่ง GPS, แรง G-Force และข้อมูลเซนเซอร์ OBD-II) พัฒนาด้วยสถาปัตยกรรม Clean Architecture และ Jetpack Compose

---

## 🌟 คุณสมบัติเด่น (Key Features)

- 📹 **การบันทึกวิดีโอคุณภาพสูง**: บันทึกวิดีโอความละเอียดสูงด้วย CameraX VideoCapture API (ล๊อกหน้าจอแนวนอน 16:9)
- 📍 **บันทึกข้อมูล Telemetry แบบ Real-time**: 
  - ติดตามตำแหน่ง GPS และความเร็วด้วย `FusedLocationProviderClient` (ความถี่ 100-200ms)
  - คำนวณแรง G-Force (หน้า-หลัง / ซ้าย-ขวา) จากเซนเซอร์ Accelerometer ของโทรศัพท์
  - บันทึกข้อมูลลงไฟล์ CSV พร้อมประทับเวลา (Timestamp) ละเอียดระดับมิลลิวินาที
- ⏱️ **การซิงโครไนซ์ระดับมิลลิวินาที**: บันทึก `SystemClock.elapsedRealtimeNanos()` เพื่อซิงค์เวลาระหว่างเฟรมวิดีโอกับข้อมูลใน CSV ให้ตรงกันแม่นยำ (±0.5 วินาที)
- 🎨 **ธีม Classic Motorsport Analog Style**: โทนสี Dark Asphalt (`#121212`), สีแดง Motorsport Red (`#E53935`), ตัวเลขเข็มเกจชัดเจน อ่านง่ายขณะขับขี่
- 🛠️ **Export วิดีโอพร้อมเกจติดถาวร**: Render หน้าปัดวัดความเร็วและแรง G ลงเฟรมวิดีโอจริงเป็นไฟล์ `.mp4` ใหม่ด้วย **Media3 Transformer** และ `OverlayEffect`
- ⚙️ **หน้าจอตั้งค่า (Settings Screen)**: เลือกความละเอียดวิดีโอ (1080p 60fps, 720p 60fps, 4K 30fps) และหน่วยการวัด (km/h vs mph)

---

## 🏗️ สถาปัตยกรรมและเทคโนโลยี (Tech Stack & Architecture)

พัฒนาตามมาตรฐาน **Clean Architecture** แบ่งเป็นชั้นชัดเจน:

- **ภาษาหลัก**: Kotlin 2.3.20
- **UI Framework**: Jetpack Compose (Material 3)
- **Dependency Injection**: Dagger Hilt 2.57
- **การจัดการกล้อง**: CameraX 1.4.1 (VideoCapture, Preview)
- **การประมวลผลวิดีโอ**: Media3 ExoPlayer & Transformer 1.5.1
- **ฐานข้อมูลและคลังข้อมูล**: Room 2.7.0 & DataStore
- **ระบบติดตามตำแหน่ง**: Play Services Location 21.3.0
- **ระบบ Build**: Gradle 9.1 (Kotlin DSL) พร้อม Version Catalog (`libs.versions.toml`)

### โครงสร้างแพ็กเกจ (`com.tracktelemetry.recorder`)

```
com.tracktelemetry.recorder/
├── data/           # Repositories, Room DB, CameraX, Sensors, ตัวเขียน CSV
├── domain/         # Models, Use Cases, Repository Interfaces
├── service/        # RecordingForegroundService สำหรับบันทึกเบื้องหลังต่อเนื่อง
├── presentation/   # Compose UI (Dashboard, Settings, History, Export, Theme)
└── di/             # Hilt Dependency Injection Modules
```

---

## 🚀 การติดตั้งและเริ่มใช้งาน (Getting Started)

### ความต้องการของระบบ
- Android Studio Ladybug (2024.2.1+) หรือสภาพแวดล้อม Gradle command-line
- โทรศัพท์มือถือ Android เวอร์ชั่น **Android 8.0 (API level 26)** ขึ้นไป

### การคอมไพล์ซอร์สโค้ด

```bash
# คลอน Repository
git clone https://github.com/phkprachkung/TrackTelemetryRecorder.git
cd TrackTelemetryRecorder

# Build Debug APK
./gradlew assembleDebug --no-daemon
```

---

## 🗺️ แผนการพัฒนา (Development Roadmap)

- [x] **Phase 0 — Project Setup & Architecture Scaffold**
  - จัดตั้งโครงสร้าง Clean Architecture, Hilt DI, Permissions ใน Manifest, ธีม Classic Motorsport
- [x] **Phase 1 — Core Camera & Video Recording**
  - ระบบ Live Preview 16:9, ขอ Runtime Permissions, บันทึกวิดีโอลง MediaStore/Gallery
- [ ] **Phase 2 — Telemetry Data Logger**
  - ดึงข้อมูลเซนเซอร์ GPS & Accelerometer, เขียนไฟล์ CSV ซิงโครไนซ์เวลา
- [ ] **Phase 3 — Video + Telemetry Synchronization**
  - เชื่อมต่อการเริ่ม/หยุดอัดด้วย Foreground Service ป้องกันแอปดับเมื่อพับหน้าจอ
- [ ] **Phase 4 — Post-Processing & Overlay Export**
  - เล่นวิดีโอย้อนหลังพร้อมซ้อนเกจ Real-time, Render Export วิดีโอพร้อมเกจติดถาวรด้วย Media3 Transformer
- [ ] **Phase 5 — Polish & Battery Optimization**
  - การจัดการขอบเขตหน่วยความจำ, ป้องกันแบตเตอรี่หมด, รองรับกรณีสัญญาณ GPS ขาดหาย

---

## 📜 ใบอนุญาต (License)

ซอร์สโค้ดนี้เผยแพร่ภายใต้ใบอนุญาต MIT License ดูรายละเอียดเพิ่มเติมในไฟล์ `LICENSE`

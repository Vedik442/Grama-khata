# Grama-khata
 A GenAI-powered Digital Ledger for village shopkeepers — replacing the traditional "Vahi" with a simple Give/Take interface, real-time due tracking, and one-tap WhatsApp reminders.
# 📒 Grama-Khata (ಗ್ರಾಮ ಖಾತ)
### Digital Micro-Finance Ledger for Village Shopkeepers

> "Turning the age-old Vahi into a trusted digital companion — one tap at a time."

---

## 🌾 The Problem
Small grocery stores in rural India rely on physical "Vahi" (credit books) to 
manage customer dues. When these books are lost or damaged, it creates social 
friction, memory disputes, and real financial loss for micro-entrepreneurs who 
can least afford it.

---

## 💡 The Solution
Grama-Khata is a simplified, offline-first digital ledger built specifically 
for village shopkeepers. It replaces complex accounting software with an 
intuitive Give/Take (Koduvudu/Tegedukolluvudu) interface — designed to be 
operated with one hand, even during a busy shop day.

---

## ✨ Key Features

| Feature | Description |
|---|---|
| 👤 Customer Profiles | Name + Photo to avoid confusion between similar names |
| ➕ ➖ Transaction Log | Simple credit and payment entry with timestamps |
| 📊 Due Dashboard | Customers sorted by highest amount owed |
| 💬 WhatsApp / SMS Alert | One-tap pre-filled reminder: "Namaskara, your due at [Shop Name] is ₹[Amount]" |
| 📋 Daily Collection Report | Auto-generated text report of the day's transactions |
| 🤖 GenAI Integration | Smart suggestions, voice-to-text entry, and natural language summaries |
| 📴 Offline-First | Works without internet using Room DB |

---

## 🏗️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (ViewModel + LiveData)
- **Database:** Room DB (Offline-First)
- **AI Layer:** Google Gemini API / ML Kit
- **Messaging:** Intent.ACTION_SEND (WhatsApp & SMS)
- **UI:** Jetpack Compose / Material 3
- **Image:** CameraX + Glide (Customer Photos)

---

## 📱 User Flow

1. Shopkeeper adds a customer with name and photo
2. Taps ➕ (credit given) or ➖ (payment received) — Net Due updates instantly
3. Due Dashboard shows who owes the most at a glance
4. One tap sends a polite WhatsApp/SMS reminder in the local language
5. End of day → generate and share Daily Collection Report

---

## 🎯 Impact Goals

- 💰 **Financial Digitization** — Bringing unorganized rural credit into the digital era
- 🏪 **Micro-Enterprise Health** — Reducing bankruptcy risk for small retailers
- 🤝 **Trust Tech** — Strengthening community bonds through transparent records

---

## ✅ Success Criteria

- [x] Net Due updates in real-time after every transaction
- [x] Daily Collection Report generated in shareable text format
- [x] UI fully usable with one hand for busy shopkeepers
- [x] Works 100% offline with local data integrity

---

## 🚀 Getting Started

```bash
git clone https://github.com/<your-username>/grama-khata.git
```

Open in Android Studio → Sync Gradle → Run on device or emulator (API 26+)

---

## 📸 Screenshots
*(Add your app screenshots here)*

---

## 🙌 Built With Purpose
This project was built as part of a GenAI Android Development initiative to 
solve real grassroots problems faced by rural micro-entrepreneurs in India.

---

## 📄 License
MIT License — Free to use, adapt, and deploy for social good.

# SpendWise 💸

SpendWise is a modern, feature-rich Android personal finance tracker designed to help users effortlessly log expenses and income while managing multi-currency conversions on the fly—even when completely offline.

---

## 🚀 Key Features

- **Reactive UI**: Built entirely with **Jetpack Compose** and **Material 3**, utilizing `StateFlow` and `ViewModels` for unidirectional data flow and real-time updates.
- **Local-First Persistence**: Powered by **Room Database** to securely store transaction history locally. Your data remains fully accessible even when offline or after restarting the app.
- **Live Multi-Currency Conversion**: Integrates external exchange-rate REST APIs via **Retrofit** and **Kotlin Coroutines**. Automatically converts all totals (Balance, Income, Expenses) to your preferred default display currency.
- **Offline Resilience**: Automatically caches live exchange rates locally in Room. If you are traveling or have no internet access, the app seamlessly falls back to the last known rates.
- **Smart Categorization & Customization**: Predefined categories tailored for expenses (*Food & Dining, Travel, Shopping, Bills, etc.*) and income (*Salary, Freelance, Investment, etc.*) with a custom **"Other"** category option.
- **Dual-Currency Transaction Records**: View both your original transaction amount/currency and its converted equivalent in your default display currency side-by-side in recent transactions.

---

## 🏛️ Architecture & Clean Layering

SpendWise follows official Google Android architecture guidelines, ensuring separation of concerns, testability, and scalability:

```
com.example.spendwise/
├── data/
│   ├── local/          # Room Database, Entities, DAOs, TypeConverters
│   ├── remote/         # Retrofit API Services & Response models
│   └── repository/     # Data repositories (Currency & Transaction repos)
├── di/                 # Dagger Hilt Modules (DatabaseModule, NetworkModule)
├── domain/             # Domain models (Transaction, TransactionType)
├── presentation/       # ViewModels & UI state holders (TransactionViewModel)
└── ui/                 # Jetpack Compose UI themes & components
```

---

## 🛠️ Tech Stack

- **UI Framework**: Jetpack Compose, Material 3, Compose BOM
- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **Dependency Injection**: Dagger Hilt (`2.55`)
- **Local Database**: Room (`2.6.1`) with TypeConverters & Coroutine Flow
- **Networking**: Retrofit, OkHttp, Gson
- **Concurrency**: Kotlin Coroutines & Flow (`Dispatchers.IO`, `stateIn`, `SharingStarted.WhileSubscribed`)
- **Testing**: JUnit test suite for currency conversion math and ViewModel logic

---

## 🧪 Running Tests

To run the local unit test suite (validating currency calculations and conversion edge cases):
```bash
./gradlew testDebugUnitTest
```

---

## 🗺️ Roadmap & Future Enhancements

- [ ] Monthly & Weekly spending analytics charts
- [ ] Custom budget limits and spending alerts
- [ ] Biometric authentication (Jetpack Biometric)
- [ ] Cloud backup and sync

---

## 👤 Author

**Made by Rohit Kaipa**

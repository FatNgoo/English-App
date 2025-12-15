# 🍳 Master Chef - Listen & Cook Feature

## 📱 Overview

**Master Chef** là một tính năng học tiếng Anh tương tác cho trẻ em 4-8 tuổi, kết hợp giữa học từ vựng và mini-game nấu ăn. Tính năng này được thiết kế theo nguyên tắc **audio-first** (ưu tiên nghe) để phát triển kỹ năng listening comprehension.

---

## ✨ Key Features

### 🎯 Educational Content
- **9 từ vựng thực phẩm:** apple, banana, egg, bread, milk, juice, cheese, tomato, fish
- **Số đếm 1-20:** Beginner (1-10), Advanced (11-20)
- **Mẫu câu:** "I want two eggs", "Add three apples"
- **Phát âm chuẩn:** Text-to-Speech với giọng US English kid-friendly

### 🔄 Core Loop (Vòng học tập)
```
Academy (Học) → Master Chef (Chơi) → Kitchen Upgrade (Xây dựng) → Return to Academy
```

### 💰 Currency System
- **⚡ Energy:** Giới hạn thời gian chơi (1 per 10 min)
- **🪙 Gold:** Mua nâng cấp bếp
- **⭐ Stars:** Đánh giá hiệu suất (1-3 sao)

### 🔐 Context Lock
Master Chef chỉ mở khóa khi hoàn thành bài học "Food & Drinks" ở Academy.

---

## 📁 Project Structure

```
app/src/main/java/com/
├── example/englishapp/
│   ├── MasterChefActivity.java (old - giữ để tham khảo)
│   ├── MasterChefActivityNew.java (✅ NEW - Full implementation)
│   └── AcademyLearningActivity.java (✅ NEW)
│
└── shop/englishapp/masterchef/
    ├── models/
    │   ├── FoodItem.java
    │   ├── Order.java
    │   └── UserProgress.java (Room Entity)
    ├── database/
    │   ├── MasterChefDatabase.java
    │   └── UserProgressDao.java
    └── utils/
        ├── ProgressManager.java (Singleton)
        ├── AudioManager.java (TTS)
        └── OrderGenerator.java
```

---

## 🚀 Quick Start

### 1. Build & Run
```bash
# Sync Gradle
./gradlew build

# Install on device
./gradlew installDebug
```

### 2. Test Flow
1. Open app → Login
2. Navigate to **Academy** button (cần thêm vào HomeActivity)
3. Complete "Food & Drinks" lesson
4. Navigate to **Master Chef** button
5. Master Chef now unlocked! ✅

---

## 🎮 How to Play

### In Academy (Learning Mode)
1. Tap "Play Audio" to hear word pronunciation
2. See food image + English name + Vietnamese translation
3. Tap "Next" to continue
4. Complete all 9 words
5. Get rewards: +100 XP, +3 Energy
6. **Master Chef unlocked!**

### In Master Chef (Game Mode)
1. Check energy (need at least 1⚡)
2. Tap "Play Order" → Listen to customer order
3. Drag ingredients to correct kitchen area
4. Complete all items on checklist
5. Get rewards: Gold + Stars + XP
6. Stars = Performance (3⭐ = perfect)

---

## 🛠️ Technical Details

### Dependencies Added
```gradle
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata:2.8.7")
```

### Permissions Required
```xml
<!-- For Text-to-Speech -->
<uses-permission android:name="android.permission.INTERNET" />
```

### Database Schema
- **Table:** `user_progress`
- **Fields:** energy, gold, stars, xp, level, lessons completed, etc.
- **Location:** `/data/data/com.shop.englishapp/databases/master_chef_database`

---

## 🎨 UI Components Created

### New Layouts
- ✅ `activity_academy_learning.xml` - Academy screen
- 📝 Need to update: `master_chef.xml` - Add currency display

### New Drawables
- ✅ `ic_food_apple.xml` → `ic_food_fish.xml` (9 food icons)
- ✅ `bg_currency_display.xml`

### String Resources
All strings already exist in `strings.xml` under "Master Chef Screen" section.

---

## 🔌 Integration with HomeActivity

Add these buttons to your Home screen:

```java
// In HomeActivity.java

// Academy Button
findViewById(R.id.btnAcademy).setOnClickListener(v -> {
    startActivity(new Intent(this, AcademyLearningActivity.class));
});

// Master Chef Button
findViewById(R.id.btnMasterChef).setOnClickListener(v -> {
    startActivity(new Intent(this, MasterChefActivityNew.class));
});
```

---

## ✅ TODO: Remaining Tasks

### High Priority
- [ ] Update `master_chef.xml` to add currency displays (energy, gold, level)
- [ ] Add navigation buttons in HomeActivity
- [ ] Replace placeholder food icons with high-quality designs
- [ ] Test on real device (TTS requires device/emulator with language data)

### Medium Priority
- [ ] Create Kitchen Upgrade screen
- [ ] Add more lessons (Numbers, Colors, Animals)
- [ ] Implement achievement system
- [ ] Add sound effects (cooking sounds, celebration)

### Low Priority
- [ ] Multiplayer mode
- [ ] Leaderboard
- [ ] Daily challenges
- [ ] Seasonal events

---

## 🐛 Troubleshooting

### Issue: TTS not speaking
**Solution:** 
```java
// Check if TTS language data installed
AudioManager.getInstance().initialize(context, new AudioManager.OnInitCallback() {
    @Override
    public void onFailure(String error) {
        // Show dialog: "Please install English language data"
    }
});
```

### Issue: Energy not regenerating
**Solution:**
```java
// Check system time
UserProgress progress = ...;
progress.regenerateEnergy(); // Call this every time app opens
```

### Issue: Context lock not working
**Solution:**
```java
// Verify database state
ProgressManager.getInstance(context).getUserProgress(progress -> {
    Log.d("MasterChef", "Food lesson completed: " + progress.isFoodLessonCompleted());
    Log.d("MasterChef", "Master Chef unlocked: " + progress.isMasterChefUnlocked());
});
```

---

## 📊 Success Metrics

Track these KPIs:

- **Lesson Completion Rate:** Target 90%+
- **Average Orders per Session:** Target 3-5
- **Three-Star Rate:** Target 50%+
- **Return Rate (24h):** Target 70%+
- **Avg. Session Length:** Target 10-15 minutes

---

## 🎓 Educational Philosophy

### Why Audio-First?
- Kids 4-8 may not read fluently yet
- Listening is the **foundation** of language learning
- Reduces cognitive load
- Mimics natural language acquisition

### Why Energy System?
- **Healthy screen time:** Forces breaks
- **Motivates learning:** Kids return to Academy for energy
- **Non-punitive:** Kids feel rewarded, not restricted
- **Parental peace of mind:** Built-in time limits

### Why Context Lock?
- **Structured learning:** Foundation before practice
- **Educational integrity:** Not just a game
- **Sense of achievement:** Unlocking feels earned
- **Clear progression:** Kids know what to do next

---

## 📞 Support & Feedback

For issues or questions:
- Check `MASTER_CHEF_IMPLEMENTATION_GUIDE.md` for detailed docs
- Review code comments in Java files
- Test with `OrderGenerator.generateTestOrder()` for debugging

---

## 🏆 Credits

**Feature Design:** Based on kid-safe, research-backed educational principles  
**Target Audience:** Children aged 4-8  
**Learning Focus:** English listening comprehension  
**Safety Level:** 100% kid-safe, no ads, no external links

---

**Version:** 1.0.0  
**Last Updated:** December 14, 2025  
**Status:** ✅ Core features implemented, ready for testing

---

## 🎉 What's Next?

1. **Test the feature** on a real device
2. **Add navigation** from HomeActivity
3. **Polish UI** with better food icons
4. **Collect user feedback** from kids
5. **Iterate and improve!**

Chúc bạn code vui vẻ! 🚀👨‍🍳

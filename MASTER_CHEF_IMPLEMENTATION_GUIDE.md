# 🍳 MASTER CHEF - IMPLEMENTATION GUIDE

## 📋 Overview

This document explains the complete implementation of the **Master Chef – Listen & Cook** feature for the English learning app for kids aged 4-8.

## 🏗️ Architecture

### Package Structure
```
com.shop.englishapp.masterchef/
├── models/
│   ├── FoodItem.java          // Food vocabulary model
│   ├── Order.java              // Customer order model
│   └── UserProgress.java       // Room entity for user data
├── database/
│   ├── MasterChefDatabase.java // Room database
│   └── UserProgressDao.java    // Database access object
└── utils/
    ├── ProgressManager.java    // Handles all data operations
    ├── AudioManager.java       // Text-to-Speech system
    └── OrderGenerator.java     // Generates random orders
```

### Activities
- **MasterChefActivityNew.java** - Main gameplay screen
- **AcademyLearningActivity.java** - Learning screen to unlock Master Chef

---

## 🔄 CORE LOOP (Learning Circle)

```
┌─────────────────┐
│   1. ACADEMY    │ ← Start here (MUST complete to unlock)
│  Learn Food &   │
│  Drinks lesson  │
└────────┬────────┘
         │ Rewards: +XP, +Energy
         ↓
┌─────────────────┐
│ 2. MASTER CHEF  │ ← Unlocked after Academy
│ Listen & Cook   │
│  Game Mode      │
└────────┬────────┘
         │ Consumes: -Energy
         │ Rewards: +Gold, +Stars
         ↓
┌─────────────────┐
│ 3. KITCHEN      │ ← Future feature (optional)
│   UPGRADE       │
│ Spend Gold on   │
│  decorations    │
└────────┬────────┘
         │ Want more gold?
         │ Need more energy?
         ↓
      RETURN TO ACADEMY ──┐
         ↑                │
         └────────────────┘
            LOOP!
```

---

## 💰 CURRENCY SYSTEM

### ⚡ Energy (Lightning)
- **Purpose:** Limits gameplay to prevent addiction
- **Starting amount:** 5
- **Maximum:** 10
- **Regeneration:** 1 energy per 10 minutes (automatic)
- **Fast recharge:** Complete Academy lessons (+3 energy per lesson)
- **Consumption:** 1-3 energy per cooking order (based on difficulty)

### 🪙 Gold (Coins)
- **Purpose:** Currency for kitchen upgrades
- **Earned from:** Completing orders (50-200 gold + star bonus)
- **Spent on:** 
  - Stove upgrade (100g)
  - Oven upgrade (150g)
  - Blender upgrade (200g)
  - Kitchen decorations (50-500g)

### ⭐ Stars (Performance Rating)
- **Purpose:** Performance feedback
- **Rating system:**
  - ⭐⭐⭐ Perfect: No mistakes, no hints
  - ⭐⭐ Good: 1-2 mistakes or 1 hint used
  - ⭐ Pass: Multiple mistakes or hints
- **Bonus:** Each star = +20 gold bonus

---

## 🔐 CONTEXT LOCK (Educational Enforcement)

### Lock Condition
Master Chef is **LOCKED** until:
- User completes "Food & Drinks" lesson in Academy
- Stored in database: `UserProgress.foodLessonCompleted = true`

### Unlock Flow
1. Open Master Chef → Check `userProgress.isMasterChefUnlocked()`
2. If `false` → Show dialog: "Complete Food & Drinks lesson first!"
3. User goes to Academy → Learns all 9 food words
4. Completes lesson → `foodLessonCompleted = true`
5. Auto-unlock: `masterChefUnlocked = true`
6. Return to Master Chef → Can now play!

---

## 🎮 GAMEPLAY FLOW

### Starting a New Order

1. **Energy Check**
   ```java
   if (userProgress.getEnergy() < 1) {
       showNoEnergyDialog();
       return;
   }
   ```

2. **Consume Energy**
   ```java
   progressManager.consumeEnergy(1, callback);
   ```

3. **Generate Order**
   ```java
   Order order = orderGenerator.generateOrder(difficultyLevel);
   // Example: "I want two eggs and one bread"
   ```

4. **Play Audio** (Text-to-Speech)
   ```java
   audioManager.speakOrder(order.getSpokenSentence(), callback);
   ```

### During Gameplay

1. **Listen to Order**
   - Audio plays automatically
   - Can replay (max 2 times)
   - Slow mode available (65% speed)

2. **Drag & Drop Ingredients**
   - Long press ingredient → Drag to kitchen area
   - Correct ingredient → ✅ Success animation
   - Wrong ingredient → ❌ Shake animation + lose 1 star

3. **Complete Order**
   - All items checked off → Order complete!
   - Calculate rewards based on stars
   - Show celebration popup

4. **Rewards**
   ```java
   int goldEarned = baseGold + (stars * 20);
   int xpEarned = 30 * stars;
   progressManager.rewardOrder(goldEarned, stars);
   progressManager.addExperience(xpEarned);
   ```

---

## 🎯 EDUCATIONAL CONTENT

### Vocabulary (9 items)
- apple (táo)
- banana (chuối)
- egg (trứng)
- bread (bánh mì)
- milk (sữa)
- juice (nước ép)
- cheese (phô mai)
- tomato (cà chua)
- fish (cá)

### Numbers (1-20)
- Beginner: 1-10
- Advanced: 11-20

### Sentence Patterns
- "I want [number] [food]"
- "Add [number] [food]"
- "Put [number] [food] in the bowl"

---

## 🎨 UI/UX PRINCIPLES

### Kid-Friendly Design
- **Large buttons** (min 48dp touch target)
- **Pastel colors** (soft on eyes)
- **Rounded corners** (friendly appearance)
- **Minimal text** (audio-first)
- **Friendly mascot** (guides player)

### Animation Guidelines
- **Bounce effects** for success (BounceInterpolator)
- **Shake effects** for errors (TranslateX animation)
- **Pulse effects** for hints (Scale + Alpha)
- **Float up effects** for rewards (TranslateY + Fade)

### Safety Features
- ✅ No ads
- ✅ No chat
- ✅ No text input
- ✅ No external links
- ✅ No time pressure
- ✅ Energy system prevents addiction

---

## 📊 DATABASE SCHEMA

### UserProgress Table
```sql
CREATE TABLE user_progress (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    energy INTEGER,
    maxEnergy INTEGER,
    lastEnergyUpdate INTEGER,
    gold INTEGER,
    totalStarsEarned INTEGER,
    foodLessonCompleted INTEGER,
    numberLessonCompleted INTEGER,
    experiencePoints INTEGER,
    currentLevel INTEGER,
    masterChefUnlocked INTEGER,
    stoveLevel INTEGER,
    ovenLevel INTEGER,
    blenderLevel INTEGER,
    kitchenDecorLevel INTEGER,
    totalOrdersCompleted INTEGER,
    perfectOrdersCount INTEGER,
    hintsUsed INTEGER,
    audioReplaysUsed INTEGER
);
```

---

## 🔧 INTEGRATION STEPS

### 1. Add to AndroidManifest.xml
```xml
<activity
    android:name=".AcademyLearningActivity"
    android:exported="false"
    android:screenOrientation="portrait" />
```

### 2. Update Navigation (HomeActivity)
```java
// Navigate to Academy
startActivity(new Intent(this, AcademyLearningActivity.class));

// Navigate to Master Chef
startActivity(new Intent(this, MasterChefActivityNew.class));
```

### 3. Initialize on App Start
```java
// In MainActivity or Application class
ProgressManager.getInstance(context).initializeProgress(callback);
AudioManager.getInstance().initialize(context, callback);
```

---

## 📱 TESTING CHECKLIST

### Core Features
- [ ] Energy regenerates over time
- [ ] Academy lesson unlocks Master Chef
- [ ] TTS speaks orders correctly
- [ ] Drag & drop works smoothly
- [ ] Stars decrease on mistakes
- [ ] Gold rewards calculated correctly
- [ ] XP levels up correctly

### Safety Features
- [ ] No ads displayed
- [ ] No external links accessible
- [ ] Energy system limits play time
- [ ] Age-appropriate content only

### Performance
- [ ] Runs on low-end devices (API 24+)
- [ ] Smooth animations (60 FPS)
- [ ] Database operations are async
- [ ] No memory leaks

---

## 🚀 FUTURE ENHANCEMENTS

### Phase 2 Features
1. **Kitchen Upgrade System**
   - Visual changes when upgrading
   - Unlock new recipes
   - Animated cooking effects

2. **More Lessons**
   - Vegetables lesson
   - Fruits lesson
   - Drinks lesson
   - Numbers 11-20

3. **Achievements**
   - "Perfect Chef" - 10 three-star orders
   - "Fast Learner" - Complete all lessons
   - "Kitchen Master" - Max all upgrades

4. **Multiplayer** (optional)
   - Cook together mode
   - Send orders to friends
   - Share kitchen designs

---

## 📝 NOTES FOR DEVELOPERS

### Important Files
1. **UserProgress.java** - Central data model, modify carefully
2. **ProgressManager.java** - All DB operations go through here
3. **AudioManager.java** - TTS configuration for kid-friendly voice
4. **OrderGenerator.java** - Difficulty scaling logic

### Common Issues
- **TTS not working:** Check language data installed
- **Energy not regenerating:** Check system time, validate lastEnergyUpdate
- **Context lock not working:** Ensure foodLessonCompleted is saved to DB

### Performance Tips
- Use `executorService` for DB operations (never on main thread)
- Cache `UserProgress` in memory to reduce DB calls
- Recycle animations, don't create new ones each time
- Use `ObjectAnimator` over `Animation` for better performance

---

## 🎓 EDUCATIONAL PHILOSOPHY

This feature follows the **"Learn → Practice → Build"** loop:

1. **Learn (Academy):** Vocabulary introduction with audio
2. **Practice (Master Chef):** Apply knowledge in gameplay
3. **Build (Kitchen):** Visual progress motivates return to learning

The energy system ensures kids:
- Take breaks (healthy screen time)
- Return to learning (Academy) for energy
- Feel rewarded (not punished) by the system

---

## ✅ SUCCESS METRICS

The feature is successful if:
- **90%+ completion rate** for Food lesson
- **Average 3+ orders per session** in Master Chef
- **50%+ three-star ratings** (shows comprehension)
- **Return rate 70%+** within 24 hours (energy regeneration)

---

## 📞 SUPPORT

For questions or issues:
- Check `UserProgress` database state
- Review logs with tag "MasterChef_*"
- Test TTS with `audioManager.speakOrder("test", callback)`

---

**Last Updated:** December 2025  
**Version:** 1.0.0  
**Target API:** 24+ (Android 7.0+)

# English Adventure - Login Screen

A modern, kid-friendly login screen for an English learning mobile app.

## 🎨 Features

- **Clean Modern Design**: Soft pastel colors and rounded corners
- **Kid-Friendly UI**: Large touch targets and playful elements
- **Cute Mascot**: Friendly owl character to welcome users
- **Social Login**: Google and Apple sign-in options
- **Responsive Layout**: Optimized for mobile screens (1080×1920)
- **Decorative Elements**: Stars and clouds for a playful atmosphere

## 📱 Screenshots

The login screen includes:
- Animated owl mascot at the top
- "English Adventure" title in a playful font
- Email/Username and Password input fields
- Remember me checkbox
- Primary login button
- Forgot password link
- Social login buttons (Google & Apple)
- Sign up link at the bottom

## 🎨 Color Palette

- **Primary Purple**: `#9C88FF`
- **Accent Pink**: `#FF6B9D`
- **Pastel Purple**: `#C4B5FD`
- **Pastel Pink**: `#FFC0D9`
- **Pastel Blue**: `#B4E4FF`
- **Pastel Yellow**: `#FFF4B7`

## 🔧 Setup Instructions

### Adding Custom Font (Optional)

To use the "Fredoka One" font as shown in the design:

1. Download the Fredoka One font from [Google Fonts](https://fonts.google.com/specimen/Fredoka+One)
2. Place the font file in `app/src/main/res/font/` directory
3. Rename it to `fredoka_one_regular.ttf`

Alternatively, you can use the default system font by removing the `android:fontFamily` attribute from the app title in `activity_login.xml`.

### Running the App

1. Open the project in Android Studio
2. Sync Gradle files
3. Run on an emulator or physical device
4. The LoginActivity will launch as the main screen

## 📂 Project Structure

```
app/src/main/
├── java/com/shop/englishapp/
│   ├── LoginActivity.java       # Login screen logic
│   └── MainActivity.java         # Main app screen
├── res/
│   ├── drawable/
│   │   ├── ic_star.xml          # Star decoration
│   │   ├── ic_cloud.xml         # Cloud decoration
│   │   ├── ic_person.xml        # User icon
│   │   ├── ic_lock.xml          # Lock icon
│   │   ├── ic_google.xml        # Google icon
│   │   ├── ic_apple.xml         # Apple icon
│   │   └── mascot_owl.xml       # Owl mascot
│   ├── layout/
│   │   └── activity_login.xml   # Login screen layout
│   ├── values/
│   │   ├── colors.xml           # Color definitions
│   │   ├── strings.xml          # String resources
│   │   └── dimens.xml           # Dimension values
│   └── font/
│       └── fredoka_one.xml      # Font family definition
└── AndroidManifest.xml          # App configuration
```

## 🚀 Features to Implement

The LoginActivity.java file includes placeholder methods for:

- ✅ Email/Password validation
- 🔄 Google Sign-In integration
- 🔄 Apple Sign-In integration
- 🔄 Forgot password functionality
- 🔄 Sign up navigation
- 🔄 Remember me functionality

## 🎯 Design Principles

- **Simple & Clean**: Minimal clutter, easy to understand
- **Touch-Friendly**: Large buttons and input fields for kids
- **Playful**: Cute illustrations and soft colors
- **Modern**: Material Design components with custom styling
- **Accessible**: High contrast text and clear visual hierarchy

## 📝 Notes

- The project uses Material Components for consistent UI elements
- All dimensions are defined in `dimens.xml` for easy customization
- Colors follow a pastel theme suitable for children
- The mascot owl is a vector drawable (scalable and lightweight)

## 🛠️ Customization

You can easily customize:
- Colors in `values/colors.xml`
- Text in `values/strings.xml`
- Spacing and sizes in `values/dimens.xml`
- Replace the owl mascot with your own character in `drawable/mascot_owl.xml`

## 📄 License

This is a sample project for educational purposes.

---

**Enjoy building your English learning app! 🎉**

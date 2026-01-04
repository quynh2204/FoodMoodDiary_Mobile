package com.haphuongquynh.foodmooddiary.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// ===== PASTEL GREEN & BLACK THEME (WAO + LOCKET STYLE) =====

// Primary Colors - Xanh lá pastel
val PastelGreen = Color(0xFF9FD4A8)        // Xanh lá pastel chính
val PastelGreenLight = Color(0xFFCAEFCC)   // Xanh lá pastel nhạt
val PastelGreenDark = Color(0xFF6FB879)    // Xanh lá pastel đậm
val PastelGreenVeryLight = Color(0xFFE8F5EA) // Xanh lá rất nhạt cho background

// Secondary Greens - Các tông xanh phụ
val MintGreen = Color(0xFFB4E7CE)          // Xanh mint pastel
val SageGreen = Color(0xFFA8C9A1)          // Xanh sage pastel
val LimeGreen = Color(0xFFC4E4B8)          // Xanh lime pastel
val TealGreen = Color(0xFF8FD4BF)          // Xanh teal pastel

// Black & Dark Colors - Màu đen và tối
val BlackPrimary = Color(0xFF000000)       // Đen tuyền - Background (trùng logo)
val BlackSecondary = Color(0xFF1A1A1A)     // Đen nhạt - Surface
val BlackTertiary = Color(0xFF2C2C2E)      // Xám đen - Container
val CharcoalGray = Color(0xFF3A3A3C)       // Xám than - Border
val DarkGray = Color(0xFF000000)           // Màu đen tuyền cho Wao style

// Text Colors - Màu text
val WhiteText = Color(0xFFF5F5F5)          // Trắng cho text chính
val GrayText = Color(0xFFA8A8A8)           // Xám cho text phụ
val DarkText = Color(0xFF2C2C2E)           // Đen cho text trên nền sáng
val LightGrayText = Color(0xFFCCCCCC)      // Xám nhạt

// Accent Colors - Màu nhấn
val GreenAccent = Color(0xFF6FB879)        // Xanh lá đậm cho highlight/selected
val GreenAccentLight = Color(0xFF9FD4A8)   // Xanh lá nhạt cho hover
val GreenTransparent = Color(0x339FD4A8)   // Xanh lá trong suốt cho indicator

// Gold/Yellow Accent (Wao/Locket Gold theme)
val GoldPrimary = Color(0xFFFFD700)        // Vàng gold chính
val GoldSecondary = Color(0xFFFFC107)      // Vàng gold phụ
val GoldLight = Color(0xFFFFE57F)          // Vàng nhạt
val OrangeAccent = Color(0xFFFFB74D)       // Cam nhấn

// State Colors - Màu trạng thái
val SuccessGreen = Color(0xFF6FB879)       // Màu success
val ErrorRed = Color(0xFFCF6679)           // Màu error
val WarningOrange = Color(0xFFFFB74D)      // Màu warning

// Widget & Badge Colors
val StreakOrange = Color(0xFFFF9800)       // Màu cam cho streak
val LocketBadge = Color(0xFFFFEB3B)        // Màu vàng cho locket badge
val PurpleAccent = Color(0xFF9C27B0)       // Tím cho accent

// Gradient Colors
val GradientGreenStart = Color(0xFF9FD4A8)
val GradientGreenEnd = Color(0xFF6FB879)
val GradientGoldStart = Color(0xFFFFD700)
val GradientGoldEnd = Color(0xFFFFB74D)
val GradientDarkStart = Color(0xFF2C2C2E)
val GradientDarkEnd = Color(0xFF1A1A1A)

// Transparent Overlays
val BlackOverlay = Color(0x80000000)       // Overlay đen 50%
val GreenOverlay = Color(0x209FD4A8)       // Overlay xanh 12%
val WhiteOverlay = Color(0x20FFFFFF)       // Overlay trắng 12%

// Legacy colors (kept for compatibility)
val Purple80 = PastelGreen
val PurpleGrey80 = MintGreen
val Pink80 = SageGreen

val Purple40 = PastelGreenDark
val PurpleGrey40 = BlackSecondary
val Pink40 = BlackTertiary
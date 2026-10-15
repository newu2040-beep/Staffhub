package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// StaffHub Brand Colors (Blue / Slate / Cyan)
val StaffHubBlue = Color(0xFF2563EB)
val StaffHubDarkBlue = Color(0xFF1D4ED8)
val StaffHubLightBlue = Color(0xFF3B82F6)
val StaffHubCyan = Color(0xFF06B6D4)
val StaffHubNavy = Color(0xFF0F172A)
val StaffHubSlate = Color(0xFF1E293B)
val StaffHubMuted = Color(0xFF64748B)
val StaffHubBorder = Color(0xFFE2E8F0)
val StaffHubBackground = Color(0xFFF8FAFC)
val StaffHubCard = Color(0xFFFFFFFF)

// Status Colors
val StatusPresent = Color(0xFF10B981) // Teal / Emerald
val StatusPresentBg = Color(0xFFECFDF5)
val StatusPresentText = Color(0xFF047857)

val StatusAbsent = Color(0xFFEF4444) // Coral Red
val StatusAbsentBg = Color(0xFFFEF2F2)
val StatusAbsentText = Color(0xFFB91C1C)

val StatusLeave = Color(0xFF8B5CF6) // Purple
val StatusLeaveBg = Color(0xFFF5F3FF)
val StatusLeaveText = Color(0xFF6D28D9)

val StatusLate = Color(0xFFF59E0B) // Amber
val StatusLateBg = Color(0xFFFFFBEB)
val StatusLateText = Color(0xFFB45309)

val StatusHalfDay = Color(0xFF0284C7)
val StatusHalfDayBg = Color(0xFFF0F9FF)
val StatusHalfDayText = Color(0xFF0369A1)

// Theme Palette Enums with rich Pastel and Vibrant choices
enum class AppTheme(
    val displayName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val lightBg: Color = Color(0xFFF8FAFC),
    val isPastel: Boolean = false
) {
    OCEAN("Ocean Blue", Color(0xFF2563EB), Color(0xFF06B6D4), Color(0xFFF8FAFC), false),
    PASTEL_MINT("Pastel Mint", Color(0xFF059669), Color(0xFFA7F3D0), Color(0xFFF0FDF4), true),
    PASTEL_LAVENDER("Pastel Lavender", Color(0xFF7C3AED), Color(0xFFDDD6FE), Color(0xFFFAF5FF), true),
    PASTEL_PEACH("Pastel Peach", Color(0xFFEA580C), Color(0xFFFED7AA), Color(0xFFFFF7ED), true),
    PASTEL_ROSE("Pastel Rose", Color(0xFFE11D48), Color(0xFFFECDD3), Color(0xFFFFF1F2), true),
    PASTEL_SKY("Pastel Sky", Color(0xFF0284C7), Color(0xFFBAE6FD), Color(0xFFF0F9FF), true),
    PASTEL_BUTTER("Pastel Butter", Color(0xFFD97706), Color(0xFFFEF08A), Color(0xFFFEFCE8), true),
    PASTEL_SAGE("Pastel Sage", Color(0xFF15803D), Color(0xFFBBF7D0), Color(0xFFF0FDF4), true),
    PASTEL_LILAC("Pastel Lilac", Color(0xFF9333EA), Color(0xFFE9D5FF), Color(0xFFFAF5FF), true),
    PASTEL_CORAL("Pastel Coral", Color(0xFFDC2626), Color(0xFFFECACA), Color(0xFFFEF2F2), true),
    PASTEL_MATCHA("Pastel Matcha", Color(0xFF65A30D), Color(0xFFD9F99D), Color(0xFFF7FEE7), true),
    MIDNIGHT("Midnight", Color(0xFF0F172A), Color(0xFF3B82F6), Color(0xFF0F172A), false),
    GRAPHITE("Graphite", Color(0xFF334155), Color(0xFF64748B), Color(0xFFF8FAFC), false)
}

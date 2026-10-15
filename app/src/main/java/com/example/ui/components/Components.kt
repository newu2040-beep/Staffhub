package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.ui.theme.StaffHubBlue
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusAbsentBg
import com.example.ui.theme.StatusAbsentText
import com.example.ui.theme.StatusHalfDay
import com.example.ui.theme.StatusHalfDayBg
import com.example.ui.theme.StatusHalfDayText
import com.example.ui.theme.StatusLate
import com.example.ui.theme.StatusLateBg
import com.example.ui.theme.StatusLateText
import com.example.ui.theme.StatusLeave
import com.example.ui.theme.StatusLeaveBg
import com.example.ui.theme.StatusLeaveText
import com.example.ui.theme.StatusPresent
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentText

@Composable
fun StaffAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    backgroundColor: Color = StaffHubBlue,
    avatarUrl: String? = null,
    imageUrl: String? = avatarUrl
) {
    val finalUrl = imageUrl ?: avatarUrl
    if (!finalUrl.isNullOrBlank()) {
        coil.compose.AsyncImage(
            model = finalUrl,
            contentDescription = name,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
            .ifEmpty { "S" }

        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.38f).sp
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: AttendanceStatus,
    modifier: Modifier = Modifier,
    checkInTime: String? = null
) {
    val (bgColor, textColor, label, icon) = when (status) {
        AttendanceStatus.PRESENT -> Tuple4(StatusPresentBg, StatusPresentText, "Present", "✓")
        AttendanceStatus.ABSENT -> Tuple4(StatusAbsentBg, StatusAbsentText, "Absent", "✕")
        AttendanceStatus.LATE -> Tuple4(StatusLateBg, StatusLateText, "Late", "!")
        AttendanceStatus.ON_LEAVE -> Tuple4(StatusLeaveBg, StatusLeaveText, "On Leave", "✈")
        AttendanceStatus.HALF_DAY -> Tuple4(StatusHalfDayBg, StatusHalfDayText, "Half-day", "½")
        AttendanceStatus.WEEKLY_OFF -> Tuple4(Color(0xFFF1F5F9), Color(0xFF64748B), "Weekly Off", "—")
        AttendanceStatus.HOLIDAY -> Tuple4(Color(0xFFFEF3C7), Color(0xFFB45309), "Holiday", "★")
    }

    val displayString = if (!checkInTime.isNullOrBlank() && status == AttendanceStatus.PRESENT) {
        "$label • $checkInTime"
    } else {
        "$icon $label"
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = displayString,
            color = textColor,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    backgroundBrush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(backgroundBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

enum class NavigationTab(val title: String, val icon: ImageVector, val outlinedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    ATTENDANCE("Attendance", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    STAFF("Staff", Icons.Filled.People, Icons.Outlined.People),
    MORE("More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

@Composable
fun StaffHubBottomNavigation(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color(0x20000000))
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    val activeColor = MaterialTheme.colorScheme.primary
                    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent,
                        label = "pillBg"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .testTag("nav_tab_${tab.name.lowercase()}")
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillBg)
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.icon else tab.outlinedIcon,
                            contentDescription = tab.title,
                            tint = if (isSelected) activeColor else inactiveColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) activeColor else inactiveColor
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.StaffMember
import com.example.ui.components.StaffAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StaffHubBlue
import com.example.ui.theme.StatusAbsent
import com.example.ui.theme.StatusLeave
import com.example.ui.theme.StatusPresent
import com.example.ui.viewmodel.StaffHubViewModel

@Composable
fun AttendanceScreen(
    viewModel: StaffHubViewModel,
    onBackClick: () -> Unit,
    onStaffClick: (StaffMember) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("Calendar") } // "Calendar" or "List"
    var selectedMonthIndex by remember { mutableIntStateOf(9) } // 9 = October
    val months = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    val currentMonthName = months[selectedMonthIndex]

    var selectedDay by remember { mutableIntStateOf(14) }
    val staffList by viewModel.staffList.collectAsState()
    val attendanceList by viewModel.attendanceList.collectAsState()

    // Day color map matching mockup Screen 3
    // Present: 1, 2, 3, 4, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18
    // Absent: 5, 12
    // Leave: 19
    val statusForDay = mapOf(
        1 to AttendanceStatus.PRESENT,
        2 to AttendanceStatus.PRESENT,
        3 to AttendanceStatus.PRESENT,
        4 to AttendanceStatus.PRESENT,
        5 to AttendanceStatus.ABSENT,
        6 to AttendanceStatus.WEEKLY_OFF,
        7 to AttendanceStatus.PRESENT,
        8 to AttendanceStatus.PRESENT,
        9 to AttendanceStatus.PRESENT,
        10 to AttendanceStatus.PRESENT,
        11 to AttendanceStatus.PRESENT,
        12 to AttendanceStatus.ABSENT,
        13 to AttendanceStatus.PRESENT,
        14 to AttendanceStatus.PRESENT,
        15 to AttendanceStatus.PRESENT,
        16 to AttendanceStatus.PRESENT,
        17 to AttendanceStatus.PRESENT,
        18 to AttendanceStatus.PRESENT,
        19 to AttendanceStatus.ON_LEAVE
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Attendance",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Pill Toggle: Calendar | List
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    // Calendar Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedTab == "Calendar") StaffHubBlue else Color.Transparent)
                            .clickable { selectedTab = "Calendar" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Calendar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == "Calendar") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // List Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedTab == "List") StaffHubBlue else Color.Transparent)
                            .clickable { selectedTab = "List" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "List",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedTab == "List") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (selectedTab == "Calendar") {
            item {
                // Month Header & Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currentMonthName 2025",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row {
                        IconButton(
                            onClick = {
                                if (selectedMonthIndex > 0) selectedMonthIndex-- else selectedMonthIndex = 11
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Month",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                if (selectedMonthIndex < 11) selectedMonthIndex++ else selectedMonthIndex = 0
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Month",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Days of week row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Calendar Grid (31 days, starting on Wednesday for Oct 2025: 3 leading empty spaces)
                val totalDays = 31
                val leadingSpaces = 3 // Wed is 3rd column (0=Sun, 1=Mon, 2=Tue, 3=Wed)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    var currentDayNum = 1
                    for (row in 0 until 5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                if (cellIndex < leadingSpaces || currentDayNum > totalDays) {
                                    Spacer(modifier = Modifier.size(38.dp))
                                } else {
                                    val dayNum = currentDayNum
                                    val status = statusForDay[dayNum]
                                    val isSelected = dayNum == selectedDay

                                    val (badgeBg, textClr) = when (status) {
                                        AttendanceStatus.PRESENT -> Pair(Color(0xFFD1FAE5), Color(0xFF047857)) // Mint / Green
                                        AttendanceStatus.ABSENT -> Pair(Color(0xFFFEE2E2), Color(0xFFB91C1C)) // Red
                                        AttendanceStatus.ON_LEAVE -> Pair(Color(0xFF818CF8), Color.White) // Indigo/Purple
                                        else -> Pair(Color.Transparent, MaterialTheme.colorScheme.onSurface)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected && status == null) StaffHubBlue.copy(alpha = 0.2f) else badgeBg)
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) StaffHubBlue else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                selectedDay = dayNum
                                                viewModel.setSelectedCalendarDate(String.format("2025-%02d-%02d", selectedMonthIndex + 1, dayNum))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNum",
                                            fontSize = 13.5.sp,
                                            fontWeight = if (status != null || isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = textClr
                                        )
                                    }
                                    currentDayNum++
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Legend Bar (matching mockup: Present • Absent • Leave)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Present
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusPresent))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Present", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.width(20.dp))

                    // Absent
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusAbsent))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Absent", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.width(20.dp))

                    // Leave
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusLeave))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Leave", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(22.dp))

                // "This Month" Summary Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = StaffHubBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "This Month",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Present Days
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "22", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                Text(text = "Present\nDays", fontSize = 11.5.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
                            }
                            // Absent Days
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "4", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                                Text(text = "Absent\nDays", fontSize = 11.5.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
                            }
                            // Leave Days
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "2", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D28D9))
                                Text(text = "Leave\nDays", fontSize = 11.5.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
                            }
                            // Total Days
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "28", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "Total\nDays", fontSize = 11.5.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Attendance Rate Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(54.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { 0.85f },
                                    color = Color(0xFF10B981),
                                    trackColor = Color(0xFFE2E8F0),
                                    strokeWidth = 5.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = "85%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Attendance Rate",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Good job! Keep it up",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            // List View for Selected Date
            items(staffList) { staff ->
                val record = attendanceList.find { it.staffId == staff.id }
                val currentStatus = record?.status ?: AttendanceStatus.PRESENT

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { onStaffClick(staff) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StaffAvatar(
                                name = staff.fullName,
                                size = 42.dp,
                                backgroundColor = Color(staff.avatarBgColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = staff.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${staff.jobTitle} • ${staff.department}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Quick Mark Attendance Pills
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = currentStatus)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

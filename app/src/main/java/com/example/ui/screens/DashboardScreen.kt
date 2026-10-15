package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.StaffMember
import com.example.ui.components.QuickActionButton
import com.example.ui.components.StaffAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.StaffHubBlue
import com.example.ui.theme.StaffHubDarkBlue
import com.example.ui.viewmodel.StaffHubViewModel

@Composable
fun DashboardScreen(
    viewModel: StaffHubViewModel,
    onNavigateToStaff: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToMore: () -> Unit,
    onAddStaffClick: () -> Unit,
    onStaffClick: (StaffMember) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val staffList by viewModel.staffList.collectAsState()
    val attendanceList by viewModel.attendanceList.collectAsState()

    val totalStaff = staffList.size
    val presentCount = attendanceList.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = attendanceList.count { it.status == AttendanceStatus.ABSENT }
    val leaveCount = attendanceList.count { it.status == AttendanceStatus.ON_LEAVE }

    val presentPercentage = if (totalStaff > 0) ((presentCount.toFloat() / totalStaff.toFloat()) * 100).toInt() else 83
    val absentPercentage = if (totalStaff > 0) ((absentCount.toFloat() / totalStaff.toFloat()) * 100).toInt() else 17

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // User Header Row (matching Screen 2 mockup)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StaffAvatar(
                        name = currentUser?.displayName ?: "Rahul Shah",
                        size = 48.dp,
                        backgroundColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Good Morning,",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.displayName ?: "Rahul Shah",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Owner badge pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = currentUser?.role?.label ?: "Owner",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onNavigateToMore,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onNavigateToMore,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2x2 Metric Cards (matching mockup Screen 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Total Staff (Primary Vibrant Blue Card)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable { onNavigateToStaff() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(StaffHubBlue, StaffHubDarkBlue)
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                text = "Total Staff",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "$totalStaff",
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Outlined.People,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                // Card 2: Present Today (Mint / Cyan tint card)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F7F2)),
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable { onNavigateToAttendance() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Present Today",
                            color = Color(0xFF0F172A),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$presentCount",
                                color = Color(0xFF047857),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFD1FAE5)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = Color(0xFF047857),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "$presentPercentage%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 3: Absent Today (Rose / Coral card)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDECEB)),
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable { onNavigateToAttendance() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Absent Today",
                            color = Color(0xFF0F172A),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$absentCount",
                                color = Color(0xFFB91C1C),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEE2E2)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "↑ $absentPercentage%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }
                    }
                }

                // Card 4: On Leave (Lavender card)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EBFD)),
                    modifier = Modifier
                        .weight(1f)
                        .height(115.dp)
                        .clickable { onNavigateToAttendance() }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "On Leave",
                            color = Color(0xFF0F172A),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "$leaveCount",
                                color = Color(0xFF6D28D9),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEDE9FE)
                            ) {
                                Text(
                                    text = "✈ Leave",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6D28D9),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions Row (matching mockup Screen 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    title = "Add Staff",
                    icon = Icons.Default.PersonAdd,
                    backgroundBrush = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669))),
                    onClick = onAddStaffClick,
                    testTag = "quick_add_staff_button"
                )
                QuickActionButton(
                    title = "Attendance",
                    icon = Icons.Default.FactCheck,
                    backgroundBrush = Brush.linearGradient(listOf(StaffHubBlue, StaffHubDarkBlue)),
                    onClick = onNavigateToAttendance,
                    testTag = "quick_attendance_button"
                )
                QuickActionButton(
                    title = "Reports",
                    icon = Icons.Default.Assessment,
                    backgroundBrush = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))),
                    onClick = onNavigateToReports,
                    testTag = "quick_reports_button"
                )
                QuickActionButton(
                    title = "More",
                    icon = Icons.Default.MoreHoriz,
                    backgroundBrush = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
                    onClick = onNavigateToMore,
                    testTag = "quick_more_button"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Today's Attendance Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Attendance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "View all",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StaffHubBlue,
                    modifier = Modifier.clickable { onNavigateToAttendance() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Today's Attendance Rows
        if (staffList.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No staff added yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Add Staff' above to add team members manually.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(staffList.take(6)) { staff ->
                val record = attendanceList.find { it.staffId == staff.id }
                val status = record?.status ?: AttendanceStatus.PRESENT

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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = staff.jobTitle,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(
                            status = status,
                            checkInTime = record?.checkInTime
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Details",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }

    item {
        Spacer(modifier = Modifier.height(100.dp))
    }
    }
}

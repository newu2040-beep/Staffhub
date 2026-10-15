package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ExportFormat
import com.example.data.model.ExportResolution
import com.example.data.model.ReportType
import com.example.ui.theme.StaffHubBlue
import com.example.ui.viewmodel.StaffHubViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ExportReportsScreen(
    viewModel: StaffHubViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedReportType by viewModel.selectedReportType.collectAsState()
    val selectedExportFormat by viewModel.selectedExportFormat.collectAsState()
    val selectedExportResolution by viewModel.selectedExportResolution.collectAsState()
    val reportPeriod by viewModel.reportPeriod.collectAsState()
    val isGenerating by viewModel.isGeneratingReport.collectAsState()
    val lastExportedFile by viewModel.lastExportedFile.collectAsState()
    val customStartDate by viewModel.customStartDate.collectAsState()
    val customEndDate by viewModel.customEndDate.collectAsState()

    var periodMode by remember { mutableStateOf("Period") } // "Period" or "Custom"
    var showCustomDateDialog by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions.values.any { it }
        Toast.makeText(context, if (permissionGranted) "Gallery & Storage Access Active" else "Permissions Updated", Toast.LENGTH_SHORT).show()
    }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Predefined periods
    fun setPeriodPreset(type: String) {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        when (type) {
            "Today" -> {
                viewModel.setCustomDateRange(todayStr, todayStr)
            }
            "This Week" -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                val start = sdf.format(cal.time)
                cal.add(Calendar.DAY_OF_WEEK, 6)
                val end = sdf.format(cal.time)
                viewModel.setCustomDateRange(start, end)
            }
            "This Month" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = sdf.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = sdf.format(cal.time)
                viewModel.setCustomDateRange(start, end)
            }
            "Last Month" -> {
                cal.add(Calendar.MONTH, -1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = sdf.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                val end = sdf.format(cal.time)
                viewModel.setCustomDateRange(start, end)
            }
            "Year to Date" -> {
                val year = cal.get(Calendar.YEAR)
                viewModel.setCustomDateRange("$year-01-01", todayStr)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
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
                text = "Export Reports",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Period Toggle: Period | Custom
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
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (periodMode == "Period") StaffHubBlue else Color.Transparent)
                            .clickable {
                                periodMode = "Period"
                                setPeriodPreset("This Month")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Period Presets",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (periodMode == "Period") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (periodMode == "Custom") StaffHubBlue else Color.Transparent)
                            .clickable {
                                periodMode = "Custom"
                                showCustomDateDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Custom Range",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (periodMode == "Custom") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Preset Quick Buttons
            if (periodMode == "Period") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf("This Month", "Today", "This Week", "Last Month", "Year to Date")
                    presets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.clickable { setPeriodPreset(preset) }
                        ) {
                            Text(
                                text = preset,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StaffHubBlue,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Interactive Date Range Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showCustomDateDialog = true
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Date Range",
                            tint = StaffHubBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Selected Date Range",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = reportPeriod,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Change", fontSize = 12.sp, color = StaffHubBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Select Date",
                            tint = StaffHubBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Gallery & Storage Permissions Access Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StaffHubBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = StaffHubBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Gallery & File Permissions",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (permissionGranted) "Access granted" else "Required to export & save files",
                                fontSize = 11.5.sp,
                                color = if (permissionGranted) Color(0xFF059669) else Color(0xFF64748B)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_MEDIA_IMAGES,
                                        Manifest.permission.READ_MEDIA_VIDEO,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                )
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = if (permissionGranted) "Active" else "Grant Access", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Resolution & Quality Header (4K & 8K options)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.HighQuality, contentDescription = null, tint = StaffHubBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Resolution & Master Fidelity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Resolution selection cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExportResolution.entries.forEach { res ->
                    val isSelected = selectedExportResolution == res

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) {
                                    if (res == ExportResolution.EXTREME_8K) Color(0xFFF3E8FF)
                                    else if (res == ExportResolution.ULTRA_4K) Color(0xFFFEF3C7)
                                    else StaffHubBlue.copy(alpha = 0.12f)
                                } else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) {
                                    if (res == ExportResolution.EXTREME_8K) Color(0xFF9333EA)
                                    else if (res == ExportResolution.ULTRA_4K) Color(0xFFD97706)
                                    else StaffHubBlue
                                } else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.setExportResolution(res) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = res.shortLabel,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (res == ExportResolution.EXTREME_8K) Color(0xFF7E22CE)
                            else if (res == ExportResolution.ULTRA_4K) Color(0xFFB45309)
                            else if (isSelected) StaffHubBlue
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (res == ExportResolution.EXTREME_8K) "Master"
                            else if (res == ExportResolution.ULTRA_4K) "Ultra"
                            else if (res == ExportResolution.HIGH_2K) "QHD"
                            else "FHD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Report Type Section Header
            Text(
                text = "Report Type (Exports Only Related Data)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Report Types List
            val reportOptions = listOf(
                Tuple3(ReportType.ATTENDANCE_SUMMARY, Icons.Default.Assessment, Color(0xFF2563EB)),
                Tuple3(ReportType.STAFF_DETAILS, Icons.Default.Badge, Color(0xFF0D9488)),
                Tuple3(ReportType.DEPARTMENT_REPORT, Icons.Default.CorporateFare, Color(0xFF8B5CF6)),
                Tuple3(ReportType.SALARY_REPORT, Icons.Default.Payments, Color(0xFFD97706))
            )

            reportOptions.forEach { (type, icon, iconColor) ->
                val isSelected = selectedReportType == type

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) StaffHubBlue.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, StaffHubBlue) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.setReportType(type) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(iconColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = type.title,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = type.subtitle,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.setReportType(type) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = StaffHubBlue
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Export Format Section Header
            Text(
                text = "Export Format",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Format Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val formats = listOf(
                    FormatBadgeData(ExportFormat.PDF, "PDF", Color(0xFFEF4444), Color(0xFFFEE2E2)),
                    FormatBadgeData(ExportFormat.PNG, "PNG", Color(0xFF0284C7), Color(0xFFE0F2FE)),
                    FormatBadgeData(ExportFormat.TXT, "TXT", Color(0xFF2563EB), Color(0xFFDBEAFE)),
                    FormatBadgeData(ExportFormat.CSV, "CSV", Color(0xFF10B981), Color(0xFFD1FAE5)),
                    FormatBadgeData(ExportFormat.MARKDOWN, "MD", Color(0xFF8B5CF6), Color(0xFFEDE9FE))
                )

                formats.forEach { item ->
                    val isSelected = selectedExportFormat == item.format

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) item.accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) item.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { viewModel.setExportFormat(item.format) }
                            .padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(item.bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = item.accentColor
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.format.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Generate & Export Button
            Button(
                onClick = {
                    viewModel.generateAndShareReport(context)
                },
                enabled = !isGenerating,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StaffHubBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("generate_report_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Rendering ${selectedExportResolution.shortLabel}...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate & Export (${selectedExportResolution.shortLabel})",
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Reports are filtered by $reportPeriod and contain strictly ${selectedReportType.title} records.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            AnimatedVisibility(visible = lastExportedFile != null) {
                lastExportedFile?.let { file ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFDF5),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6EE7B7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exported: ${file.name}",
                                fontSize = 12.sp,
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Custom Date Range Dialog
    if (showCustomDateDialog) {
        var startInput by remember { mutableStateOf(customStartDate) }
        var endInput by remember { mutableStateOf(customEndDate) }

        Dialog(onDismissRequest = { showCustomDateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Set Custom Date Range",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = StaffHubBlue)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = startInput,
                        onValueChange = { startInput = it },
                        label = { Text("Start Date (YYYY-MM-DD)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = endInput,
                        onValueChange = { endInput = it },
                        label = { Text("End Date (YYYY-MM-DD)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showCustomDateDialog = false },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (startInput.isNotBlank() && endInput.isNotBlank()) {
                                    viewModel.setCustomDateRange(startInput.trim(), endInput.trim())
                                    showCustomDateDialog = false
                                    Toast.makeText(context, "Period set to $startInput – $endInput", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Please enter valid dates", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StaffHubBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply Range")
                        }
                    }
                }
            }
        }
    }
}

private data class Tuple3<A, B, C>(val a: A, val b: B, val c: C)
private data class FormatBadgeData(val format: ExportFormat, val label: String, val accentColor: Color, val bgColor: Color)

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.StaffMember
import com.example.ui.components.NavigationTab
import com.example.ui.components.StaffHubBottomNavigation
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExportReportsScreen
import com.example.ui.screens.LeaveDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.ShiftDialog
import com.example.ui.screens.StaffDetailDialog
import com.example.ui.screens.StaffListScreen
import com.example.ui.theme.StaffHubTheme
import com.example.ui.viewmodel.StaffHubViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: StaffHubViewModel = viewModel()
            val currentTheme by viewModel.currentTheme.collectAsState()

            StaffHubTheme(selectedTheme = currentTheme) {
                StaffHubApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun StaffHubApp(viewModel: StaffHubViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val shifts by viewModel.shifts.collectAsState()
    val leaves by viewModel.leaveRequests.collectAsState()

    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    var showReportsScreen by remember { mutableStateOf(false) }
    var selectedStaffForDetail by remember { mutableStateOf<StaffMember?>(null) }
    var isAddingNewStaff by remember { mutableStateOf(false) }
    var showShiftDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    // If logged out, render Login screen
    if (currentUser == null) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                selectedTab = NavigationTab.HOME
            }
        )
        return
    }

    // Handle Android system back button cleanly
    BackHandler(enabled = showReportsScreen || selectedTab != NavigationTab.HOME) {
        if (showReportsScreen) {
            showReportsScreen = false
        } else {
            selectedTab = NavigationTab.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (showReportsScreen) {
                ExportReportsScreen(
                    viewModel = viewModel,
                    onBackClick = { showReportsScreen = false }
                )
            } else {
                when (selectedTab) {
                    NavigationTab.HOME -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToStaff = { selectedTab = NavigationTab.STAFF },
                        onNavigateToAttendance = { selectedTab = NavigationTab.ATTENDANCE },
                        onNavigateToReports = { showReportsScreen = true },
                        onNavigateToMore = { selectedTab = NavigationTab.MORE },
                        onAddStaffClick = { isAddingNewStaff = true },
                        onStaffClick = { staff -> selectedStaffForDetail = staff }
                    )
                    NavigationTab.ATTENDANCE -> AttendanceScreen(
                        viewModel = viewModel,
                        onBackClick = { selectedTab = NavigationTab.HOME },
                        onStaffClick = { staff -> selectedStaffForDetail = staff }
                    )
                    NavigationTab.STAFF -> StaffListScreen(
                        viewModel = viewModel,
                        onBackClick = { selectedTab = NavigationTab.HOME },
                        onStaffClick = { staff -> selectedStaffForDetail = staff },
                        onAddStaffClick = { isAddingNewStaff = true }
                    )
                    NavigationTab.MORE -> MoreScreen(
                        viewModel = viewModel,
                        onNavigateToReports = { showReportsScreen = true },
                        onOpenShifts = { showShiftDialog = true },
                        onOpenLeaves = { showLeaveDialog = true },
                        onSignOut = {
                            selectedTab = NavigationTab.HOME
                        }
                    )
                }

                // Compact Floating Bottom Navigation Bar
                StaffHubBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            // Dialogs & Sheets
            if (selectedStaffForDetail != null) {
                StaffDetailDialog(
                    staff = selectedStaffForDetail,
                    isNew = false,
                    onDismiss = { selectedStaffForDetail = null },
                    onSave = { updated ->
                        viewModel.updateStaff(updated)
                        selectedStaffForDetail = null
                    },
                    onDelete = { id ->
                        viewModel.deleteStaff(id)
                        selectedStaffForDetail = null
                    },
                    onMarkAttendance = { status ->
                        selectedStaffForDetail?.let {
                            viewModel.markAttendance(it.id, status)
                        }
                        selectedStaffForDetail = null
                    }
                )
            }

            if (isAddingNewStaff) {
                StaffDetailDialog(
                    staff = null,
                    isNew = true,
                    onDismiss = { isAddingNewStaff = false },
                    onSave = { newStaff ->
                        viewModel.addStaff(newStaff)
                        isAddingNewStaff = false
                    },
                    onDelete = { isAddingNewStaff = false },
                    onMarkAttendance = {}
                )
            }

            if (showShiftDialog) {
                ShiftDialog(
                    shifts = shifts,
                    onDismiss = { showShiftDialog = false },
                    onAddShift = { newShift ->
                        viewModel.addShift(newShift)
                    }
                )
            }

            if (showLeaveDialog) {
                LeaveDialog(
                    leaves = leaves,
                    onDismiss = { showLeaveDialog = false },
                    onSubmitLeave = { req ->
                        viewModel.submitLeave(req)
                    },
                    onUpdateStatus = { id, status ->
                        viewModel.updateLeaveStatus(id, status, "Processed by Owner")
                    }
                )
            }
        }
    }
}

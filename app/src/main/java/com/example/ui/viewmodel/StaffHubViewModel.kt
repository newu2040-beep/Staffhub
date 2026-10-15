package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Department
import com.example.data.model.ExportFormat
import com.example.data.model.LeaveRequest
import com.example.data.model.Organization
import com.example.data.model.ReportType
import com.example.data.model.Shift
import com.example.data.model.StaffMember
import com.example.data.model.UserProfile
import com.example.data.repository.ReportExporter
import com.example.data.repository.StaffHubRepository
import com.example.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StaffHubViewModel(
    private val repository: StaffHubRepository = StaffHubRepository()
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = repository.currentUser
    val organization: StateFlow<Organization> = repository.organization
    val staffList: StateFlow<List<StaffMember>> = repository.staffList
    val attendanceList: StateFlow<List<AttendanceRecord>> = repository.attendanceList
    val shifts: StateFlow<List<Shift>> = repository.shifts
    val leaveRequests: StateFlow<List<LeaveRequest>> = repository.leaveRequests
    val departments: StateFlow<List<Department>> = repository.departments
    val selectedCalendarDate: StateFlow<String> = repository.selectedCalendarDate

    // UI Configuration State
    private val _currentTheme = MutableStateFlow(AppTheme.OCEAN)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _staffSearchQuery = MutableStateFlow("")
    val staffSearchQuery: StateFlow<String> = _staffSearchQuery.asStateFlow()

    private val _staffFilter = MutableStateFlow("All")
    val staffFilter: StateFlow<String> = _staffFilter.asStateFlow()

    // Reports State
    private val _reportPeriod = MutableStateFlow("01 Oct 2025 – 31 Oct 2025")
    val reportPeriod: StateFlow<String> = _reportPeriod.asStateFlow()

    private val _selectedReportType = MutableStateFlow(ReportType.ATTENDANCE_SUMMARY)
    val selectedReportType: StateFlow<ReportType> = _selectedReportType.asStateFlow()

    private val _selectedExportFormat = MutableStateFlow(ExportFormat.PDF)
    val selectedExportFormat: StateFlow<ExportFormat> = _selectedExportFormat.asStateFlow()

    private val _selectedExportResolution = MutableStateFlow(com.example.data.model.ExportResolution.STANDARD)
    val selectedExportResolution: StateFlow<com.example.data.model.ExportResolution> = _selectedExportResolution.asStateFlow()

    private val _isGeneratingReport = MutableStateFlow(false)
    val isGeneratingReport: StateFlow<Boolean> = _isGeneratingReport.asStateFlow()

    private val _lastExportedFile = MutableStateFlow<File?>(null)
    val lastExportedFile: StateFlow<File?> = _lastExportedFile.asStateFlow()

    // Filtered Staff List for Directory
    val filteredStaff: StateFlow<List<StaffMember>> = combine(
        staffList,
        _staffSearchQuery,
        _staffFilter
    ) { list, query, filter ->
        list.filter { staff ->
            val matchesQuery = query.isBlank() ||
                    staff.fullName.contains(query, ignoreCase = true) ||
                    staff.jobTitle.contains(query, ignoreCase = true) ||
                    staff.employeeId.contains(query, ignoreCase = true) ||
                    staff.department.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Present" -> staff.status == "Active" && staff.presentDays > 0
                "Absent" -> staff.absentDays > 3
                "On Leave" -> staff.status == "On Leave" || staff.leaveDays > 2
                "Service" -> staff.department.equals("Service", ignoreCase = true)
                "Kitchen" -> staff.department.equals("Kitchen", ignoreCase = true)
                "Front Desk" -> staff.department.equals("Front Desk", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTheme(theme: AppTheme) {
        _currentTheme.value = theme
    }

    fun setStaffSearchQuery(query: String) {
        _staffSearchQuery.value = query
    }

    fun setStaffFilter(filter: String) {
        _staffFilter.value = filter
    }

    fun setSelectedCalendarDate(date: String) {
        repository.setSelectedCalendarDate(date)
    }

    fun setReportPeriod(period: String) {
        _reportPeriod.value = period
    }

    fun setReportType(type: ReportType) {
        _selectedReportType.value = type
    }

    fun setExportFormat(format: ExportFormat) {
        _selectedExportFormat.value = format
    }

    fun addStaff(staff: StaffMember) {
        repository.addStaff(staff)
    }

    fun updateStaff(staff: StaffMember) {
        repository.updateStaff(staff)
    }

    fun deleteStaff(staffId: String) {
        repository.deleteStaff(staffId)
    }

    fun markAttendance(staffId: String, status: AttendanceStatus, checkIn: String? = null, notes: String? = null) {
        repository.markAttendance(
            staffId = staffId,
            status = status,
            date = selectedCalendarDate.value,
            checkIn = checkIn,
            notes = notes
        )
    }

    fun submitLeave(request: LeaveRequest) {
        repository.submitLeaveRequest(request)
    }

    fun updateLeaveStatus(requestId: String, status: String, decisionNote: String?) {
        repository.updateLeaveStatus(requestId, status, decisionNote)
    }

    fun addShift(shift: Shift) {
        repository.addShift(shift)
    }

    fun updateOrganization(name: String, category: String, country: String, timeZone: String) {
        repository.updateOrganization(name, category, country, timeZone)
    }

    fun signIn(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.signInWithEmail(email, pass, onSuccess, onError)
    }

    fun signUp(name: String, email: String, pass: String, orgName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.signUpWithEmail(name, email, pass, orgName, onSuccess, onError)
    }

    fun signInWithGoogle(
        activity: android.app.Activity,
        credentialManager: androidx.credentials.CredentialManager,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit = {}
    ) {
        repository.signInWithGoogle(activity, credentialManager, onSuccess, onError, onCancel)
    }

    fun sendPasswordReset(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.sendPasswordReset(email, onSuccess, onError)
    }

    fun signOut() {
        repository.signOut()
    }

    fun setExportResolution(resolution: com.example.data.model.ExportResolution) {
        _selectedExportResolution.value = resolution
    }

    fun generateAndShareReport(context: Context) {
        _isGeneratingReport.value = true
        val file = ReportExporter.generateAndShareReport(
            context = context,
            reportType = _selectedReportType.value,
            exportFormat = _selectedExportFormat.value,
            periodText = _reportPeriod.value,
            orgName = organization.value.name,
            staffList = staffList.value,
            attendanceList = attendanceList.value,
            resolution = _selectedExportResolution.value
        )
        _lastExportedFile.value = file
        _isGeneratingReport.value = false
    }
}

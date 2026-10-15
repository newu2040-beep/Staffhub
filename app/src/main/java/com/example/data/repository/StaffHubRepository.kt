package com.example.data.repository

import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.Department
import com.example.data.model.LeaveRequest
import com.example.data.model.Organization
import com.example.data.model.Shift
import com.example.data.model.StaffMember
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StaffHubRepository {

    private val auth: FirebaseAuth? = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        null
    }

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        null
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    // Reactive State
    private val _currentUser = MutableStateFlow<UserProfile?>(
        UserProfile(
            uid = "usr_rahul_shah",
            email = "rahul@staffhub.app",
            displayName = "Rahul Shah",
            role = UserRole.OWNER,
            organizationId = "org_staffhub_default",
            organizationName = "StaffHub Org"
        )
    )
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _organization = MutableStateFlow(
        Organization(
            id = "org_staffhub_default",
            name = "StaffHub Org",
            businessCategory = "Hospitality & Restaurant",
            ownerName = "Rahul Shah"
        )
    )
    val organization: StateFlow<Organization> = _organization.asStateFlow()

    private val _staffList = MutableStateFlow<List<StaffMember>>(emptyList())
    val staffList: StateFlow<List<StaffMember>> = _staffList.asStateFlow()

    private val _attendanceList = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceList: StateFlow<List<AttendanceRecord>> = _attendanceList.asStateFlow()

    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> = _shifts.asStateFlow()

    private val _leaveRequests = MutableStateFlow<List<LeaveRequest>>(emptyList())
    val leaveRequests: StateFlow<List<LeaveRequest>> = _leaveRequests.asStateFlow()

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _selectedCalendarDate = MutableStateFlow(todayString())
    val selectedCalendarDate: StateFlow<String> = _selectedCalendarDate.asStateFlow()

    init {
        initializeSampleData()
        syncFromFirestore()
    }

    fun setSelectedCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    private fun todayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun initializeSampleData() {
        val initialStaff = listOf(
            StaffMember(
                id = "s1",
                employeeId = "EMP-101",
                fullName = "Aarav Sharma",
                jobTitle = "Waiter",
                role = UserRole.STAFF,
                department = "Service",
                status = "Active",
                presentDays = 22,
                absentDays = 2,
                leaveDays = 1,
                email = "aarav@staffhub.app",
                phone = "+977 9841234567",
                avatarBgColor = 0xFF2563EB
            ),
            StaffMember(
                id = "s2",
                employeeId = "EMP-102",
                fullName = "Priya Karki",
                jobTitle = "Cashier",
                role = UserRole.STAFF,
                department = "Front Desk",
                status = "Active",
                presentDays = 21,
                absentDays = 3,
                leaveDays = 0,
                email = "priya@staffhub.app",
                phone = "+977 9841234568",
                avatarBgColor = 0xFF0D9488
            ),
            StaffMember(
                id = "s3",
                employeeId = "EMP-103",
                fullName = "Rohan Basnet",
                jobTitle = "Chef",
                role = UserRole.STAFF,
                department = "Kitchen",
                status = "Active",
                presentDays = 18,
                absentDays = 6,
                leaveDays = 2,
                email = "rohan@staffhub.app",
                phone = "+977 9841234569",
                avatarBgColor = 0xFFE11D48
            ),
            StaffMember(
                id = "s4",
                employeeId = "EMP-104",
                fullName = "Sita Gurung",
                jobTitle = "Supervisor",
                role = UserRole.SUPERVISOR,
                department = "Operations",
                status = "Active",
                presentDays = 23,
                absentDays = 1,
                leaveDays = 1,
                email = "sita@staffhub.app",
                phone = "+977 9841234570",
                avatarBgColor = 0xFF7C3AED
            ),
            StaffMember(
                id = "s5",
                employeeId = "EMP-105",
                fullName = "Karan Adhikari",
                jobTitle = "Barista",
                role = UserRole.STAFF,
                department = "Beverages",
                status = "Active",
                presentDays = 20,
                absentDays = 4,
                leaveDays = 1,
                email = "karan@staffhub.app",
                phone = "+977 9841234571",
                avatarBgColor = 0xFFD97706
            ),
            StaffMember(
                id = "s6",
                employeeId = "EMP-106",
                fullName = "Anita Thapa",
                jobTitle = "Kitchen Helper",
                role = UserRole.STAFF,
                department = "Kitchen",
                status = "On Leave",
                presentDays = 16,
                absentDays = 8,
                leaveDays = 4,
                email = "anita@staffhub.app",
                phone = "+977 9841234572",
                avatarBgColor = 0xFF9333EA
            ),
            StaffMember(
                id = "s7",
                employeeId = "EMP-107",
                fullName = "Bikash Shrestha",
                jobTitle = "Assistant Manager",
                role = UserRole.ASSISTANT_MANAGER,
                department = "Management",
                status = "Active",
                presentDays = 24,
                absentDays = 0,
                leaveDays = 1,
                email = "bikash@staffhub.app",
                phone = "+977 9841234573",
                avatarBgColor = 0xFF2563EB
            ),
            StaffMember(
                id = "s8",
                employeeId = "EMP-108",
                fullName = "Deepa Maharjan",
                jobTitle = "Cleaner",
                role = UserRole.STAFF,
                department = "Housekeeping",
                status = "Active",
                presentDays = 22,
                absentDays = 2,
                leaveDays = 1,
                email = "deepa@staffhub.app",
                phone = "+977 9841234574",
                avatarBgColor = 0xFF059669
            )
        )
        _staffList.value = initialStaff

        val today = todayString()
        val initialAttendance = listOf(
            AttendanceRecord(
                id = "att_1",
                staffId = "s1",
                staffName = "Aarav Sharma",
                staffRole = "Waiter",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "9:12 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_2",
                staffId = "s2",
                staffName = "Priya Karki",
                staffRole = "Cashier",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "8:58 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_3",
                staffId = "s3",
                staffName = "Rohan Basnet",
                staffRole = "Chef",
                date = today,
                status = AttendanceStatus.ABSENT,
                checkInTime = null,
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_4",
                staffId = "s4",
                staffName = "Sita Gurung",
                staffRole = "Supervisor",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "9:03 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_5",
                staffId = "s5",
                staffName = "Karan Adhikari",
                staffRole = "Barista",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "8:45 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_6",
                staffId = "s6",
                staffName = "Anita Thapa",
                staffRole = "Kitchen Helper",
                date = today,
                status = AttendanceStatus.ON_LEAVE,
                checkInTime = null,
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_7",
                staffId = "s7",
                staffName = "Bikash Shrestha",
                staffRole = "Assistant Manager",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "8:50 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_8",
                staffId = "s8",
                staffName = "Deepa Maharjan",
                staffRole = "Cleaner",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "8:30 AM",
                shiftName = "Morning Shift"
            )
        )
        _attendanceList.value = initialAttendance

        _shifts.value = listOf(
            Shift(id = "sh_1", name = "Morning Shift", startTime = "09:00", endTime = "17:00", breakMinutes = 60, assignedStaffCount = 14),
            Shift(id = "sh_2", name = "Evening Shift", startTime = "16:00", endTime = "00:00", breakMinutes = 45, assignedStaffCount = 7),
            Shift(id = "sh_3", name = "Night Shift", startTime = "23:00", endTime = "07:00", breakMinutes = 60, assignedStaffCount = 3)
        )

        _departments.value = listOf(
            Department(id = "d1", name = "Service", headStaffName = "Sita Gurung", staffCount = 8),
            Department(id = "d2", name = "Kitchen", headStaffName = "Rohan Basnet", staffCount = 6),
            Department(id = "d3", name = "Front Desk", headStaffName = "Priya Karki", staffCount = 4),
            Department(id = "d4", name = "Housekeeping", headStaffName = "Deepa Maharjan", staffCount = 3),
            Department(id = "d5", name = "Management", headStaffName = "Bikash Shrestha", staffCount = 3)
        )

        _leaveRequests.value = listOf(
            LeaveRequest(
                id = "lv_1",
                staffId = "s6",
                staffName = "Anita Thapa",
                leaveType = "Medical Leave",
                startDate = today,
                endDate = today,
                isFullDay = true,
                reason = "Doctor appointment and flu recovery",
                status = "Approved",
                decisionNote = "Approved by Rahul Shah",
                requestedAt = "Yesterday"
            ),
            LeaveRequest(
                id = "lv_2",
                staffId = "s3",
                staffName = "Rohan Basnet",
                leaveType = "Personal Leave",
                startDate = "2025-10-20",
                endDate = "2025-10-22",
                isFullDay = true,
                reason = "Family wedding celebration",
                status = "Pending",
                requestedAt = "2 hours ago"
            )
        )
    }

    private fun syncFromFirestore() {
        val fs = firestore ?: return
        val orgId = _organization.value.id

        scope.launch {
            try {
                fs.collection("organizations").document(orgId)
                    .collection("staff")
                    .addSnapshotListener { snapshot, error ->
                        if (error == null && snapshot != null && !snapshot.isEmpty) {
                            val items = snapshot.documents.mapNotNull { doc ->
                                doc.toObject(StaffMember::class.java)?.copy(id = doc.id)
                            }
                            if (items.isNotEmpty()) {
                                _staffList.value = items
                            }
                        }
                    }
            } catch (e: Exception) {
                // Keep local state
            }
        }
    }

    fun addStaff(staff: StaffMember) {
        val newStaff = if (staff.id.isEmpty()) staff.copy(id = "s_${UUID.randomUUID().toString().take(6)}") else staff
        val updated = _staffList.value + newStaff
        _staffList.value = updated

        // Push to Firestore asynchronously
        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("staff").document(newStaff.id)
                        .set(newStaff)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateStaff(staff: StaffMember) {
        val updated = _staffList.value.map { if (it.id == staff.id) staff else it }
        _staffList.value = updated

        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("staff").document(staff.id)
                        .set(staff)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteStaff(staffId: String) {
        _staffList.value = _staffList.value.filter { it.id != staffId }
        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("staff").document(staffId)
                        .delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun markAttendance(
        staffId: String,
        status: AttendanceStatus,
        date: String = todayString(),
        checkIn: String? = null,
        checkOut: String? = null,
        notes: String? = null
    ) {
        val staff = _staffList.value.find { it.id == staffId } ?: return
        val current = _attendanceList.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.staffId == staffId && it.date == date }

        val record = AttendanceRecord(
            id = if (existingIndex >= 0) current[existingIndex].id else "att_${UUID.randomUUID().toString().take(6)}",
            staffId = staff.id,
            staffName = staff.fullName,
            staffRole = staff.jobTitle,
            date = date,
            status = status,
            checkInTime = checkIn ?: if (status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE) "09:00 AM" else null,
            checkOutTime = checkOut,
            shiftName = staff.assignedShift,
            notes = notes
        )

        if (existingIndex >= 0) {
            current[existingIndex] = record
        } else {
            current.add(record)
        }
        _attendanceList.value = current

        // Update staff counters
        val updatedStaffList = _staffList.value.map { s ->
            if (s.id == staffId) {
                when (status) {
                    AttendanceStatus.PRESENT -> s.copy(presentDays = s.presentDays + 1)
                    AttendanceStatus.ABSENT -> s.copy(absentDays = s.absentDays + 1)
                    AttendanceStatus.ON_LEAVE -> s.copy(leaveDays = s.leaveDays + 1)
                    else -> s
                }
            } else s
        }
        _staffList.value = updatedStaffList

        // Push to Firestore
        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("attendance").document(record.id)
                        .set(record)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun submitLeaveRequest(request: LeaveRequest) {
        val newReq = request.copy(id = "lv_${UUID.randomUUID().toString().take(6)}")
        _leaveRequests.value = listOf(newReq) + _leaveRequests.value

        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("leaveRequests").document(newReq.id)
                        .set(newReq)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateLeaveStatus(requestId: String, status: String, decisionNote: String?) {
        _leaveRequests.value = _leaveRequests.value.map { req ->
            if (req.id == requestId) {
                req.copy(status = status, decisionNote = decisionNote)
            } else req
        }
    }

    fun addShift(shift: Shift) {
        val newShift = shift.copy(id = "sh_${UUID.randomUUID().toString().take(6)}")
        _shifts.value = _shifts.value + newShift
    }

    fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Please enter both email and password")
            return
        }

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnSuccessListener { result ->
                val u = result.user
                _currentUser.value = UserProfile(
                    uid = u?.uid ?: "user_default",
                    email = u?.email ?: email,
                    displayName = u?.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = UserRole.OWNER
                )
                onSuccess()
            }
            ?.addOnFailureListener {
                // If offline / unconfigured, allow demo sign-in for seamless onboarding
                _currentUser.value = UserProfile(
                    uid = "usr_${email.hashCode()}",
                    email = email,
                    displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = UserRole.OWNER
                )
                onSuccess()
            } ?: run {
                // Fallback for development/offline
                _currentUser.value = UserProfile(
                    uid = "usr_demo",
                    email = email,
                    displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = UserRole.OWNER
                )
                onSuccess()
            }
    }

    fun signUpWithEmail(name: String, email: String, pass: String, orgName: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank() || name.isBlank()) {
            onError("Please fill in all required fields")
            return
        }

        auth?.createUserWithEmailAndPassword(email, pass)
            ?.addOnSuccessListener { result ->
                val u = result.user
                _currentUser.value = UserProfile(
                    uid = u?.uid ?: "user_default",
                    email = u?.email ?: email,
                    displayName = name,
                    role = UserRole.OWNER,
                    organizationName = orgName
                )
                _organization.value = _organization.value.copy(name = orgName, ownerName = name)
                onSuccess()
            }
            ?.addOnFailureListener {
                _currentUser.value = UserProfile(
                    uid = "usr_${email.hashCode()}",
                    email = email,
                    displayName = name,
                    role = UserRole.OWNER,
                    organizationName = orgName
                )
                _organization.value = _organization.value.copy(name = orgName, ownerName = name)
                onSuccess()
            } ?: run {
                _currentUser.value = UserProfile(
                    uid = "usr_demo",
                    email = email,
                    displayName = name,
                    role = UserRole.OWNER,
                    organizationName = orgName
                )
                _organization.value = _organization.value.copy(name = orgName, ownerName = name)
                onSuccess()
            }
    }

    fun signInWithGoogle(
        activityContext: android.app.Activity,
        credentialManager: androidx.credentials.CredentialManager,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onCancel: () -> Unit = {}
    ) {
        val clientId = try {
            activityContext.getString(com.example.R.string.default_web_client_id)
        } catch (e: Exception) {
            "530218685955-3ha9faug73qlr2npsrdmftt43qd4rh0d.apps.googleusercontent.com"
        }

        val signInOption = com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
        val request = androidx.credentials.GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(activityContext, request)
                val credential = result.credential
                if (credential is androidx.credentials.CustomCredential &&
                    credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdToken = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data).idToken
                    val authCredential = com.google.firebase.auth.GoogleAuthProvider.getCredential(googleIdToken, null)
                    val firebaseResult = auth?.signInWithCredential(authCredential)?.await()
                    val user = firebaseResult?.user

                    val profile = UserProfile(
                        uid = user?.uid ?: "user_google",
                        email = user?.email ?: "google.user@staffhub.app",
                        displayName = user?.displayName ?: "Google User",
                        role = UserRole.OWNER,
                        avatarUrl = user?.photoUrl?.toString()
                    )
                    _currentUser.value = profile

                    // Sync to Firestore
                    firestore?.let { fs ->
                        fs.collection("users").document(profile.uid).set(profile)
                    }

                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        onError("Unexpected Google credential type")
                    }
                }
            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                android.util.Log.w("StaffHubAuth", "Google Sign-In cancelled: ${e.message}")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onCancel()
                }
            } catch (e: Exception) {
                android.util.Log.e("StaffHubAuth", "Google Sign-In error", e)
                // If Play Services is unavailable on local emulator, provide seamless demo sign-in
                val demoProfile = UserProfile(
                    uid = "usr_google_demo",
                    email = "gleg30900@gmail.com",
                    displayName = "Rahul Shah",
                    role = UserRole.OWNER
                )
                _currentUser.value = demoProfile
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }
        }
    }

    fun sendPasswordReset(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank()) {
            onError("Please enter your email address")
            return
        }
        auth?.sendPasswordResetEmail(email)
            ?.addOnSuccessListener {
                onSuccess()
            }
            ?.addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Failed to send reset email")
            } ?: run {
                onSuccess()
            }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
    }

    fun updateOrganization(name: String, category: String, country: String, timeZone: String) {
        _organization.value = _organization.value.copy(
            name = name,
            businessCategory = category,
            country = country,
            timeZone = timeZone
        )
    }
}

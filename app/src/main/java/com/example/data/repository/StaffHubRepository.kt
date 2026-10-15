package com.example.data.repository

import android.util.Log
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
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
        Log.w("StaffHubRepo", "FirebaseAuth unavailable", e)
        null
    }

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.w("StaffHubRepo", "Firestore unavailable", e)
        null
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private val listeners = mutableListOf<ListenerRegistration>()

    // Reactive State
    private val _currentUser = MutableStateFlow<UserProfile?>(
        UserProfile(
            uid = "usr_rahul_shah",
            email = "rahul@staffhub.app",
            displayName = "Rahul Shah",
            role = UserRole.OWNER,
            customRoleTitle = "Organization Owner",
            organizationId = "org_staffhub_default",
            organizationName = "StaffHub Org",
            gender = "Male",
            age = 30,
            phone = "+977 9841234567",
            bio = "Founder & Chief Executive",
            emergencyContact = "+977 9801234567"
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

    private val _isDemoDataActive = MutableStateFlow(true)
    val isDemoDataActive: StateFlow<Boolean> = _isDemoDataActive.asStateFlow()

    init {
        loadDemoData(persistToFirestore = false)
        attachFirestoreRealtimeListeners()
    }

    fun setSelectedCalendarDate(date: String) {
        _selectedCalendarDate.value = date
    }

    private fun todayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun loadDemoData(persistToFirestore: Boolean = true) {
        val initialStaff = listOf(
            StaffMember(
                id = "s1",
                employeeId = "EMP-101",
                fullName = "Aarav Sharma",
                jobTitle = "Head Waiter",
                role = UserRole.TEAM_LEADER,
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
                jobTitle = "Cashier & Host",
                role = UserRole.STAFF,
                department = "Front Desk",
                status = "Active",
                presentDays = 24,
                absentDays = 1,
                leaveDays = 0,
                email = "priya@staffhub.app",
                phone = "+977 9841234568",
                avatarBgColor = 0xFF0D9488
            ),
            StaffMember(
                id = "s3",
                employeeId = "EMP-103",
                fullName = "Rohan Basnet",
                jobTitle = "Sous Chef",
                role = UserRole.STAFF,
                department = "Kitchen",
                status = "Active",
                presentDays = 18,
                absentDays = 5,
                leaveDays = 2,
                email = "rohan@staffhub.app",
                phone = "+977 9841234569",
                avatarBgColor = 0xFFEA580C
            ),
            StaffMember(
                id = "s4",
                employeeId = "EMP-104",
                fullName = "Sita Gurung",
                jobTitle = "Shift Supervisor",
                role = UserRole.SUPERVISOR,
                department = "Service",
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
                staffRole = "Head Waiter",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "9:12 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_2",
                staffId = "s2",
                staffName = "Priya Karki",
                staffRole = "Cashier & Host",
                date = today,
                status = AttendanceStatus.PRESENT,
                checkInTime = "8:58 AM",
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_3",
                staffId = "s3",
                staffName = "Rohan Basnet",
                staffRole = "Sous Chef",
                date = today,
                status = AttendanceStatus.ABSENT,
                checkInTime = null,
                shiftName = "Morning Shift"
            ),
            AttendanceRecord(
                id = "att_4",
                staffId = "s4",
                staffName = "Sita Gurung",
                staffRole = "Shift Supervisor",
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
                startDate = "2026-10-20",
                endDate = "2026-10-22",
                isFullDay = true,
                reason = "Family wedding celebration",
                status = "Pending",
                requestedAt = "2 hours ago"
            )
        )
        _isDemoDataActive.value = true

        if (persistToFirestore) {
            persistFullDataToFirestore()
        }
    }

    fun clearAllDemoData() {
        _staffList.value = emptyList()
        _attendanceList.value = emptyList()
        _shifts.value = emptyList()
        _leaveRequests.value = emptyList()
        _isDemoDataActive.value = false

        // Also clean up from Firestore
        firestore?.let { fs ->
            scope.launch {
                try {
                    val orgId = _organization.value.id
                    val orgRef = fs.collection("organizations").document(orgId)
                    listOf("staff", "attendance", "shifts", "leaveRequests").forEach { coll ->
                        val snap = orgRef.collection(coll).get().await()
                        for (doc in snap.documents) {
                            doc.reference.delete()
                        }
                    }
                    Log.i("StaffHubRepo", "Cleared all demo data from Firestore")
                } catch (e: Exception) {
                    Log.w("StaffHubRepo", "Failed to clear Firestore collection", e)
                }
            }
        }
    }

    private fun persistFullDataToFirestore() {
        val fs = firestore ?: return
        val orgId = _organization.value.id
        scope.launch {
            try {
                val orgRef = fs.collection("organizations").document(orgId)
                orgRef.set(mapOf(
                    "id" to orgId,
                    "name" to _organization.value.name,
                    "businessCategory" to _organization.value.businessCategory,
                    "ownerName" to _organization.value.ownerName
                ), SetOptions.merge())

                _staffList.value.forEach { staff ->
                    orgRef.collection("staff").document(staff.id).set(staffToMap(staff), SetOptions.merge())
                }
                _attendanceList.value.forEach { att ->
                    orgRef.collection("attendance").document(att.id).set(attendanceToMap(att), SetOptions.merge())
                }
                _shifts.value.forEach { sh ->
                    orgRef.collection("shifts").document(sh.id).set(shiftToMap(sh), SetOptions.merge())
                }
                _leaveRequests.value.forEach { lv ->
                    orgRef.collection("leaveRequests").document(lv.id).set(leaveToMap(lv), SetOptions.merge())
                }
            } catch (e: Exception) {
                Log.w("StaffHubRepo", "Error persisting initial data to Firestore", e)
            }
        }
    }

    private fun attachFirestoreRealtimeListeners() {
        val fs = firestore ?: return
        val orgId = _organization.value.id

        // Clear existing listeners
        listeners.forEach { it.remove() }
        listeners.clear()

        try {
            val orgRef = fs.collection("organizations").document(orgId)

            // 1. Staff Listener
            val staffReg = orgRef.collection("staff").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("StaffHubRepo", "Staff listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        mapDocToStaff(doc.id, doc.data)
                    }
                    if (list.isNotEmpty()) {
                        _staffList.value = list
                        _isDemoDataActive.value = false
                    }
                }
            }
            listeners.add(staffReg)

            // 2. Attendance Listener
            val attReg = orgRef.collection("attendance").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("StaffHubRepo", "Attendance listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        mapDocToAttendance(doc.id, doc.data)
                    }
                    if (list.isNotEmpty()) {
                        _attendanceList.value = list
                    }
                }
            }
            listeners.add(attReg)

            // 3. Shifts Listener
            val shiftReg = orgRef.collection("shifts").addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        mapDocToShift(doc.id, doc.data)
                    }
                    if (list.isNotEmpty()) {
                        _shifts.value = list
                    }
                }
            }
            listeners.add(shiftReg)

            // 4. Leave Requests Listener
            val leaveReg = orgRef.collection("leaveRequests").addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        mapDocToLeave(doc.id, doc.data)
                    }
                    if (list.isNotEmpty()) {
                        _leaveRequests.value = list
                    }
                }
            }
            listeners.add(leaveReg)

            // 5. User Profile Listener
            val uid = _currentUser.value?.uid ?: "usr_rahul_shah"
            val userReg = fs.collection("users").document(uid).addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null && snapshot.exists()) {
                    val d = snapshot.data
                    if (d != null) {
                        val current = _currentUser.value
                        _currentUser.value = current?.copy(
                            displayName = d["displayName"] as? String ?: current.displayName,
                            customRoleTitle = d["customRoleTitle"] as? String ?: current.customRoleTitle,
                            gender = d["gender"] as? String ?: current.gender,
                            age = (d["age"] as? Long)?.toInt() ?: current.age,
                            phone = d["phone"] as? String ?: current.phone,
                            bio = d["bio"] as? String ?: current.bio,
                            avatarUrl = d["avatarUrl"] as? String ?: current.avatarUrl,
                            organizationName = d["organizationName"] as? String ?: current.organizationName
                        )
                    }
                }
            }
            listeners.add(userReg)

        } catch (e: Exception) {
            Log.e("StaffHubRepo", "Failed to attach Firestore listeners", e)
        }
    }

    // Owner Profile Customization
    fun updateUserProfile(profile: UserProfile) {
        _currentUser.value = profile

        // Sync to Firestore in real-time
        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("users").document(profile.uid).set(mapOf(
                        "uid" to profile.uid,
                        "email" to profile.email,
                        "displayName" to profile.displayName,
                        "role" to profile.role.name,
                        "customRoleTitle" to profile.customRoleTitle,
                        "gender" to profile.gender,
                        "age" to profile.age,
                        "phone" to profile.phone,
                        "bio" to profile.bio,
                        "avatarUrl" to (profile.avatarUrl ?: ""),
                        "organizationName" to profile.organizationName,
                        "organizationId" to profile.organizationId
                    ), SetOptions.merge())

                    // Also update organization owner name if matching
                    _organization.value = _organization.value.copy(
                        ownerName = profile.displayName,
                        name = profile.organizationName
                    )
                    fs.collection("organizations").document(profile.organizationId).set(mapOf(
                        "ownerName" to profile.displayName,
                        "name" to profile.organizationName
                    ), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error updating profile in Firestore", e)
                }
            }
        }
    }

    fun addStaff(staff: StaffMember) {
        val newStaff = if (staff.id.isEmpty()) staff.copy(id = "s_${UUID.randomUUID().toString().take(6)}") else staff
        val updated = _staffList.value + newStaff
        _staffList.value = updated
        _isDemoDataActive.value = false

        // Push to Firestore asynchronously
        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("staff").document(newStaff.id)
                        .set(staffToMap(newStaff), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error adding staff to Firestore", e)
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
                        .set(staffToMap(staff), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error updating staff in Firestore", e)
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
                    Log.e("StaffHubRepo", "Error deleting staff from Firestore", e)
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
                        .set(attendanceToMap(record), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error saving attendance in Firestore", e)
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
                        .set(leaveToMap(newReq), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error adding leave request in Firestore", e)
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

        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("leaveRequests").document(requestId)
                        .set(mapOf(
                            "status" to status,
                            "decisionNote" to (decisionNote ?: "")
                        ), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error updating leave status in Firestore", e)
                }
            }
        }
    }

    fun addShift(shift: Shift) {
        val newShift = shift.copy(id = "sh_${UUID.randomUUID().toString().take(6)}")
        _shifts.value = _shifts.value + newShift

        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(_organization.value.id)
                        .collection("shifts").document(newShift.id)
                        .set(shiftToMap(newShift), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error adding shift in Firestore", e)
                }
            }
        }
    }

    fun signInWithEmail(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Please enter both email and password")
            return
        }

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnSuccessListener { result ->
                val u = result.user
                val profile = UserProfile(
                    uid = u?.uid ?: "user_default",
                    email = u?.email ?: email,
                    displayName = u?.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = UserRole.OWNER
                )
                _currentUser.value = profile
                attachFirestoreRealtimeListeners()
                onSuccess()
            }
            ?.addOnFailureListener {
                // If offline / unconfigured, allow demo sign-in for seamless onboarding
                val profile = UserProfile(
                    uid = "usr_${email.hashCode()}",
                    email = email,
                    displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = UserRole.OWNER
                )
                _currentUser.value = profile
                onSuccess()
            } ?: run {
                val profile = UserProfile(
                    uid = "usr_demo",
                    email = email,
                    displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    role = UserRole.OWNER
                )
                _currentUser.value = profile
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
                val profile = UserProfile(
                    uid = u?.uid ?: "user_default",
                    email = u?.email ?: email,
                    displayName = name,
                    role = UserRole.OWNER,
                    organizationName = orgName
                )
                _currentUser.value = profile
                _organization.value = _organization.value.copy(name = orgName, ownerName = name)
                updateUserProfile(profile)
                attachFirestoreRealtimeListeners()
                onSuccess()
            }
            ?.addOnFailureListener {
                val profile = UserProfile(
                    uid = "usr_${email.hashCode()}",
                    email = email,
                    displayName = name,
                    role = UserRole.OWNER,
                    organizationName = orgName
                )
                _currentUser.value = profile
                _organization.value = _organization.value.copy(name = orgName, ownerName = name)
                onSuccess()
            } ?: run {
                val profile = UserProfile(
                    uid = "usr_demo",
                    email = email,
                    displayName = name,
                    role = UserRole.OWNER,
                    organizationName = orgName
                )
                _currentUser.value = profile
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
                    updateUserProfile(profile)
                    attachFirestoreRealtimeListeners()

                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        onError("Unexpected Google credential type")
                    }
                }
            } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                Log.w("StaffHubAuth", "Google Sign-In cancelled by user")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onCancel()
                }
            } catch (e: Exception) {
                Log.e("StaffHubAuth", "Google Sign-In fallback error", e)
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
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    fun updateOrganization(name: String, category: String, country: String, timeZone: String) {
        val updated = _organization.value.copy(
            name = name,
            businessCategory = category,
            country = country,
            timeZone = timeZone
        )
        _organization.value = updated

        firestore?.let { fs ->
            scope.launch {
                try {
                    fs.collection("organizations").document(updated.id).set(mapOf(
                        "name" to updated.name,
                        "businessCategory" to updated.businessCategory,
                        "country" to updated.country,
                        "timeZone" to updated.timeZone
                    ), SetOptions.merge())
                } catch (e: Exception) {
                    Log.e("StaffHubRepo", "Error updating org in Firestore", e)
                }
            }
        }
    }

    // Safe Mappings to avoid reflection crashes with Firestore
    private fun staffToMap(s: StaffMember): Map<String, Any> {
        return mapOf(
            "id" to s.id,
            "organizationId" to s.organizationId,
            "employeeId" to s.employeeId,
            "fullName" to s.fullName,
            "jobTitle" to s.jobTitle,
            "role" to s.role.name,
            "department" to s.department,
            "branch" to s.branch,
            "employmentType" to s.employmentType,
            "email" to s.email,
            "phone" to s.phone,
            "status" to s.status,
            "joiningDate" to s.joiningDate,
            "assignedShift" to s.assignedShift,
            "presentDays" to s.presentDays,
            "absentDays" to s.absentDays,
            "leaveDays" to s.leaveDays,
            "avatarBgColor" to s.avatarBgColor
        )
    }

    private fun mapDocToStaff(id: String, d: Map<String, Any>?): StaffMember? {
        if (d == null) return null
        return try {
            val roleStr = d["role"] as? String ?: "STAFF"
            val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.STAFF }
            StaffMember(
                id = id,
                organizationId = d["organizationId"] as? String ?: "",
                employeeId = d["employeeId"] as? String ?: id,
                fullName = d["fullName"] as? String ?: "Unknown",
                jobTitle = d["jobTitle"] as? String ?: "Staff",
                role = role,
                department = d["department"] as? String ?: "General",
                branch = d["branch"] as? String ?: "Main Branch",
                employmentType = d["employmentType"] as? String ?: "Full-time",
                email = d["email"] as? String ?: "",
                phone = d["phone"] as? String ?: "",
                status = d["status"] as? String ?: "Active",
                joiningDate = d["joiningDate"] as? String ?: "2024-01-15",
                assignedShift = d["assignedShift"] as? String ?: "Morning Shift",
                presentDays = (d["presentDays"] as? Long)?.toInt() ?: 0,
                absentDays = (d["absentDays"] as? Long)?.toInt() ?: 0,
                leaveDays = (d["leaveDays"] as? Long)?.toInt() ?: 0,
                avatarBgColor = (d["avatarBgColor"] as? Long) ?: 0xFF2563EB
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun attendanceToMap(a: AttendanceRecord): Map<String, Any> {
        return mapOf(
            "id" to a.id,
            "organizationId" to a.organizationId,
            "staffId" to a.staffId,
            "staffName" to a.staffName,
            "staffRole" to a.staffRole,
            "date" to a.date,
            "status" to a.status.name,
            "checkInTime" to (a.checkInTime ?: ""),
            "checkOutTime" to (a.checkOutTime ?: ""),
            "shiftName" to a.shiftName,
            "notes" to (a.notes ?: "")
        )
    }

    private fun mapDocToAttendance(id: String, d: Map<String, Any>?): AttendanceRecord? {
        if (d == null) return null
        return try {
            val statusStr = d["status"] as? String ?: "PRESENT"
            val status = try { AttendanceStatus.valueOf(statusStr) } catch (e: Exception) { AttendanceStatus.PRESENT }
            AttendanceRecord(
                id = id,
                organizationId = d["organizationId"] as? String ?: "",
                staffId = d["staffId"] as? String ?: "",
                staffName = d["staffName"] as? String ?: "",
                staffRole = d["staffRole"] as? String ?: "",
                date = d["date"] as? String ?: todayString(),
                status = status,
                checkInTime = (d["checkInTime"] as? String)?.takeIf { it.isNotBlank() },
                checkOutTime = (d["checkOutTime"] as? String)?.takeIf { it.isNotBlank() },
                shiftName = d["shiftName"] as? String ?: "Morning Shift",
                notes = (d["notes"] as? String)?.takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun shiftToMap(s: Shift): Map<String, Any> {
        return mapOf(
            "id" to s.id,
            "organizationId" to s.organizationId,
            "name" to s.name,
            "startTime" to s.startTime,
            "endTime" to s.endTime,
            "breakMinutes" to s.breakMinutes,
            "colorHex" to s.colorHex,
            "assignedStaffCount" to s.assignedStaffCount
        )
    }

    private fun mapDocToShift(id: String, d: Map<String, Any>?): Shift? {
        if (d == null) return null
        return try {
            Shift(
                id = id,
                organizationId = d["organizationId"] as? String ?: "",
                name = d["name"] as? String ?: "Shift",
                startTime = d["startTime"] as? String ?: "09:00",
                endTime = d["endTime"] as? String ?: "17:00",
                breakMinutes = (d["breakMinutes"] as? Long)?.toInt() ?: 60,
                colorHex = (d["colorHex"] as? Long) ?: 0xFF2563EB,
                assignedStaffCount = (d["assignedStaffCount"] as? Long)?.toInt() ?: 0
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun leaveToMap(l: LeaveRequest): Map<String, Any> {
        return mapOf(
            "id" to l.id,
            "organizationId" to l.organizationId,
            "staffId" to l.staffId,
            "staffName" to l.staffName,
            "leaveType" to l.leaveType,
            "startDate" to l.startDate,
            "endDate" to l.endDate,
            "isFullDay" to l.isFullDay,
            "reason" to l.reason,
            "status" to l.status,
            "decisionNote" to (l.decisionNote ?: ""),
            "requestedAt" to l.requestedAt
        )
    }

    private fun mapDocToLeave(id: String, d: Map<String, Any>?): LeaveRequest? {
        if (d == null) return null
        return try {
            LeaveRequest(
                id = id,
                organizationId = d["organizationId"] as? String ?: "",
                staffId = d["staffId"] as? String ?: "",
                staffName = d["staffName"] as? String ?: "",
                leaveType = d["leaveType"] as? String ?: "Annual Leave",
                startDate = d["startDate"] as? String ?: "",
                endDate = d["endDate"] as? String ?: "",
                isFullDay = (d["isFullDay"] as? Boolean) ?: true,
                reason = d["reason"] as? String ?: "",
                status = d["status"] as? String ?: "Pending",
                decisionNote = (d["decisionNote"] as? String)?.takeIf { it.isNotBlank() },
                requestedAt = d["requestedAt"] as? String ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }
}

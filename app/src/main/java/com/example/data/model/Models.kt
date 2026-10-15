package com.example.data.model

enum class UserRole(val label: String) {
    OWNER("Owner"),
    MANAGER("Manager"),
    ASSISTANT_MANAGER("Assistant Manager"),
    SUPERVISOR("Supervisor"),
    TEAM_LEADER("Team Leader"),
    STAFF("Staff")
}

enum class AttendanceStatus(val label: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    ON_LEAVE("On Leave"),
    HALF_DAY("Half-day"),
    WEEKLY_OFF("Weekly Off"),
    HOLIDAY("Holiday")
}

data class StaffMember(
    val id: String = "",
    val organizationId: String = "",
    val employeeId: String = "",
    val fullName: String = "",
    val jobTitle: String = "",
    val role: UserRole = UserRole.STAFF,
    val department: String = "General",
    val branch: String = "Main Branch",
    val employmentType: String = "Full-time",
    val email: String = "",
    val phone: String = "",
    val status: String = "Active",
    val joiningDate: String = "2024-01-15",
    val assignedShift: String = "Morning Shift (09:00 - 17:00)",
    val presentDays: Int = 22,
    val absentDays: Int = 2,
    val leaveDays: Int = 1,
    val avatarUrl: String? = null,
    val avatarBgColor: Long = 0xFF2563EB
)

data class AttendanceRecord(
    val id: String = "",
    val organizationId: String = "",
    val staffId: String = "",
    val staffName: String = "",
    val staffRole: String = "",
    val date: String = "", // "YYYY-MM-DD"
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val checkInTime: String? = null, // e.g. "09:12 AM"
    val checkOutTime: String? = null, // e.g. "05:00 PM"
    val shiftName: String = "Morning Shift",
    val notes: String? = null,
    val correctionReason: String? = null
)

data class Shift(
    val id: String = "",
    val organizationId: String = "",
    val name: String = "",
    val startTime: String = "09:00",
    val endTime: String = "17:00",
    val breakMinutes: Int = 60,
    val colorHex: Long = 0xFF2563EB,
    val assignedStaffCount: Int = 0
)

data class LeaveRequest(
    val id: String = "",
    val organizationId: String = "",
    val staffId: String = "",
    val staffName: String = "",
    val leaveType: String = "Annual Leave",
    val startDate: String = "",
    val endDate: String = "",
    val isFullDay: Boolean = true,
    val reason: String = "",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val decisionNote: String? = null,
    val requestedAt: String = ""
)

data class Department(
    val id: String = "",
    val name: String = "",
    val headStaffName: String = "",
    val staffCount: Int = 0
)

data class Organization(
    val id: String = "",
    val name: String = "StaffHub Organization",
    val businessCategory: String = "Café & Restaurant",
    val country: String = "Nepal",
    val timeZone: String = "Asia/Kathmandu (UTC+05:45)",
    val ownerName: String = "Rahul Shah",
    val ownerEmail: String = "rahul@staffhub.app",
    val workingDaysPerWeek: Int = 6,
    val standardCheckIn: String = "09:00",
    val standardCheckOut: String = "17:00"
)

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "Rahul Shah",
    val role: UserRole = UserRole.OWNER,
    val customRoleTitle: String = "Organization Owner",
    val organizationId: String = "org_staffhub_default",
    val organizationName: String = "StaffHub Org",
    val avatarUrl: String? = null,
    val gender: String = "Male", // "Male" or "Female" only
    val age: Int = 30,
    val phone: String = "+977 9841234567",
    val bio: String = "Founder & Chief Executive",
    val emergencyContact: String = ""
)

enum class ReportType(val title: String, val subtitle: String) {
    ATTENDANCE_SUMMARY("Attendance Summary", "Present, absent, leave, total days"),
    STAFF_DETAILS("Staff Details", "Individual staff performance"),
    DEPARTMENT_REPORT("Department Report", "Team-wise attendance"),
    SALARY_REPORT("Salary Report", "Attendance & working days")
}

enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
    PDF("pdf", "application/pdf", "PDF"),
    TXT("txt", "text/plain", "TXT"),
    CSV("csv", "text/csv", "CSV"),
    MARKDOWN("md", "text/markdown", "Markdown"),
    PNG("png", "image/png", "PNG Image")
}

enum class ExportResolution(val label: String, val shortLabel: String, val scaleFactor: Float, val resolutionTag: String) {
    STANDARD("Standard (1080p)", "1080p", 1f, "Full HD"),
    HIGH_2K("2K QHD (1440p)", "2K", 1.5f, "Quad HD"),
    ULTRA_4K("4K Ultra HD (2160p)", "4K", 2.5f, "Ultra HD (3840×2160)"),
    EXTREME_8K("8K Ultra Master (4320p)", "8K", 4.0f, "Extreme Master (7680×4320)")
}


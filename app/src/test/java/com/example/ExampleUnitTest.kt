package com.example

import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.StaffMember
import com.example.data.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testAttendanceRateCalculation() {
        val presentDays = 22
        val scheduledWorkingDays = 26
        val rate = ((presentDays.toFloat() / scheduledWorkingDays.toFloat()) * 100).toInt()
        assertEquals(84, rate)
    }

    @Test
    fun testStaffSearchFilter() {
        val staffList = listOf(
            StaffMember(fullName = "Aarav Sharma", jobTitle = "Waiter", department = "Service"),
            StaffMember(fullName = "Priya Karki", jobTitle = "Cashier", department = "Front Desk"),
            StaffMember(fullName = "Rohan Basnet", jobTitle = "Chef", department = "Kitchen")
        )

        val query = "priya"
        val filtered = staffList.filter {
            it.fullName.contains(query, ignoreCase = true) ||
                    it.jobTitle.contains(query, ignoreCase = true)
        }

        assertEquals(1, filtered.size)
        assertEquals("Priya Karki", filtered.first().fullName)
    }

    @Test
    fun testAttendanceStatusValues() {
        assertEquals("Present", AttendanceStatus.PRESENT.label)
        assertEquals("Absent", AttendanceStatus.ABSENT.label)
        assertEquals("On Leave", AttendanceStatus.ON_LEAVE.label)
        assertEquals("Late", AttendanceStatus.LATE.label)
    }

    @Test
    fun testUserRoleLabels() {
        assertEquals("Owner", UserRole.OWNER.label)
        assertEquals("Manager", UserRole.MANAGER.label)
        assertEquals("Supervisor", UserRole.SUPERVISOR.label)
    }
}

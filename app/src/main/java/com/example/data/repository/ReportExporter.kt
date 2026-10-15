package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.ExportFormat
import com.example.data.model.ExportResolution
import com.example.data.model.ReportType
import com.example.data.model.StaffMember
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExporter {

    fun generateAndShareReport(
        context: Context,
        reportType: ReportType,
        exportFormat: ExportFormat,
        periodText: String,
        orgName: String,
        staffList: List<StaffMember>,
        attendanceList: List<AttendanceRecord>,
        resolution: ExportResolution = ExportResolution.STANDARD
    ): File? {
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        // Filter attendance records by the selected date range if dates are extracted
        val filteredAttendance = filterAttendanceByPeriod(attendanceList, periodText)

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "StaffHub_${reportType.name.lowercase()}_${resolution.shortLabel}_$timestamp.${exportFormat.extension}"
        val file = File(reportsDir, fileName)

        try {
            when (exportFormat) {
                ExportFormat.PDF -> generatePdfReport(file, reportType, periodText, orgName, staffList, filteredAttendance, resolution)
                ExportFormat.PNG -> generatePngReport(file, reportType, periodText, orgName, staffList, filteredAttendance, resolution)
                ExportFormat.CSV -> generateCsvReport(file, reportType, periodText, orgName, staffList, filteredAttendance, resolution)
                ExportFormat.TXT -> generateTxtReport(file, reportType, periodText, orgName, staffList, filteredAttendance, resolution)
                ExportFormat.MARKDOWN -> generateMarkdownReport(file, reportType, periodText, orgName, staffList, filteredAttendance, resolution)
            }

            shareFile(context, file, exportFormat.mimeType, "StaffHub Report: ${reportType.title} ($periodText)")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun filterAttendanceByPeriod(records: List<AttendanceRecord>, periodText: String): List<AttendanceRecord> {
        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        val matches = dateRegex.findAll(periodText).map { it.value }.toList()
        if (matches.size >= 2) {
            val start = matches[0]
            val end = matches[1]
            return records.filter { it.date in start..end }
        } else if (matches.size == 1) {
            val singleDate = matches[0]
            return records.filter { it.date == singleDate }
        }
        return records
    }

    private fun shareFile(context: Context, file: File, mimeType: String, subject: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Generated via StaffHub - Workforce Management System.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share Report via")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun generatePdfReport(
        file: File,
        reportType: ReportType,
        periodText: String,
        orgName: String,
        staffList: List<StaffMember>,
        attendanceList: List<AttendanceRecord>,
        resolution: ExportResolution
    ) {
        val pdfDoc = PdfDocument()
        val scale = resolution.scaleFactor
        val baseWidth = 595
        val baseHeight = 842
        val pageWidth = (baseWidth * scale).toInt()
        val pageHeight = (baseHeight * scale).toInt()

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        canvas.save()
        canvas.scale(scale, scale)

        val paint = Paint().apply { isAntiAlias = true }

        // Header Background
        paint.color = AndroidColor.rgb(37, 99, 235) // StaffHub Blue
        canvas.drawRect(0f, 0f, 595f, 95f, paint)

        // Brand Logo Circle
        paint.color = AndroidColor.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("StaffHub", 36f, 44f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(reportType.title, 36f, 64f, paint)

        paint.textSize = 9.5f
        paint.color = AndroidColor.rgb(224, 242, 254)
        canvas.drawText("Resolution: ${resolution.resolutionTag}", 36f, 82f, paint)

        // Organization & Period (Right aligned)
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        paint.color = AndroidColor.WHITE
        canvas.drawText(orgName, 559f, 42f, paint)
        canvas.drawText("Period: $periodText", 559f, 62f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Summary Bar
        paint.color = AndroidColor.rgb(241, 245, 249)
        canvas.drawRoundRect(36f, 110f, 559f, 165f, 12f, 12f, paint)

        paint.color = AndroidColor.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                val present = attendanceList.count { it.status == AttendanceStatus.PRESENT }
                val absent = attendanceList.count { it.status == AttendanceStatus.ABSENT }
                val leave = attendanceList.count { it.status == AttendanceStatus.ON_LEAVE }
                val late = attendanceList.count { it.status == AttendanceStatus.LATE }
                canvas.drawText("Records: ${attendanceList.size}", 54f, 142f, paint)
                canvas.drawText("Present: $present", 160f, 142f, paint)
                canvas.drawText("Absent: $absent", 280f, 142f, paint)
                canvas.drawText("Late: $late", 380f, 142f, paint)
                canvas.drawText("Leave: $leave", 470f, 142f, paint)
            }
            ReportType.STAFF_DETAILS -> {
                val active = staffList.count { it.status == "Active" }
                canvas.drawText("Total Staff: ${staffList.size}", 54f, 142f, paint)
                canvas.drawText("Active: $active", 200f, 142f, paint)
                canvas.drawText("On Leave: ${staffList.size - active}", 360f, 142f, paint)
            }
            ReportType.DEPARTMENT_REPORT -> {
                val depts = staffList.groupBy { it.department }
                canvas.drawText("Departments: ${depts.size}", 54f, 142f, paint)
                canvas.drawText("Total Headcount: ${staffList.size}", 240f, 142f, paint)
            }
            ReportType.SALARY_REPORT -> {
                val totalDays = staffList.sumOf { it.presentDays }
                canvas.drawText("Total Payable Days: $totalDays", 54f, 142f, paint)
                canvas.drawText("Staff Count: ${staffList.size}", 300f, 142f, paint)
            }
        }

        // Table Header
        var y = 198f
        paint.color = AndroidColor.rgb(37, 99, 235)
        canvas.drawRect(36f, y - 18f, 559f, y + 8f, paint)

        paint.color = AndroidColor.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                canvas.drawText("DATE", 44f, y, paint)
                canvas.drawText("STAFF NAME", 130f, y, paint)
                canvas.drawText("SHIFT", 270f, y, paint)
                canvas.drawText("STATUS", 390f, y, paint)
                canvas.drawText("CHECK-IN", 480f, y, paint)
            }
            ReportType.STAFF_DETAILS -> {
                canvas.drawText("ID", 44f, y, paint)
                canvas.drawText("NAME", 100f, y, paint)
                canvas.drawText("ROLE", 230f, y, paint)
                canvas.drawText("DEPT", 340f, y, paint)
                canvas.drawText("PHONE", 430f, y, paint)
                canvas.drawText("STATUS", 505f, y, paint)
            }
            ReportType.DEPARTMENT_REPORT -> {
                canvas.drawText("DEPARTMENT", 44f, y, paint)
                canvas.drawText("HEAD STAFF", 200f, y, paint)
                canvas.drawText("STAFF COUNT", 340f, y, paint)
                canvas.drawText("ATTENDANCE %", 460f, y, paint)
            }
            ReportType.SALARY_REPORT -> {
                canvas.drawText("ID", 44f, y, paint)
                canvas.drawText("NAME", 100f, y, paint)
                canvas.drawText("DEPT", 230f, y, paint)
                canvas.drawText("PRESENT", 340f, y, paint)
                canvas.drawText("ABSENT", 410f, y, paint)
                canvas.drawText("EST. PAYABLE", 480f, y, paint)
            }
        }

        y += 24f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                attendanceList.take(24).forEachIndexed { index, record ->
                    if (index % 2 == 1) {
                        paint.color = AndroidColor.rgb(248, 250, 252)
                        canvas.drawRect(36f, y - 14f, 559f, y + 8f, paint)
                    }
                    paint.color = AndroidColor.rgb(15, 23, 42)
                    canvas.drawText(record.date, 44f, y, paint)
                    canvas.drawText(record.staffName, 130f, y, paint)
                    canvas.drawText(record.shiftName, 270f, y, paint)
                    canvas.drawText(record.status.label, 390f, y, paint)
                    canvas.drawText(record.checkInTime ?: "--", 480f, y, paint)
                    y += 22f
                }
            }
            ReportType.STAFF_DETAILS -> {
                staffList.take(24).forEachIndexed { index, staff ->
                    if (index % 2 == 1) {
                        paint.color = AndroidColor.rgb(248, 250, 252)
                        canvas.drawRect(36f, y - 14f, 559f, y + 8f, paint)
                    }
                    paint.color = AndroidColor.rgb(15, 23, 42)
                    canvas.drawText(staff.employeeId, 44f, y, paint)
                    canvas.drawText(staff.fullName, 100f, y, paint)
                    canvas.drawText(staff.jobTitle, 230f, y, paint)
                    canvas.drawText(staff.department, 340f, y, paint)
                    canvas.drawText(staff.phone, 430f, y, paint)
                    canvas.drawText(staff.status, 505f, y, paint)
                    y += 22f
                }
            }
            ReportType.DEPARTMENT_REPORT -> {
                val depts = staffList.groupBy { it.department }
                depts.entries.forEachIndexed { index, entry ->
                    if (index % 2 == 1) {
                        paint.color = AndroidColor.rgb(248, 250, 252)
                        canvas.drawRect(36f, y - 14f, 559f, y + 8f, paint)
                    }
                    val totalP = entry.value.sumOf { it.presentDays }
                    val totalDays = entry.value.sumOf { it.presentDays + it.absentDays }
                    val rate = if (totalDays > 0) (totalP * 100) / totalDays else 90

                    paint.color = AndroidColor.rgb(15, 23, 42)
                    canvas.drawText(entry.key, 44f, y, paint)
                    canvas.drawText(entry.value.firstOrNull()?.fullName ?: "N/A", 200f, y, paint)
                    canvas.drawText("${entry.value.size} Members", 340f, y, paint)
                    canvas.drawText("$rate%", 460f, y, paint)
                    y += 24f
                }
            }
            ReportType.SALARY_REPORT -> {
                staffList.take(24).forEachIndexed { index, staff ->
                    if (index % 2 == 1) {
                        paint.color = AndroidColor.rgb(248, 250, 252)
                        canvas.drawRect(36f, y - 14f, 559f, y + 8f, paint)
                    }
                    paint.color = AndroidColor.rgb(15, 23, 42)
                    canvas.drawText(staff.employeeId, 44f, y, paint)
                    canvas.drawText(staff.fullName, 100f, y, paint)
                    canvas.drawText(staff.department, 230f, y, paint)
                    canvas.drawText("${staff.presentDays} d", 340f, y, paint)
                    canvas.drawText("${staff.absentDays} d", 410f, y, paint)
                    canvas.drawText("${staff.presentDays * 8} hrs", 480f, y, paint)
                    y += 22f
                }
            }
        }

        // Footer
        paint.color = AndroidColor.rgb(100, 116, 139)
        paint.textSize = 8.5f
        canvas.drawText("Generated by StaffHub • Made with love by Rahul Shah • High-Resolution Export", 36f, 815f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${resolution.shortLabel} Output", 559f, 815f, paint)

        canvas.restore()
        pdfDoc.finishPage(page)

        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
    }

    private fun generatePngReport(
        file: File,
        reportType: ReportType,
        periodText: String,
        orgName: String,
        staffList: List<StaffMember>,
        attendanceList: List<AttendanceRecord>,
        resolution: ExportResolution
    ) {
        val width = when (resolution) {
            ExportResolution.STANDARD -> 1080
            ExportResolution.HIGH_2K -> 1440
            ExportResolution.ULTRA_4K -> 2160
            ExportResolution.EXTREME_8K -> 2880
        }
        val height = (width * 1.414f).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scale = width / 595f

        canvas.drawColor(AndroidColor.WHITE)
        canvas.save()
        canvas.scale(scale, scale)

        val paint = Paint().apply { isAntiAlias = true }

        // Top Banner
        paint.color = AndroidColor.rgb(37, 99, 235)
        canvas.drawRect(0f, 0f, 595f, 95f, paint)

        paint.color = AndroidColor.WHITE
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("StaffHub", 36f, 44f, paint)

        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("${reportType.title} • ${resolution.resolutionTag}", 36f, 68f, paint)

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        canvas.drawText(orgName, 559f, 42f, paint)
        canvas.drawText(periodText, 559f, 62f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Content
        var y = 160f
        paint.color = AndroidColor.rgb(15, 23, 42)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                canvas.drawText("ATTENDANCE LOGS (${attendanceList.size} Total)", 36f, y, paint)
                y += 24f
                paint.typeface = Typeface.DEFAULT
                attendanceList.take(22).forEach { att ->
                    canvas.drawText("${att.date}  •  ${att.staffName}  •  ${att.status.label} (${att.checkInTime ?: "--"})", 36f, y, paint)
                    y += 20f
                }
            }
            ReportType.STAFF_DETAILS -> {
                canvas.drawText("STAFF DIRECTORY (${staffList.size} Members)", 36f, y, paint)
                y += 24f
                paint.typeface = Typeface.DEFAULT
                staffList.take(22).forEach { s ->
                    canvas.drawText("${s.employeeId}  •  ${s.fullName}  •  ${s.jobTitle}  •  ${s.department}  •  ${s.phone}", 36f, y, paint)
                    y += 20f
                }
            }
            ReportType.DEPARTMENT_REPORT -> {
                canvas.drawText("DEPARTMENT BREAKDOWN", 36f, y, paint)
                y += 24f
                paint.typeface = Typeface.DEFAULT
                val depts = staffList.groupBy { it.department }
                depts.forEach { (dept, members) ->
                    canvas.drawText("$dept: ${members.size} Staff Members", 36f, y, paint)
                    y += 20f
                }
            }
            ReportType.SALARY_REPORT -> {
                canvas.drawText("WORKING HOURS & PAYROLL ESTIMATES", 36f, y, paint)
                y += 24f
                paint.typeface = Typeface.DEFAULT
                staffList.take(22).forEach { s ->
                    canvas.drawText("${s.employeeId}  •  ${s.fullName}  •  Present: ${s.presentDays}d  •  Payable Hours: ${s.presentDays * 8}h", 36f, y, paint)
                    y += 20f
                }
            }
        }

        // Footer
        paint.color = AndroidColor.rgb(100, 116, 139)
        paint.textSize = 8.5f
        canvas.drawText("StaffHub Infographic • Resolution: ${resolution.resolutionTag} • Rahul Shah", 36f, 815f, paint)

        canvas.restore()

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()
    }

    private fun generateCsvReport(
        file: File,
        reportType: ReportType,
        periodText: String,
        orgName: String,
        staffList: List<StaffMember>,
        attendanceList: List<AttendanceRecord>,
        resolution: ExportResolution
    ) {
        val sb = StringBuilder()
        sb.append("StaffHub Report - ${reportType.title}\n")
        sb.append("Organization,\"$orgName\"\n")
        sb.append("Period,\"$periodText\"\n")
        sb.append("Generated At,\"${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\"\n\n")

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                sb.append("Date,Staff ID,Staff Name,Role,Shift,Status,Check-In,Check-Out,Notes\n")
                attendanceList.forEach { a ->
                    sb.append("\"${a.date}\",\"${a.staffId}\",\"${a.staffName}\",\"${a.staffRole}\",\"${a.shiftName}\",\"${a.status.label}\",\"${a.checkInTime ?: ""}\",\"${a.checkOutTime ?: ""}\",\"${a.notes ?: ""}\"\n")
                }
            }
            ReportType.STAFF_DETAILS -> {
                sb.append("Employee ID,Full Name,Job Title,Role,Department,Branch,Employment Type,Phone,Email,Status\n")
                staffList.forEach { s ->
                    sb.append("\"${s.employeeId}\",\"${s.fullName}\",\"${s.jobTitle}\",\"${s.role.label}\",\"${s.department}\",\"${s.branch}\",\"${s.employmentType}\",\"${s.phone}\",\"${s.email}\",\"${s.status}\"\n")
                }
            }
            ReportType.DEPARTMENT_REPORT -> {
                sb.append("Department,Staff Count,Department Head\n")
                val depts = staffList.groupBy { it.department }
                depts.forEach { (dept, members) ->
                    sb.append("\"$dept\",${members.size},\"${members.firstOrNull()?.fullName ?: "N/A"}\"\n")
                }
            }
            ReportType.SALARY_REPORT -> {
                sb.append("Employee ID,Full Name,Department,Present Days,Absent Days,Leave Days,Total Hours\n")
                staffList.forEach { s ->
                    sb.append("\"${s.employeeId}\",\"${s.fullName}\",\"${s.department}\",${s.presentDays},${s.absentDays},${s.leaveDays},${s.presentDays * 8}\n")
                }
            }
        }

        file.writeText(sb.toString())
    }

    private fun generateTxtReport(
        file: File,
        reportType: ReportType,
        periodText: String,
        orgName: String,
        staffList: List<StaffMember>,
        attendanceList: List<AttendanceRecord>,
        resolution: ExportResolution
    ) {
        val sb = StringBuilder()
        sb.append("=".repeat(60)).append("\n")
        sb.append("STAFFHUB - ${reportType.title.uppercase()}\n")
        sb.append("=".repeat(60)).append("\n")
        sb.append("Organization: $orgName\n")
        sb.append("Period:       $periodText\n")
        sb.append("Generated:    ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
        sb.append("-".repeat(60)).append("\n\n")

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                sb.append(String.format("%-12s %-20s %-12s %-10s\n", "Date", "Staff Name", "Status", "Check-In"))
                sb.append("-".repeat(60)).append("\n")
                attendanceList.forEach { a ->
                    sb.append(String.format("%-12s %-20s %-12s %-10s\n", a.date, a.staffName.take(18), a.status.label, a.checkInTime ?: "--"))
                }
            }
            ReportType.STAFF_DETAILS -> {
                sb.append(String.format("%-10s %-20s %-16s %-12s\n", "ID", "Name", "Role", "Department"))
                sb.append("-".repeat(60)).append("\n")
                staffList.forEach { s ->
                    sb.append(String.format("%-10s %-20s %-16s %-12s\n", s.employeeId, s.fullName.take(18), s.jobTitle.take(15), s.department))
                }
            }
            ReportType.DEPARTMENT_REPORT -> {
                sb.append(String.format("%-25s %-15s\n", "Department", "Staff Count"))
                sb.append("-".repeat(60)).append("\n")
                staffList.groupBy { it.department }.forEach { (dept, members) ->
                    sb.append(String.format("%-25s %-15s\n", dept, "${members.size} staff"))
                }
            }
            ReportType.SALARY_REPORT -> {
                sb.append(String.format("%-10s %-20s %-10s %-12s\n", "ID", "Name", "Days", "Hours"))
                sb.append("-".repeat(60)).append("\n")
                staffList.forEach { s ->
                    sb.append(String.format("%-10s %-20s %-10s %-12s\n", s.employeeId, s.fullName.take(18), "${s.presentDays} d", "${s.presentDays * 8} hrs"))
                }
            }
        }

        sb.append("\n").append("=".repeat(60)).append("\n")
        sb.append("Made with love by Rahul Shah\n")
        file.writeText(sb.toString())
    }

    private fun generateMarkdownReport(
        file: File,
        reportType: ReportType,
        periodText: String,
        orgName: String,
        staffList: List<StaffMember>,
        attendanceList: List<AttendanceRecord>,
        resolution: ExportResolution
    ) {
        val sb = StringBuilder()
        sb.append("# 📊 StaffHub Report: ${reportType.title}\n\n")
        sb.append("- **Organization:** $orgName\n")
        sb.append("- **Period:** $periodText\n")
        sb.append("- **Generated At:** ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n\n")

        when (reportType) {
            ReportType.ATTENDANCE_SUMMARY -> {
                sb.append("### Attendance Logs\n\n")
                sb.append("| Date | Staff Name | Role | Status | Check-In |\n")
                sb.append("| :--- | :--- | :--- | :--- | :--- |\n")
                attendanceList.forEach { a ->
                    sb.append("| ${a.date} | ${a.staffName} | ${a.staffRole} | **${a.status.label}** | ${a.checkInTime ?: "--"} |\n")
                }
            }
            ReportType.STAFF_DETAILS -> {
                sb.append("### Staff Directory\n\n")
                sb.append("| ID | Name | Role | Department | Phone | Status |\n")
                sb.append("| :--- | :--- | :--- | :--- | :--- | :--- |\n")
                staffList.forEach { s ->
                    sb.append("| ${s.employeeId} | ${s.fullName} | ${s.jobTitle} | ${s.department} | ${s.phone} | ${s.status} |\n")
                }
            }
            ReportType.DEPARTMENT_REPORT -> {
                sb.append("### Department Breakdown\n\n")
                sb.append("| Department | Staff Count | Sample Members |\n")
                sb.append("| :--- | :--- | :--- |\n")
                staffList.groupBy { it.department }.forEach { (dept, members) ->
                    sb.append("| $dept | **${members.size}** | ${members.take(3).joinToString { it.fullName }} |\n")
                }
            }
            ReportType.SALARY_REPORT -> {
                sb.append("### Working Hours Summary\n\n")
                sb.append("| ID | Name | Department | Present Days | Payable Hours |\n")
                sb.append("| :--- | :--- | :--- | :--- | :--- |\n")
                staffList.forEach { s ->
                    sb.append("| ${s.employeeId} | ${s.fullName} | ${s.department} | ${s.presentDays} days | ${s.presentDays * 8} hrs |\n")
                }
            }
        }

        sb.append("\n---\n*Report generated via StaffHub — Made with love by Rahul Shah*\n")
        file.writeText(sb.toString())
    }
}

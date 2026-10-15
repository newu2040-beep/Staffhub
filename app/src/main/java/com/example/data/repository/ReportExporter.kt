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

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "StaffHub_${reportType.name.lowercase()}_${resolution.shortLabel}_$timestamp.${exportFormat.extension}"
        val file = File(reportsDir, fileName)

        try {
            when (exportFormat) {
                ExportFormat.PDF -> generatePdfReport(file, reportType, periodText, orgName, staffList, attendanceList, resolution)
                ExportFormat.PNG -> generatePngReport(file, reportType, periodText, orgName, staffList, attendanceList, resolution)
                ExportFormat.CSV -> generateCsvReport(file, reportType, periodText, orgName, staffList, attendanceList, resolution)
                ExportFormat.TXT -> generateTxtReport(file, reportType, periodText, orgName, staffList, attendanceList, resolution)
                ExportFormat.MARKDOWN -> generateMarkdownReport(file, reportType, periodText, orgName, staffList, attendanceList, resolution)
            }

            shareFile(context, file, exportFormat.mimeType, "StaffHub ${resolution.shortLabel} Report: ${reportType.title}")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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

        // Header Background with subtle gradient feel
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

        // Resolution Tag Badge (e.g. 4K Ultra HD or 8K Master)
        paint.textSize = 9.5f
        paint.color = AndroidColor.rgb(224, 242, 254) // Light cyan
        canvas.drawText("Resolution: ${resolution.resolutionTag}", 36f, 82f, paint)

        // Organization & Period (Right aligned)
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        paint.color = AndroidColor.WHITE
        canvas.drawText(orgName, 559f, 42f, paint)
        canvas.drawText("Period: $periodText", 559f, 62f, paint)
        paint.textAlign = Paint.Align.LEFT

        // Summary Bar
        paint.color = AndroidColor.rgb(241, 245, 249) // Slate 100
        canvas.drawRoundRect(36f, 110f, 559f, 165f, 12f, 12f, paint)

        paint.color = AndroidColor.rgb(30, 41, 59) // Slate 800
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Total Staff: ${staffList.size}", 54f, 142f, paint)

        val presentCount = staffList.sumOf { it.presentDays }
        val absentCount = staffList.sumOf { it.absentDays }
        val leaveCount = staffList.sumOf { it.leaveDays }
        canvas.drawText("Total Present Days: $presentCount", 160f, 142f, paint)
        canvas.drawText("Total Absent Days: $absentCount", 310f, 142f, paint)
        canvas.drawText("Total Leaves: $leaveCount", 440f, 142f, paint)

        // Table Header
        var y = 198f
        paint.color = AndroidColor.rgb(37, 99, 235)
        canvas.drawRect(36f, y - 18f, 559f, y + 8f, paint)

        paint.color = AndroidColor.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ID", 44f, y, paint)
        canvas.drawText("NAME", 90f, y, paint)
        canvas.drawText("ROLE / DEPT", 220f, y, paint)
        canvas.drawText("PRESENT", 360f, y, paint)
        canvas.drawText("ABSENT", 430f, y, paint)
        canvas.drawText("LEAVE", 495f, y, paint)

        y += 24f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f

        staffList.take(24).forEachIndexed { index, staff ->
            // Zebra row background
            if (index % 2 == 1) {
                paint.color = AndroidColor.rgb(248, 250, 252)
                canvas.drawRect(36f, y - 14f, 559f, y + 8f, paint)
            }

            paint.color = AndroidColor.rgb(15, 23, 42)
            canvas.drawText(staff.employeeId.ifEmpty { "EMP-${100 + index}" }, 44f, y, paint)
            canvas.drawText(staff.fullName, 90f, y, paint)
            canvas.drawText("${staff.jobTitle} (${staff.department})", 220f, y, paint)
            canvas.drawText("${staff.presentDays} d", 360f, y, paint)
            canvas.drawText("${staff.absentDays} d", 430f, y, paint)
            canvas.drawText("${staff.leaveDays} d", 495f, y, paint)

            y += 22f
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
        val height = (width * 1.414f).toInt() // Standard document aspect ratio

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

        // Summary Bar
        paint.color = AndroidColor.rgb(241, 245, 249)
        canvas.drawRoundRect(36f, 110f, 559f, 165f, 12f, 12f, paint)

        paint.color = AndroidColor.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Total Staff: ${staffList.size}", 54f, 142f, paint)

        val presentCount = staffList.sumOf { it.presentDays }
        val absentCount = staffList.sumOf { it.absentDays }
        val leaveCount = staffList.sumOf { it.leaveDays }
        canvas.drawText("Present: $presentCount", 200f, 142f, paint)
        canvas.drawText("Absent: $absentCount", 330f, 142f, paint)
        canvas.drawText("Leaves: $leaveCount", 450f, 142f, paint)

        // Table
        var y = 198f
        paint.color = AndroidColor.rgb(37, 99, 235)
        canvas.drawRect(36f, y - 18f, 559f, y + 8f, paint)

        paint.color = AndroidColor.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("ID", 44f, y, paint)
        canvas.drawText("NAME", 90f, y, paint)
        canvas.drawText("ROLE", 230f, y, paint)
        canvas.drawText("PRESENT", 360f, y, paint)
        canvas.drawText("ABSENT", 430f, y, paint)
        canvas.drawText("LEAVE", 495f, y, paint)

        y += 24f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f

        staffList.take(24).forEachIndexed { index, staff ->
            if (index % 2 == 1) {
                paint.color = AndroidColor.rgb(248, 250, 252)
                canvas.drawRect(36f, y - 14f, 559f, y + 8f, paint)
            }

            paint.color = AndroidColor.rgb(15, 23, 42)
            canvas.drawText(staff.employeeId.ifEmpty { "EMP-${100 + index}" }, 44f, y, paint)
            canvas.drawText(staff.fullName, 90f, y, paint)
            canvas.drawText(staff.jobTitle, 230f, y, paint)
            canvas.drawText("${staff.presentDays} d", 360f, y, paint)
            canvas.drawText("${staff.absentDays} d", 430f, y, paint)
            canvas.drawText("${staff.leaveDays} d", 495f, y, paint)
            y += 22f
        }

        // Footer
        paint.color = AndroidColor.rgb(100, 116, 139)
        paint.textSize = 8.5f
        canvas.drawText("StaffHub Visual Infographic • Resolution: ${resolution.resolutionTag} • Made with love by Rahul Shah", 36f, 815f, paint)

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
        sb.append("Export Resolution,\"${resolution.resolutionTag}\"\n")
        sb.append("Generated At,\"${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\"\n\n")

        sb.append("Employee ID,Full Name,Job Title,Role,Department,Branch,Present Days,Absent Days,Leave Days,Status\n")

        staffList.forEach { staff ->
            sb.append("\"${staff.employeeId}\",")
            sb.append("\"${staff.fullName.replace("\"", "\"\"")}\",")
            sb.append("\"${staff.jobTitle}\",")
            sb.append("\"${staff.role.label}\",")
            sb.append("\"${staff.department}\",")
            sb.append("\"${staff.branch}\",")
            sb.append("${staff.presentDays},")
            sb.append("${staff.absentDays},")
            sb.append("${staff.leaveDays},")
            sb.append("\"${staff.status}\"\n")
        }

        FileOutputStream(file).use { out ->
            out.write(sb.toString().toByteArray(Charsets.UTF_8))
        }
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
        sb.append("=================================================================\n")
        sb.append("                       STAFFHUB REPORT                           \n")
        sb.append("=================================================================\n")
        sb.append("Report Type  : ${reportType.title}\n")
        sb.append("Organization : $orgName\n")
        sb.append("Period       : $periodText\n")
        sb.append("Resolution   : ${resolution.resolutionTag}\n")
        sb.append("Generated On : ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n")
        sb.append("Total Staff  : ${staffList.size}\n")
        sb.append("-----------------------------------------------------------------\n\n")

        sb.append(String.format("%-10s %-20s %-16s %-8s %-8s %-8s\n", "ID", "NAME", "ROLE", "PRES", "ABS", "LEAVE"))
        sb.append("-----------------------------------------------------------------\n")

        staffList.forEach { s ->
            sb.append(
                String.format(
                    "%-10s %-20s %-16s %-8d %-8d %-8d\n",
                    s.employeeId,
                    if (s.fullName.length > 19) s.fullName.substring(0, 19) else s.fullName,
                    if (s.jobTitle.length > 15) s.jobTitle.substring(0, 15) else s.jobTitle,
                    s.presentDays,
                    s.absentDays,
                    s.leaveDays
                )
            )
        }

        sb.append("-----------------------------------------------------------------\n")
        sb.append("Generated with StaffHub Workforce Management • Made with love by Rahul Shah\n")

        FileOutputStream(file).use { out ->
            out.write(sb.toString().toByteArray(Charsets.UTF_8))
        }
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
        sb.append("# StaffHub Report: ${reportType.title}\n\n")
        sb.append("- **Organization:** $orgName\n")
        sb.append("- **Reporting Period:** $periodText\n")
        sb.append("- **Resolution Quality:** ${resolution.resolutionTag}\n")
        sb.append("- **Generated At:** ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
        sb.append("- **Total Active Staff:** ${staffList.size}\n\n")

        sb.append("### Summary Overview\n\n")
        val totalPresent = staffList.sumOf { it.presentDays }
        val totalAbsent = staffList.sumOf { it.absentDays }
        val totalLeave = staffList.sumOf { it.leaveDays }
        sb.append("| Metric | Count |\n")
        sb.append("|---|---|\n")
        sb.append("| Total Staff | ${staffList.size} |\n")
        sb.append("| Total Present Days | $totalPresent |\n")
        sb.append("| Total Absent Days | $totalAbsent |\n")
        sb.append("| Total Leave Days | $totalLeave |\n\n")

        sb.append("### Staff Details Table\n\n")
        sb.append("| ID | Employee Name | Job Title | Department | Present | Absent | Leave | Status |\n")
        sb.append("|---|---|---|---|---|---|---|---|\n")

        staffList.forEach { s ->
            sb.append("| ${s.employeeId} | ${s.fullName} | ${s.jobTitle} | ${s.department} | ${s.presentDays} | ${s.absentDays} | ${s.leaveDays} | ${s.status} |\n")
        }

        sb.append("\n---\n*Report generated by StaffHub Workforce Management • Made with love by Rahul Shah*\n")

        FileOutputStream(file).use { out ->
            out.write(sb.toString().toByteArray(Charsets.UTF_8))
        }
    }
}

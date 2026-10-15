package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserProfile
import com.example.ui.components.StaffAvatar
import com.example.ui.theme.StaffHubBlue

@Composable
fun OwnerProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(profile.displayName) }
    var roleTitle by remember { mutableStateOf(profile.customRoleTitle) }
    var gender by remember { mutableStateOf(if (profile.gender.equals("Female", ignoreCase = true)) "Female" else "Male") }
    var ageStr by remember { mutableStateOf(profile.age.toString()) }
    var phone by remember { mutableStateOf(profile.phone) }
    var email by remember { mutableStateOf(profile.email) }
    var orgName by remember { mutableStateOf(profile.organizationName) }
    var bio by remember { mutableStateOf(profile.bio) }
    var selectedAvatarUrl by remember { mutableStateOf(profile.avatarUrl ?: "") }

    // Preset avatar URLs for easy photo customization
    val avatarPresets = listOf(
        "" to "Initial Avatar",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" to "Executive A",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150" to "Executive B",
        "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150" to "Founder Female",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150" to "Founder Male"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 520.dp)
                .padding(vertical = 24.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Customize Owner Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Personalize your executive identity & details",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Avatar and Photo preview
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StaffAvatar(
                        name = if (name.isNotBlank()) name else "Owner",
                        imageUrl = selectedAvatarUrl.takeIf { it.isNotBlank() },
                        size = 80.dp,
                        backgroundColor = StaffHubBlue
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Select Avatar Photo Preset",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        avatarPresets.forEach { (url, label) ->
                            val isSelected = selectedAvatarUrl == url
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) StaffHubBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) StaffHubBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatarUrl = url },
                                contentAlignment = Alignment.Center
                            ) {
                                if (url.isBlank()) {
                                    Icon(Icons.Default.Face, contentDescription = label, tint = StaffHubBlue, modifier = Modifier.size(24.dp))
                                } else {
                                    StaffAvatar(name = label, imageUrl = url, size = 42.dp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Full Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = StaffHubBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Role Title
                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = { roleTitle = it },
                    label = { Text("Role / Designation") },
                    leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = StaffHubBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // GENDER (MALE AND FEMALE ONLY as requested)
                Text(
                    text = "Gender (Male & Female Only)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val genders = listOf("Male", "Female")
                    genders.forEach { g ->
                        val isSelected = gender == g
                        val icon = if (g == "Male") Icons.Default.Male else Icons.Default.Female
                        val activeColor = if (g == "Male") Color(0xFF2563EB) else Color(0xFFDB2777)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) activeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable { gender = g }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = g,
                                    tint = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = g,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Age and Phone in a Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ageStr,
                        onValueChange = { if (it.all { ch -> ch.isDigit() } && it.length <= 3) ageStr = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(0.4f)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = StaffHubBlue) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = StaffHubBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Organization Name
                OutlinedTextField(
                    value = orgName,
                    onValueChange = { orgName = it },
                    label = { Text("Organization Name") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = StaffHubBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Tagline") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        val parsedAge = ageStr.toIntOrNull() ?: profile.age
                        val updated = profile.copy(
                            displayName = name.trim().ifEmpty { profile.displayName },
                            customRoleTitle = roleTitle.trim().ifEmpty { "Owner" },
                            gender = gender,
                            age = parsedAge,
                            phone = phone.trim(),
                            email = email.trim(),
                            organizationName = orgName.trim().ifEmpty { profile.organizationName },
                            bio = bio.trim(),
                            avatarUrl = selectedAvatarUrl.takeIf { it.isNotBlank() }
                        )
                        onSave(updated)
                        Toast.makeText(context, "Owner Profile Saved & Synced", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StaffHubBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save & Sync Profile",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

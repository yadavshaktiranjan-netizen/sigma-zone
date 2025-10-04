package com.sigma.zone.screens

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.sigma.zone.model.Student
import java.io.File
import java.io.FileOutputStream

@Composable
fun DisplayScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }

    LaunchedEffect(refreshTrigger) {
        isLoading = true
        db.collection("user_courses")
            .get()
            .addOnSuccessListener { documents ->
                students = documents.map {
                    Student(
                        id = it.id,
                        name = it.getString("name") ?: "",
                        email = it.getString("email") ?: "",
                        mobile = it.getString("mobile") ?: "",
                        studentClass = it.getString("class") ?: "",
                        course = it.getString("course") ?: "",
                        schoolCollege = it.getString("school_college") ?: "",
                        medium = it.getString("medium") ?: "",
                        board = it.getString("board") ?: "",
                        courseType = it.getString("course_type") ?: ""
                    )
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                errorMessage = e.message
                isLoading = false
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFFCE4EC))
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Text("Registered Students", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { downloadCSV(students) }) {
                Text("Download CSV")
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red)
            } else {
                LazyColumn {
                    items(students) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(student.name, fontWeight = FontWeight.Bold)
                                    Text("${student.courseType} - ${student.course}")
                                    Text("Class: ${student.studentClass}, Mobile: ${student.mobile}")
                                }
                                Row {
                                    Text(
                                        "Edit",
                                        color = Color.Blue,
                                        modifier = Modifier
                                            .padding(end = 16.dp)
                                            .clickable {
                                                navController.currentBackStackEntry?.savedStateHandle?.set("editStudent", student)
                                                navController.navigate("course_form")
                                            }
                                    )
                                    Text(
                                        "Delete",
                                        color = Color.Red,
                                        modifier = Modifier.clickable {
                                            db.collection("user_courses").document(student.id)
                                                .delete()
                                                .addOnSuccessListener { refreshTrigger++ }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun downloadCSV(students: List<Student>) {
    if (students.isEmpty()) return
    val csvHeader = "Name,Email,Mobile,Class,Course,School/College,Medium,Board,Course Type\n"
    val csvBody = students.joinToString("\n") {
        "${it.name},${it.email},${it.mobile},${it.studentClass},${it.course},${it.schoolCollege},${it.medium},${it.board},${it.courseType}"
    }
    val csv = csvHeader + csvBody
    val fileName = "students.csv"
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(path, fileName)
    try {
        FileOutputStream(file).use { it.write(csv.toByteArray()) }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

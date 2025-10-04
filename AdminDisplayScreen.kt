package com.sigma.zone.screens

import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.sigma.zone.model.Student
import java.io.File
import java.io.FileOutputStream
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDisplayScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var students by remember { mutableStateOf(listOf<Student>()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf("") }
    var selectedBoard by remember { mutableStateOf("") }

    val classOptions = listOf("", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    val courseOptions = listOf("", "JEE", "NEET", "Foundation", "NDA", "Airforce", "Other")
    val boardOptions = listOf("", "CBSE", "ICSE", "Bihar Board", "State Board", "Other")

    LaunchedEffect(Unit) {
        db.collection("user_courses").get().addOnSuccessListener { docs ->
            students = docs.map {
                Student(
                    id = it.id,
                    name = it.getString("name") ?: "",
                    email = it.getString("email") ?: "",
                    mobile = it.getString("mobile") ?: "",
                    studentClass = it.getString("class") ?: "",
                    course = it.getString("course") ?: "",
                    courseType = it.getString("course_type") ?: "",
                    schoolCollege = it.getString("school_college") ?: "",
                    medium = it.getString("medium") ?: "",
                    board = it.getString("board") ?: ""
                )
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Panel - Students") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("course_form") },
                containerColor = Color(0xFF4285F4)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Student", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(12.dp)) {
            // Filtering dropdowns
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterDropdown(label = "Class", options = classOptions, selected = selectedClass, onSelect = { selectedClass = it })
                FilterDropdown(label = "Course", options = courseOptions, selected = selectedCourse, onSelect = { selectedCourse = it })
                FilterDropdown(label = "Board", options = boardOptions, selected = selectedBoard, onSelect = { selectedBoard = it })
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name / mobile / class") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { downloadCSV(students) }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Download CSV")
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(students.filter {
                    (selectedClass.isBlank() || it.studentClass == selectedClass) &&
                    (selectedCourse.isBlank() || it.course == selectedCourse) &&
                    (selectedBoard.isBlank() || it.board == selectedBoard) &&
                    (it.name.contains(searchQuery, true) ||
                     it.mobile.contains(searchQuery, true) ||
                     it.studentClass.contains(searchQuery, true))
                }) { student ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Name: ${student.name}")
                            Text("Class: ${student.studentClass} - Course: ${student.courseType} ${student.course}")
                            Text("Mobile: ${student.mobile}")
                            Text("Medium: ${student.medium}, Board: ${student.board}")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.width(120.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(if (option.isBlank()) "All" else option) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}

private fun downloadCSV(students: List<Student>) {
    if (students.isEmpty()) return
    val csvHeader = "Name,Email,Mobile,Class,Course,CourseType,School,Medium,Board\n"
    val csvBody = students.joinToString("\n") {
        "${it.name},${it.email},${it.mobile},${it.studentClass},${it.course},${it.courseType},${it.schoolCollege},${it.medium},${it.board}"
    }
    val csv = csvHeader + csvBody
    val fileName = "students.csv"
    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(path, fileName)
    FileOutputStream(file).use { it.write(csv.toByteArray()) }
}

package com.sigma.zone.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.sigma.zone.model.Student
import kotlinx.coroutines.launch

// -------------------- COURSE FORM SCREEN --------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormScreen(
    navController: NavController,
    courseType: String,
    student: Student? = null
) {
    // Prefill fields if editing
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var mobile by remember { mutableStateOf(student?.mobile ?: "") }
    var studentClass by remember { mutableStateOf(student?.studentClass ?: "") }
    var selectedCourse by remember { mutableStateOf(student?.course ?: "") }
    var schoolCollege by remember { mutableStateOf(student?.schoolCollege ?: "") }
    var medium by remember { mutableStateOf(student?.medium ?: "") }
    var board by remember { mutableStateOf(student?.board ?: "") }

    // Validation errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var classError by remember { mutableStateOf<String?>(null) }
    var courseError by remember { mutableStateOf<String?>(null) }
    var schoolError by remember { mutableStateOf<String?>(null) }
    var boardError by remember { mutableStateOf<String?>(null) }
    var mediumError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val classOptions = listOf("4", "5", "6", "7", "8", "9", "10", "11", "12")
    val courseOptions = listOf(
        "JEE", "NEET", "Foundation", "Commerce", "Arts",
        "Polytechnic", "Paramedical", "NDA", "Competitive"
    )
    val boardOptions = listOf("CBSE", "ICSE", "Bihar Board", "Other State Board")
    val mediumOptions = listOf("English", "Hindi", "Bilingual")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEBEE), Color(0xFFE3F2FD))
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (student != null) "Edit ${student.courseType}" else "Join $courseType",
                            color = Color(0xFF800000), // Maroon
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFFB71C1C), // Maroon background
                        contentColor = Color.White        // White text
                    )
                }
            }
        ) { padding ->
            // Scrollable form
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(WindowInsets.ime.asPaddingValues()), // keyboard safe
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null
                    )
                    nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = emailError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { if (it.length <= 10) mobile = it },
                        label = { Text("Mobile Number") },
                        isError = mobileError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    mobileError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    OutlinedTextField(
                        value = schoolCollege,
                        onValueChange = { schoolCollege = it },
                        label = { Text("School / College Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = schoolError != null
                    )
                    schoolError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    DropdownSelector(
                        label = "Select Class",
                        options = classOptions,
                        selectedOption = studentClass,
                        onOptionSelected = { studentClass = it }
                    )
                    classError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    DropdownSelector(
                        label = "Select Course",
                        options = courseOptions,
                        selectedOption = selectedCourse,
                        onOptionSelected = { selectedCourse = it }
                    )
                    courseError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    DropdownSelector(
                        label = "Medium",
                        options = mediumOptions,
                        selectedOption = medium,
                        onOptionSelected = { medium = it }
                    )
                    mediumError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    DropdownSelector(
                        label = "Select Board",
                        options = boardOptions,
                        selectedOption = board,
                        onOptionSelected = { board = it }
                    )
                    boardError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
                item {
                    errorMessage?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    // Advanced styled button row, scrollable, just below Select Board
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF388E3C), Color(0xFFD32F2F))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                // Reset errors
                                nameError = null; emailError = null; mobileError = null
                                classError = null; courseError = null; schoolError = null
                                mediumError = null; boardError = null

                                // Validate fields
                                when {
                                    name.isBlank() -> nameError = "Name cannot be empty"
                                    email.isBlank() || !email.contains("@") -> emailError = "Enter a valid email"
                                    mobile.length != 10 -> mobileError = "Enter 10 digit mobile number"
                                    studentClass.isEmpty() -> classError = "Select a class"
                                    selectedCourse.isEmpty() -> courseError = "Select a course"
                                    schoolCollege.isBlank() -> schoolError = "School/College cannot be empty"
                                    medium.isEmpty() -> mediumError = "Select a medium"
                                    board.isEmpty() -> boardError = "Select a board"
                                    else -> showDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF388E3C), // Green
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(8.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(if (student != null) "Update" else "Submit")
                            }
                        }
                        OutlinedButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1976D2) // Blue text
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF1976D2), Color(0xFF64B5F6)))
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(8.dp)
                        ) {
                            Text("Clear")
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp)) // Extra space for navigation bar
                }
            }
        }

        // -------------------- CONFIRM CLEAR DIALOG --------------------
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        name = ""
                        email = ""
                        mobile = ""
                        studentClass = ""
                        selectedCourse = ""
                        schoolCollege = ""
                        medium = ""
                        board = ""
                        nameError = null; emailError = null; mobileError = null
                        classError = null; courseError = null; schoolError = null
                        mediumError = null; boardError = null
                        errorMessage = null
                        showClearDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Form cleared successfully")
                        }
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
                },
                title = { Text("Clear Form?") },
                text = { Text("Are you sure you want to clear all fields?") }
            )
        }

        // Snackbar for error messages (will also use the custom style)
        LaunchedEffect(errorMessage) {
            errorMessage?.let {
                snackbarHostState.showSnackbar(it)
                errorMessage = null
            }
        }
    }

    // -------------------- CONFIRMATION DIALOG --------------------
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    isLoading = true
                    val db = FirebaseFirestore.getInstance()
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "mobile" to mobile,
                        "class" to studentClass,
                        "course" to selectedCourse,
                        "school_college" to schoolCollege,
                        "medium" to medium,
                        "board" to board,
                        "course_type" to courseType,
                        "timestamp" to System.currentTimeMillis()
                    )
                    if (student != null && student.id.isNotBlank()) {
                        // UPDATE EXISTING STUDENT
                        db.collection("user_courses")
                            .document(student.id)
                            .set(userData)
                            .addOnSuccessListener {
                                isLoading = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Welcome to join SIGMA CLASSES")
                                }
                                navController.popBackStack()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                errorMessage = "Error: ${e.message}"
                            }
                    } else {
                        // ADD NEW STUDENT (check duplicate mobile)
                        db.collection("user_courses")
                            .whereEqualTo("mobile", mobile)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (documents.isEmpty) {
                                    db.collection("user_courses")
                                        .add(userData)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Welcome to join SIGMA CLASSES")
                                            }
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "studentData",
                                                userData
                                            )
                                            navController.navigate("thank_you")
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            errorMessage = "Error: ${e.message}"
                                        }
                                } else {
                                    isLoading = false
                                    errorMessage = "This mobile number is already registered"
                                }
                            }
                            .addOnFailureListener { e -> // Added listener for the get() query itself
                                isLoading = false
                                errorMessage = "Error checking mobile: ${e.message}"
                            }
                    }
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
            title = { Text(if (student != null) "Confirm Update" else "Confirm Submission") },
            text = { Text(if (student != null) "Update this student?" else "Do you want to submit your details?") }
        )
    }
}

// -------------------- DROPDOWN SELECTOR --------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Changed from .width(IntrinsicSize.Min) to fillMaxWidth
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// -------------------- THANK YOU SCREEN --------------------
@Composable
fun ThankYouScreen(navController: NavController) {
    val studentData = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<HashMap<String, Any>>("studentData")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEBEE), Color(0xFFE3F2FD))
                )
            )
    ) {
        val scale = remember { Animatable(0.8f) }
        LaunchedEffect(Unit) { scale.animateTo(1f, animationSpec = tween(600)) }
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .scale(scale.value)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Light pink background for the card
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .widthIn(max = 420.dp), // Max width for larger screens
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Thank You!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C) // Maroon text
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your registration is successful",
                    fontSize = 18.sp,
                    color = Color.Gray // Subdued text color
                )
                Spacer(modifier = Modifier.height(20.dp))

                studentData?.let {
                    Card(
                        shape = RoundedCornerShape(16.dp), // Rounded corners for inner card
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)), // Light yellow for details
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            it.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = key.replace("_", " ").replaceFirstChar { char ->
                                            if (char.isLowerCase()) char.titlecase() else char.toString()
                                        }, // Nicer formatting for keys
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFB71C1C) // Maroon text for keys
                                    )
                                    Text(
                                        text = value.toString(),
                                        fontWeight = FontWeight.Bold // Bold for values
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)) // Maroon button
                ) {
                    Text("Go Back", color = Color.White)
                }
            }
        }
    }
}

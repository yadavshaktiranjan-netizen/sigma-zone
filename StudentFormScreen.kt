import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController
import com.sigma.zone.model.Student
import androidx.compose.ui.zIndex
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(
    navController: NavController,
    student: Student? = navController.currentBackStackEntry?.savedStateHandle?.get<Student>("editStudent")
) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // State variables (prefill if editing)
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var mobile by remember { mutableStateOf(student?.mobile ?: "") }
    var studentClass by remember { mutableStateOf(student?.studentClass ?: "") }
    var course by remember { mutableStateOf(student?.course ?: "") }
    var courseType by remember { mutableStateOf(student?.courseType ?: "") }
    var schoolCollege by remember { mutableStateOf(student?.schoolCollege ?: "") }
    var medium by remember { mutableStateOf(student?.medium ?: "") }
    var board by remember { mutableStateOf(student?.board ?: "") }
    var notes by remember { mutableStateOf("") }

    // Validation errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var classError by remember { mutableStateOf<String?>(null) }
    var courseError by remember { mutableStateOf<String?>(null) }
    var courseTypeError by remember { mutableStateOf<String?>(null) }
    var schoolError by remember { mutableStateOf<String?>(null) }
    var mediumError by remember { mutableStateOf<String?>(null) }
    var boardError by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Helper: Validate all fields
    fun validate(): Boolean {
        nameError = if (name.isBlank()) "Name required" else null
        emailError = if (email.isBlank() || !email.contains("@")) "Valid email required" else null
        mobileError = if (mobile.length != 10) "10 digit mobile required" else null
        classError = if (studentClass.isBlank()) "Class required" else null
        courseError = if (course.isBlank()) "Course required" else null
        courseTypeError = if (courseType.isBlank()) "Course type required" else null
        schoolError = if (schoolCollege.isBlank()) "School/College required" else null
        mediumError = if (medium.isBlank()) "Medium required" else null
        boardError = if (board.isBlank()) "Board required" else null
        return listOf(nameError, emailError, mobileError, classError, courseError, courseTypeError, schoolError, mediumError, boardError).all { it == null }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp), // Add bottom padding for buttons
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), isError = nameError != null)
            nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), isError = emailError != null)
            emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            OutlinedTextField(value = mobile, onValueChange = { if (it.length <= 10) mobile = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth(), isError = mobileError != null)
            mobileError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            DropdownField("Select Class", listOf("4","5","6","7","8","9","10","11","12"), studentClass) { studentClass = it }
            classError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            DropdownField("Select Course", listOf("JEE","NEET","Foundation","NDA","Airforce","Other"), course) { course = it }
            courseError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            DropdownField("Course Type", listOf("Regular","Crash","Online","Offline"), courseType) { courseType = it }
            courseTypeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(value = schoolCollege, onValueChange = { schoolCollege = it }, label = { Text("School / College Name") }, modifier = Modifier.fillMaxWidth(), isError = schoolError != null)
            schoolError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            DropdownField("Medium", listOf("English","Hindi","Bilingual"), medium) { medium = it }
            mediumError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            DropdownField("Board", listOf("CBSE","ICSE","Bihar Board","State Board","Other"), board) { board = it }
            boardError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Extra Notes (Optional)") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
        // Buttons always visible at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    name = ""; email = ""; mobile = ""; studentClass = ""
                    course = ""; courseType = ""; schoolCollege = ""
                    medium = ""; board = ""; notes = ""
                    nameError = null; emailError = null; mobileError = null; classError = null; courseError = null; courseTypeError = null; schoolError = null; mediumError = null; boardError = null
                    errorMessage = null
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1976D2) // Blue color
                ),
                border = BorderStroke(1.dp, Color(0xFF1976D2)),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("Clear")
            }
            Button(
                onClick = {
                    if (validate()) {
                        showDialog = true
                    } else {
                        errorMessage = "Please fix errors above"
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.large,
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text(if (student != null) "Update" else "Submit")
            }
        }

        // Confirmation Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        isLoading = true
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val userId = user.uid
                            val userData = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "mobile" to mobile,
                                "class" to studentClass,
                                "course" to course,
                                "course_type" to courseType,
                                "school_college" to schoolCollege,
                                "medium" to medium,
                                "board" to board,
                                "notes" to notes,
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("users")
                                .document(userId)
                                .collection("profile")
                                .document("data")
                                .set(userData)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "✅ User data saved successfully!", Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    errorMessage = "❌ Error saving data: ${e.message}"
                                }
                        } else {
                            isLoading = false
                            errorMessage = "❌ User not logged in! Please sign in to save your data."
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}

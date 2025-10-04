package com.sigma.zone.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Subject(val id: String, val name: String, val icon: String, val chapters: List<Chapter>)
data class Chapter(val id: String, val title: String, val notes: String, val videos: List<Video>, val tests: List<Test>)
data class Video(val id: String, val title: String, val url: String)
data class Test(val id: String, val title: String)

val subjects = listOf(
    Subject("math", "Mathematics", "calculate", listOf(
        Chapter("algebra", "Algebra", "Notes for Algebra...", listOf(Video("v1", "Intro to Algebra", "url")), listOf(Test("t1", "Algebra Basics Quiz"))),
        Chapter("geometry", "Geometry", "Notes for Geometry...", listOf(Video("v2", "Intro to Geometry", "url")), listOf(Test("t2", "Geometry Basics Quiz")))
    )),
    Subject("phy", "Physics", "science", listOf(
        Chapter("mechanics", "Mechanics", "Notes for Mechanics...", listOf(Video("v3", "Intro to Mechanics", "url")), listOf(Test("t3", "Mechanics Basics Quiz")))
    )),
    Subject("chem", "Chemistry", "science", listOf(
        Chapter("organic", "Organic Chemistry", "Notes for Organic Chemistry...", listOf(Video("v4", "Intro to Organic Chemistry", "url")), listOf(Test("t4", "Organic Chemistry Basics Quiz")))
    )),
    Subject("bio", "Biology", "public", listOf(
        Chapter("cells", "Cells", "Notes for Cells...", listOf(Video("v5", "The Cell", "url")), listOf(Test("t5", "Cell Quiz")))
    )),
    Subject("eng", "English", "book", listOf(
        Chapter("grammar", "Grammar", "Notes on Grammar...", listOf(Video("v6", "Parts of Speech", "url")), listOf(Test("t6", "Grammar Test")))
    ))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavController) {
    // navController is available for navigation, but currently not used in this screen.
    // If navigation is needed, use navController.navigate("route") as appropriate.
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedChapter by remember { mutableStateOf<Chapter?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        when {
            selectedChapter != null -> ChapterContentScreen(chapter = selectedChapter!!, onBack = {
                selectedChapter = null
            })
            selectedSubject != null -> ChapterListScreen(subject = selectedSubject!!, onSelect = { chapter ->
                selectedChapter = chapter
            }, onBack = { selectedSubject = null })
            else -> SubjectListScreen(subjects) { subject ->
                selectedSubject = subject
            }
        }
    }
}

fun getIconForSubject(iconName: String): ImageVector {
    return when (iconName) {
        "science" -> Icons.Default.Science
        "calculate" -> Icons.Default.Calculate
        "book" -> Icons.Default.Book
        "public" -> Icons.Default.Public
        else -> Icons.Default.Book
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectListScreen(subjects: List<Subject>, onSelect: (Subject) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Subject", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(subjects) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { onSelect(subject) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(getIconForSubject(subject.icon), contentDescription = subject.name, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(subject.name, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListScreen(subject: Subject, onSelect: (Chapter) -> Unit, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subject.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(subject.chapters) { chapter ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .clickable { onSelect(chapter) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = chapter.title, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(chapter.title, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterContentScreen(chapter: Chapter, onBack: () -> Unit) {
    var showNotes by remember { mutableStateOf(true) }
    var showVideos by remember { mutableStateOf(false) }
    var showTests by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chapter.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = showNotes,
                    onClick = {
                        showNotes = true; showVideos = false; showTests = false
                    },
                    label = { Text("Notes") },
                    leadingIcon = { Icon(Icons.Filled.Book, contentDescription = null) }
                )
                FilterChip(
                    selected = showVideos,
                    onClick = {
                        showNotes = false; showVideos = true; showTests = false
                    },
                    label = { Text("Videos") },
                    leadingIcon = { Icon(Icons.Filled.PlayCircle, contentDescription = null) }
                )
                FilterChip(
                    selected = showTests,
                    onClick = {
                        showNotes = false; showVideos = false; showTests = true
                    },
                    label = { Text("Tests") },
                    leadingIcon = { Icon(Icons.Filled.Quiz, contentDescription = null) }
                )
            }
            Spacer(Modifier.height(20.dp))
            AnimatedVisibility(visible = showNotes) {
                Column {
                    Text("📘 Notes:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(chapter.notes, modifier = Modifier.padding(8.dp), fontSize = 16.sp)
                }
            }
            AnimatedVisibility(visible = showVideos) {
                Column {
                    Text("🎥 Videos:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    chapter.videos.forEach { video ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(10.dp))
                                Text(video.title, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = showTests) {
                Column {
                    Text("📝 Tests:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    chapter.tests.forEach { test ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Icon(Icons.Filled.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(10.dp))
                                Text(test.title, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

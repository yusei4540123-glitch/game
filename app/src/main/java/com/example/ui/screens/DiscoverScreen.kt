package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Experience
import com.example.ui.components.BloxHaptics
import com.example.ui.theme.BloxBorder
import com.example.ui.theme.BloxDarkBackground
import com.example.ui.theme.BloxGreenLight
import com.example.ui.theme.BloxGreenRobux
import com.example.ui.theme.BloxSurface
import com.example.ui.theme.BloxSurfaceElevated
import com.example.ui.theme.BloxTextPrimary
import com.example.ui.theme.BloxTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    experiences: List<Experience>,
    onPlayExperience: (Experience) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var detailExperience by remember { mutableStateOf<Experience?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf("All", "Obby", "Simulator", "Tycoon", "Survival", "Roleplay", "Studio")

    val filteredExperiences = experiences.filter { exp ->
        val matchesCategory = (selectedCategory == "All") || exp.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() ||
                exp.title.contains(searchQuery, ignoreCase = true) ||
                exp.creator.contains(searchQuery, ignoreCase = true) ||
                exp.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloxDarkBackground)
    ) {
        // Top Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            Text(
                text = "Discover Experiences",
                color = BloxTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search experiences, games, creators...", color = BloxTextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = BloxTextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = BloxTextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BloxGreenRobux,
                    unfocusedBorderColor = BloxBorder,
                    focusedTextColor = BloxTextPrimary,
                    unfocusedTextColor = BloxTextPrimary,
                    focusedContainerColor = BloxSurface,
                    unfocusedContainerColor = BloxSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) BloxGreenRobux else BloxSurface)
                        .border(1.dp, if (isSelected) BloxGreenRobux else BloxBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedCategory = cat
                            BloxHaptics.playClick(context)
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else BloxTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Experiences List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExperiences) { exp ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = BloxSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            detailExperience = exp
                            BloxHaptics.playClick(context)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(
                                    id = if (exp.category == "Obby" || exp.category == "Studio") R.drawable.img_obby_banner_1790278303257 else R.drawable.img_blox_universe_1790278319122
                                ),
                                contentDescription = exp.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exp.title,
                                color = BloxTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "By ${exp.creator} • ${exp.category}",
                                color = BloxTextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👍 ${exp.ratingPercent}%", color = BloxGreenLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("👥 ${exp.activePlayers} Playing", color = BloxTextSecondary, fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                BloxHaptics.playClick(context)
                                onPlayExperience(exp)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Play", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Experience Detail Modal Sheet
    if (detailExperience != null) {
        val exp = detailExperience!!
        ModalBottomSheet(
            onDismissRequest = { detailExperience = null },
            sheetState = detailSheetState,
            containerColor = BloxDarkBackground,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Image(
                        painter = painterResource(
                            id = if (exp.category == "Obby" || exp.category == "Studio") R.drawable.img_obby_banner_1790278303257 else R.drawable.img_blox_universe_1790278319122
                        ),
                        contentDescription = exp.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = exp.title,
                    color = BloxTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Developer: ${exp.creator} • Genre: ${exp.category}",
                    color = BloxTextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = exp.description,
                    color = BloxTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Active", color = BloxTextSecondary, fontSize = 11.sp)
                        Text("${exp.activePlayers}", color = BloxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Visits", color = BloxTextSecondary, fontSize = 11.sp)
                        Text(exp.totalVisits, color = BloxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rating", color = BloxTextSecondary, fontSize = 11.sp)
                        Text("${exp.ratingPercent}%", color = BloxGreenLight, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val toPlay = detailExperience
                        detailExperience = null
                        if (toPlay != null) {
                            onPlayExperience(toPlay)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("JOIN EXPERIENCE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

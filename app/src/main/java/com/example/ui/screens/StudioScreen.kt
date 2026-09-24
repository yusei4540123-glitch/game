package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Experience
import com.example.data.model.UserProfile
import com.example.ui.components.BloxHaptics
import com.example.ui.theme.BloxBorder
import com.example.ui.theme.BloxDarkBackground
import com.example.ui.theme.BloxGold
import com.example.ui.theme.BloxGreenLight
import com.example.ui.theme.BloxGreenRobux
import com.example.ui.theme.BloxRedRoblox
import com.example.ui.theme.BloxSurface
import com.example.ui.theme.BloxSurfaceElevated
import com.example.ui.theme.BloxTextPrimary
import com.example.ui.theme.BloxTextSecondary

data class StudioBlock(
    val col: Int,
    val row: Int,
    val type: PlatformType
)

@Composable
fun StudioScreen(
    userProfile: UserProfile,
    userCreations: List<Experience>,
    onTestPlay: (title: String, customMapData: String) -> Unit,
    onPublish: (Experience) -> Unit,
    onDeleteCreation: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Level Builder, 1: My Creations

    // Builder State
    var mapTitle by remember { mutableStateOf("Sky Tower Obby") }
    var selectedTool by remember { mutableStateOf(PlatformType.SOLID) }

    // Grid blocks (columns 0..6, rows 0..9)
    val placedBlocks = remember {
        mutableStateListOf(
            StudioBlock(1, 8, PlatformType.CHECKPOINT),
            StudioBlock(2, 8, PlatformType.SOLID),
            StudioBlock(3, 8, PlatformType.SOLID),
            StudioBlock(4, 7, PlatformType.SOLID),
            StudioBlock(2, 6, PlatformType.LAVA),
            StudioBlock(3, 5, PlatformType.BOUNCE),
            StudioBlock(5, 4, PlatformType.SOLID),
            StudioBlock(3, 2, PlatformType.CHECKPOINT),
            StudioBlock(3, 1, PlatformType.TROPHY)
        )
    }

    fun serializeMap(): String {
        return placedBlocks.joinToString("|") { block ->
            val px = block.col * 50f + 20f
            val py = block.row * 65f + 100f
            val pw = if (block.type == PlatformType.CHECKPOINT || block.type == PlatformType.TROPHY) 50f else 60f
            val ph = 20f
            "$px;$py;$pw;$ph;${block.type.name}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloxDarkBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Studio",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Blox Studio",
                color = BloxTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BloxSurface,
            contentColor = BloxTextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF00E5FF)
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Map Builder", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Places (${userCreations.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            // Level Builder View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Map Name input
                OutlinedTextField(
                    value = mapTitle,
                    onValueChange = { mapTitle = it },
                    label = { Text("Creation Title", color = BloxTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = BloxBorder,
                        focusedTextColor = BloxTextPrimary,
                        unfocusedTextColor = BloxTextPrimary,
                        focusedContainerColor = BloxSurface,
                        unfocusedContainerColor = BloxSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Block Type Palette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaletteItem("Solid", PlatformType.SOLID, Color(0xFF2E3440), selectedTool == PlatformType.SOLID) {
                        selectedTool = PlatformType.SOLID
                        BloxHaptics.playClick(context)
                    }
                    PaletteItem("Lava", PlatformType.LAVA, BloxRedRoblox, selectedTool == PlatformType.LAVA) {
                        selectedTool = PlatformType.LAVA
                        BloxHaptics.playClick(context)
                    }
                    PaletteItem("Bounce", PlatformType.BOUNCE, BloxGold, selectedTool == PlatformType.BOUNCE) {
                        selectedTool = PlatformType.BOUNCE
                        BloxHaptics.playClick(context)
                    }
                    PaletteItem("Flag", PlatformType.CHECKPOINT, BloxGreenLight, selectedTool == PlatformType.CHECKPOINT) {
                        selectedTool = PlatformType.CHECKPOINT
                        BloxHaptics.playClick(context)
                    }
                    PaletteItem("Trophy", PlatformType.TROPHY, Color(0xFFFFD700), selectedTool == PlatformType.TROPHY) {
                        selectedTool = PlatformType.TROPHY
                        BloxHaptics.playClick(context)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Grid Editor Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF14171A))
                        .border(1.dp, BloxBorder, RoundedCornerShape(12.dp))
                        .pointerInput(selectedTool) {
                            detectTapGestures { offset ->
                                val gridCols = 7
                                val gridRows = 10
                                val cellW = size.width / gridCols
                                val cellH = size.height / gridRows
                                val col = (offset.x / cellW).toInt().coerceIn(0, gridCols - 1)
                                val row = (offset.y / cellH).toInt().coerceIn(0, gridRows - 1)

                                val existingIdx = placedBlocks.indexOfFirst { it.col == col && it.row == row }
                                if (existingIdx >= 0) {
                                    if (placedBlocks[existingIdx].type == selectedTool) {
                                        // Tap again to erase
                                        placedBlocks.removeAt(existingIdx)
                                    } else {
                                        placedBlocks[existingIdx] = StudioBlock(col, row, selectedTool)
                                    }
                                } else {
                                    placedBlocks.add(StudioBlock(col, row, selectedTool))
                                }
                                BloxHaptics.playClick(context)
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridCols = 7
                        val gridRows = 10
                        val cellW = size.width / gridCols
                        val cellH = size.height / gridRows

                        // Draw Grid lines
                        for (c in 0..gridCols) {
                            drawLine(
                                color = Color(0x1AFFFFFF),
                                start = Offset(c * cellW, 0f),
                                end = Offset(c * cellW, size.height),
                                strokeWidth = 1f
                            )
                        }
                        for (r in 0..gridRows) {
                            drawLine(
                                color = Color(0x1AFFFFFF),
                                start = Offset(0f, r * cellH),
                                end = Offset(size.width, r * cellH),
                                strokeWidth = 1f
                            )
                        }

                        // Draw Placed Blocks
                        for (block in placedBlocks) {
                            val bx = block.col * cellW + 4f
                            val by = block.row * cellH + 4f
                            val bw = cellW - 8f
                            val bh = cellH - 8f

                            val color = when (block.type) {
                                PlatformType.SOLID -> Color(0xFF3B4252)
                                PlatformType.LAVA -> BloxRedRoblox
                                PlatformType.BOUNCE -> BloxGold
                                PlatformType.CHECKPOINT -> BloxGreenLight
                                PlatformType.TROPHY -> Color(0xFFFFD700)
                            }

                            drawRoundRect(
                                color = color,
                                topLeft = Offset(bx, by),
                                size = Size(bw, bh),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                    }

                    Text(
                        text = "Tap grid to place / tap again to delete",
                        color = BloxTextSecondary.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Test Play & Publish
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val mapData = serializeMap()
                            onTestPlay(mapTitle, mapData)
                            BloxHaptics.playClick(context)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BloxSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Test", tint = BloxGreenLight)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Play", color = BloxTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val mapData = serializeMap()
                            val exp = Experience(
                                id = "custom_${System.currentTimeMillis()}",
                                title = mapTitle.ifEmpty { "My Custom Obby" },
                                description = "Built in Blox Studio by ${userProfile.displayName} with ${placedBlocks.size} parts!",
                                creator = userProfile.displayName,
                                category = "Studio",
                                ratingPercent = 100,
                                activePlayers = 1,
                                totalVisits = "1",
                                thumbnailResName = "img_obby_banner_1790278303257",
                                accentColor = 0xFF00E5FF,
                                isUserCreated = true,
                                customMapData = mapData
                            )
                            onPublish(exp)
                            BloxHaptics.playSuccess(context)
                            selectedTab = 1 // Switch to My Places
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Public, contentDescription = "Publish", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // My Published Places List
            if (userCreations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "No creations",
                            tint = BloxTextSecondary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Creations Yet",
                            color = BloxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Switch to Map Builder and hit 'Publish' to build your universe!",
                            color = BloxTextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(userCreations) { creation ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BloxSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = creation.title,
                                        color = BloxTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "By ${creation.creator} • ${creation.category}",
                                        color = BloxTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            onTestPlay(creation.title, creation.customMapData)
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(BloxGreenRobux, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = {
                                            onDeleteCreation(creation.id)
                                        },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(BloxSurfaceElevated, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = BloxRedRoblox
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
}

@Composable
fun PaletteItem(
    name: String,
    type: PlatformType,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.35f) else BloxSurface)
            .border(
                1.5.dp,
                if (isSelected) color else BloxBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = name,
                color = BloxTextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

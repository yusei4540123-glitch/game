package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.sin
import kotlin.random.Random

data class ObbyPlatform(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: PlatformType // SOLID, LAVA, BOUNCE, CHECKPOINT, TROPHY
)

enum class PlatformType {
    SOLID, LAVA, BOUNCE, CHECKPOINT, TROPHY
}

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float
)

@Composable
fun ObbyGameScreen(
    userProfile: UserProfile,
    gameTitle: String = "Tower of Hell: Infinite Obby",
    customBlocks: String = "",
    onLeaveGame: () -> Unit,
    onWinReward: (Int) -> Unit
) {
    val context = LocalContext.current

    // Physics State
    var playerX by remember { mutableFloatStateOf(100f) }
    var playerY by remember { mutableFloatStateOf(700f) }
    var velocityX by remember { mutableFloatStateOf(0f) }
    var velocityY by remember { mutableFloatStateOf(0f) }
    var isGrounded by remember { mutableStateOf(false) }

    // Checkpoint & Game Flow
    var spawnX by remember { mutableFloatStateOf(100f) }
    var spawnY by remember { mutableFloatStateOf(700f) }
    var deaths by remember { mutableIntStateOf(0) }
    var checkpointsHit by remember { mutableIntStateOf(1) }
    var hasWon by remember { mutableStateOf(false) }
    var gameTimeSeconds by remember { mutableIntStateOf(0) }

    // Controls
    var moveLeftPressed by remember { mutableStateOf(false) }
    var moveRightPressed by remember { mutableStateOf(false) }

    // Camera view offset (scrolls up as player ascends the tower)
    var cameraOffsetY by remember { mutableFloatStateOf(0f) }

    // Confetti particles on victory
    val confettiList = remember { mutableStateListOf<ConfettiParticle>() }

    // Platforms Layout (Tower of Hell stages)
    val platforms = remember(customBlocks) {
        val list = mutableListOf<ObbyPlatform>()
        if (customBlocks.isNotEmpty() && customBlocks.contains(";")) {
            // Parse custom Studio map
            val parts = customBlocks.split("|")
            for (p in parts) {
                val tokens = p.split(";")
                if (tokens.size >= 5) {
                    val px = tokens[0].toFloatOrNull() ?: 100f
                    val py = tokens[1].toFloatOrNull() ?: 600f
                    val pw = tokens[2].toFloatOrNull() ?: 120f
                    val ph = tokens[3].toFloatOrNull() ?: 24f
                    val type = try { PlatformType.valueOf(tokens[4]) } catch (_: Exception) { PlatformType.SOLID }
                    list.add(ObbyPlatform(px, py, pw, ph, type))
                }
            }
        } else {
            // Built-in Tower of Hell Level
            // Stage 0: Ground spawn
            list.add(ObbyPlatform(20f, 760f, 360f, 40f, PlatformType.SOLID))
            list.add(ObbyPlatform(50f, 730f, 60f, 30f, PlatformType.CHECKPOINT))

            // Stage 1: Stepping stones
            list.add(ObbyPlatform(240f, 690f, 90f, 20f, PlatformType.SOLID))
            list.add(ObbyPlatform(130f, 610f, 80f, 20f, PlatformType.SOLID))
            list.add(ObbyPlatform(250f, 540f, 80f, 20f, PlatformType.SOLID))

            // Stage 2: Red Lava hazard jump
            list.add(ObbyPlatform(100f, 480f, 180f, 20f, PlatformType.SOLID))
            list.add(ObbyPlatform(160f, 460f, 60f, 20f, PlatformType.LAVA)) // Killbrick on top!
            list.add(ObbyPlatform(300f, 420f, 70f, 20f, PlatformType.SOLID))

            // Stage 3: Super Bounce Pad
            list.add(ObbyPlatform(180f, 360f, 70f, 20f, PlatformType.BOUNCE))
            list.add(ObbyPlatform(40f, 260f, 100f, 20f, PlatformType.SOLID))
            list.add(ObbyPlatform(50f, 230f, 60f, 30f, PlatformType.CHECKPOINT))

            // Stage 4: Narrow precision climb
            list.add(ObbyPlatform(180f, 200f, 60f, 18f, PlatformType.SOLID))
            list.add(ObbyPlatform(280f, 150f, 60f, 18f, PlatformType.SOLID))
            list.add(ObbyPlatform(160f, 90f, 60f, 18f, PlatformType.SOLID))

            // Stage 5: Final Lava Gauntlet
            list.add(ObbyPlatform(60f, 30f, 260f, 20f, PlatformType.SOLID))
            list.add(ObbyPlatform(120f, 10f, 40f, 20f, PlatformType.LAVA))
            list.add(ObbyPlatform(200f, 10f, 40f, 20f, PlatformType.LAVA))

            // Stage 6: The Golden Summit Trophy
            list.add(ObbyPlatform(150f, -60f, 100f, 20f, PlatformType.SOLID))
            list.add(ObbyPlatform(180f, -110f, 40f, 50f, PlatformType.TROPHY))
        }
        list
    }

    // Timer loop
    LaunchedEffect(hasWon) {
        if (!hasWon) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                gameTimeSeconds++
            }
        }
    }

    // Game Physics Loop
    LaunchedEffect(Unit) {
        val gravity = 0.65f
        val friction = 0.82f
        val speed = 2.2f
        val jumpForce = -13.5f
        val bounceForce = -20f

        var lastFrameTime = System.nanoTime()

        while (true) {
            withFrameNanos { now ->
                val dt = (now - lastFrameTime) / 1_000_000_000f
                lastFrameTime = now

                if (!hasWon) {
                    // Input Handling
                    if (moveLeftPressed) velocityX -= speed
                    if (moveRightPressed) velocityX += speed

                    // Apply horizontal physics
                    velocityX *= friction
                    playerX += velocityX

                    // Constrain player to screen horizontal edges
                    if (playerX < 10f) {
                        playerX = 10f
                        velocityX = 0f
                    }
                    if (playerX > 370f) {
                        playerX = 370f
                        velocityX = 0f
                    }

                    // Apply gravity
                    velocityY += gravity
                    playerY += velocityY

                    // Collision detection with platforms
                    val playerWidth = 26f
                    val playerHeight = 44f
                    val playerBottom = playerY + playerHeight
                    val playerFeetX = playerX + playerWidth / 2f

                    var groundedThisFrame = false

                    for (plat in platforms) {
                        // Check if player lands on platform top
                        val intersectsX = (playerX + playerWidth > plat.x) && (playerX < plat.x + plat.width)
                        val intersectsY = (playerBottom >= plat.y) && (playerBottom <= plat.y + plat.height + 14f) && (velocityY >= 0f)

                        if (intersectsX && intersectsY) {
                            when (plat.type) {
                                PlatformType.SOLID -> {
                                    playerY = plat.y - playerHeight
                                    velocityY = 0f
                                    groundedThisFrame = true
                                }
                                PlatformType.BOUNCE -> {
                                    playerY = plat.y - playerHeight
                                    velocityY = bounceForce
                                    groundedThisFrame = false
                                    BloxHaptics.playClick(context)
                                }
                                PlatformType.CHECKPOINT -> {
                                    playerY = plat.y - playerHeight
                                    velocityY = 0f
                                    groundedThisFrame = true
                                    if (spawnY != plat.y - playerHeight) {
                                        spawnX = plat.x + plat.width / 2f - playerWidth / 2f
                                        spawnY = plat.y - playerHeight
                                        checkpointsHit++
                                        BloxHaptics.playSuccess(context)
                                    }
                                }
                                PlatformType.LAVA -> {
                                    // "OOF!" Respawn
                                    deaths++
                                    BloxHaptics.playClick(context)
                                    playerX = spawnX
                                    playerY = spawnY
                                    velocityX = 0f
                                    velocityY = 0f
                                    break
                                }
                                PlatformType.TROPHY -> {
                                    // Victory!
                                    hasWon = true
                                    BloxHaptics.playSuccess(context)
                                    onWinReward(150) // Reward Robux
                                    // Spawn confetti
                                    for (i in 0..60) {
                                        confettiList.add(
                                            ConfettiParticle(
                                                x = playerX,
                                                y = playerY - 30f,
                                                vx = (Random.nextFloat() - 0.5f) * 12f,
                                                vy = -Random.nextFloat() * 14f - 4f,
                                                color = listOf(BloxGold, BloxGreenLight, BloxRedRoblox, Color(0xFF00E5FF))[Random.nextInt(4)],
                                                size = Random.nextFloat() * 8f + 4f
                                            )
                                        )
                                    }
                                    break
                                }
                            }
                        }
                    }

                    isGrounded = groundedThisFrame

                    // Fall into void below ground
                    if (playerY > 850f) {
                        deaths++
                        BloxHaptics.playClick(context)
                        playerX = spawnX
                        playerY = spawnY
                        velocityX = 0f
                        velocityY = 0f
                    }

                    // Smooth Camera follow (keep player in middle-lower third)
                    val targetCamY = 500f - playerY
                    cameraOffsetY += (targetCamY - cameraOffsetY) * 0.12f
                } else {
                    // Update confetti
                    val iter = confettiList.iterator()
                    while (iter.hasNext()) {
                        val p = iter.next()
                        p.x += p.vx
                        p.y += p.vy
                        p.vy += 0.3f
                        if (p.y > 1000f) iter.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
    ) {
        // Obby World Canvas
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // Background sky gradient with subtle neon vertical grid
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090A0C),
                        Color(0xFF14171A),
                        Color(0xFF1B2026)
                    )
                )
            )

            // Tower Grid background accents
            val gridStep = 60f
            for (gx in 0..(canvasW / gridStep).toInt()) {
                drawLine(
                    color = Color(0x11FFFFFF),
                    start = Offset(gx * gridStep, 0f),
                    end = Offset(gx * gridStep, canvasH),
                    strokeWidth = 1f
                )
            }

            // Draw Platforms relative to camera offset
            for (plat in platforms) {
                val py = plat.y + cameraOffsetY
                // Only draw visible platforms
                if (py > -80f && py < canvasH + 80f) {
                    when (plat.type) {
                        PlatformType.SOLID -> {
                            // Sleek 3D Blocky platform
                            drawRoundRect(
                                color = Color(0xFF2E3440),
                                topLeft = Offset(plat.x, py),
                                size = Size(plat.width, plat.height),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                            // Top neon highlight
                            drawRect(
                                color = Color(0xFF00E5FF),
                                topLeft = Offset(plat.x, py),
                                size = Size(plat.width, 3f)
                            )
                        }
                        PlatformType.LAVA -> {
                            // Glowing pulsating red lava killbrick
                            drawRoundRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF1744), Color(0xFFFF5252))
                                ),
                                topLeft = Offset(plat.x, py),
                                size = Size(plat.width, plat.height),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                            // Danger warning stripes
                            drawLine(
                                color = Color(0xFFFFEB3B),
                                start = Offset(plat.x + 4f, py + 2f),
                                end = Offset(plat.x + plat.width - 4f, py + 2f),
                                strokeWidth = 2f
                            )
                        }
                        PlatformType.BOUNCE -> {
                            // High bounce springboard
                            drawRoundRect(
                                color = Color(0xFFFFD600),
                                topLeft = Offset(plat.x, py),
                                size = Size(plat.width, plat.height),
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                            drawRect(
                                color = Color(0xFFFF6D00),
                                topLeft = Offset(plat.x + 4f, py + plat.height - 5f),
                                size = Size(plat.width - 8f, 5f)
                            )
                        }
                        PlatformType.CHECKPOINT -> {
                            // Checkpoint flag platform
                            drawRoundRect(
                                color = Color(0xFF00C853),
                                topLeft = Offset(plat.x, py),
                                size = Size(plat.width, plat.height),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                            // Checkpoint green vertical beam
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0x6600E676))
                                ),
                                topLeft = Offset(plat.x + plat.width * 0.4f, py - 60f),
                                size = Size(plat.width * 0.2f, 60f)
                            )
                        }
                        PlatformType.TROPHY -> {
                            // Golden Trophy Cup
                            val tx = plat.x + plat.width / 2f
                            drawCircle(
                                color = Color(0xFFFFD700),
                                radius = 18f,
                                center = Offset(tx, py - 15f)
                            )
                            drawRect(
                                color = Color(0xFFFFA000),
                                topLeft = Offset(tx - 6f, py - 5f),
                                size = Size(12f, 15f)
                            )
                        }
                    }
                }
            }

            // Draw Blocky Player Character
            val drawPlayerX = playerX
            val drawPlayerY = playerY + cameraOffsetY
            val pw = 26f
            val ph = 44f

            // Shadow on ground below player
            drawOval(
                color = Color(0x55000000),
                topLeft = Offset(drawPlayerX, drawPlayerY + ph - 2f),
                size = Size(pw, 6f)
            )

            // Legs
            drawRoundRect(
                color = Color(userProfile.leftLegColor),
                topLeft = Offset(drawPlayerX + 2f, drawPlayerY + 26f),
                size = Size(10f, 18f),
                cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                color = Color(userProfile.rightLegColor),
                topLeft = Offset(drawPlayerX + 14f, drawPlayerY + 26f),
                size = Size(10f, 18f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Torso
            drawRoundRect(
                color = Color(userProfile.torsoColor),
                topLeft = Offset(drawPlayerX + 3f, drawPlayerY + 12f),
                size = Size(20f, 16f),
                cornerRadius = CornerRadius(2f, 2f)
            )

            // Head
            drawRoundRect(
                color = Color(userProfile.headColor),
                topLeft = Offset(drawPlayerX + 5f, drawPlayerY),
                size = Size(16f, 14f),
                cornerRadius = CornerRadius(3f, 3f)
            )

            // Face smile / eyes
            drawCircle(Color.Black, radius = 1.5f, center = Offset(drawPlayerX + 9f, drawPlayerY + 6f))
            drawCircle(Color.Black, radius = 1.5f, center = Offset(drawPlayerX + 17f, drawPlayerY + 6f))

            // Confetti rendering
            for (p in confettiList) {
                drawCircle(p.color, radius = p.size, center = Offset(p.x, p.y + cameraOffsetY))
            }
        }

        // Top HUD Overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onLeaveGame,
                modifier = Modifier
                    .size(40.dp)
                    .background(BloxSurfaceElevated.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Leave",
                    tint = BloxTextPrimary
                )
            }

            // Stats Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BloxSurfaceElevated.copy(alpha = 0.9f))
                    .border(1.dp, BloxBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Flag, contentDescription = "Checkpoints", tint = BloxGreenLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("$checkpointsHit", color = BloxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Spacer(modifier = Modifier.width(12.dp))

                Text("💀 $deaths", color = BloxRedRoblox, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                Spacer(modifier = Modifier.width(12.dp))

                Text("⏱ ${gameTimeSeconds}s", color = BloxTextSecondary, fontSize = 13.sp)
            }

            // Reset to Checkpoint button
            IconButton(
                onClick = {
                    deaths++
                    playerX = spawnX
                    playerY = spawnY
                    velocityX = 0f
                    velocityY = 0f
                    BloxHaptics.playClick(context)
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(BloxSurfaceElevated.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Respawn", tint = BloxTextPrimary)
            }
        }

        // Victory Dialog Overlay
        AnimatedVisibility(
            visible = hasWon,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BloxSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, BloxGold),
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = BloxGold,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SUMMIT CONQUERED!",
                        color = BloxGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Time: ${gameTimeSeconds}s • Deaths: $deaths",
                        color = BloxTextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reward: ", color = BloxTextPrimary, fontWeight = FontWeight.Medium)
                        Text("+150 Robux", color = BloxGreenLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                hasWon = false
                                playerX = spawnX
                                playerY = spawnY
                                velocityX = 0f
                                velocityY = 0f
                                gameTimeSeconds = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BloxSurfaceElevated),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Replay", color = BloxTextPrimary)
                        }

                        Button(
                            onClick = onLeaveGame,
                            colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Lobby", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Virtual On-Screen Game Controls (Bottom)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Directional Buttons (Left & Right)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Left Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(if (moveLeftPressed) BloxRedRoblox else BloxSurfaceElevated.copy(alpha = 0.85f))
                        .border(2.dp, BloxBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    moveLeftPressed = true
                                    tryAwaitRelease()
                                    moveLeftPressed = false
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("◀", color = BloxTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                // Right Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(if (moveRightPressed) BloxRedRoblox else BloxSurfaceElevated.copy(alpha = 0.85f))
                        .border(2.dp, BloxBorder, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    moveRightPressed = true
                                    tryAwaitRelease()
                                    moveRightPressed = false
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", color = BloxTextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Big Jump Button
            Box(
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(BloxGreenLight, BloxGreenRobux)
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                if (isGrounded) {
                                    velocityY = -13.5f
                                    isGrounded = false
                                    BloxHaptics.playClick(context)
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("▲", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("JUMP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

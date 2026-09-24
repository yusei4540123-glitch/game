package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BloxHaptics
import com.example.ui.components.RobuxBadge
import com.example.ui.theme.BloxBorder
import com.example.ui.theme.BloxGold
import com.example.ui.theme.BloxGreenLight
import com.example.ui.theme.BloxGreenRobux
import com.example.ui.theme.BloxRedRoblox
import com.example.ui.theme.BloxSurface
import com.example.ui.theme.BloxSurfaceElevated
import com.example.ui.theme.BloxTextPrimary
import com.example.ui.theme.BloxTextSecondary
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Pet(
    val id: String,
    val name: String,
    val rarity: String,
    val multiplier: Int,
    val color: Color,
    val secondaryColor: Color
)

@Composable
fun PetSimulatorScreen(
    currentRobux: Int,
    onLeaveGame: () -> Unit,
    onAddRobux: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var coins by remember { mutableIntStateOf(1250) }
    var chestHealth by remember { mutableIntStateOf(100) }
    val maxChestHealth = 100
    val chestScale = remember { Animatable(1f) }

    // Pets collection
    val ownedPets = remember {
        mutableStateListOf(
            Pet("pet_dog", "Blocky Dog", "Common", 2, Color(0xFFD7CCC8), Color(0xFF8D6E63)),
            Pet("pet_cat", "Cyber Cat", "Rare", 5, Color(0xFF80DEEA), Color(0xFF00ACC1))
        )
    }

    var equippedPet by remember { mutableStateOf<Pet?>(ownedPets.firstOrNull()) }
    var newlyHatchedPet by remember { mutableStateOf<Pet?>(null) }
    var isHatching by remember { mutableStateOf(false) }

    // Floating damage texts
    var clickEffectVisible by remember { mutableStateOf(false) }

    val petPoolCommon = listOf(
        Pet("pet_dog", "Blocky Dog", "Common", 2, Color(0xFFD7CCC8), Color(0xFF8D6E63)),
        Pet("pet_bunny", "Fluffy Bunny", "Common", 3, Color(0xFFFFCDD2), Color(0xFFE91E63)),
        Pet("pet_bear", "Grizzly Bear", "Rare", 6, Color(0xFF8D6E63), Color(0xFF5D4037))
    )

    val petPoolRoyal = listOf(
        Pet("pet_cat", "Cyber Cat", "Rare", 8, Color(0xFF80DEEA), Color(0xFF00ACC1)),
        Pet("pet_fox", "Fire Fox", "Epic", 15, Color(0xFFFF9800), Color(0xFFE65100)),
        Pet("pet_golem", "Crystal Golem", "Epic", 22, Color(0xFFB388FF), Color(0xFF651FFF))
    )

    val petPoolMythic = listOf(
        Pet("pet_dragon", "Celestial Frost Dragon", "Mythic", 60, Color(0xFF00E5FF), Color(0xFF0D47A1)),
        Pet("pet_demon", "Inferno Blox Demon", "Mythic", 85, Color(0xFFFF1744), Color(0xFF880E4F)),
        Pet("pet_dominus_pet", "Golden Dominus Pet", "Legendary", 150, Color(0xFFFFD700), Color(0xFFFF6F00))
    )

    fun hatchEgg(cost: Int, pool: List<Pet>) {
        if (coins >= cost && !isHatching) {
            coins -= cost
            isHatching = true
            BloxHaptics.playClick(context)

            coroutineScope.launch {
                kotlinx.coroutines.delay(1200)
                val pet = pool[Random.nextInt(pool.size)].copy(id = "pet_${System.currentTimeMillis()}")
                ownedPets.add(0, pet)
                equippedPet = pet
                newlyHatchedPet = pet
                isHatching = false
                BloxHaptics.playSuccess(context)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onLeaveGame,
                    modifier = Modifier
                        .size(40.dp)
                        .background(BloxSurfaceElevated, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Leave",
                        tint = BloxTextPrimary
                    )
                }

                // Currency row (Coins + Robux)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(BloxSurfaceElevated)
                            .border(1.dp, BloxBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins",
                            tint = BloxGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%,d", coins),
                            color = BloxGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    RobuxBadge(amount = currentRobux, onClick = {})
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Equipped Pet Status Pill
            if (equippedPet != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(BloxSurface)
                        .border(1.dp, equippedPet!!.color, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Pet",
                        tint = equippedPet!!.color,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Equipped: ${equippedPet!!.name} (${equippedPet!!.multiplier}x Boost)",
                        color = BloxTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Giant Tap-to-Mine Coin Chest
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .scale(chestScale.value)
                    .clickable {
                        BloxHaptics.playClick(context)
                        val multiplier = equippedPet?.multiplier ?: 1
                        val gained = 15 * multiplier
                        coins += gained
                        chestHealth -= 10

                        coroutineScope.launch {
                            chestScale.animateTo(0.92f, tween(50))
                            chestScale.animateTo(1f, tween(80))
                        }

                        if (chestHealth <= 0) {
                            // Chest break bonus!
                            chestHealth = maxChestHealth
                            val bonus = 150 * multiplier
                            coins += bonus
                            BloxHaptics.playSuccess(context)
                            // Chance for free 25 Robux on break!
                            if (Random.nextInt(100) < 30) {
                                onAddRobux(25)
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Glowing base
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFD700).copy(alpha = 0.4f), Color.Transparent)
                        ),
                        radius = w * 0.48f,
                        center = Offset(w / 2f, h / 2f)
                    )

                    // 3D Blocky Chest Body
                    val chestW = w * 0.7f
                    val chestH = h * 0.55f
                    val cx = (w - chestW) / 2f
                    val cy = (h - chestH) / 2f

                    // Bottom Box
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFF8D6E63), Color(0xFF5D4037))),
                        topLeft = Offset(cx, cy + chestH * 0.35f),
                        size = Size(chestW, chestH * 0.65f),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Gold Bands
                    drawRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(cx + chestW * 0.15f, cy + chestH * 0.35f),
                        size = Size(chestW * 0.12f, chestH * 0.65f)
                    )
                    drawRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(cx + chestW * 0.73f, cy + chestH * 0.35f),
                        size = Size(chestW * 0.12f, chestH * 0.65f)
                    )

                    // Chest Lid
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Color(0xFFA1887F), Color(0xFF6D4C41))),
                        topLeft = Offset(cx - 4f, cy),
                        size = Size(chestW + 8f, chestH * 0.4f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )

                    // Golden Lock
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = 12f,
                        center = Offset(w / 2f, cy + chestH * 0.4f)
                    )
                    drawCircle(
                        color = Color(0xFF3E2723),
                        radius = 4f,
                        center = Offset(w / 2f, cy + chestH * 0.4f)
                    )
                }
            }

            // Chest Health Bar
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.width(180.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(BloxSurfaceElevated)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(chestHealth / maxChestHealth.toFloat())
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Brush.horizontalGradient(listOf(BloxGold, Color(0xFFFF6D00))))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tap chest to mine coins!", color = BloxTextSecondary, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Egg Hatching Station
            Text(
                text = "HATCH EGGS",
                color = BloxTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Common Egg
                EggCard(
                    title = "Basic Egg",
                    cost = 250,
                    costCoins = true,
                    eggColor = Color(0xFFECEFF1),
                    canAfford = coins >= 250,
                    onHatch = { hatchEgg(250, petPoolCommon) },
                    modifier = Modifier.weight(1f)
                )

                // Royal Egg
                EggCard(
                    title = "Royal Egg",
                    cost = 750,
                    costCoins = true,
                    eggColor = Color(0xFFFFD54F),
                    canAfford = coins >= 750,
                    onHatch = { hatchEgg(750, petPoolRoyal) },
                    modifier = Modifier.weight(1f)
                )

                // Mythic Egg
                EggCard(
                    title = "Mythic Egg",
                    cost = 2000,
                    costCoins = true,
                    eggColor = Color(0xFFBA68C8),
                    canAfford = coins >= 2000,
                    onHatch = { hatchEgg(2000, petPoolMythic) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // My Pets Inventory Row
            Text(
                text = "MY PETS (${ownedPets.size})",
                color = BloxTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ownedPets) { pet ->
                    val isEquipped = equippedPet?.id == pet.id
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BloxSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isEquipped) pet.color else BloxBorder
                        ),
                        modifier = Modifier
                            .clickable {
                                equippedPet = pet
                                BloxHaptics.playClick(context)
                            }
                            .width(110.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pet.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🐾", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pet.name,
                                color = BloxTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${pet.multiplier}x",
                                color = BloxGreenLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            if (isEquipped) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "EQUIPPED",
                                    color = BloxGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hatching Animation Modal
        if (isHatching || newlyHatchedPet != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                if (isHatching) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Shaking Egg Canvas
                        val infiniteTransition = rememberInfiniteTransition(label = "hatch")
                        val wiggle by infiniteTransition.animateFloat(
                            initialValue = -12f,
                            targetValue = 12f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(120, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "wiggle"
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(1.1f)
                                .rotate(wiggle)
                                .clip(RoundedCornerShape(60.dp, 60.dp, 40.dp, 40.dp))
                                .background(Brush.verticalGradient(listOf(BloxGold, BloxRedRoblox))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✨", fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "HATCHING EGG...",
                            color = BloxGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                } else if (newlyHatchedPet != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BloxSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, newlyHatchedPet!!.color),
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "NEW PET UNLOCKED!",
                                color = BloxGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(newlyHatchedPet!!.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🦄", fontSize = 40.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = newlyHatchedPet!!.name,
                                color = BloxTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${newlyHatchedPet!!.rarity} • ${newlyHatchedPet!!.multiplier}x Coin Multiplier",
                                color = BloxGreenLight,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { newlyHatchedPet = null },
                                colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("AWESOME!", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EggCard(
    title: String,
    cost: Int,
    costCoins: Boolean,
    eggColor: Color,
    canAfford: Boolean,
    onHatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BloxSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(23.dp, 23.dp, 16.dp, 16.dp))
                    .background(eggColor)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(23.dp, 23.dp, 16.dp, 16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🥚", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = BloxTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$cost 🪙",
                color = if (canAfford) BloxGold else BloxTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onHatch,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BloxGreenRobux,
                    disabledContainerColor = BloxSurfaceElevated
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hatch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

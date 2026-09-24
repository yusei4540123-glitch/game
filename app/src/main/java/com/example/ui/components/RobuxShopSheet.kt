package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RobuxShopSheet(
    currentRobux: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddRobux: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var promoCode by remember { mutableStateOf("") }
    var promoMessage by remember { mutableStateOf<String?>(null) }
    var isSpinning by remember { mutableStateOf(false) }
    val spinRotation = remember { Animatable(0f) }
    var claimedDaily by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BloxDarkBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RobuxBadge(amount = currentRobux, onClick = {})
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Robux Central",
                        color = BloxTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .background(BloxSurfaceElevated, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = BloxTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lucky Wheel Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BloxSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Spin",
                            tint = BloxGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Robux Lucky Wheel",
                            color = BloxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Spinning Wheel Graphic
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(110.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .rotate(spinRotation.value)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        listOf(
                                            BloxGreenRobux,
                                            BloxGold,
                                            BloxRedRoblox,
                                            Color(0xFF00E5FF),
                                            BloxGreenRobux
                                        )
                                    )
                                )
                                .border(3.dp, Color.White, CircleShape)
                        )
                        // Needle center
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BloxDarkBackground)
                                .border(2.dp, BloxGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("R$", color = BloxGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                BloxHaptics.playClick(context)
                                coroutineScope.launch {
                                    val rewards = listOf(50, 100, 200, 350, 500)
                                    val reward = rewards[Random.nextInt(rewards.size)]
                                    val targetAngle = spinRotation.value + 1440f + Random.nextInt(360)
                                    spinRotation.animateTo(
                                        targetValue = targetAngle,
                                        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
                                    )
                                    onAddRobux(reward)
                                    BloxHaptics.playSuccess(context)
                                    promoMessage = "🎉 You won +$reward Robux from the wheel!"
                                    isSpinning = false
                                }
                            }
                        },
                        enabled = !isSpinning,
                        colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSpinning) "Spinning..." else "SPIN FOR FREE ROBUX",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Check-in Claim Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BloxSurface),
                shape = RoundedCornerShape(16.dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(BloxSurfaceElevated, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Daily",
                                tint = BloxGreenLight
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Builder Allowance",
                                color = BloxTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "+100 Robux free every 24h",
                                color = BloxTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (!claimedDaily) {
                                claimedDaily = true
                                onAddRobux(100)
                                BloxHaptics.playSuccess(context)
                                promoMessage = "🎁 Claimed +100 Robux daily allowance!"
                            }
                        },
                        enabled = !claimedDaily,
                        colors = ButtonDefaults.buttonColors(containerColor = BloxSurfaceElevated),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (claimedDaily) "Claimed" else "Claim",
                            color = if (claimedDaily) BloxTextSecondary else BloxGreenLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Promo Code Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BloxSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Code",
                            tint = Color(0xFF00E5FF)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Redeem BloxWorld Code",
                            color = BloxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Try: BLOXY2026, BUILDERMAN, TOWEROFHELL, or PETSIMX",
                        color = BloxTextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it.uppercase() },
                            placeholder = { Text("ENTER CODE", color = BloxTextSecondary, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloxGreenRobux,
                                unfocusedBorderColor = BloxBorder,
                                focusedTextColor = BloxTextPrimary,
                                unfocusedTextColor = BloxTextPrimary,
                                focusedContainerColor = BloxSurfaceElevated,
                                unfocusedContainerColor = BloxSurfaceElevated
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                BloxHaptics.playClick(context)
                                when (promoCode.trim()) {
                                    "BLOXY2026" -> {
                                        onAddRobux(250)
                                        BloxHaptics.playSuccess(context)
                                        promoMessage = "✨ Code Redeemed: +250 Robux!"
                                        promoCode = ""
                                    }
                                    "BUILDERMAN" -> {
                                        onAddRobux(500)
                                        BloxHaptics.playSuccess(context)
                                        promoMessage = "👑 Founder Code: +500 Robux!"
                                        promoCode = ""
                                    }
                                    "TOWEROFHELL" -> {
                                        onAddRobux(150)
                                        BloxHaptics.playSuccess(context)
                                        promoMessage = "🧗 Obby Master: +150 Robux!"
                                        promoCode = ""
                                    }
                                    "PETSIMX" -> {
                                        onAddRobux(300)
                                        BloxHaptics.playSuccess(context)
                                        promoMessage = "🐾 Pet Trainer: +300 Robux!"
                                        promoCode = ""
                                    }
                                    else -> {
                                        promoMessage = "❌ Invalid or expired code. Try BLOXY2026!"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Redeem", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (promoMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = promoMessage!!,
                            color = if (promoMessage!!.startsWith("❌")) BloxRedRoblox else BloxGreenLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

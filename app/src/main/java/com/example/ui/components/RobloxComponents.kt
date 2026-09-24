package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.BloxBorder
import com.example.ui.theme.BloxGreenLight
import com.example.ui.theme.BloxGreenRobux
import com.example.ui.theme.BloxRedRoblox
import com.example.ui.theme.BloxSurfaceElevated
import com.example.ui.theme.BloxTextPrimary

// Haptic feedback utility
object BloxHaptics {
    fun playClick(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(25)
            }
        }
    }

    fun playSuccess(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 60), -1))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(70)
            }
        }
    }
}

/**
 * Iconic tilted square Roblox block logo
 */
@Composable
fun RobloxLogo(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)

        // Rotate 15 degrees for authentic Roblox tilt
        rotate(12f, center) {
            val outerSize = w * 0.78f
            val outerLeft = (w - outerSize) / 2f
            val outerTop = (h - outerSize) / 2f

            // Outer rounded square
            drawRoundRect(
                color = color,
                topLeft = Offset(outerLeft, outerTop),
                size = Size(outerSize, outerSize),
                cornerRadius = CornerRadius(outerSize * 0.18f, outerSize * 0.18f)
            )

            // Inner square cutout
            val innerSize = outerSize * 0.42f
            val innerLeft = (w - innerSize) / 2f
            val innerTop = (h - innerSize) / 2f

            drawRoundRect(
                color = Color(0xFF191B1D),
                topLeft = Offset(innerLeft, innerTop),
                size = Size(innerSize, innerSize),
                cornerRadius = CornerRadius(innerSize * 0.15f, innerSize * 0.15f)
            )
        }
    }
}

/**
 * Robux Badge showing hexagonal icon + currency amount
 */
@Composable
fun RobuxBadge(
    amount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BloxSurfaceElevated)
            .border(1.dp, BloxBorder, RoundedCornerShape(20.dp))
            .clickable {
                BloxHaptics.playClick(context)
                onClick()
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hexagonal Robux Icon
        Canvas(modifier = Modifier.size(18.dp)) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.5f, 0f)
                lineTo(w, h * 0.28f)
                lineTo(w, h * 0.72f)
                lineTo(w * 0.5f, h)
                lineTo(0f, h * 0.72f)
                lineTo(0f, h * 0.28f)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(BloxGreenLight, BloxGreenRobux)
                )
            )
            // Center diamond cutout
            val inner = Path().apply {
                moveTo(w * 0.5f, h * 0.3f)
                lineTo(w * 0.7f, h * 0.5f)
                lineTo(w * 0.5f, h * 0.7f)
                lineTo(w * 0.3f, h * 0.5f)
                close()
            }
            drawPath(path = inner, color = Color(0xFF191B1D))
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = formatRobux(amount),
            color = BloxTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatRobux(amount: Int): String {
    return when {
        amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000.0)
        amount >= 10_000 -> String.format("%.1fK", amount / 1000.0)
        else -> String.format("%,d", amount)
    }
}

/**
 * 3D-Shaded Blocky Robloxian Avatar Renderer
 */
@Composable
fun BlockyAvatarView(
    userProfile: UserProfile,
    modifier: Modifier = Modifier,
    isAnimated: Boolean = true,
    showPlatform: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatarAnim")
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    val armSwing by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "armSwing"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val cx = width / 2f
        val cy = height / 2f + if (isAnimated) bobbingOffset else 0f

        // Optional Cyber pedestal platform
        if (showPlatform) {
            val platY = height * 0.88f
            val platRadiusX = width * 0.42f
            val platRadiusY = height * 0.08f

            // Shadow
            drawOval(
                color = Color(0x77000000),
                topLeft = Offset(cx - platRadiusX * 0.9f, platY - platRadiusY * 0.5f),
                size = Size(platRadiusX * 1.8f, platRadiusY * 1.5f)
            )

            // Neon glowing ring
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00B06F), Color(0x3300B06F), Color.Transparent),
                    center = Offset(cx, platY),
                    radius = platRadiusX * 1.2f
                ),
                topLeft = Offset(cx - platRadiusX * 1.1f, platY - platRadiusY),
                size = Size(platRadiusX * 2.2f, platRadiusY * 2f)
            )

            // Pedestal platform base
            drawOval(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF32353A), Color(0xFF1E2024)),
                    start = Offset(cx, platY - platRadiusY),
                    end = Offset(cx, platY + platRadiusY)
                ),
                topLeft = Offset(cx - platRadiusX, platY - platRadiusY),
                size = Size(platRadiusX * 2f, platRadiusY * 2f)
            )

            // Neon cyan edge
            drawOval(
                color = Color(0xFF00E5FF),
                topLeft = Offset(cx - platRadiusX, platY - platRadiusY),
                size = Size(platRadiusX * 2f, platRadiusY * 2f),
                style = Stroke(width = 2.5f)
            )
        }

        // Avatar Body Geometry Dimensions
        val headSize = width * 0.23f
        val torsoWidth = width * 0.28f
        val torsoHeight = height * 0.26f
        val armWidth = width * 0.12f
        val armHeight = height * 0.24f
        val legWidth = width * 0.13f
        val legHeight = height * 0.25f

        val torsoTop = cy - torsoHeight * 0.35f
        val torsoLeft = cx - torsoWidth / 2f

        val headTop = torsoTop - headSize * 0.95f
        val headLeft = cx - headSize / 2f

        val legTop = torsoTop + torsoHeight
        val leftLegLeft = torsoLeft + 2f
        val rightLegLeft = torsoLeft + torsoWidth - legWidth - 2f

        val leftArmLeft = torsoLeft - armWidth - 3f
        val rightArmLeft = torsoLeft + torsoWidth + 3f
        val armTop = torsoTop + 4f

        // --- BACK ACCESSORIES (WINGS / SWORD) ---
        if (userProfile.accessoryId == "gear_wings") {
            drawWings(cx, torsoTop + torsoHeight * 0.3f, width)
        } else if (userProfile.accessoryId == "gear_sword") {
            drawBackKatana(cx, torsoTop + torsoHeight * 0.4f, width)
        }

        // --- LEGS ---
        drawBlockyLimb(
            left = leftLegLeft,
            top = legTop,
            w = legWidth,
            h = legHeight,
            baseColor = Color(userProfile.leftLegColor),
            roundTop = false
        )
        drawBlockyLimb(
            left = rightLegLeft,
            top = legTop,
            w = legWidth,
            h = legHeight,
            baseColor = Color(userProfile.rightLegColor),
            roundTop = false
        )

        // --- TORSO ---
        drawBlockyTorso(
            left = torsoLeft,
            top = torsoTop,
            w = torsoWidth,
            h = torsoHeight,
            baseColor = Color(userProfile.torsoColor),
            shirtId = userProfile.shirtId
        )

        // --- ARMS ---
        val leftArmAngle = if (isAnimated) armSwing else 0f
        val rightArmAngle = if (isAnimated) -armSwing else 0f

        rotate(leftArmAngle, Offset(leftArmLeft + armWidth / 2f, armTop)) {
            drawBlockyLimb(
                left = leftArmLeft,
                top = armTop,
                w = armWidth,
                h = armHeight,
                baseColor = Color(userProfile.leftArmColor),
                roundTop = true
            )
            // Handheld gear
            if (userProfile.accessoryId == "gear_cola") {
                drawColaCan(leftArmLeft - 4f, armTop + armHeight - 8f, armWidth)
            }
        }

        rotate(rightArmAngle, Offset(rightArmLeft + armWidth / 2f, armTop)) {
            drawBlockyLimb(
                left = rightArmLeft,
                top = armTop,
                w = armWidth,
                h = armHeight,
                baseColor = Color(userProfile.rightArmColor),
                roundTop = true
            )
            if (userProfile.accessoryId == "gear_coil") {
                drawGravityCoil(rightArmLeft + armWidth + 2f, armTop + armHeight - 12f)
            }
        }

        // --- HEAD ---
        drawBlockyHead(
            left = headLeft,
            top = headTop,
            size = headSize,
            headColor = Color(userProfile.headColor),
            faceType = userProfile.faceType
        )

        // --- HATS ---
        drawHat(
            hatId = userProfile.hatId,
            headLeft = headLeft,
            headTop = headTop,
            headSize = headSize,
            cx = cx
        )
    }
}

private fun DrawScope.drawBlockyLimb(
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    baseColor: Color,
    roundTop: Boolean
) {
    val corner = if (roundTop) 5f else 3f
    // Main front face
    drawRoundRect(
        color = baseColor,
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Left highlight strip
    drawRoundRect(
        color = Color(0x33FFFFFF),
        topLeft = Offset(left, top),
        size = Size(w * 0.22f, h),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Right shadow strip
    drawRoundRect(
        color = Color(0x33000000),
        topLeft = Offset(left + w * 0.78f, top),
        size = Size(w * 0.22f, h),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Bottom joint shadow
    drawRoundRect(
        color = Color(0x44000000),
        topLeft = Offset(left, top + h - 6f),
        size = Size(w, 6f),
        cornerRadius = CornerRadius(corner, corner)
    )
}

private fun DrawScope.drawBlockyTorso(
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    baseColor: Color,
    shirtId: String
) {
    // Front face
    drawRoundRect(
        color = baseColor,
        topLeft = Offset(left, top),
        size = Size(w, h),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Left 3D bevel highlight
    drawRect(
        color = Color(0x22FFFFFF),
        topLeft = Offset(left, top),
        size = Size(w * 0.15f, h)
    )
    // Right 3D bevel shadow
    drawRect(
        color = Color(0x33000000),
        topLeft = Offset(left + w * 0.85f, top),
        size = Size(w * 0.15f, h)
    )

    // Shirt pattern / graphic
    when (shirtId) {
        "shirt_classic" -> {
            // Iconic Roblox tilt square badge
            val badgeSize = w * 0.32f
            val bx = left + (w - badgeSize) / 2f
            val by = top + h * 0.24f
            rotate(12f, Offset(bx + badgeSize / 2f, by + badgeSize / 2f)) {
                drawRoundRect(
                    color = BloxRedRoblox,
                    topLeft = Offset(bx, by),
                    size = Size(badgeSize, badgeSize),
                    cornerRadius = CornerRadius(4f, 4f)
                )
                val cut = badgeSize * 0.4f
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(bx + (badgeSize - cut) / 2f, by + (badgeSize - cut) / 2f),
                    size = Size(cut, cut),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
        }
        "shirt_galaxy" -> {
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE040FB), Color(0xFF00E5FF), Color(0xFF1A237E)),
                    center = Offset(left + w * 0.5f, top + h * 0.5f),
                    radius = w * 0.7f
                ),
                topLeft = Offset(left + 2f, top + 2f),
                size = Size(w - 4f, h - 4f),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
        "shirt_suit" -> {
            // White shirt triangle
            val collar = Path().apply {
                moveTo(left + w * 0.3f, top)
                lineTo(left + w * 0.7f, top)
                lineTo(left + w * 0.5f, top + h * 0.5f)
                close()
            }
            drawPath(collar, Color.White)
            // Red Bowtie
            val bowtie = Path().apply {
                val cx = left + w * 0.5f
                val cy = top + h * 0.16f
                moveTo(cx, cy)
                lineTo(cx - 10f, cy - 6f)
                lineTo(cx - 10f, cy + 6f)
                close()
                moveTo(cx, cy)
                lineTo(cx + 10f, cy - 6f)
                lineTo(cx + 10f, cy + 6f)
                close()
            }
            drawPath(bowtie, Color(0xFFE53935))
        }
        "shirt_cyber" -> {
            // Glowing cyan armor lines
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(left + 6f, top + 6f),
                end = Offset(left + w * 0.5f, top + h * 0.6f),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(left + w - 6f, top + 6f),
                end = Offset(left + w * 0.5f, top + h * 0.6f),
                strokeWidth = 3f
            )
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 5f,
                center = Offset(left + w * 0.5f, top + h * 0.6f)
            )
        }
    }
}

private fun DrawScope.drawBlockyHead(
    left: Float,
    top: Float,
    size: Float,
    headColor: Color,
    faceType: String
) {
    // 3D Blocky Head
    val corner = size * 0.15f
    drawRoundRect(
        color = headColor,
        topLeft = Offset(left, top),
        size = Size(size, size),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Top bevel highlight
    drawRoundRect(
        color = Color(0x33FFFFFF),
        topLeft = Offset(left, top),
        size = Size(size, size * 0.2f),
        cornerRadius = CornerRadius(corner, corner)
    )
    // Right 3D shading
    drawRect(
        color = Color(0x22000000),
        topLeft = Offset(left + size * 0.85f, top),
        size = Size(size * 0.15f, size)
    )

    // Classic Roblox Neck stud top peg
    val studW = size * 0.4f
    val studH = size * 0.12f
    drawRoundRect(
        color = headColor,
        topLeft = Offset(left + (size - studW) / 2f, top - studH * 0.8f),
        size = Size(studW, studH),
        cornerRadius = CornerRadius(studH * 0.4f, studH * 0.4f)
    )

    // Render Face Decal
    val eyeY = top + size * 0.42f
    val eyeW = size * 0.12f
    val eyeH = size * 0.18f

    when (faceType) {
        "smile" -> {
            // Left Eye
            drawOval(
                color = Color.Black,
                topLeft = Offset(left + size * 0.24f, eyeY),
                size = Size(eyeW, eyeH)
            )
            drawCircle(Color.White, radius = eyeW * 0.3f, center = Offset(left + size * 0.26f, eyeY + eyeH * 0.3f))
            // Right Eye
            drawOval(
                color = Color.Black,
                topLeft = Offset(left + size * 0.64f, eyeY),
                size = Size(eyeW, eyeH)
            )
            drawCircle(Color.White, radius = eyeW * 0.3f, center = Offset(left + size * 0.66f, eyeY + eyeH * 0.3f))
            // Smile Arc
            val mouthPath = Path().apply {
                val my = top + size * 0.72f
                moveTo(left + size * 0.28f, my)
                quadraticTo(left + size * 0.5f, my + size * 0.2f, left + size * 0.72f, my)
            }
            drawPath(mouthPath, Color.Black, style = Stroke(width = size * 0.07f))
        }
        "chill" -> {
            // Chill half-closed eyelids
            drawLine(Color.Black, Offset(left + size * 0.22f, eyeY + 4f), Offset(left + size * 0.4f, eyeY + 4f), strokeWidth = size * 0.06f)
            drawLine(Color.Black, Offset(left + size * 0.6f, eyeY + 4f), Offset(left + size * 0.78f, eyeY + 4f), strokeWidth = size * 0.06f)
            // Smirk
            val smirk = Path().apply {
                val my = top + size * 0.73f
                moveTo(left + size * 0.35f, my + 2f)
                quadraticTo(left + size * 0.55f, my + 6f, left + size * 0.72f, my - 4f)
            }
            drawPath(smirk, Color.Black, style = Stroke(width = size * 0.07f))
        }
        "winning" -> {
            // Wide triumphant grin
            drawCircle(Color.Black, radius = eyeW * 0.6f, center = Offset(left + size * 0.3f, eyeY + 4f))
            drawCircle(Color.Black, radius = eyeW * 0.6f, center = Offset(left + size * 0.7f, eyeY + 4f))
            val winMouth = Path().apply {
                moveTo(left + size * 0.22f, top + size * 0.65f)
                lineTo(left + size * 0.78f, top + size * 0.65f)
                quadraticTo(left + size * 0.5f, top + size * 0.92f, left + size * 0.22f, top + size * 0.65f)
                close()
            }
            drawPath(winMouth, Color.Black)
            // White teeth line
            drawRect(Color.White, topLeft = Offset(left + size * 0.26f, top + size * 0.66f), size = Size(size * 0.48f, size * 0.08f))
        }
        "beast" -> {
            // Fierce angled red eyes
            val lEye = Path().apply {
                moveTo(left + size * 0.2f, eyeY - 2f)
                lineTo(left + size * 0.42f, eyeY + 6f)
                lineTo(left + size * 0.25f, eyeY + 12f)
                close()
            }
            drawPath(lEye, Color(0xFFFF1744))
            val rEye = Path().apply {
                moveTo(left + size * 0.8f, eyeY - 2f)
                lineTo(left + size * 0.58f, eyeY + 6f)
                lineTo(left + size * 0.75f, eyeY + 12f)
                close()
            }
            drawPath(rEye, Color(0xFFFF1744))
            // Sharp fangs
            val fangs = Path().apply {
                val my = top + size * 0.75f
                moveTo(left + size * 0.25f, my)
                lineTo(left + size * 0.35f, my + 8f)
                lineTo(left + size * 0.45f, my)
                lineTo(left + size * 0.55f, my + 8f)
                lineTo(left + size * 0.65f, my)
                lineTo(left + size * 0.75f, my + 8f)
            }
            drawPath(fangs, Color(0xFFFF1744), style = Stroke(width = 3.5f))
        }
        "cyborg" -> {
            // Normal left eye
            drawOval(Color.Black, topLeft = Offset(left + size * 0.24f, eyeY), size = Size(eyeW, eyeH))
            // Cybernetic HUD visor on right eye
            drawRoundRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(left + size * 0.52f, eyeY - 6f),
                size = Size(size * 0.38f, size * 0.32f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawCircle(Color.Red, radius = 3.5f, center = Offset(left + size * 0.7f, eyeY + 6f))
        }
    }
}

private fun DrawScope.drawHat(
    hatId: String,
    headLeft: Float,
    headTop: Float,
    headSize: Float,
    cx: Float
) {
    when (hatId) {
        "hat_builder" -> {
            // Classic Yellow Builder Hardhat
            val brimW = headSize * 1.34f
            val brimH = headSize * 0.2f
            val brimTop = headTop - headSize * 0.05f
            val brimLeft = cx - brimW / 2f

            // Dome
            val domeW = headSize * 1.05f
            val domeH = headSize * 0.45f
            drawRoundRect(
                color = Color(0xFFFDD835),
                topLeft = Offset(cx - domeW / 2f, brimTop - domeH * 0.85f),
                size = Size(domeW, domeH),
                cornerRadius = CornerRadius(domeH * 0.7f, domeH * 0.7f)
            )
            // Center Ridge
            drawRoundRect(
                color = Color(0xFFFBC02D),
                topLeft = Offset(cx - domeW * 0.12f, brimTop - domeH * 0.95f),
                size = Size(domeW * 0.24f, domeH),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Brim
            drawRoundRect(
                color = Color(0xFFFDD835),
                topLeft = Offset(brimLeft, brimTop),
                size = Size(brimW, brimH),
                cornerRadius = CornerRadius(brimH * 0.5f, brimH * 0.5f)
            )
        }
        "hat_cap" -> {
            val brimW = headSize * 1.2f
            val capDome = headSize * 0.38f
            drawRoundRect(
                color = Color(0xFFE53935),
                topLeft = Offset(headLeft - headSize * 0.05f, headTop - capDome * 0.7f),
                size = Size(headSize * 1.1f, capDome),
                cornerRadius = CornerRadius(capDome * 0.5f, capDome * 0.5f)
            )
            // White brim
            drawRect(
                color = Color.White,
                topLeft = Offset(headLeft - headSize * 0.1f, headTop - 2f),
                size = Size(brimW, 7f)
            )
        }
        "hat_valkyrie" -> {
            // Golden Valkyrie Helm
            val helmTop = headTop - headSize * 0.3f
            drawRoundRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(headLeft - 2f, helmTop),
                size = Size(headSize + 4f, headSize * 0.4f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Ruby gem center
            drawCircle(Color(0xFFE53935), radius = 5f, center = Offset(cx, helmTop + 10f))
            // Left Wing
            val leftWing = Path().apply {
                moveTo(headLeft - 4f, helmTop + 5f)
                lineTo(headLeft - headSize * 0.45f, helmTop - headSize * 0.4f)
                lineTo(headLeft, helmTop - headSize * 0.1f)
                close()
            }
            drawPath(leftWing, Color(0xFFFFE082))
            // Right Wing
            val rightWing = Path().apply {
                moveTo(headLeft + headSize + 4f, helmTop + 5f)
                lineTo(headLeft + headSize * 1.45f, helmTop - headSize * 0.4f)
                lineTo(headLeft + headSize, helmTop - headSize * 0.1f)
                close()
            }
            drawPath(rightWing, Color(0xFFFFE082))
        }
        "hat_dominus" -> {
            // Dominus Hood
            val hoodW = headSize * 1.35f
            val hoodH = headSize * 1.15f
            val hoodTop = headTop - headSize * 0.25f
            drawRoundRect(
                color = Color(0xFF311B92),
                topLeft = Offset(cx - hoodW / 2f, hoodTop),
                size = Size(hoodW, hoodH),
                cornerRadius = CornerRadius(hoodW * 0.35f, hoodW * 0.35f)
            )
            // Golden embroidered rim
            drawRoundRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(cx - hoodW / 2f + 4f, hoodTop + 4f),
                size = Size(hoodW - 8f, hoodH - 8f),
                cornerRadius = CornerRadius(hoodW * 0.3f, hoodW * 0.3f),
                style = Stroke(width = 3.5f)
            )
            // Dark shadow center
            drawCircle(Color(0xFF12005E), radius = headSize * 0.38f, center = Offset(cx, headTop + headSize * 0.45f))
            // Glowing white void eyes
            drawCircle(Color.White, radius = 3.5f, center = Offset(cx - 12f, headTop + headSize * 0.45f))
            drawCircle(Color.White, radius = 3.5f, center = Offset(cx + 12f, headTop + headSize * 0.45f))
        }
        "hat_horns" -> {
            // Crimson Demon Horns
            val lHorn = Path().apply {
                moveTo(headLeft + 6f, headTop + 2f)
                quadraticTo(headLeft - 18f, headTop - 25f, headLeft - 6f, headTop - 35f)
                quadraticTo(headLeft + 4f, headTop - 18f, headLeft + 16f, headTop)
                close()
            }
            drawPath(lHorn, Brush.linearGradient(listOf(Color(0xFFFF1744), Color(0xFFFF8A80))))
            val rHorn = Path().apply {
                moveTo(headLeft + headSize - 6f, headTop + 2f)
                quadraticTo(headLeft + headSize + 18f, headTop - 25f, headLeft + headSize + 6f, headTop - 35f)
                quadraticTo(headLeft + headSize - 4f, headTop - 18f, headLeft + headSize - 16f, headTop)
                close()
            }
            drawPath(rHorn, Brush.linearGradient(listOf(Color(0xFFFF1744), Color(0xFFFF8A80))))
        }
        "hat_headphones" -> {
            // DJ Neon Headphones
            val arcTop = headTop - headSize * 0.15f
            drawArc(
                color = Color(0xFF263238),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(headLeft - 4f, arcTop),
                size = Size(headSize + 8f, headSize * 0.7f),
                style = Stroke(width = 6f)
            )
            // Left ear cup
            drawCircle(Color(0xFF00E5FF), radius = 10f, center = Offset(headLeft - 4f, headTop + headSize * 0.45f))
            // Right ear cup
            drawCircle(Color(0xFF00E5FF), radius = 10f, center = Offset(headLeft + headSize + 4f, headTop + headSize * 0.45f))
        }
    }
}

private fun DrawScope.drawWings(cx: Float, cy: Float, totalW: Float) {
    val wingW = totalW * 0.38f
    val wingH = totalW * 0.35f
    // Left Golden Wing
    val lWing = Path().apply {
        moveTo(cx - 15f, cy)
        cubicTo(cx - wingW * 0.6f, cy - wingH * 0.8f, cx - wingW * 1.1f, cy - wingH * 0.4f, cx - wingW * 1.1f, cy + 10f)
        cubicTo(cx - wingW * 0.7f, cy + wingH * 0.5f, cx - wingW * 0.3f, cy + 20f, cx - 15f, cy + 15f)
        close()
    }
    drawPath(lWing, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFEA00), Color(0xFFFFAB00))))
    // Right Golden Wing
    val rWing = Path().apply {
        moveTo(cx + 15f, cy)
        cubicTo(cx + wingW * 0.6f, cy - wingH * 0.8f, cx + wingW * 1.1f, cy - wingH * 0.4f, cx + wingW * 1.1f, cy + 10f)
        cubicTo(cx + wingW * 0.7f, cy + wingH * 0.5f, cx + wingW * 0.3f, cy + 20f, cx + 15f, cy + 15f)
        close()
    }
    drawPath(rWing, Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFEA00), Color(0xFFFFAB00))))
}

private fun DrawScope.drawBackKatana(cx: Float, cy: Float, totalW: Float) {
    // Diagonal Katana Sheath across back
    val swordLen = totalW * 0.6f
    rotate(45f, Offset(cx, cy)) {
        // Blade/sheath
        drawRoundRect(
            color = Color(0xFF212121),
            topLeft = Offset(cx - 4f, cy - swordLen / 2f),
            size = Size(8f, swordLen),
            cornerRadius = CornerRadius(2f, 2f)
        )
        // Emerald green ribbon
        drawRect(
            color = Color(0xFF00E676),
            topLeft = Offset(cx - 5f, cy - swordLen * 0.25f),
            size = Size(10f, 6f)
        )
        // Golden Tsuba crossguard
        drawRect(
            color = Color(0xFFFFD700),
            topLeft = Offset(cx - 8f, cy - swordLen * 0.35f),
            size = Size(16f, 4f)
        )
    }
}

private fun DrawScope.drawColaCan(x: Float, y: Float, armW: Float) {
    drawRoundRect(
        color = Color(0xFFD32F2F),
        topLeft = Offset(x, y),
        size = Size(armW * 0.85f, armW * 1.3f),
        cornerRadius = CornerRadius(3f, 3f)
    )
    drawRect(
        color = Color.White,
        topLeft = Offset(x + 1f, y + armW * 0.4f),
        size = Size(armW * 0.85f - 2f, armW * 0.35f)
    )
}

private fun DrawScope.drawGravityCoil(x: Float, y: Float) {
    // Spring Coil
    for (i in 0..3) {
        drawOval(
            color = Color(0xFF00B0FF),
            topLeft = Offset(x, y + i * 5f),
            size = Size(16f, 6f),
            style = Stroke(width = 2.5f)
        )
    }
}

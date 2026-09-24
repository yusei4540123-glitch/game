package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CatalogItem
import com.example.data.model.UserProfile
import com.example.ui.components.BlockyAvatarView
import com.example.ui.components.BloxHaptics
import com.example.ui.components.RobuxBadge
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

@Composable
fun AvatarEditorScreen(
    userProfile: UserProfile,
    catalogItems: List<CatalogItem>,
    onOpenRobuxShop: () -> Unit,
    onSaveAvatar: (
        head: Long, torso: Long, leftArm: Long, rightArm: Long,
        leftLeg: Long, rightLeg: Long, face: String, hat: String, shirt: String, acc: String
    ) -> Unit,
    onBuyItem: (CatalogItem) -> Unit
) {
    val context = LocalContext.current
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("Skin Tone", "Hats", "Faces", "Clothing", "Gear", "Catalog Shop")

    // Working Avatar State
    var headColor by remember(userProfile) { mutableStateOf(userProfile.headColor) }
    var torsoColor by remember(userProfile) { mutableStateOf(userProfile.torsoColor) }
    var leftArmColor by remember(userProfile) { mutableStateOf(userProfile.leftArmColor) }
    var rightArmColor by remember(userProfile) { mutableStateOf(userProfile.rightArmColor) }
    var leftLegColor by remember(userProfile) { mutableStateOf(userProfile.leftLegColor) }
    var rightLegColor by remember(userProfile) { mutableStateOf(userProfile.rightLegColor) }
    var selectedFace by remember(userProfile) { mutableStateOf(userProfile.faceType) }
    var selectedHat by remember(userProfile) { mutableStateOf(userProfile.hatId) }
    var selectedShirt by remember(userProfile) { mutableStateOf(userProfile.shirtId) }
    var selectedAccessory by remember(userProfile) { mutableStateOf(userProfile.accessoryId) }

    // Body Part Selector for Skin Tone
    var selectedBodyPart by remember { mutableStateOf("All") }
    val bodyParts = listOf("All", "Head", "Torso", "Left Arm", "Right Arm", "Left Leg", "Right Leg")

    val classicRobloxColors = listOf(
        0xFFFEE12B to "Classic Yellow",
        0xFF0055BF to "Bright Blue",
        0xFF35A749 to "Brilliant Green",
        0xFFE53935 to "Bright Red",
        0xFF212121 to "Black",
        0xFFFFFFFF to "White",
        0xFFFF9800 to "Bright Orange",
        0xFF00E5FF to "Cyan",
        0xFFBA68C8 to "Lavender",
        0xFFD7CCC8 to "Mocha Tan",
        0xFF607D8B to "Slate",
        0xFF3E2723 to "Deep Brown"
    )

    // Current working profile preview
    val previewProfile = userProfile.copy(
        headColor = headColor,
        torsoColor = torsoColor,
        leftArmColor = leftArmColor,
        rightArmColor = rightArmColor,
        leftLegColor = leftLegColor,
        rightLegColor = rightLegColor,
        faceType = selectedFace,
        hatId = selectedHat,
        shirtId = selectedShirt,
        accessoryId = selectedAccessory
    )

    fun applyColor(colorValue: Long) {
        when (selectedBodyPart) {
            "All" -> {
                headColor = colorValue
                torsoColor = colorValue
                leftArmColor = colorValue
                rightArmColor = colorValue
                leftLegColor = colorValue
                rightLegColor = colorValue
            }
            "Head" -> headColor = colorValue
            "Torso" -> torsoColor = colorValue
            "Left Arm" -> leftArmColor = colorValue
            "Right Arm" -> rightArmColor = colorValue
            "Left Leg" -> leftLegColor = colorValue
            "Right Leg" -> rightLegColor = colorValue
        }
        onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, selectedFace, selectedHat, selectedShirt, selectedAccessory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloxDarkBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Avatar Editor",
                color = BloxTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            RobuxBadge(amount = userProfile.robux, onClick = onOpenRobuxShop)
        }

        // Live Avatar Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF1F2226), BloxDarkBackground)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            BlockyAvatarView(
                userProfile = previewProfile,
                modifier = Modifier.size(210.dp),
                isAnimated = true,
                showPlatform = true
            )
        }

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            containerColor = BloxSurface,
            contentColor = BloxTextPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                    color = BloxGreenRobux
                )
            }
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = {
                        selectedCategoryIndex = index
                        BloxHaptics.playClick(context)
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Content Area based on Selected Tab
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (selectedCategoryIndex) {
                0 -> {
                    // Skin Tone Selector
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "SELECT BODY PART",
                            color = BloxTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(bodyParts) { part ->
                                val isSelected = selectedBodyPart == part
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BloxGreenRobux else BloxSurfaceElevated)
                                        .clickable {
                                            selectedBodyPart = part
                                            BloxHaptics.playClick(context)
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = part,
                                        color = if (isSelected) Color.White else BloxTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "COLOR PALETTE",
                            color = BloxTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(classicRobloxColors) { (colorLong, name) ->
                                Box(
                                    modifier = Modifier
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(colorLong))
                                        .border(2.dp, BloxBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            applyColor(colorLong)
                                            BloxHaptics.playClick(context)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Empty or subtle checkmark if matched
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Hats Inventory
                    val ownedHats = catalogItems.filter { it.type == "hat" && it.isOwned }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // "None" option
                        item {
                            ItemCard(
                                title = "None",
                                isEquipped = selectedHat == "none",
                                onClick = {
                                    selectedHat = "none"
                                    onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, selectedFace, "none", selectedShirt, selectedAccessory)
                                    BloxHaptics.playClick(context)
                                }
                            )
                        }
                        items(ownedHats) { item ->
                            ItemCard(
                                title = item.name,
                                isEquipped = selectedHat == item.id,
                                onClick = {
                                    selectedHat = item.id
                                    onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, selectedFace, item.id, selectedShirt, selectedAccessory)
                                    BloxHaptics.playClick(context)
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // Faces Inventory
                    val ownedFaces = catalogItems.filter { it.type == "face" && it.isOwned }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(ownedFaces) { item ->
                            val faceCode = item.id.replace("face_", "")
                            ItemCard(
                                title = item.name,
                                isEquipped = selectedFace == faceCode,
                                onClick = {
                                    selectedFace = faceCode
                                    onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, faceCode, selectedHat, selectedShirt, selectedAccessory)
                                    BloxHaptics.playClick(context)
                                }
                            )
                        }
                    }
                }
                3 -> {
                    // Clothing Inventory
                    val ownedShirts = catalogItems.filter { it.type == "shirt" && it.isOwned }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(ownedShirts) { item ->
                            ItemCard(
                                title = item.name,
                                isEquipped = selectedShirt == item.id,
                                onClick = {
                                    selectedShirt = item.id
                                    onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, selectedFace, selectedHat, item.id, selectedAccessory)
                                    BloxHaptics.playClick(context)
                                }
                            )
                        }
                    }
                }
                4 -> {
                    // Gear Inventory
                    val ownedGear = catalogItems.filter { it.type == "gear" && it.isOwned }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            ItemCard(
                                title = "None",
                                isEquipped = selectedAccessory == "none",
                                onClick = {
                                    selectedAccessory = "none"
                                    onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, selectedFace, selectedHat, selectedShirt, "none")
                                    BloxHaptics.playClick(context)
                                }
                            )
                        }
                        items(ownedGear) { item ->
                            ItemCard(
                                title = item.name,
                                isEquipped = selectedAccessory == item.id,
                                onClick = {
                                    selectedAccessory = item.id
                                    onSaveAvatar(headColor, torsoColor, leftArmColor, rightArmColor, leftLegColor, rightLegColor, selectedFace, selectedHat, selectedShirt, item.id)
                                    BloxHaptics.playClick(context)
                                }
                            )
                        }
                    }
                }
                5 -> {
                    // Catalog Shop to purchase unowned items
                    val shopItems = catalogItems.filter { !it.isOwned }
                    if (shopItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("You own all catalog items! 🎉", color = BloxGold, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(shopItems) { item ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BloxSurface),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(BloxSurfaceElevated),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ShoppingBag,
                                                contentDescription = item.name,
                                                tint = BloxGreenLight
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.name,
                                            color = BloxTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = item.rarity,
                                            color = BloxGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { onBuyItem(item) },
                                            enabled = userProfile.robux >= item.priceRobux,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BloxGreenRobux,
                                                disabledContainerColor = BloxSurfaceElevated
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${item.priceRobux} R$",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
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
}

@Composable
fun ItemCard(
    title: String,
    isEquipped: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BloxSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isEquipped) BloxGreenRobux else BloxBorder
        ),
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(90.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = BloxTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
            if (isEquipped) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Equipped",
                        tint = BloxGreenRobux,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "EQUIPPED",
                        color = BloxGreenRobux,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

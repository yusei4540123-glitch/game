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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Experience
import com.example.data.model.Friend
import com.example.data.model.UserProfile
import com.example.ui.components.BloxHaptics
import com.example.ui.components.RobloxLogo
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
fun HomeScreen(
    userProfile: UserProfile,
    experiences: List<Experience>,
    friends: List<Friend>,
    onPlayExperience: (Experience) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onOpenRobuxShop: () -> Unit,
    onNavigateToAvatar: () -> Unit
) {
    val context = LocalContext.current
    val featuredExp = experiences.firstOrNull { it.id == "exp_obby_tower" } ?: experiences.firstOrNull()
    val popularExps = experiences.filter { it.id != featuredExp?.id }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BloxDarkBackground)
            .padding(bottom = 80.dp)
    ) {
        // App Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 18.dp, end = 18.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RobloxLogo(size = 30.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BloxWorld",
                        color = BloxTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                RobuxBadge(amount = userProfile.robux, onClick = onOpenRobuxShop)
            }
        }

        // User Welcome & Avatar Strip
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BloxSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar Head thumbnail
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(userProfile.headColor))
                                .border(2.dp, BloxBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userProfile.displayName,
                                    color = BloxTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (userProfile.isPremium) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "★",
                                        color = BloxGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "@${userProfile.username} • Level ${userProfile.level}",
                                color = BloxTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = onNavigateToAvatar,
                        colors = ButtonDefaults.buttonColors(containerColor = BloxSurfaceElevated),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Edit", color = BloxTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Friends Online Horizontal Bar
        item {
            Column(modifier = Modifier.padding(top = 14.dp)) {
                Text(
                    text = "FRIENDS ONLINE",
                    color = BloxTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(friends) { friend ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(74.dp)
                                .clickable {
                                    // Quick join or view friend
                                    BloxHaptics.playClick(context)
                                }
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(friend.avatarHeadColor))
                                        .border(2.dp, BloxBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎮", fontSize = 20.sp)
                                }
                                if (friend.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(BloxGreenLight)
                                            .border(2.dp, BloxDarkBackground, CircleShape)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = friend.displayName,
                                color = BloxTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Featured Hero Experience
        if (featuredExp != null) {
            item {
                Column(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
                    Text(
                        text = "FEATURED EXPERIENCE",
                        color = BloxTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = BloxSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                BloxHaptics.playClick(context)
                                onPlayExperience(featuredExp)
                            }
                    ) {
                        Column {
                            // Hero Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_obby_banner_1790278303257),
                                    contentDescription = featuredExp.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Gradient scrim
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                )

                                // Rating & Players chips on banner
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ThumbUp, contentDescription = "Likes", tint = BloxGreenLight, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${featuredExp.ratingPercent}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("👥 ${featuredExp.activePlayers / 1000}K Playing", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Card Bottom Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = featuredExp.title,
                                        color = BloxTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "By ${featuredExp.creator}",
                                        color = BloxTextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        BloxHaptics.playClick(context)
                                        onPlayExperience(featuredExp)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BloxGreenRobux),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PLAY", color = Color.White, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Popular & Recommended Experiences Grid
        item {
            Column(modifier = Modifier.padding(top = 22.dp, start = 16.dp, end = 16.dp)) {
                Text(
                    text = "POPULAR IN BLOXWORLD",
                    color = BloxTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(popularExps) { exp ->
            Card(
                colors = CardDefaults.cardColors(containerColor = BloxSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable {
                        BloxHaptics.playClick(context)
                        onPlayExperience(exp)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Experience Thumbnail preview
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(exp.accentColor))
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (exp.category == "Obby") R.drawable.img_obby_banner_1790278303257 else R.drawable.img_blox_universe_1790278319122
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
                            text = "By ${exp.creator}",
                            color = BloxTextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👍 ${exp.ratingPercent}%", color = BloxGreenLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("👥 ${exp.activePlayers / 1000}K", color = BloxTextSecondary, fontSize = 11.sp)
                        }
                    }

                    IconButton(
                        onClick = {
                            onToggleFavorite(exp.id, exp.isFavorite)
                            BloxHaptics.playClick(context)
                        }
                    ) {
                        Icon(
                            imageVector = if (exp.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (exp.isFavorite) BloxRedRoblox else BloxTextSecondary
                        )
                    }
                }
            }
        }
    }
}

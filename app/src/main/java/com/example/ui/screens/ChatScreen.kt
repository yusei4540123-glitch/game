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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Chat
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
import com.example.data.model.ChatMessage
import com.example.data.model.Friend
import com.example.data.model.UserProfile
import com.example.ui.components.BloxHaptics
import com.example.ui.theme.BloxBorder
import com.example.ui.theme.BloxDarkBackground
import com.example.ui.theme.BloxGreenLight
import com.example.ui.theme.BloxGreenRobux
import com.example.ui.theme.BloxSurface
import com.example.ui.theme.BloxSurfaceElevated
import com.example.ui.theme.BloxTextPrimary
import com.example.ui.theme.BloxTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    userProfile: UserProfile,
    messages: List<ChatMessage>,
    friends: List<Friend>,
    onSendMessage: (channel: String, text: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Global Chat, 1: Friends
    var currentText by remember { mutableStateOf("") }
    var selectedFriend by remember { mutableStateOf<Friend?>(null) }

    val quickChatPhrases = listOf(
        "Wanna play Tower of Hell? 🔥",
        "GG! Well played!",
        "Check out my new avatar! ✨",
        "Anyone trading in Pet Sim? 🐾",
        "Join my server!",
        "Nice studio build!"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BloxDarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Chat",
                tint = BloxGreenRobux,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Blox Chat",
                color = BloxTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tabs: Global Universe vs Friends
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = BloxSurface,
            contentColor = BloxTextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = BloxGreenRobux
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    selectedFriend = null
                },
                text = { Text("Universe Chat", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Friends (${friends.count { it.isOnline }})", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 1 && selectedFriend == null) {
            // Friends List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(friends) { friend ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BloxSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BloxBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFriend = friend
                                BloxHaptics.playClick(context)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(friend.avatarHeadColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎮", fontSize = 18.sp)
                                }
                                if (friend.isOnline) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(CircleShape)
                                            .background(BloxGreenLight)
                                            .border(2.dp, BloxDarkBackground, CircleShape)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = friend.displayName,
                                    color = BloxTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = friend.status,
                                    color = if (friend.isOnline) BloxGreenLight else BloxTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Text("Message", color = BloxGreenRobux, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Chat Conversation View (Global or with Selected Friend)
            val channel = if (selectedFriend != null) selectedFriend!!.username else "global"

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // If friend selected, show back bar
                if (selectedFriend != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BloxSurfaceElevated)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◀ Back to Friends",
                            color = BloxGreenRobux,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { selectedFriend = null }
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Chat with ${selectedFriend!!.displayName}",
                            color = BloxTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.isFromUser
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isUser) BloxGreenRobux else BloxSurface)
                                    .border(1.dp, if (isUser) BloxGreenRobux else BloxBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth(0.78f)
                            ) {
                                if (!isUser) {
                                    Text(
                                        text = msg.senderName,
                                        color = Color(0xFF00E5FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = msg.messageText,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp)),
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 9.sp,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                // Quick Chat Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    items(quickChatPhrases) { phrase ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(BloxSurfaceElevated)
                                .clickable {
                                    onSendMessage(channel, phrase)
                                    BloxHaptics.playClick(context)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(phrase, color = BloxTextPrimary, fontSize = 11.sp)
                        }
                    }
                }

                // Message Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 90.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentText,
                        onValueChange = { currentText = it },
                        placeholder = { Text("Type a message...", color = BloxTextSecondary, fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BloxGreenRobux,
                            unfocusedBorderColor = BloxBorder,
                            focusedTextColor = BloxTextPrimary,
                            unfocusedTextColor = BloxTextPrimary,
                            focusedContainerColor = BloxSurface,
                            unfocusedContainerColor = BloxSurface
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (currentText.isNotBlank()) {
                                onSendMessage(channel, currentText.trim())
                                currentText = ""
                                BloxHaptics.playClick(context)
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(BloxGreenRobux, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

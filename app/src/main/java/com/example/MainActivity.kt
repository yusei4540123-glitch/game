package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserProfile
import com.example.ui.components.BloxHaptics
import com.example.ui.components.RobuxShopSheet
import com.example.ui.screens.AvatarEditorScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ObbyGameScreen
import com.example.ui.screens.PetSimulatorScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.BloxBorder
import com.example.ui.theme.BloxDarkBackground
import com.example.ui.theme.BloxGreenRobux
import com.example.ui.theme.BloxSurface
import com.example.ui.theme.BloxTextPrimary
import com.example.ui.theme.BloxTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ActiveGameSession
import com.example.ui.viewmodel.BloxViewModel

enum class BloxTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    DISCOVER("Discover", Icons.Default.Explore),
    AVATAR("Avatar", Icons.Default.Person),
    STUDIO("Studio", Icons.Default.Build),
    CHAT("Chat", Icons.Default.ChatBubble)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BloxApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloxApp(viewModel: BloxViewModel = viewModel()) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val experiences by viewModel.allExperiences.collectAsStateWithLifecycle()
    val userCreations by viewModel.userCreations.collectAsStateWithLifecycle()
    val catalogItems by viewModel.catalogItems.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val activeGame by viewModel.activeGame.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }
    var isRobuxShopOpen by remember { mutableStateOf(false) }
    val robuxSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val profile = userProfile ?: UserProfile()

    // If active game is running, show full screen game view
    when (val session = activeGame) {
        is ActiveGameSession.Obby -> {
            ObbyGameScreen(
                userProfile = profile,
                gameTitle = session.title,
                customBlocks = session.customMapData,
                onLeaveGame = { viewModel.exitGame() },
                onWinReward = { amount -> viewModel.addRobux(amount) }
            )
        }
        is ActiveGameSession.PetSimulator -> {
            PetSimulatorScreen(
                currentRobux = profile.robux,
                onLeaveGame = { viewModel.exitGame() },
                onAddRobux = { amount -> viewModel.addRobux(amount) }
            )
        }
        ActiveGameSession.None -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        containerColor = BloxSurface,
                        contentColor = BloxTextPrimary,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        BloxTab.entries.forEachIndexed { index, tab ->
                            val isSelected = currentTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    currentTab = index
                                    BloxHaptics.playClick(context)
                                },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    indicatorColor = BloxGreenRobux,
                                    unselectedIconColor = BloxTextSecondary,
                                    unselectedTextColor = BloxTextSecondary
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BloxDarkBackground)
                        .padding(innerPadding)
                ) {
                    Crossfade(targetState = currentTab, label = "tabFade") { tabIndex ->
                        when (tabIndex) {
                            0 -> HomeScreen(
                                userProfile = profile,
                                experiences = experiences,
                                friends = viewModel.friends,
                                onPlayExperience = { exp -> viewModel.launchGame(exp) },
                                onToggleFavorite = { id, currentFav -> viewModel.toggleFavorite(id, currentFav) },
                                onOpenRobuxShop = { isRobuxShopOpen = true },
                                onNavigateToAvatar = { currentTab = 2 }
                            )
                            1 -> DiscoverScreen(
                                experiences = experiences,
                                onPlayExperience = { exp -> viewModel.launchGame(exp) }
                            )
                            2 -> AvatarEditorScreen(
                                userProfile = profile,
                                catalogItems = catalogItems,
                                onOpenRobuxShop = { isRobuxShopOpen = true },
                                onSaveAvatar = { h, t, la, ra, ll, rl, f, hat, s, acc ->
                                    viewModel.updateAvatar(h, t, la, ra, ll, rl, f, hat, s, acc)
                                },
                                onBuyItem = { item -> viewModel.buyCatalogItem(item) }
                            )
                            3 -> StudioScreen(
                                userProfile = profile,
                                userCreations = userCreations,
                                onTestPlay = { title, customMapData ->
                                    viewModel.launchCustomObby(title, customMapData)
                                },
                                onPublish = { exp -> viewModel.publishCreation(exp) },
                                onDeleteCreation = { id -> viewModel.deleteCreation(id) }
                            )
                            4 -> ChatScreen(
                                userProfile = profile,
                                messages = chatMessages,
                                friends = viewModel.friends,
                                onSendMessage = { ch, txt -> viewModel.sendChatMessage(ch, txt) }
                            )
                        }
                    }
                }
            }

            // Robux Shop Bottom Sheet
            if (isRobuxShopOpen) {
                RobuxShopSheet(
                    currentRobux = profile.robux,
                    sheetState = robuxSheetState,
                    onDismiss = { isRobuxShopOpen = false },
                    onAddRobux = { amount -> viewModel.addRobux(amount) }
                )
            }
        }
    }
}

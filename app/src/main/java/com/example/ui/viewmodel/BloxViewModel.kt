package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BloxDatabase
import com.example.data.db.BloxRepository
import com.example.data.model.CatalogItem
import com.example.data.model.ChatMessage
import com.example.data.model.Experience
import com.example.data.model.Friend
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ActiveGameSession {
    data object None : ActiveGameSession
    data class Obby(val title: String, val customMapData: String = "") : ActiveGameSession
    data object PetSimulator : ActiveGameSession
}

class BloxViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BloxRepository

    val userProfile: StateFlow<UserProfile?>
    val allExperiences: StateFlow<List<Experience>>
    val userCreations: StateFlow<List<Experience>>
    val catalogItems: StateFlow<List<CatalogItem>>
    val friends: List<Friend>

    private val _activeGame = MutableStateFlow<ActiveGameSession>(ActiveGameSession.None)
    val activeGame: StateFlow<ActiveGameSession> = _activeGame.asStateFlow()

    private val _selectedChatChannel = MutableStateFlow("global")
    val selectedChatChannel: StateFlow<String> = _selectedChatChannel.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>>

    init {
        val db = BloxDatabase.getDatabase(application)
        repository = BloxRepository(db.bloxDao())

        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }

        userProfile = repository.userProfile
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

        allExperiences = repository.allExperiences
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        userCreations = repository.userCreations
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        catalogItems = repository.catalogItems
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        friends = repository.getFriendsList()

        chatMessages = repository.getChatMessages("global")
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addRobux(amount: Int) {
        viewModelScope.launch {
            val current = userProfile.value?.robux ?: 0
            repository.updateRobux(current + amount)
        }
    }

    fun updateAvatar(
        head: Long, torso: Long, leftArm: Long, rightArm: Long,
        leftLeg: Long, rightLeg: Long, face: String, hat: String, shirt: String, acc: String
    ) {
        viewModelScope.launch {
            repository.updateAvatar(head, torso, leftArm, rightArm, leftLeg, rightLeg, face, hat, shirt, acc)
        }
    }

    fun buyCatalogItem(item: CatalogItem) {
        viewModelScope.launch {
            val current = userProfile.value?.robux ?: 0
            repository.purchaseCatalogItem(item, current)
        }
    }

    fun toggleFavorite(id: String, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, currentFav)
        }
    }

    fun publishCreation(experience: Experience) {
        viewModelScope.launch {
            repository.saveCustomCreation(experience)
        }
    }

    fun deleteCreation(id: String) {
        viewModelScope.launch {
            repository.deleteCustomCreation(id)
        }
    }

    fun launchGame(experience: Experience) {
        if (experience.category == "Simulator") {
            _activeGame.value = ActiveGameSession.PetSimulator
        } else {
            _activeGame.value = ActiveGameSession.Obby(
                title = experience.title,
                customMapData = experience.customMapData
            )
        }
    }

    fun launchCustomObby(title: String, customMapData: String) {
        _activeGame.value = ActiveGameSession.Obby(title, customMapData)
    }

    fun exitGame() {
        _activeGame.value = ActiveGameSession.None
    }

    fun sendChatMessage(channel: String, text: String) {
        viewModelScope.launch {
            val username = userProfile.value?.displayName ?: "Me"
            repository.sendChatMessage(channel, username, text, isUser = true)

            // Dynamic response from bot friends / players
            kotlinx.coroutines.delay(1500)
            val autoResponses = listOf(
                "Nice one! Adding you as friend!",
                "Check out level 5, it's wild!",
                "Trading my rare pet for Robux items!",
                "Haha GG!",
                "Join our server voice call in Discord!"
            )
            val botName = listOf("BlockySam", "PixelGamer9", "ValkyrieQueen", "Builderman")[kotlin.random.Random.nextInt(4)]
            repository.sendChatMessage(channel, botName, autoResponses[kotlin.random.Random.nextInt(autoResponses.size)], isUser = false)
        }
    }
}

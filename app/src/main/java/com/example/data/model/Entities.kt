package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "BloxMaster_99",
    val displayName: String = "Alex Blox",
    val robux: Int = 850,
    val isPremium: Boolean = true,
    val level: Int = 12,
    val exp: Int = 340,
    // Avatar Colors (ARGB)
    val headColor: Long = 0xFFFEE12B,       // Classic Roblox Yellow
    val torsoColor: Long = 0xFF0055BF,      // Classic Roblox Blue
    val leftArmColor: Long = 0xFFFEE12B,
    val rightArmColor: Long = 0xFFFEE12B,
    val leftLegColor: Long = 0xFF35A749,    // Classic Roblox Green
    val rightLegColor: Long = 0xFF35A749,
    // Equipped Items
    val faceType: String = "smile",         // smile, chill, winning, beast, cyborg
    val hatId: String = "hat_builder",      // hat_builder, hat_valkyrie, hat_dominus, hat_cap, hat_horns, none
    val shirtId: String = "shirt_classic",  // shirt_classic, shirt_galaxy, shirt_suit, shirt_cyber
    val accessoryId: String = "gear_sword"  // gear_sword, gear_wings, gear_cola, gear_coil, none
)

@Entity(tableName = "experiences")
data class Experience(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val creator: String,
    val category: String, // Obby, Simulator, Tycoon, Survival, Adventure, Studio
    val ratingPercent: Int,
    val activePlayers: Int,
    val totalVisits: String,
    val thumbnailResName: String,
    val accentColor: Long,
    val isFavorite: Boolean = false,
    val isUserCreated: Boolean = false,
    val customMapData: String = "" // JSON or block layout string
)

@Entity(tableName = "catalog_items")
data class CatalogItem(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // "hat", "face", "shirt", "gear"
    val priceRobux: Int,
    val isOwned: Boolean,
    val rarity: String, // Common, Rare, Epic, Legendary, Mythic
    val description: String,
    val colorHex: Long = 0xFFFFFFFF
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val channel: String, // "global" or friend username
    val senderName: String,
    val messageText: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class Friend(
    val username: String,
    val displayName: String,
    val status: String,
    val isOnline: Boolean,
    val currentActivity: String,
    val avatarHeadColor: Long,
    val avatarTorsoColor: Long
)

package com.example.data.db

import com.example.data.model.CatalogItem
import com.example.data.model.ChatMessage
import com.example.data.model.Experience
import com.example.data.model.Friend
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BloxRepository(private val dao: BloxDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allExperiences: Flow<List<Experience>> = dao.getAllExperiences()
    val favoriteExperiences: Flow<List<Experience>> = dao.getFavoriteExperiences()
    val userCreations: Flow<List<Experience>> = dao.getUserCreations()
    val catalogItems: Flow<List<CatalogItem>> = dao.getAllCatalogItems()

    fun getChatMessages(channel: String): Flow<List<ChatMessage>> = dao.getChatMessages(channel)

    suspend fun initializeDefaultDataIfEmpty() {
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile == null) {
            dao.insertOrUpdateProfile(UserProfile())
        }

        // Check if experiences already seeded
        val existingExps = dao.getAllExperiences().firstOrNull()
        if (existingExps.isNullOrEmpty()) {
            val defaultExperiences = listOf(
                Experience(
                    id = "exp_obby_tower",
                    title = "Tower of Hell: Infinite Obby",
                    description = "Ascend the chaotic tower of neon hurdles, moving laser barriers, and bounce pads. Race against the clock and reach the golden summit!",
                    creator = "BloxWorks Studio",
                    category = "Obby",
                    ratingPercent = 94,
                    activePlayers = 42800,
                    totalVisits = "1.2B",
                    thumbnailResName = "img_obby_banner_1790278303257",
                    accentColor = 0xFFE13B36,
                    isFavorite = true
                ),
                Experience(
                    id = "exp_pet_sim",
                    title = "Pet Hatching Simulator X",
                    description = "Hatch mythical blocky eggs, collect golden dragons and celestial cats, tap coin crystals, and unlock fantasy floating islands!",
                    creator = "BigBlox Games",
                    category = "Simulator",
                    ratingPercent = 92,
                    activePlayers = 38400,
                    totalVisits = "850M",
                    thumbnailResName = "img_blox_universe_1790278319122",
                    accentColor = 0xFF00A2FF,
                    isFavorite = true
                ),
                Experience(
                    id = "exp_blox_tycoon",
                    title = "Blox City: Megapolis Tycoon",
                    description = "Build skyscrapers, drive hypercars, roleplay with friends in the penthouse, and become the richest Bloxian tycoon in town.",
                    creator = "Metropolis Team",
                    category = "Tycoon",
                    ratingPercent = 89,
                    activePlayers = 21500,
                    totalVisits = "410M",
                    thumbnailResName = "img_blox_universe_1790278319122",
                    accentColor = 0xFFFFAA00
                ),
                Experience(
                    id = "exp_speed_run",
                    title = "Speed Run 4: Hyperspace",
                    description = "Sprint across 30 fast-paced dimensions with speed boost coils, zero gravity jumps, and energetic electronic beats.",
                    creator = "VoxelRacer",
                    category = "Obby",
                    ratingPercent = 91,
                    activePlayers = 16300,
                    totalVisits = "320M",
                    thumbnailResName = "img_obby_banner_1790278303257",
                    accentColor = 0xFF00B06F
                ),
                Experience(
                    id = "exp_disaster",
                    title = "Natural Disaster Survival",
                    description = "Survive tsunamis, acid rain, meteor showers, and violent earthquakes on a destructible floating voxel island.",
                    creator = "StickMasterBlox",
                    category = "Survival",
                    ratingPercent = 96,
                    activePlayers = 29400,
                    totalVisits = "2.1B",
                    thumbnailResName = "img_blox_universe_1790278319122",
                    accentColor = 0xFFFF5722
                ),
                Experience(
                    id = "exp_hide_seek",
                    title = "Blox Royale: Prop Hunt & Arena",
                    description = "Disguise yourself as everyday items or hunt down hidden props with laser blasters before the countdown runs out!",
                    creator = "TwoBlox Devs",
                    category = "Roleplay",
                    ratingPercent = 88,
                    activePlayers = 12700,
                    totalVisits = "195M",
                    thumbnailResName = "img_blox_universe_1790278319122",
                    accentColor = 0xFF9C27B0
                )
            )
            dao.insertExperiences(defaultExperiences)
        }

        // Seed Catalog
        val existingItems = dao.getAllCatalogItems().firstOrNull()
        if (existingItems.isNullOrEmpty()) {
            val defaultCatalog = listOf(
                // Hats
                CatalogItem("hat_builder", "Classic Builder Hardhat", "hat", 0, true, "Common", "The iconic yellow construction hardhat for true makers.", 0xFFFDD835),
                CatalogItem("hat_cap", "Blox Baseball Cap", "hat", 50, true, "Common", "Casual red-and-white cap featuring the tilt logo.", 0xFFE53935),
                CatalogItem("hat_valkyrie", "Valkyrie Helm of Glory", "hat", 1200, false, "Legendary", "Ancient mythical helmet forged with golden wings and ruby accents.", 0xFFFFD700),
                CatalogItem("hat_dominus", "Dominus Aureus Hood", "hat", 2500, false, "Mythic", "Legendary feathered hood of ultimate prestige and darkness.", 0xFF8E24AA),
                CatalogItem("hat_fedora", "Sparkle Time Fedora", "hat", 750, false, "Epic", "Sleek obsidian fedora with sparkling cyan ribbon.", 0xFF00E5FF),
                CatalogItem("hat_horns", "Crimson Demon Horns", "hat", 350, false, "Rare", "Fiery glowing horns that radiate ember particles.", 0xFFFF1744),
                CatalogItem("hat_headphones", "Cyber Neon DJ Headphones", "hat", 200, false, "Rare", "Pulse with real-time soundwaves and glowing LED rings.", 0xFF00E676),

                // Faces
                CatalogItem("face_smile", "Classic Blox Smile", "face", 0, true, "Common", "The timeless friendly smile of a veteran player.", 0xFFFFFFFF),
                CatalogItem("face_chill", "Chill Relaxed Face", "face", 40, true, "Common", "Never worried, always chill in any lobby.", 0xFFFFFFFF),
                CatalogItem("face_winning", "Winning Smile", "face", 100, false, "Rare", "Confident and triumphant grin for winners.", 0xFFFFFFFF),
                CatalogItem("face_beast", "Beast Mode Grin", "face", 450, false, "Epic", "Intense fiery gaze ready for arena combat.", 0xFFFF5252),
                CatalogItem("face_cyborg", "Cyborg HUD Visor", "face", 600, false, "Legendary", "Tactical holographic target overlay over right eye.", 0xFF00E5FF),

                // Shirts
                CatalogItem("shirt_classic", "Classic Blox Logo Tee", "shirt", 0, true, "Common", "Retro grey tee with the iconic block emblem.", 0xFF37474F),
                CatalogItem("shirt_galaxy", "Galaxy Nebula Hoodie", "shirt", 150, false, "Rare", "Deep space nebula swirling with purple and cyan stars.", 0xFF7C4DFF),
                CatalogItem("shirt_suit", "Secret Agent Tuxedo", "shirt", 250, false, "Epic", "Sophisticated black tuxedo with silk lapels and crimson bowtie.", 0xFF212121),
                CatalogItem("shirt_cyber", "Cyberpunk Armor Vest", "shirt", 500, false, "Legendary", "Reinforced Kevlar composite with glowing neon power lines.", 0xFF00E5FF),

                // Gear
                CatalogItem("gear_sword", "Darkheart Emerald Katana", "gear", 300, true, "Rare", "Twin katanas sheathed at the back with green energy trail.", 0xFF00E676),
                CatalogItem("gear_wings", "Golden Celestial Wings", "gear", 1500, false, "Mythic", "Magnificent glowing wings that flap gently as you walk.", 0xFFFFD700),
                CatalogItem("gear_cola", "Vintage Bloxy Cola", "gear", 80, true, "Common", "Crisp carbonated voxel soda that restores stamina.", 0xFFD84315),
                CatalogItem("gear_coil", "Gravity Coil", "gear", 400, false, "Epic", "Spring-loaded anti-gravity coil that grants super high jumps!", 0xFF00B0FF)
            )
            dao.insertCatalogItems(defaultCatalog)
        }

        // Seed initial Global chat messages
        val existingMessages = dao.getChatMessages("global").firstOrNull()
        if (existingMessages.isNullOrEmpty()) {
            dao.insertChatMessage(ChatMessage(channel = "global", senderName = "NoobMaster77", messageText = "Anyone want to race in Tower of Hell?? Level 4 is insane!", isFromUser = false, timestamp = System.currentTimeMillis() - 180000))
            dao.insertChatMessage(ChatMessage(channel = "global", senderName = "Builderman_Official", messageText = "Welcome to BloxWorld! Don't forget to check the Avatar Shop for daily rewards.", isFromUser = false, timestamp = System.currentTimeMillis() - 120000))
            dao.insertChatMessage(ChatMessage(channel = "global", senderName = "ValkyrieQueen", messageText = "Just hatched a Mythic Dragon in Pet Sim X!! 🐲✨", isFromUser = false, timestamp = System.currentTimeMillis() - 60000))
        }
    }

    suspend fun updateRobux(newAmount: Int) = dao.updateRobux(newAmount)

    suspend fun updateAvatar(
        head: Long, torso: Long, leftArm: Long, rightArm: Long,
        leftLeg: Long, rightLeg: Long, face: String, hat: String, shirt: String, acc: String
    ) {
        dao.updateAvatarCustomization(head, torso, leftArm, rightArm, leftLeg, rightLeg, face, hat, shirt, acc)
    }

    suspend fun purchaseCatalogItem(item: CatalogItem, currentRobux: Int): Boolean {
        if (currentRobux >= item.priceRobux) {
            val newRobux = currentRobux - item.priceRobux
            dao.updateRobux(newRobux)
            dao.markItemOwned(item.id)
            return true
        }
        return false
    }

    suspend fun toggleFavorite(experienceId: String, currentStatus: Boolean) {
        dao.setFavorite(experienceId, !currentStatus)
    }

    suspend fun saveCustomCreation(experience: Experience) {
        dao.insertExperience(experience)
    }

    suspend fun deleteCustomCreation(id: String) {
        dao.deleteUserCreation(id)
    }

    suspend fun sendChatMessage(channel: String, sender: String, text: String, isUser: Boolean = true) {
        dao.insertChatMessage(
            ChatMessage(
                channel = channel,
                senderName = sender,
                messageText = text,
                isFromUser = isUser,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun getFriendsList(): List<Friend> {
        return listOf(
            Friend("BlockySam", "Sam The Builder", "In Experience: Tower of Hell", true, "Tower of Hell: Infinite Obby", 0xFFFEE12B, 0xFFE53935),
            Friend("PixelGamer9", "Pixel Hunter", "In Experience: Pet Sim X", true, "Pet Hatching Simulator X", 0xFFFEE12B, 0xFF00E5FF),
            Friend("Builderman", "Blox Official", "In Studio: Crafting World", true, "Studio Creator", 0xFFFEE12B, 0xFF35A749),
            Friend("CyberNinja", "Neon Shadow", "Online in Lobby", true, "Main Hub", 0xFF212121, 0xFF00E5FF),
            Friend("GoldenValk", "Valkyrie Pro", "In Experience: Speed Run 4", true, "Speed Run 4", 0xFFFFD700, 0xFF8E24AA),
            Friend("FrostyBlox", "Arctic King", "Offline (3h ago)", false, "None", 0xFFB2EBF2, 0xFF0277BD)
        )
    }
}

package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CatalogItem
import com.example.data.model.ChatMessage
import com.example.data.model.Experience
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface BloxDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET robux = :newBalance WHERE id = 1")
    suspend fun updateRobux(newBalance: Int)

    @Query("""
        UPDATE user_profile 
        SET headColor = :head, torsoColor = :torso, leftArmColor = :leftArm, 
            rightArmColor = :rightArm, leftLegColor = :leftLeg, rightLegColor = :rightLeg,
            faceType = :face, hatId = :hat, shirtId = :shirt, accessoryId = :acc
        WHERE id = 1
    """)
    suspend fun updateAvatarCustomization(
        head: Long, torso: Long, leftArm: Long, rightArm: Long,
        leftLeg: Long, rightLeg: Long, face: String, hat: String, shirt: String, acc: String
    )

    // Experiences
    @Query("SELECT * FROM experiences ORDER BY isUserCreated DESC, activePlayers DESC")
    fun getAllExperiences(): Flow<List<Experience>>

    @Query("SELECT * FROM experiences WHERE isFavorite = 1")
    fun getFavoriteExperiences(): Flow<List<Experience>>

    @Query("SELECT * FROM experiences WHERE isUserCreated = 1")
    fun getUserCreations(): Flow<List<Experience>>

    @Query("SELECT * FROM experiences WHERE id = :id LIMIT 1")
    suspend fun getExperienceById(id: String): Experience?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiences(experiences: List<Experience>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperience(experience: Experience)

    @Query("UPDATE experiences SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: String, isFav: Boolean)

    @Query("DELETE FROM experiences WHERE id = :id AND isUserCreated = 1")
    suspend fun deleteUserCreation(id: String)

    // Catalog Items
    @Query("SELECT * FROM catalog_items")
    fun getAllCatalogItems(): Flow<List<CatalogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalogItems(items: List<CatalogItem>)

    @Query("UPDATE catalog_items SET isOwned = 1 WHERE id = :itemId")
    suspend fun markItemOwned(itemId: String)

    // Chat
    @Query("SELECT * FROM chat_messages WHERE channel = :channel ORDER BY timestamp ASC")
    fun getChatMessages(channel: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)
}

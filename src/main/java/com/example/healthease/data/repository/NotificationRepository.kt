package com.example.healthease.data.repository

import com.example.healthease.data.local.NotificationDao
import com.example.healthease.data.local.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val dao: NotificationDao) {
    fun observeAll(userId: String): Flow<List<NotificationEntity>> = dao.observeAll(userId)
    fun observeUnread(userId: String): Flow<Int> = dao.observeUnreadCount(userId)
    suspend fun add(n: NotificationEntity) = dao.insert(n)
    suspend fun markRead(id: String) = dao.markRead(id)
    suspend fun markAllRead(userId: String) = dao.markAllRead(userId)
    suspend fun clearAll(userId: String) = dao.clearAll(userId)

    /** Helper used by reminders to drop a notification into the in-app center. */
    suspend fun notify(
        userId: String,
        type: String,
        title: String,
        body: String,
        actionTarget: String? = null
    ) = dao.insert(
        NotificationEntity(
            userId = userId,
            type = type,
            title = title,
            body = body,
            actionTarget = actionTarget
        )
    )
}
package com.haphuongquynh.foodmooddiary.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.haphuongquynh.foodmooddiary.data.local.FoodMoodDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking

/**
 * Content Provider for sharing food entries with other apps
 */
class FoodEntryProvider : ContentProvider() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FoodEntryProviderEntryPoint {
        fun database(): FoodMoodDatabase
    }

    private val database: FoodMoodDatabase by lazy {
        val appContext = context?.applicationContext ?: throw IllegalStateException("Context is null")
        EntryPointAccessors.fromApplication(
            appContext,
            FoodEntryProviderEntryPoint::class.java
        ).database()
    }

    companion object {
        const val AUTHORITY = "com.haphuongquynh.foodmooddiary.provider"
        private const val CODE_FOOD_ENTRIES = 1
        private const val CODE_FOOD_ENTRY_ID = 2
        
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/food_entries")
        
        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "food_entries", CODE_FOOD_ENTRIES)
            addURI(AUTHORITY, "food_entries/#", CODE_FOOD_ENTRY_ID)
        }
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (URI_MATCHER.match(uri)) {
            CODE_FOOD_ENTRIES -> {
                runBlocking {
                    val cursor = database.foodEntryDao().getAllEntriesCursor()
                    cursor.setNotificationUri(context?.contentResolver, uri)
                    cursor
                }
            }
            CODE_FOOD_ENTRY_ID -> {
                val id = uri.lastPathSegment ?: return null
                runBlocking {
                    val cursor = database.foodEntryDao().getEntryByIdCursor(id)
                    cursor.setNotificationUri(context?.contentResolver, uri)
                    cursor
                }
            }
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return when (URI_MATCHER.match(uri)) {
            CODE_FOOD_ENTRIES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.food_entries"
            CODE_FOOD_ENTRY_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.food_entry"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Read-only provider
        throw UnsupportedOperationException("Insert not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // Read-only provider
        throw UnsupportedOperationException("Delete not supported")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Read-only provider
        throw UnsupportedOperationException("Update not supported")
    }
}

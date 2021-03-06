package com.frogobox.movie.provider

import android.content.*
import android.database.Cursor
import android.net.Uri
import com.frogobox.base.source.model.FavoriteTvShow
import com.frogobox.base.source.local.AppDatabase
import com.frogobox.base.util.Constant
import java.util.ArrayList
import java.util.concurrent.Callable

/**
 * Created by Faisal Amir
 * FrogoBox Inc License
 * =========================================
 * movie
 * Copyright (C) 16/11/2019.
 * All rights reserved
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * LinkedIn : linkedin.com/in/faisalamircs
 * -----------------------------------------
 * FrogoBox Software Industries
 * com.frogobox.movie.provider
 *
 */
class FavoriteTvShowProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val code = Constant.RoomDatabase.TvShow.URI_MATCHER.match(uri)
        if (code == Constant.RoomDatabase.TvShow.CODE_DIR || code == Constant.RoomDatabase.TvShow.CODE_ITEM) {
            val context = context ?: return null
            val favoriteTvShow = AppDatabase.getInstance(context).favoriteTvShowDao()
            val cursor: Cursor
            if (code == Constant.RoomDatabase.TvShow.CODE_DIR) {
                cursor = favoriteTvShow.getCursorAllData()
            } else {
                cursor = favoriteTvShow.getCursorById(ContentUris.parseId(uri))
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        when (Constant.RoomDatabase.TvShow.URI_MATCHER.match(uri)) {
            Constant.RoomDatabase.TvShow.CODE_DIR -> return Constant.RoomDatabase.TvShow.CONTENT_LIST_TYPE
            Constant.RoomDatabase.TvShow.CODE_ITEM -> return Constant.RoomDatabase.TvShow.CONTENT_ITEM_TYPE
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (Constant.RoomDatabase.TvShow.URI_MATCHER.match(uri)) {
            Constant.RoomDatabase.TvShow.CODE_DIR -> {
                val context = context ?: return null
                val id = AppDatabase.getInstance(context).favoriteTvShowDao()
                    .insertCursor(FavoriteTvShow().fromContentValues(values!!))
                context.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
            Constant.RoomDatabase.TvShow.CODE_ITEM -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(
        uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when (Constant.RoomDatabase.TvShow.URI_MATCHER.match(uri)) {
            Constant.RoomDatabase.TvShow.CODE_DIR -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            Constant.RoomDatabase.TvShow.CODE_ITEM -> {
                val context = context ?: return 0
                val count = AppDatabase.getInstance(context).favoriteTvShowDao()
                    .deleteById(ContentUris.parseId(uri))
                context.contentResolver.notifyChange(uri, null)
                return count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    @Throws(OperationApplicationException::class)
    override/* This gets propagated up from the Callable */ fun applyBatch(
        operations: ArrayList<ContentProviderOperation>
    ): Array<ContentProviderResult?> {
        val context = context ?: return arrayOfNulls(0)
        val database = AppDatabase.getInstance(context)
        return database.runInTransaction(Callable {
            super@FavoriteTvShowProvider.applyBatch(
                operations
            )
        })
    }

}
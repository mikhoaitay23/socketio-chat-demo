package com.example.socketio_chat_demo.utils

import android.content.Context

object SharedPreferenceUtils {

    private const val PREFERENCE_NAME = "Firebase_chat_demo"
    private const val KEY_CURRENT_USER_ID = "KEY_CURRENT_USER_ID"

    fun putStringValue(context: Context, key: String?, value: String?) {
        val editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(key, value).apply()
    }

    fun getStringValue(context: Context, key: String?): String? {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getString(key, "")
    }

    fun putBooleanValue(context: Context, key: String, value: Boolean) {
        val editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value).apply()
    }

    fun getBooleanValue(context: Context, key: String): Boolean {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getBoolean(key, false)
    }

    fun putIntValue(context: Context, key: String?, value: Int) {
        val editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(key, value).apply()
    }

    fun getIntValue(context: Context, key: String?): Int {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(key, 0)
    }

    fun putLongValue(context: Context, key: String?, value: Long) {
        val editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
        editor.putLong(key, value).apply()
    }

    fun getLongValue(context: Context, key: String?): Long {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getLong(key, 0L)
    }

    fun setCurrentUserId(context: Context, id: String) {
        putStringValue(context, KEY_CURRENT_USER_ID, id)
    }

    fun getCurrentUserId(context: Context): String? {
        return getStringValue(context, KEY_CURRENT_USER_ID)
    }
}
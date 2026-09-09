package com.almari.shared.data

import android.content.Context

private var appContext: Context? = null

fun initializeHomeStateStorage(context: Context) { appContext = context.applicationContext }

actual object HomeStateStorage {
    actual fun read(): String? = appContext?.getSharedPreferences("almari", Context.MODE_PRIVATE)?.getString("home", null)
    actual fun write(value: String) { appContext?.getSharedPreferences("almari", Context.MODE_PRIVATE)?.edit()?.putString("home", value)?.apply() }
    actual fun readToken(): String? = appContext?.getSharedPreferences("almari", Context.MODE_PRIVATE)?.getString("token", null)
    actual fun writeToken(value: String?) { appContext?.getSharedPreferences("almari", Context.MODE_PRIVATE)?.edit()?.apply { if (value == null) remove("token") else putString("token", value) }?.apply() }
}

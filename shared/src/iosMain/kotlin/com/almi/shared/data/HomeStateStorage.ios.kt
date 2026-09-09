package com.almi.shared.data

import platform.Foundation.NSUserDefaults

actual object HomeStateStorage {
    actual fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey("almi.home")
    actual fun write(value: String) { NSUserDefaults.standardUserDefaults.setObject(value, forKey = "almi.home") }
    actual fun readToken(): String? = NSUserDefaults.standardUserDefaults.stringForKey("almi.token")
    actual fun writeToken(value: String?) { if (value == null) NSUserDefaults.standardUserDefaults.removeObjectForKey("almi.token") else NSUserDefaults.standardUserDefaults.setObject(value, forKey = "almi.token") }
}

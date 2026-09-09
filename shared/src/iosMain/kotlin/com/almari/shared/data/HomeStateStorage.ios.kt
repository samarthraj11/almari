package com.almari.shared.data

import platform.Foundation.NSUserDefaults

actual object HomeStateStorage {
    actual fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey("almari.home")
    actual fun write(value: String) { NSUserDefaults.standardUserDefaults.setObject(value, forKey = "almari.home") }
    actual fun readToken(): String? = NSUserDefaults.standardUserDefaults.stringForKey("almari.token")
    actual fun writeToken(value: String?) { if (value == null) NSUserDefaults.standardUserDefaults.removeObjectForKey("almari.token") else NSUserDefaults.standardUserDefaults.setObject(value, forKey = "almari.token") }
}

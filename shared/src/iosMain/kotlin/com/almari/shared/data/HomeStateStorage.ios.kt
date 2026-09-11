package com.almari.shared.data

import platform.Foundation.NSUserDefaults

actual object HomeStateStorage {
    actual fun read(): String? = NSUserDefaults.standardUserDefaults.stringForKey("almari.home")
    actual fun write(value: String) { NSUserDefaults.standardUserDefaults.setObject(value, forKey = "almari.home") }
}

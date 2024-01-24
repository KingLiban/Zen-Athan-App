buildscript {
    extra.apply {
        set("nav_version", "2.5.3")
        set("room_version", "2.6.1")
    }
}
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
}
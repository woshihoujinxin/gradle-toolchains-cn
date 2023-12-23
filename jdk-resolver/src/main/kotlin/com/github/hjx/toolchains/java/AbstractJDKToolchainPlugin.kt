package com.github.hjx.toolchains.java

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

abstract class AbstractJDKToolchainPlugin: Plugin<Any> {

    override fun apply(target: Any) {
        if (target is Settings) {
            apply(target)
        } else {
            throw GradleException("Settings plugins must be applied in the settings script.")
        }
    }

    abstract fun apply(settings: Settings)

}
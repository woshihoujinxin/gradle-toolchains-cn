package io.github.hjx.toolchains.java

import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.jvm

@Suppress("unused")
abstract class JDKToolchainsConventionPlugin: AbstractJDKToolchainPlugin() {

    override fun apply(settings: Settings) {
        settings.plugins.apply(JDKToolchainsPlugin::class.java)

        settings.toolchainManagement {
            jvm {
                javaRepositories {
                    repository("jdk") {
                        resolverClass.set(JDKToolchainResolver::class.java)
                    }
                }
            }
        }
    }

}
@file:Suppress("UNUSED_VARIABLE")

import java.io.FileNotFoundException

plugins {
    `kotlin-dsl`
    signing
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "io.github.hjx"
val pluginVersion = property("pluginVersion") ?: throw GradleException("`pluginVersion` missing in gradle.properties!")
version = pluginVersion

repositories {
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/nexus/content/groups/public")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    maven("https://jitpack.io'")
    maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
    maven("https://mirrors.tencent.com/nexus/repository/gradle-plugins/")
    maven("https://repo.huaweicloud.com/repository/maven/'")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
}

gradlePlugin {
    vcsUrl.set("https://github.com/woshihoujinxin/gradle-toolchains-cn")
    website.set("https://github.com/woshihoujinxin/gradle-toolchains-cn")

    val discoToolchains by plugins.creating {
        id = "io.github.hjx.toolchains.jdk-resolver"
        implementationClass = "io.github.hjx.toolchains.java.JDKToolchainsPlugin"
        displayName = "Foojay Disco API Toolchains Resolver"
        description = "Toolchains resolver using the Foojay Disco API for resolving Java runtimes."
        tags.set(listOf("jdk", "cn", "toolchains"))
    }

    val discoToolchainsConvenience by plugins.creating {
        id = "io.github.hjx.toolchains.jdk-resolver-convention"
        implementationClass = "io.github.hjx.toolchains.java.JDKToolchainsConventionPlugin"
        displayName = "Foojay Disco API Toolchains Resolver Convention"
        description = "Toolchains resolver using the Foojay Disco API for resolving Java runtimes. Automatically configures toolchain management."
        tags.set(listOf("jdk", "cn", "toolchains"))
    }

}

publishing {
    repositories {
        mavenLocal()
        maven {
            url = uri(layout.projectDirectory.dir("repo"))
        }
    }
}

signing {
//    useInMemoryPgpKeys(
//            project.providers.environmentVariable("PGP_SIGNING_KEY").orNull,
//            project.providers.environmentVariable("PGP_SIGNING_KEY_PASSPHRASE").orNull
//    )
    useGpgCmd()
}

testing {
    suites {
        val functionalTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit5")
            }
        }
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit5")
            }
        }
    }
}

gradlePlugin.testSourceSets(sourceSets.getAt("functionalTest"))

tasks.named<Task>("check") {
    // Run the functional tests as part of `check`
    dependsOn(testing.suites.named("functionalTest"))
}

val readReleaseNotes by tasks.registering {
    description = "Ensure we've got some release notes handy"
    doLast {
        val releaseNotesFile = file("release-notes-$version.txt")
        if (!releaseNotesFile.exists()) {
            throw FileNotFoundException("Couldn't find release notes file $releaseNotesFile.absolutePath")
        }
        val releaseNotes = releaseNotesFile.readText().trim()
        if (releaseNotes.isBlank()) {
            throw IllegalArgumentException("Release notes file $releaseNotesFile.absolutePath is empty")
        }
        gradlePlugin.plugins["discoToolchains"].description = releaseNotes
        gradlePlugin.plugins["discoToolchainsConvenience"].description = releaseNotes
    }
}

tasks.publishPlugins {
    dependsOn(readReleaseNotes)
}

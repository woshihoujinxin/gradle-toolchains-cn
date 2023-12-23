# Foojay Toolchains Plugin

The `org.gradle.disco-toolchains` plugin provides a [repository for downloading JVMs](https://docs.gradle.org/current/userguide/toolchains.html#sub:download_repositories).
It is based on the [foojay DiscoAPI](https://github.com/foojayio/discoapi).
Requires Gradle 7.6 or later to work.

> **TAKE HEED!** As opposed to most of the Gradle plugins, which are Project plugins 
> and must be applied in `build.gradle[.kts]` files, this is a **SETTINGS PLUGIN** and 
> must be applied in `settings.gradle[.kts]` files.

本插件基于foojay toolchains plugin 解决国内用户 从国内源解决工具链问题，思路

# Usage

To make use of the plugin add following to your `settings.gradle[.kts]` file.

<details open>

<summary>Kotlin DSL</summary>

```kotlin
// settings.gradle.kts
plugins {
    id("io.github.hjx.toolchains.jdk-resolver-convention") version "0.0.1"
}
```

</details>

<details>

<summary>Groovy DSL</summary>

```groovy
// settings.gradle
plugins {
    id("io.github.hjx.toolchains.jdk-resolver-convention") version "0.0.1"
}
```

</details>

This is a convention plugin meant to simplify configuration.
What it does is equivalent to applying the base plugin and some extra configuration:

<details open>

<summary>Kotlin DSL</summary>

```kotlin
// settings.gradle.kts
plugins {
    id("io.github.hjx.toolchains.jdk-resolver") version "0.0.1"
}

toolchainManagement {
    jvm {
        javaRepositories {
            repository("jdk") {
                resolverClass.set(io.github.hjx.toolchains.java.JDKToolchainResolver::class.java)
            }
        }
    }
}
```

</details>

<details>

<summary>Groovy DSL</summary>

```groovy
// settings.gradle
plugins {
    id("io.github.hjx.toolchains.jdk-resolver") version "0.0.1"
}

toolchainManagement {
    jvm {
        javaRepositories {
            repository("jdk") {
                resolverClass = io.github.hjx.toolchains.java.JDKToolchainResolver
            }
        }
    }
}
```

</details>

Feel free to use either approach.

For further information about using Toolchain Download Repositories consult the [Gradle Manual](https://docs.gradle.org/current/userguide/toolchains.html#sub:download_repositories).

# Matching Toolchain Specifications

The main thing the plugin does is to match [Gradle's toolchain specifications](https://docs.gradle.org/current/javadoc/org/gradle/jvm/toolchain/JavaToolchainSpec.html) to foojay DiscoAPI distributions and packages. 

## Vendors

仅支持Temurin的国内代理

| Gradle JVM Vendor       | Foojay Distribution       |
|-------------------------|---------------------------|
| ADOPTIUM                | Temurin                   |

**To note:**
Not all Gradle vendors have an equivalent DiscoAPI distribution, empty cells indicate that no toolchain will be provisioned.
If no vendor is specified, distributions are iterated in the order they are provided by the DiscoAPI, and the first one that has a compatible installation package available is selected.
The exception to the Foojay ordering of distributions is that "Temurin" (ADOPTIUM) and then "AOJ" (ADOPTONEJDK) come first, due to the history of the auto-provisioning feature in Gradle, specifically that AdoptOpenJDK/Adoptium have been the default sources for downloading JVMs.

## Implementations

When specifying toolchains Gradle distinguishes between `J9` JVMs and `VENDOR_SPECIFIC` ones (ie. any other).
What this criteria does in the plugin is to influence the Vendor-to-Distribution matching table.
`VENDOR_SPECIFICATION` doesn't change it at all, while `J9` alter it like this:

| Gradle JVM Vendor       | Foojay Distribution |
|-------------------------|---------------------|
| \<no vendor specified\> | Semeru              |
| ADOPTIUM                | -                   |

Empty cells indicate that no toolchain will be provisioned

## Versions

Once the vendor and the implementation values of the toolchain spec have been used to select a DiscoAPI distribution, a specific package of that distribution needs to be picked by the plugin, in order for it to obtain a download link. 
The inputs it uses to do this are:
* the major **Java version** number for the spec
* the **operating system** running the build that made the request
* the **CPU architecture** of the system running the build that made the request

Additional criteria used for selection:
* for each major version number only packages having the latest minor version will be considered 
* only packages containing an archive of a format known to Gradle will be considered (zip, tar, tgz)
* JDKs have priority over JREs

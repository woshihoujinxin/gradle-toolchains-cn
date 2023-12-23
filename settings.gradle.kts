plugins {
    id("com.gradle.enterprise") version "3.12.1"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.6.0"
}

gradleEnterprise {
    buildScan {
        publishAlways()
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "gradle-toolchains-cn"
include("jdk-resolver")

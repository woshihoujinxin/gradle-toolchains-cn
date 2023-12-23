package io.github.hjx.toolchains.java

import org.gradle.api.GradleException
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.platform.Architecture
import org.gradle.platform.OperatingSystem
import org.gradle.platform.OperatingSystem.*
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.concurrent.TimeUnit.SECONDS


class JDKDownloadApi {

    val CONNECT_TIMEOUT = SECONDS.toMillis(10).toInt()
    val READ_TIMEOUT = SECONDS.toMillis(20).toInt()

    val SCHEMA = "https"

    val ENDPOINT_ROOT = "mirrors.tuna.tsinghua.edu.cn"
    val DISTRIBUTIONS_ENDPOINT = "$ENDPOINT_ROOT/Adoptium"

    val distributions = mutableListOf<Distribution2>()

    fun toUri(links: Links?): URI? = links?.pkg_download_redirect

    fun toLinks(
            version: JavaLanguageVersion,
            vendor: JvmVendorSpec,
            implementation: JvmImplementation,
            operatingSystem: OperatingSystem,
            architecture: Architecture
    ): Links? {
        val versions = listOf(8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21)
        if (!versions.contains(version.asInt())) {
            throw IllegalArgumentException("本插件不支持${versions}外的版本");
        }

        val operatingSystemMapping = mapOf(
            LINUX to "linux",
            UNIX to "unix",
            WINDOWS to "windows",
            MAC_OS to "mac",
            SOLARIS to "unix",
            FREE_BSD to "linux"
        )

        val architectureMapping = mapOf(
            Architecture.X86 to "x32",
            Architecture.X86_64 to "x64",
            Architecture.AARCH64 to "aarch64",
        )

        val jdkFromHuaweiOpenJDK = listOf(9, 10, 11, 12, 13, 14, 15, 16)

        if (jdkFromHuaweiOpenJDK.contains(version.asInt())) {
            return getDownloadLinkFromHuaweiOpenJDK(version, operatingSystem)
        } else {
            // 来自清华
            return getDownloadLinkFromTuna(
                architectureMapping,
                architecture,
                version,
                operatingSystemMapping,
                operatingSystem
            )
        }

    }

    fun getDownloadLinkFromTuna(
        architectureMapping: Map<Architecture, String>,
        architecture: Architecture,
        version: JavaLanguageVersion,
        operatingSystemMapping: Map<OperatingSystem, String>,
        operatingSystem: OperatingSystem
    ): Links {
        //  https://mirrors.tuna.tsinghua.edu.cn/Adoptium/(11)/jdk/(x64)/(windows)/
        var endpoint = ""
        val arch = architectureMapping.get(architecture).toString()
        endpoint = "${DISTRIBUTIONS_ENDPOINT}/${version}/jdk/${arch}/${operatingSystemMapping.get(operatingSystem)}"
        println("Download url is [ ${SCHEMA}://${endpoint} ]")
        fetchDistributionsIfMissing(endpoint)
        if (!distributions.isEmpty()) {
            val uri = URI.create("${SCHEMA}://${endpoint}/${distributions.get(0).packageName}")
            return Links(uri, uri)
        } else {
            val uri = URI("https://repo.huaweicloud.com/java/jdk/11.0.2+9/jdk-11.0.2_windows-x64_bin.zip")
            return Links(uri, uri)
        }
    }

    fun getDownloadLinkFromHuaweiOpenJDK(version: JavaLanguageVersion, operatingSystem: OperatingSystem): Links? {
        val baseUrl = "https://repo.huaweicloud.com/openjdk"
        val versionSuffix = when (version) {
            JavaLanguageVersion.of(9) -> "9.0.4"
            JavaLanguageVersion.of(10) -> "10.0.2"
            JavaLanguageVersion.of(11) -> "11.0.2"
            JavaLanguageVersion.of(12) -> "12.0.2"
            JavaLanguageVersion.of(13) -> "13.0.2"
            JavaLanguageVersion.of(14) -> "14.0.2"
            JavaLanguageVersion.of(15) -> "15.0.2"
            JavaLanguageVersion.of(16) -> "16.0.2"
            else -> return null // Handle unsupported versions
        }

        val downloadLink = when (operatingSystem) {
            WINDOWS -> "windows-x64_bin.tar.gz"
            LINUX -> "linux-x64_bin.tar.gz"
            MAC_OS -> "osx-x64_bin.tar.gz"
            else -> return null // Handle unsupported operating systems
        }

        val endpoint = "$baseUrl/$versionSuffix/openjdk-${versionSuffix}_$downloadLink"
        val uri = URI.create(endpoint)
        return Links(uri, uri)
    }

    fun getDownloadLinkFromHuawei(version: JavaLanguageVersion, operatingSystem: OperatingSystem): Links? {
        val baseUrl = "https://repo.huaweicloud.com/java/jdk"
        val versionSuffix = when (version) {
            JavaLanguageVersion.of(11) -> "11.0.2+9"
            JavaLanguageVersion.of(12) -> "12.0.2+10"
            JavaLanguageVersion.of(13) -> "13+33"
            else -> return null // Handle unsupported versions
        }

        val downloadLink = when (operatingSystem) {
            WINDOWS -> "windows-x64_bin.zip"
            LINUX -> "linux-x64_bin.tar.gz"
            MAC_OS -> "osx-x64_bin.dmg"
            else -> return null // Handle unsupported operating systems
        }

        val endpoint = "$baseUrl/$versionSuffix/jdk-${versionSuffix}_$downloadLink"
        val uri = URI.create(endpoint)
        return Links(uri, uri)
    }

    private fun fetchDistributionsIfMissing(endpoint: String) {
        if (distributions.isEmpty()) {
            val con = createConnection(endpoint)
            val text = readResponse(con)
            con.disconnect()

            distributions.addAll(parseDistributionsByJsoup(text))
        }
    }


    private fun createConnection(endpoint: String): HttpURLConnection {
        val url = URL("$SCHEMA://${endpoint}")
        val con = url.openConnection() as HttpURLConnection
        con.setRequestProperty("Content-Type", "application/html")
        con.requestMethod = "GET"
        con.connectTimeout = CONNECT_TIMEOUT
        con.readTimeout = READ_TIMEOUT
        return con
    }

    private fun readResponse(con: HttpURLConnection): String {
        val status = con.responseCode
        if (status != HttpURLConnection.HTTP_OK) {
            throw GradleException("Requesting vendor list failed: ${readContent(con.errorStream)}")
        }
        return readContent(con.inputStream)
    }

    private fun readContent(stream: InputStream) = stream.bufferedReader().use(BufferedReader::readText)
}

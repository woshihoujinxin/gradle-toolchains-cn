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
        val versions = listOf(8, 9, 11, 12, 13, 17, 18, 19, 20, 21)
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

        //  https://mirrors.tuna.tsinghua.edu.cn/Adoptium/(11)/jdk/(x64)/(windows)/
        var arch = ""
        var endpoint = ""
        if (version.equals(JavaLanguageVersion.of(9))){
            if (operatingSystem.equals(WINDOWS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/9.0.1+11/jdk-9.0.1_windows-x64_bin.exe"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(LINUX)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/9.0.1+11/jdk-9.0.1_linux-x64_bin.tar.gz"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(MAC_OS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/9.0.1+11/jdk-9.0.1_osx-x64_bin.dmg"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            }
        } else if (version.equals(JavaLanguageVersion.of(10))){
            if (operatingSystem.equals(WINDOWS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/10.0.2+13/jdk-10.0.2_windows-x64_bin.exe"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(LINUX)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/10.0.2+13/jdk-10.0.2_linux-x64_bin.tar.gz"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(MAC_OS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/10.0.2+13/jdk-10.0.2_osx-x64_bin.dmg"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            }
        } else if (version.equals(JavaLanguageVersion.of(11))){
            if (operatingSystem.equals(WINDOWS)) {
                endpoint =  "https://repo.huaweicloud.com/java/jdk/11.0.2+9/jdk-11.0.2_windows-x64_bin.zip"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(LINUX)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/11.0.2+9/jdk-11.0.2_linux-x64_bin.tar.gz"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(MAC_OS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/11.0.2+9/jdk-11.0.2_osx-x64_bin.dmg"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            }
        } else if (version.equals(JavaLanguageVersion.of(12))){
            if (operatingSystem.equals(WINDOWS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/12.0.2+10/jdk-12.0.2_windows-x64_bin.zip"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(LINUX)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/12.0.2+10/jdk-12.0.2_linux-x64_bin.tar.gz"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(MAC_OS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/12.0.2+10/jdk-12.0.2_osx-x64_bin.dmg"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            }
        } else if (version.equals(JavaLanguageVersion.of(13))){
            if (operatingSystem.equals(WINDOWS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/13+33/jdk-13_windows-x64_bin.zip"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(LINUX)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/13+33/jdk-13_linux-x64_bin.tar.gz"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            } else if (operatingSystem.equals(MAC_OS)) {
                endpoint = "https://repo.huaweicloud.com/java/jdk/13+33/jdk-13_osx-x64_bin.dmg"
                val uri = URI.create(endpoint)
                return Links(uri, uri)
            }
        } else {
            arch = architectureMapping.get(architecture).toString()
            endpoint = "${DISTRIBUTIONS_ENDPOINT}/${version}/jdk/${arch}/${operatingSystemMapping.get(operatingSystem)}"
        }
        println("Download url is [ ${SCHEMA}://${endpoint} ]")
        fetchDistributionsIfMissing(endpoint)
        if(!distributions.isEmpty()) {
            val uri = URI.create("${SCHEMA}://${endpoint}/${distributions.get(0).packageName}")
            return Links(uri, uri)
        } else {
            val pkg_download_redirect = URI("https://repo.huaweicloud.com/java/jdk/13+33/jdk-13_windows-x64_bin.zip")
            val pkg_info_uri = URI("https://repo.huaweicloud.com/java/jdk/13+33/jdk-13_windows-x64_bin.zip")
            return Links(pkg_download_redirect, pkg_info_uri)
        }
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

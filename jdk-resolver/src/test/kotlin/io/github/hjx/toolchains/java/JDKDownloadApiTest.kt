package io.github.hjx.toolchains.java

import org.gradle.jvm.toolchain.JavaLanguageVersion.of
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmImplementation.J9
import org.gradle.jvm.toolchain.JvmImplementation.VENDOR_SPECIFIC
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.jvm.toolchain.JvmVendorSpec.*
import org.gradle.jvm.toolchain.internal.DefaultJvmVendorSpec.any
import org.gradle.platform.Architecture
import org.gradle.platform.OperatingSystem
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JDKDownloadApiTest {

    private val api = JDKDownloadApi()


    private fun assertDownloadUri(
        expected: String,
        javaVersion: Int,
        vendor: JvmVendorSpec,
        isJ9: Boolean,
        os: OperatingSystem,
        arch: Architecture
    ) {
        val links = api.toLinks(
            of(javaVersion),
            vendor,
            if (isJ9) J9 else VENDOR_SPECIFIC,
            os,
            arch
        )
        assertEquals(
            expected,
            api.toUri(links).toString(),
            "Expected URI differs from actual, for details see ${links?.pkg_info_uri}"
        )
    }

    @Test
    fun `download URI provided correctly`() {

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/8/jdk/x64/windows/OpenJDK8U-jdk_x64_windows_hotspot_8u392b08.zip",
//            8, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/11/jdk/x64/windows/OpenJDK11U-jdk_x64_windows_hotspot_11.0.21_9.zip",
//            11, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )



//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/17/jdk/x64/windows/OpenJDK17U-jdk_x64_windows_hotspot_17.0.9_9.zip",
//            17, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/18/jdk/x64/windows/OpenJDK18U-jdk_x64_windows_hotspot_18.0.2.1_1.zip",
//            18, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/19/jdk/x64/windows/OpenJDK19U-jdk_x64_windows_hotspot_19.0.2_7.zip",
//            19, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/20/jdk/x64/windows/OpenJDK20U-jdk_x64_windows_hotspot_20.0.2_9.zip",
//            20, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/21/jdk/x64/windows/OpenJDK21U-jdk_x64_windows_hotspot_21.0.1_12.zip",
//            21, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/21/jdk/aarch64/mac/OpenJDK21U-jdk_aarch64_mac_hotspot_21.0.1_12.tar.gz",
//            21, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.AARCH64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/8/jdk/x64/mac/OpenJDK8U-jdk_x64_mac_hotspot_8u392b08.tar.gz",
//            8, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
//        )

//        assertDownloadUri(
//            "https://mirrors.tuna.tsinghua.edu.cn/Adoptium/8/jdk/x64/linux/OpenJDK8U-jdk_x64_linux_hotspot_8u392b08.tar.gz",
//            8, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
//        )

        assertDownloadUri(
            "https://repo.huaweicloud.com/openjdk/9.0.4/openjdk-9.0.4_windows-x64_bin.tar.gz",
            9, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            "https://repo.huaweicloud.com/openjdk/9.0.4/openjdk-9.0.4_linux-x64_bin.tar.gz",
            9, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            "https://repo.huaweicloud.com/openjdk/9.0.4/openjdk-9.0.4_osx-x64_bin.tar.gz",
            9, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )

        val links10 = listOf(
            "https://repo.huaweicloud.com/openjdk/10.0.2/openjdk-10.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/10.0.2/openjdk-10.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/10.0.2/openjdk-10.0.2_osx-x64_bin.tar.gz",
        )

        val links11 = listOf(
            "https://repo.huaweicloud.com/openjdk/11.0.2/openjdk-11.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/11.0.2/openjdk-11.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/11.0.2/openjdk-11.0.2_osx-x64_bin.tar.gz",
        )

        val links12 = listOf(
            "https://repo.huaweicloud.com/openjdk/12.0.2/openjdk-12.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/12.0.2/openjdk-12.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/12.0.2/openjdk-12.0.2_osx-x64_bin.tar.gz",
        )

        val links13 = listOf(
            "https://repo.huaweicloud.com/openjdk/13.0.2/openjdk-13.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/13.0.2/openjdk-13.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/13.0.2/openjdk-13.0.2_osx-x64_bin.tar.gz",
        )

        val links14 = listOf(
            "https://repo.huaweicloud.com/openjdk/14.0.2/openjdk-14.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/14.0.2/openjdk-14.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/14.0.2/openjdk-14.0.2_osx-x64_bin.tar.gz",
        )

        val links15 = listOf(
            "https://repo.huaweicloud.com/openjdk/15.0.2/openjdk-15.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/15.0.2/openjdk-15.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/15.0.2/openjdk-15.0.2_osx-x64_bin.tar.gz",
        )

        val links16 = listOf(
            "https://repo.huaweicloud.com/openjdk/16.0.2/openjdk-16.0.2_windows-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/16.0.2/openjdk-16.0.2_linux-x64_bin.tar.gz",
            "https://repo.huaweicloud.com/openjdk/16.0.2/openjdk-16.0.2_osx-x64_bin.tar.gz",
        )

        assertDownloadUri(
            links10.get(0),
            10, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links10.get(1),
            10, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links10.get(2),
            10, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )


        assertDownloadUri(
            links11.get(0),
            11, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links11.get(1),
            11, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links11.get(2),
            11, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )


        assertDownloadUri(
            links12.get(0),
            12, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links12.get(1),
            12, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links12.get(2),
            12, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )

        assertDownloadUri(
            links13.get(0),
            13, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links13.get(1),
            13, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links13.get(2),
            13, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )

        assertDownloadUri(
            links14.get(0),
            14, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links14.get(1),
            14, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links14.get(2),
            14, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )


        assertDownloadUri(
            links15.get(0),
            15, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links15.get(1),
            15, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links15.get(2),
            15, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )

        assertDownloadUri(
            links16.get(0),
            16, ADOPTIUM, false, OperatingSystem.WINDOWS, Architecture.X86_64
        )

        assertDownloadUri(
            links16.get(1),
            16, ADOPTIUM, false, OperatingSystem.LINUX, Architecture.X86_64
        )

        assertDownloadUri(
            links16.get(2),
            16, ADOPTIUM, false, OperatingSystem.MAC_OS, Architecture.X86_64
        )


    }

}

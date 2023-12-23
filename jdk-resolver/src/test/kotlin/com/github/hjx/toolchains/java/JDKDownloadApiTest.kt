package com.github.hjx.toolchains.java

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

    @Test
    fun `download URI provided correctly`() {
        assertDownloadUri(
                "https://repo.huaweicloud.com/java/jdk/13+33/jdk-13_windows-x64_bin.zip",
                13, any(), false, OperatingSystem.WINDOWS, Architecture.X86_64
        ) // jdk-13_windows-x64_bin.zip

    }

    @ParameterizedTest(name = "J9 implementation influences vendor resolution (Java {0})")
    @ValueSource(ints = [8, 11, 16])
    fun `J9 implementation influences vendor resolution`(version: Int) {
        assertMatchedDistributions(any(), J9, version, "Semeru", "AOJ OpenJ9")

        assertMatchedDistributions(ADOPTOPENJDK, J9, version, "AOJ OpenJ9")
        assertMatchedDistributions(IBM, J9, version, "Semeru")
        @Suppress("DEPRECATION")
        assertMatchedDistributions(IBM_SEMERU, J9, version, "Semeru")

        assertMatchedDistributions(ADOPTIUM, J9, version)
        assertMatchedDistributions(AZUL, J9, version)
        assertMatchedDistributions(AMAZON, J9, version)
        assertMatchedDistributions(BELLSOFT, J9, version)
        assertMatchedDistributions(MICROSOFT, J9, version)
        assertMatchedDistributions(ORACLE, J9, version)
        assertMatchedDistributions(SAP, J9, version)
        assertMatchedDistributions(APPLE, J9, version)
        assertMatchedDistributions(GRAAL_VM, J9, version)
        assertMatchedDistributions(HEWLETT_PACKARD, J9, version)
    }

    @ParameterizedTest(name = "vendor specific implementation does not influence vendor resolution (Java {0})")
    @ValueSource(ints = [8, 11, 16])
    fun `vendor specific implementation does not influence vendor resolution`(version: Int) {
        assertMatchedDistributions(any(), VENDOR_SPECIFIC, version,
                "Temurin", "AOJ",
                "ZuluPrime", "Zulu", "Trava", "Semeru certified", "Semeru", "SAP Machine", "Red Hat", "Oracle OpenJDK",
                "Oracle", "OpenLogic", "OJDKBuild", "Microsoft", "Mandrel", "Liberica Native", "Liberica", "Kona",
                "JetBrains", "GraalVM Community", "GraalVM CE $version", "GraalVM", "Gluon GraalVM", "Dragonwell",
                "Debian", "Corretto", "Bi Sheng", "AOJ OpenJ9"
        )

        assertMatchedDistributions(ADOPTOPENJDK, VENDOR_SPECIFIC, version, "AOJ")
        assertMatchedDistributions(IBM, VENDOR_SPECIFIC, version, "Semeru")
        @Suppress("DEPRECATION")
        assertMatchedDistributions(IBM_SEMERU, VENDOR_SPECIFIC, version, "Semeru")

        assertMatchedDistributions(ADOPTIUM, VENDOR_SPECIFIC, version, "Temurin")
        assertMatchedDistributions(AZUL, VENDOR_SPECIFIC, version, "Zulu")
        assertMatchedDistributions(AMAZON, VENDOR_SPECIFIC, version, "Corretto")
        assertMatchedDistributions(BELLSOFT, VENDOR_SPECIFIC, version, "Liberica")
        assertMatchedDistributions(MICROSOFT, VENDOR_SPECIFIC, version, "Microsoft")
        assertMatchedDistributions(ORACLE, VENDOR_SPECIFIC, version, "Oracle OpenJDK")
        assertMatchedDistributions(SAP, VENDOR_SPECIFIC, version, "SAP Machine")

        assertMatchedDistributions(GRAAL_VM, VENDOR_SPECIFIC, version, "GraalVM Community", "GraalVM CE $version")

        assertMatchedDistributions(APPLE, VENDOR_SPECIFIC, version)
        assertMatchedDistributions(HEWLETT_PACKARD, VENDOR_SPECIFIC, version)
    }

    private fun assertMatchedDistributions(vendor: JvmVendorSpec, implementation: JvmImplementation, version: Int, vararg expectedDistributions: String) {
        assertEquals(
                listOf(*expectedDistributions),
                api.match(vendor, implementation, of(version)).map { it.name },
                "Mismatch in matching distributions for vendor: $vendor, implementation: $implementation, version: $version"
        )
    }

    @ParameterizedTest(name = "can resolve arbitrary vendors (Java {0})")
    @ValueSource(ints = [8, 11, 16])
    fun `can resolve arbitrary vendors`(version: Int) {
        assertEquals("ZuluPrime", api.match(vendorSpec("zuluprime"), VENDOR_SPECIFIC, of(version)).firstOrNull()?.name)
        assertEquals("ZuluPrime", api.match(vendorSpec("zUluprIme"), VENDOR_SPECIFIC, of(version)).firstOrNull()?.name)
        assertEquals("JetBrains", api.match(vendorSpec("JetBrains"), VENDOR_SPECIFIC, of(version)).firstOrNull()?.name)
    }

    @Test
    fun `can pick the right package`() {
        val p = api.match("temurin", of(11), OperatingSystem.LINUX, Architecture.X86_64)
        assertNotNull(p)
        assertEquals("tar.gz", p.archive_type)
        assertEquals("temurin", p.distribution)
        assertEquals(11, p.jdk_version)
        assertEquals("11.0.21", p.distribution_version)
        assertEquals("linux", p.operating_system)
        assertEquals("x64", p.architecture)
        assertEquals("jdk", p.package_type)
    }

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
        assertEquals(expected, api.toUri(links).toString(), "Expected URI differs from actual, for details see ${links?.pkg_info_uri}")
    }

    private fun vendorSpec(vendorName: String): JvmVendorSpec = matching(vendorName)

}

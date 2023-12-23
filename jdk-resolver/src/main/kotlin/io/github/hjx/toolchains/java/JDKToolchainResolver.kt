package io.github.hjx.toolchains.java

import org.gradle.jvm.toolchain.JavaToolchainDownload
import org.gradle.jvm.toolchain.JavaToolchainRequest
import org.gradle.jvm.toolchain.JavaToolchainResolver
import java.util.*

abstract class JDKToolchainResolver: JavaToolchainResolver {

    private val api: FoojayApi = FoojayApi()
//    private val api: JDKDownloadApi = JDKDownloadApi()

    override fun resolve(request: JavaToolchainRequest): Optional<JavaToolchainDownload> {
        val spec = request.javaToolchainSpec
        val platform = request.buildPlatform
        val links = api.toLinks(
            spec.languageVersion.get(),
            spec.vendor.get(),
            spec.implementation.get(),
            platform.operatingSystem,
            platform.architecture
        )
        val uri = api.toUri(links)
        println("===============> ${uri}")
        return Optional.ofNullable(uri).map(JavaToolchainDownload::fromUri)
    }
}
package io.github.hjx.toolchains.java

import kotlin.test.Test
import kotlin.test.assertTrue

class JDKToolchainsPluginFunctionalTest: AbstractJDKToolchainsPluginFunctionalTest() {

    @Test
    fun `can use base plugin`() {
        val settings = """
            plugins {
                id("io.github.hjx.toolchains.jdk-resolver")
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
        """.trimIndent()

        val buildScript = """
            plugins {
                java
            }
            
            java {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(${getDifferentJavaVersion()}))
                }
            }
        """
        val result = runner(settings, buildScript).build()

        assertTrue("Installed toolchain from https://api.foojay.io/disco/" in result.output)
    }

    @Test
    fun `generates useful error for unsupported Gradle versions`() {
        val settings = """
            plugins {
                id("io.github.hjx.toolchains.jdk-resolver")
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
        """.trimIndent()

        val buildScript = """
            plugins {
                java
            }
            
            java {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(${getDifferentJavaVersion()}))
                }
            }
        """
        val result = runner(settings, buildScript)
                .withGradleVersion("7.5")
                .buildAndFail()

        assertTrue("FoojayToolchainsPlugin needs Gradle version 7.6 or higher" in result.output)
    }

    @Test
    fun `provides meaningful error when applied as a project plugin`() {
        val settings = ""

        val buildScript = """
            plugins {
                java
                id("io.github.hjx.toolchains.jdk-resolver")
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
            
            java {
                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(${getDifferentJavaVersion()}))
                }
            }
        """
        val result = runner(settings, buildScript).buildAndFail()

        assertTrue("> Failed to apply plugin 'io.github.hjx.toolchains.jdk-resolver'.\n" +
                "   > Settings plugins must be applied in the settings script." in result.output)
    }
}
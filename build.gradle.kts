import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.RunConfigurationContainer

plugins {
    id("java")
    id("maven-publish")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.4.1"
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

group = "com.toolbelt"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
        vendor.set(org.gradle.jvm.toolchain.JvmVendorSpec.AZUL)
    }
    withSourcesJar()
}

minecraft {
    mcVersion.set("1.12.2")
    username.set("Developer")
    injectedTags.put("VERSION", project.version)
}

tasks.injectTags.configure {
    outputClassName.set("${project.group}.Tags")
}

// Put the version from gradle into mcmod.info
tasks.processResources.configure {
    inputs.property("version", project.version)
    filesMatching("mcmod.info") {
        expand(mapOf("VERSION" to project.version))
    }
}

repositories {
    mavenCentral()
    maven {
        name = "CleanroomMC Maven"
        url = uri("https://maven.cleanroommc.com")
    }
    maven {
        name = "GTNH Maven"
        url = uri("https://nexus.gtnewhorizons.com/repository/public/")
        mavenContent {
            includeGroup("com.gtnewhorizons")
            includeGroupByRegex("com\\.gtnewhorizons\\..+")
        }
    }
    // CurseMaven for deobfuscated CurseForge mods (used by rfg.deobf)
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    // Baubles - required dependency (deobfuscated from CurseForge via rfg.deobf)
    implementation(rfg.deobf("curse.maven:baubles-227083:2518667"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
        inheritOutputDirs = true // Fix resources in IJ-Native runs
    }
    project {
        this.withGroovyBuilder {
            "settings" {
                "runConfigurations" {
                    val self = this.delegate as RunConfigurationContainer
                    self.add(Gradle("1. Run Client").apply {
                        setProperty("taskNames", listOf("runClient"))
                    })
                    self.add(Gradle("2. Run Server").apply {
                        setProperty("taskNames", listOf("runServer"))
                    })
                }
            }
        }
    }
}

tasks.processIdeaSettings.configure {
    dependsOn(tasks.injectTags)
}

object DependencyVersions {
    const val kotlinCoroutinesVersion = "1.4.1"
}

plugins {
    kotlin("multiplatform") version "1.4.10"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}
group = "com.amuza.kotlin"
version = "1.0.2-SNAPSHOT"

repositories {
    mavenCentral()
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.kotlinCoroutinesVersion}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${DependencyVersions.kotlinCoroutinesVersion}")
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }

    explicitApi()
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.getByName("commonMain").kotlin.sourceDirectories)
}

val artifactName = "flow-valve"
val artifactGroup = project.group.toString()
val artifactVersion = project.version.toString()

val pomUrl = "https://github.com/AmuzaNL/FlowValve"
val pomScmUrl = "https://github.com/AmuzaNL/FlowValve"
val pomIssueUrl = "https://github.com/AmuzaNL/FlowValve/issues"
val pomDesc = "https://github.com/AmuzaNL/FlowValve"

val githubRepo = "AmuzaNL/FlowValve"
val githubReadme = "README.md"

val pomLicenseName = "MIT"
val pomLicenseUrl = "https://opensource.org/licenses/mit-license.php"
val pomLicenseDist = "repo"

val pomDeveloperId = "peter-fortuin"
val pomDeveloperName = "Peter Fortuin"

publishing {
    publications {
        create<MavenPublication>("flow-valve") {
            groupId = artifactGroup
            artifactId = artifactName
            version = artifactVersion

            artifact("$buildDir/libs/${project.name}-jvm-${project.version}.jar")
            artifact(sourcesJar)

            pom.withXml {
                asNode().apply {
                    appendNode("description", pomDesc)
                    appendNode("name", rootProject.name)
                    appendNode("url", pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", pomLicenseName)
                        appendNode("url", pomLicenseUrl)
                        appendNode("distribution", pomLicenseDist)
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", pomDeveloperId)
                        appendNode("name", pomDeveloperName)
                    }
                    appendNode("scm").apply {
                        appendNode("url", pomScmUrl)
                    }
                }
            }
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayKey").toString()
    publish = true

    setPublications("flow-valve")

    pkg.apply {
        repo = "kotlin"
        name = artifactName
        userOrg = "amuza"
        vcsUrl = pomScmUrl
        description = "Added functionality to Kotlin Flow"
        setLabels("kotlin", "flow")
        setLicenses("MIT")
        desc = description
        websiteUrl = pomUrl
        issueTrackerUrl = pomIssueUrl
        githubRepo = "AmuzaNL/FlowValve"
        githubReleaseNotesFile = githubReadme

        version.apply {
            name = artifactVersion
            desc = pomDesc
            vcsTag = artifactVersion
        }
    }
}

tasks.named("bintrayUpload") {
    dependsOn(sourcesJar)
    dependsOn(tasks.named("jvmJar"))
}
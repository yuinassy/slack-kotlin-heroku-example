import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.kotlin.dsl.provideDelegate
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "me.myname"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Slack
    val slackSdkVersion = "1.18.0"
    implementation("com.slack.api:bolt:$slackSdkVersion")
    implementation("com.slack.api:bolt-jetty:$slackSdkVersion")
    implementation("com.slack.api:slack-api-model-kotlin-extension:$slackSdkVersion")
    implementation("com.slack.api:slack-api-client-kotlin-extension:$slackSdkVersion")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha12")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("net.logstash.logback:logstash-logback-encoder:7.0.1")

    // Database
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.3.2")

    // OR mapper
    val exposedVersion = "0.37.3"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // Other
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
}

tasks {
    val compileKotlin by existing(KotlinCompile::class) {
        kotlinOptions.jvmTarget = "11"
    }

    val shadowJar by existing(ShadowJar::class) {
        archiveFileName.set("app.jar")
    }

    // For Heroku
    val stage by registering {
        dependsOn(shadowJar)
    }
}

application {
    mainClass.set("MainKt")
}

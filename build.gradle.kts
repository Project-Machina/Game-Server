plugins {
    kotlin("jvm") version "1.6.0" apply false
}

version = "1.0-SNAPSHOT"

subprojects {
    group = "com.server.engine"

    apply {
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
        "implementation"("io.insert-koin:koin-core:3.1.4")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")
        "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-RC")
        "implementation"("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.3.1")
        "testImplementation"("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


plugins {
    kotlin("jvm") version "1.5.31" apply false
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
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
        "implementation"("io.insert-koin:koin-core:3.1.2")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:5.3.1")
        "testImplementation"("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


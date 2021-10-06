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
    }
}


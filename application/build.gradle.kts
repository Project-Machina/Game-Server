plugins {
    application
}

application {
    mainClass.set("com.server.engine.application.Application")
}

dependencies {
    implementation(project(":utilities"))
    implementation(project(":network-engine"))
    implementation(project(":game-engine"))
    implementation(project(":game-db"))
}
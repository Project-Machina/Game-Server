dependencies {
    implementation(project(":network-engine"))
    implementation(project(":game-db"))
    implementation(project(":utilities"))
    implementation("io.netty:netty-buffer:4.1.69.Final")
    testImplementation("io.netty:netty-all:4.1.69.Final")
}
dependencies {
    implementation(project(":network-engine"))
    implementation(project(":game-db"))
    implementation(project(":utilities"))
    implementation("io.netty:netty-buffer:4.1.72.Final")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    testImplementation("io.netty:netty-all:4.1.72.Final")
}
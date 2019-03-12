plugins {
    kotlin("jvm") version "1.3.21"
}

group = "be.jschoreels.strengthtrainer"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.1")
}

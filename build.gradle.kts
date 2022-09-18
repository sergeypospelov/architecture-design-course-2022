plugins {
    kotlin("jvm") version "1.7.10"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

}

dependencies {
//    implementation kotlin("test-junit")
    implementation(kotlin("reflect"))
}
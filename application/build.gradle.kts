plugins {
    id("java-common-conventions")
}

group = "com.fullcycle.admin.catalogo.application"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))

    testImplementation(project(path = ":domain", configuration = "testClasses"))
}
plugins {
    `java-library`
    jacoco
}

version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        targetCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vavr:vavr:0.10.7")

    testImplementation(platform("org.junit:junit-bom:5.14.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter:5.20.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher") // versão vem do BOM
}

jacoco {
    toolVersion = "0.8.14"
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Test>("unitTest") {
    description = "Runs the unit tests."
    group = "verification"

    useJUnitPlatform{
        includeTags("unitTest")
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"

    useJUnitPlatform{
        includeTags("integrationTest")
    }
}

tasks.register<Test>("e2eTest") {
    description = "Runs the E2E tests."
    group = "verification"

    useJUnitPlatform{
        includeTags("e2eTest")
    }
}

configurations.all {
    resolutionStrategy {
        force(
            "org.junit.platform:junit-platform-commons:1.14.0",
            "org.junit.platform:junit-platform-launcher:1.14.0",
            "org.junit.jupiter:junit-jupiter-api:5.14.0",
            "org.junit.jupiter:junit-jupiter-engine:5.14.0"
        )
    }
}
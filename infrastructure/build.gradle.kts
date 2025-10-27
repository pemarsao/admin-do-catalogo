import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-mysql:9.11.0")
    }
}

plugins {
    id("java-common-conventions")
    id("jacoco-report-aggregation")
    id("application")
    id("org.springframework.boot") version "2.7.17"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "9.11.0"
}

group = "com.fullcycle.admin.catalogo.infrastructure"

tasks.named<BootJar>("bootJar") {
    archiveFileName.set("application.jar")
    destinationDirectory.set(file("${rootDir}/build/libs"))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))

    implementation("com.google.cloud:google-cloud-storage:2.59.0")
    implementation("com.google.guava:guava:33.5.0-jre")

    implementation("com.mysql:mysql-connector-j:8.0.33") // <-- Isso é para a sua aplicação
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.8.0")
    implementation("org.springdoc:springdoc-openapi-ui:1.8.0")

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")

    implementation("org.yaml:snakeyaml:1.33") {
        version { strictly("1.33") }
    }

    testImplementation(project(path = ":domain", configuration = "testClasses"))

    testImplementation("org.springframework.amqp:spring-rabbit-test:2.4.17")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage") // desativa JUnit 4
        exclude("org.junit.jupiter") // evita versões antigas do Jupiter
        exclude("org.junit.platform")
    }
    testImplementation("org.flywaydb:flyway-core:9.11.0")
    testImplementation("org.flywaydb:flyway-mysql:9.11.0")

    testImplementation("org.testcontainers:testcontainers:1.18.3")
    testImplementation("org.testcontainers:mysql:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

    testRuntimeOnly("com.h2database:h2")
}

flyway {
    url = System.getenv("FLYWAY_DB") ?: "jdbc:mysql://localhost:3306/adm_videos"
    user = System.getenv("FLYWAY_USER") ?: "root"
    password = System.getenv("FLYWAY_PASS") ?: "123456"
    cleanDisabled = true
}

tasks.testCodeCoverageReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.projectDirectory.file("../build/reports/jacoco/report.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.projectDirectory.dir("../build/reports/jacoco"))
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }
    }
}


plugins {
    id("java-common-conventions")
}

group = "com.fullcycle.admin.catalogo.domain"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.github.javafaker:javafaker:1.0.2")
}

// build a repositories-testJar.jar with test classes for services tests
tasks.register<Jar>("testJar") {
    archiveClassifier.set("testJar")
    from(sourceSets["test"].output)
}

// the testJar must be built within the build phase
tasks.build {
    dependsOn(tasks.withType<Jar>())
}

// needed for publishing plugin to be aware of the testJar
configurations.create("testClasses") {
    extendsFrom(configurations["testImplementation"])
}

artifacts {
    add("testClasses", tasks.named<Jar>("testJar"))
}
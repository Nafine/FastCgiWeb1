plugins {
    id("java")
}

group = "se.ifmo"

repositories {
    mavenCentral()
}

dependencies {
    implementation(files("lib/fastcgi-lib.jar"))
}

tasks.register<Jar>("server"){
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes(
            "Main-Class" to "se.ifmo.Main"
        )
    }
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
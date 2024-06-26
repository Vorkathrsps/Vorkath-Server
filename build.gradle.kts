plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    id("application")
}

group = "com.cryptic"
version = "1.1.1"

repositories {
    mavenCentral()
    maven("https://raw.githubusercontent.com/OpenRune/hosting/master")
    maven("https://jitpack.io/")
    maven("https://maven.scijava.org/content/repositories/public/")
    flatDir {
        dirs("libs")
    }
}


dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.slf4j:log4j-over-slf4j:2.0.7")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")

    implementation("commons-codec:commons-codec:1.16.0")
    implementation("io.github.classgraph:classgraph:4.8.170")
    implementation("cc.ekblad:4koma:1.2.0")
    implementation("com.jcabi:jcabi-log:0.17.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("me.shib.java.lib:diction:0.1.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.newrelic.agent.java:newrelic-java:8.4.0") {
        isTransitive = false
    }
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("io.netty:netty-all:4.1.110.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner:2.17.0")
    implementation("com.displee:disio:2.2")
    implementation("com.github.jsurfer:jsurfer:1.6.5")
    implementation("com.github.jsurfer:jsurfer-jackson:1.6.3")
    implementation("com.displee:rs-cache-library:7.1.3")
    implementation("org.tukaani:xz:1.9")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.slf4j:log4j-over-slf4j:2.0.7")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("javax.json:javax.json-api:1.1.4")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("commons-io:commons-io:2.12.0")
    implementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
    implementation("org.itadaki:bzip2:0.9.1")
    implementation("co.paralleluniverse:quasar-core:0.8.0")
    implementation("com.mysql:mysql-connector-j:8.0.33")
    implementation("joda-time:joda-time:2.12.5")
    implementation("commons-lang:commons-lang:2.6")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.apache.commons:commons-compress:1.24.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("it.unimi.dsi:fastutil:8.5.13")
    implementation("com.google.guava:failureaccess:1.0.1")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("com.typesafe:config:1.4.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("com.google.guava:guava:32.0.0-jre")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
    implementation("com.diffplug.spotless:spotless-maven-plugin:2.40.0")
    implementation("com.lmax:disruptor:4.0.0.RC1")
    implementation("me.tongfei:progressbar:0.9.2")
    implementation("dev.openrune:filestore:1.2.13")
    implementation("com.github.cliftonlabs:json-simple:4.0.1")
    implementation("com.paypal.sdk:checkout-sdk:2.0.0")
    implementation("org.projectlombok:lombok:1.18.32")
    implementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    implementation("com.intellij:openapi:7.0.3")
    implementation("moe.pine:nonnull:0.1.1")
    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:24.1.0")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

}
tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
    options.release.set(21)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.cryptic.GameServer"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("--enable-preview")
}

application {
    mainClass.set("com.cryptic.GameServer")

    applicationDefaultJvmArgs = listOf(
        "-noverify",
        "-Dio.netty.tryReflectionSetAccessible=true",
        "-XX:TieredStopAtLevel=1",
        "-XX:CompileThreshold=1500",
        "--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED",
        "--add-opens", "java.base/jdk.internal.vm=ALL-UNNAMED",
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--enable-preview"
    )
}

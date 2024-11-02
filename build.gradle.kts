import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  idea
  java

  jacoco
  pmd

  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependencyManagement)

  alias(libs.plugins.versions)
  alias(libs.plugins.spotless)
}

group = "io.kraluk"
version = "0.0.1-SNAPSHOT"

val integrationTestImplementation: Configuration = configurations.create("integrationTestImplementation")
  .extendsFrom(configurations.testImplementation.get())
val integrationTestRuntimeOnly: Configuration = configurations.create("integrationTestRuntimeOnly")
  .extendsFrom(configurations.testRuntimeOnly.get())

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

  integrationTestImplementation("org.testcontainers:junit-jupiter")
  integrationTestImplementation("org.testcontainers:postgresql")
}

tasks.test {
  useJUnitPlatform()
  defaultCharacterEncoding = "UTF-8"
  jvmArgs("-XX:+EnableDynamicAgentLoading")

  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events(
      org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
    )
  }
}

sourceSets {
  create("integrationTest") {
    compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
  }
}

val integrationTest = tasks.register<Test>("integrationTest") {
  description = "Runs integration tests."
  group = "verification"
  defaultCharacterEncoding = "UTF-8"

  useJUnitPlatform()
  jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xmx1g")

  testLogging {
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
    events(
      org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
      org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
    )
  }

  testClassesDirs = sourceSets["integrationTest"].output.classesDirs
  classpath = sourceSets["integrationTest"].runtimeClasspath
  shouldRunAfter(tasks.test)

  finalizedBy(tasks.jacocoTestReport)
}

// customize if needed: https://docs.gradle.org/current/userguide/jacoco_plugin.html
// reports are in build/reports/jacoco as index.html
tasks.jacocoTestReport {
  dependsOn(integrationTest) // all tests are required to run before generating the report
}

tasks.check { dependsOn(integrationTest) }

spotless {
  java {
    googleJavaFormat(toolLibs.versions.googleJavaFormat.get())
      .aosp()
      .reflowLongStrings()
      .formatJavadoc(false)
      .reorderImports(true)
      .groupArtifact("com.google.googlejavaformat:google-java-format")
  }
}

pmd {
  toolVersion = toolLibs.versions.pmd.get()
  isIgnoreFailures = false
  isConsoleOutput = true
}

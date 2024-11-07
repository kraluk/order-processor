import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  idea
  java

  jacoco

  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependencyManagement)

  alias(libs.plugins.versions)
  alias(libs.plugins.spotless)
  alias(libs.plugins.errorprone)
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
  implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:${libs.versions.springCloudAws.get()}"))

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-logging")
  implementation("org.springframework.boot:spring-boot-starter-jdbc")
  implementation("org.springframework.boot:spring-boot-starter-jooq")
  implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
  implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")

  runtimeOnly("org.postgresql:postgresql")
  implementation("org.liquibase:liquibase-core")

  implementation("com.gruelbox:transactionoutbox-core:${libs.versions.transactionoutbox.get()}")
  implementation("com.gruelbox:transactionoutbox-spring:${libs.versions.transactionoutbox.get()}")
  implementation("com.gruelbox:transactionoutbox-jackson:${libs.versions.transactionoutbox.get()}")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
  implementation("org.javamoney:moneta:${libs.versions.javaMoney.get()}")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("org.assertj:assertj-core")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.awaitility:awaitility")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  integrationTestImplementation("org.springframework.boot:spring-boot-testcontainers")
  integrationTestImplementation("org.testcontainers:junit-jupiter")
  integrationTestImplementation("org.testcontainers:postgresql")
  integrationTestImplementation("org.testcontainers:localstack")
  integrationTestImplementation("io.awspring.cloud:spring-cloud-aws-test")
  integrationTestImplementation("io.awspring.cloud:spring-cloud-aws-testcontainers")

  errorprone("com.google.errorprone:error_prone_core:${toolLibs.versions.errorprone.get()}")
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
    toggleOffOn()
    palantirJavaFormat()
      .style("GOOGLE")
      .formatJavadoc(true)
    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
  }
}

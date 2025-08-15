import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Property

plugins {
  idea
  java

  jacoco

  alias(libs.plugins.spring.boot)
  alias(libs.plugins.spring.dependencyManagement)
  alias(libs.plugins.jooq)

  alias(libs.plugins.versions)
  alias(libs.plugins.spotless)
  alias(libs.plugins.errorprone)
}

group = "io.kraluk"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(jvm.versions.java.get().toInt())
  }
}

val testIntegration: SourceSet by sourceSets.creating {
  compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
  runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
}

val testIntegrationImplementation: Configuration by configurations.getting {
  extendsFrom(configurations.testImplementation.get())
}
val testIntegrationRuntimeOnly: Configuration by configurations.getting {
  extendsFrom(configurations.testRuntimeOnly.get())
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
  implementation("org.springframework.boot:spring-boot-starter-aop")
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

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:${libs.versions.springDoc.get()}")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${libs.versions.springDoc.get()}")
  implementation("io.micrometer:micrometer-core")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("org.assertj:assertj-core")
  testImplementation("org.awaitility:awaitility")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  testIntegrationImplementation("org.springframework.boot:spring-boot-testcontainers")
  testIntegrationImplementation("org.testcontainers:junit-jupiter")
  testIntegrationImplementation("org.testcontainers:postgresql")
  testIntegrationImplementation("org.testcontainers:localstack")
  testIntegrationImplementation("io.awspring.cloud:spring-cloud-aws-test")
  testIntegrationImplementation("io.awspring.cloud:spring-cloud-aws-testcontainers")

  compileOnly("org.jspecify:jspecify:${toolLibs.versions.jspecify.get()}")
  errorprone("com.google.errorprone:error_prone_core:${toolLibs.versions.errorprone.get()}")
  errorprone("com.uber.nullaway:nullaway:${toolLibs.versions.nullaway.get()}")

  jooqGenerator("org.jooq:jooq-meta-extensions-liquibase:${dependencyManagement.importedProperties["jooq.version"]}")
  jooqGenerator(files("src/main/resources"))
  jooqGenerator("org.liquibase:liquibase-core")
  jooqGenerator("org.slf4j:slf4j-jdk14")
}

tasks.withType<JavaCompile>().configureEach {
  options.errorprone.check("NullAway", CheckSeverity.ERROR)
  options.errorprone.option("NullAway:AnnotatedPackages", "io.kraluk")

  if (name.lowercase().contains("test")) {
    options.errorprone {
      disable("NullAway")
    }
  }
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

val testIntegrationTask = tasks.register<Test>("testIntegration") {
  description = "Runs integration tests."
  group = "verification"
  defaultCharacterEncoding = "UTF-8"

  useJUnitPlatform()
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

  testClassesDirs = sourceSets["testIntegration"].output.classesDirs
  classpath = sourceSets["testIntegration"].runtimeClasspath
  shouldRunAfter(tasks.test)

  finalizedBy(tasks.jacocoTestReport)
}

// customize if needed: https://docs.gradle.org/current/userguide/jacoco_plugin.html
// reports are in build/reports/jacoco as index.html
tasks.jacocoTestReport {
  dependsOn(testIntegrationTask) // all tests are required to run before generating the report
}

tasks.check { dependsOn(testIntegrationTask) }

jooq {
  version.set("${dependencyManagement.importedProperties["jooq.version"]}")
  edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

  configurations {
    create("main") {
      generateSchemaSourceOnCompilation.set(true)

      jooqConfiguration.apply {
        logging = org.jooq.meta.jaxb.Logging.WARN

        generator.apply {
          name = "org.jooq.codegen.DefaultGenerator"

          target.packageName = "io.kraluk.orderprocessor.jooq"

          database.apply {
            name = "org.jooq.meta.extensions.liquibase.LiquibaseDatabase"
            properties.addAll(
              listOf(
                Property()
                  .withKey("scripts")
                  .withValue("liquibase/changelog.xml"),
                Property()
                  .withKey("includeLiquibaseTables")
                  .withValue("false")
              )
            )
            forcedTypes.add(
              ForcedType()
                .withUserType("java.time.Instant")
                .withConverter("""
                  org.jooq.Converter.ofNullable(
                    java.time.OffsetDateTime.class,
                    java.time.Instant.class,
                    o -> o.toInstant(),
                    i -> i.atOffset(java.time.ZoneOffset.UTC))
                """.trimIndent())
                .withIncludeTypes("timestamp\\ with\\ time\\ zone")
            )
          }

          generate.apply {
            isDeprecated = false
            isRecords = true
            isImmutablePojos = false
            isFluentSetters = true
          }

          strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
        }
      }
    }
  }
}

spotless {
  java {
    targetExclude("**/generated-src/**/*.java")
    toggleOffOn()
    palantirJavaFormat()
      .style("GOOGLE")
      .formatJavadoc(true)
    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
  }
}

rootProject.name = "order-processor"

dependencyResolutionManagement {
  versionCatalogs {
    create("jvm") {
      version("java", "24")
    }
    create("libs") {
      plugin("spring.boot", "org.springframework.boot").version("3.5.5")
      plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.7")
      plugin("jooq", "nu.studer.jooq").version("10.1.1")

      plugin("versions", "com.github.ben-manes.versions").version("0.52.0")
      plugin("spotless", "com.diffplug.spotless").version("7.2.1")
      plugin("errorprone", "net.ltgt.errorprone").version("4.3.0")

      version("springCloudAws", "3.4.0")
      version("springDoc", "2.8.13")
      version("transactionoutbox", "6.0.609") // BE AWARE: check migration scripts before bumping the version up
      version("javaMoney", "1.4.5")
    }
    create("testLibs") {
    }
    create("toolLibs") {
      version("jspecify", "1.0.0")
      version("errorprone", "2.41.0")
      version("nullaway", "0.12.9")
    }
  }
}

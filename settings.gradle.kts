rootProject.name = "order-processor"

dependencyResolutionManagement {
  versionCatalogs {
    create("jvm") {
      version("java", "25")
    }
    create("libs") {
      plugin("spring.boot", "org.springframework.boot").version("3.5.7")
      plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.7")
      plugin("jooq", "nu.studer.jooq").version("10.1.1")

      plugin("versions", "com.github.ben-manes.versions").version("0.53.0")
      plugin("spotless", "com.diffplug.spotless").version("8.0.0")
      plugin("errorprone", "net.ltgt.errorprone").version("4.3.0")

      version("springCloudAws", "3.4.0")
      version("springDoc", "2.8.13")
      version("awsSdk", "2.36.2")
      version("transactionoutbox", "6.1.653") // BE AWARE: check migration scripts before bumping the version up
      version("javaMoney", "1.4.5")
    }
    create("testLibs") {
    }
    create("toolLibs") {
      version("jspecify", "1.0.0")
      version("errorprone", "2.43.0")
      version("nullaway", "0.12.10")
    }
  }
}

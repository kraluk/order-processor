rootProject.name = "order-processor"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      plugin("spring.boot", "org.springframework.boot").version("3.3.5")
      plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.6")

      plugin("versions", "com.github.ben-manes.versions").version("0.51.0")
      plugin("spotless", "com.diffplug.spotless").version("7.0.0.BETA4")
      plugin("errorprone", "net.ltgt.errorprone").version("4.1.0")

      version("springCloudAws", "3.2.1")
      version("transactionoutbox", "5.5.447") // BE AWARE: check migration scripts before bumping the version up
      version("javaMoney", "1.4.4")
    }
    create("testLibs") {
      version("awaitility", "4.2.2")
      version("mockito", "5.4.0")
    }
    create("toolLibs") {
      version("errorprone", "2.35.1")
    }
  }
}

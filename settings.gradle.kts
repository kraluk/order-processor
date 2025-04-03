rootProject.name = "order-processor"

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      plugin("spring.boot", "org.springframework.boot").version("3.4.4")
      plugin("spring.dependencyManagement", "io.spring.dependency-management").version("1.1.7")
      plugin("jooq", "nu.studer.jooq").version("10.0")

      plugin("versions", "com.github.ben-manes.versions").version("0.52.0")
      plugin("spotless", "com.diffplug.spotless").version("7.0.2")
      plugin("errorprone", "net.ltgt.errorprone").version("4.1.0")

      version("springCloudAws", "3.3.0")
      version("springDoc", "2.8.6")
      version("transactionoutbox", "6.0.585") // BE AWARE: check migration scripts before bumping the version up
      version("javaMoney", "1.4.5")
    }
    create("testLibs") {
    }
    create("toolLibs") {
      version("errorprone", "2.37.0")
    }
  }
}

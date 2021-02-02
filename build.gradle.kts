plugins {
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    id("java")
}

group = "com.gt.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_15
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
//        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
//        mavenBom("org.springframework:spring-framework-bom:5.3.3")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

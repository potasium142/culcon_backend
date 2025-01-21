plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.culcon"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.26")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.modelmapper:modelmapper:3.2.2")
    implementation("com.cloudinary:cloudinary-http44:1.39.0")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("com.paypal.sdk:paypal-server-sdk:0.6.1")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok")

    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")

//    ===============================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-mail")
    testImplementation("org.modelmapper:modelmapper:3.2.0")
    mockitoAgent("org.mockito:mockito-core:5.14.2") { isTransitive = false }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}


tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    useJUnitPlatform()
}

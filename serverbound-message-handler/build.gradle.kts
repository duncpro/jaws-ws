plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    // Logging
    implementation("org.slf4j:slf4j-api:1.7.31")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.2.0")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")

    // AWS Lambda
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:2.2.9")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.17.19"))
    implementation("software.amazon.awssdk:apigatewaymanagementapi")
    implementation("software.amazon.awssdk:rdsdata")

    // JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.4")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.4")

    implementation("com.google.inject:guice:5.0.1")

    // Testing Tools
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core:3.+")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// This artifact is consumed by jaws-wså/aws-cloud-app.
// Based off of https://docs.aws.amazon.com/lambda/latest/dg/java-package.html
val buildLambdaPackage by tasks.registering(Zip::class) {
    dependsOn(tasks.test)
    from(tasks.compileJava)
    from(tasks.processResources)
    into("lib") {
        from(configurations.runtimeClasspath)
    }
}
val lambdaPackage: Configuration by configurations.creating;
artifacts.add(lambdaPackage.name, buildLambdaPackage)

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

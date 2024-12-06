plugins {
  id("java")
  id("com.diffplug.spotless") version "7.0.0.BETA4"
  application
}

group = "ar.zaffa"

version = "0.1-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  implementation(libs.picocli)
  implementation(libs.classgraph)

  annotationProcessor(libs.picocli.codegen)

  testImplementation(platform(libs.junit.bom))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test { useJUnitPlatform() }

tasks.withType<JavaCompile> {
  options.annotationProcessorPath = configurations["annotationProcessor"]
  options.compilerArgs.add("-Aproject=${project.group}/${project.name}")
}

application { mainClass = "ar.zaffa.aoc.Cli" }

spotless {
  java {
    googleJavaFormat().reflowLongStrings().skipJavadocFormatting()
    formatAnnotations()
  }
}

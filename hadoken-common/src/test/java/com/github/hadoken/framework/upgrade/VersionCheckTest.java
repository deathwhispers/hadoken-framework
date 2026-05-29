package com.github.hadoken.framework.upgrade;

import org.junit.Test;
import org.springframework.boot.SpringBootVersion;

public class VersionCheckTest {

    @Test
    public void printSpringBootVersion() {
        String version = SpringBootVersion.getVersion();
        System.out.println("=== Spring Boot Version Check ===");
        System.out.println("SpringBootVersion.getVersion(): " + version);
        System.out.println("=== End Version Check ===");
    }

    @Test
    public void checkClasspath() {
        System.out.println("=== Classpath Check ===");
        Package pkg = org.springframework.boot.SpringBootVersion.class.getPackage();
        System.out.println("Package: " + pkg);
        System.out.println("Implementation Title: " + pkg.getImplementationTitle());
        System.out.println("Implementation Version: " + pkg.getImplementationVersion());
        System.out.println("Specification Version: " + pkg.getSpecificationVersion());
        System.out.println("=== End Classpath Check ===");
    }
}
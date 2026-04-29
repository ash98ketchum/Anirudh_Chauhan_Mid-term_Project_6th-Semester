package com.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * APPLICATION ENTRY POINT
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * This is the "front door" of our Spring Boot application. When you run this
 * class, the entire application starts up.
 *
 * @SpringBootApplication is a COMPOSITE annotation — it combines THREE annotations:
 *
 * 1. @Configuration
 *    → Marks this class as a source of bean definitions (Spring's way of
 *      managing objects). Think of it as a "recipe book" that tells Spring
 *      how to create and wire together the objects your app needs.
 *
 * 2. @EnableAutoConfiguration
 *    → Tells Spring Boot to automatically configure beans based on what's
 *      on the classpath. Since we have spring-boot-starter-web, it auto-configures
 *      an embedded Tomcat server. Since we have spring-boot-starter-jdbc, it
 *      auto-configures a DataSource and JdbcTemplate.
 *      Analogy: Like a smart home that turns on lights when you walk in —
 *      it detects what you need and sets it up for you.
 *
 * 3. @ComponentScan
 *    → Scans the current package (com.student) and ALL sub-packages for
 *      Spring-managed components (@Component, @Service, @Repository, @Controller).
 *      It "discovers" your classes and registers them as Spring beans.
 *      Analogy: Like a talent scout that finds all the employees (beans)
 *      in your company (packages) and assigns them to their departments.
 *
 * WHY main() CALLS SpringApplication.run()?
 *    → SpringApplication.run() does everything:
 *      - Creates the ApplicationContext (the "brain" of Spring — manages all beans)
 *      - Triggers auto-configuration
 *      - Starts the embedded Tomcat server
 *      - Initializes the DataSource and runs schema.sql
 *      - Scans for components and wires them together via dependency injection
 */
@SpringBootApplication
public class StudentCrudJdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentCrudJdbcApplication.class, args);
    }
}

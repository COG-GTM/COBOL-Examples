package com.example.cobolmigration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application configuration.
 *
 * Subprogram Pattern (replaces sub_program/main_app.cbl and sub_program/sub.cbl):
 * ---------------------------------------------------------------------------------
 * The COBOL subprogram demonstrates call-by-content vs call-by-reference:
 *
 *   CALL "sub-app" USING BY CONTENT ws-item-1 BY CONTENT ws-item-2
 *     -> The sub-program receives copies of the values. Changes in the sub-program
 *        do NOT affect the caller's variables.
 *
 *   CALL "sub-app" USING ws-item-1 ws-item-2  (default is BY REFERENCE)
 *     -> The sub-program can modify the caller's variables directly.
 *
 * In Java, this maps naturally to the service layer pattern:
 *
 *   - Passing immutable objects (String, Integer, records) = by-content equivalent.
 *     The service receives a copy; the caller's value is unaffected.
 *
 *   - Passing mutable objects (DTOs, lists) or returning modified values = by-reference equivalent.
 *     The service can modify the object's state, visible to the caller.
 *
 *   - CANCEL "sub-app" (resetting WORKING-STORAGE) has no direct equivalent in Java;
 *     the closest pattern is creating a new service instance or resetting its state.
 *     With Spring's default singleton scope, services maintain state across calls
 *     (like WORKING-STORAGE), while method parameters behave like LOCAL-STORAGE
 *     (fresh on each invocation).
 *
 * Terminal UI Programs — Conversion Notes:
 * -----------------------------------------
 * The following COBOL programs are terminal/screen-mode specific:
 *   - accept/accept.cbl: Screen mode input. Validation concepts (upper-case conversion)
 *     are implemented via request validation in controllers (e.g., @Valid, custom validators).
 *   - accept/accept_from.cbl: Environment variables and dates are accessed via Spring's
 *     @Value annotation, Environment bean, and java.time APIs.
 *   - screen_size/get_screen_size.cbl: Terminal dimensions — not applicable to web; dropped.
 *   - display_test/display-test.cbl: Positioned display with colors — not applicable; dropped.
 *   - mouse/mouse_example.cbl: Mouse paint program — not applicable; dropped.
 *   - display_timing/display_timing.cbl: Screen write benchmarking — not applicable; dropped.
 *   - read_command_args/: Command-line args — replaced by Spring Boot's ApplicationArguments.
 *   - comp_test/comp_test.cbl: COMP to display — Java handles numeric types natively.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    /**
     * Configure content negotiation to default to JSON.
     * Without this, jackson-dataformat-xml on the classpath causes Spring
     * to prefer XML serialization for all endpoints.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

}

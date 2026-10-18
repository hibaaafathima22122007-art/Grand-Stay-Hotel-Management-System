
package com.hotel;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configures Spring MVC request interception for Grand Stay.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    /**
     * Creates the configuration with the login interceptor.
     *
     * @param loginInterceptor interceptor used to check administrator access
     */
    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    /**
     * Registers the login interceptor for protected application routes.
     *
     * @param registry registry used to configure MVC interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(
                        "/dashboard",
                        "/rooms", "/rooms/**",
                        "/guests", "/guests/**",
                        "/bookings", "/bookings/**",
                        "/billing", "/billing/**",
                        "/reports", "/reports/**"
                );
    }
}

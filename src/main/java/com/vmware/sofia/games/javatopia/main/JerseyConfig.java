package com.vmware.sofia.games.javatopia.main;



import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.web.filter.RequestContextFilter;

public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RequestContextFilter.class);
        packages("com.vmware.sofia.games.javatopia.server.rest");
        register(LoggingFilter.class);
    }
}

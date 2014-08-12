package com.vmware.sofia.games.javatopia.main;

import org.apache.log4j.Logger;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by kosio on 7/23/14.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Bootstrap  extends SpringBootServletInitializer {
    static Logger log = Logger.getLogger(Bootstrap.class.getName());
    public static final String  BINARY_ROOT_FOLDER =  System.getProperty("user.home")+ "/Software/Graphviz2/";

    public static void main(String[] args) throws Exception {
        unzipGraphiz();
        SpringApplication.run(Bootstrap.class, args);

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Bootstrap.class);
    }


    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/api/*");
        // our rest resources will be available in the path /rest/*
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
           return registration;
    }



    public static void unzipGraphiz() throws Exception {
        if (new File(BINARY_ROOT_FOLDER, "/release/share/Thumbs.db").exists())  return;
        try (
                InputStream st = Bootstrap.class.getResourceAsStream("/graphviz.zip");

        ) {
            getZipFiles(st, BINARY_ROOT_FOLDER);
        }
    }

    public static void getZipFiles(InputStream is, String dest) throws Exception
    {
        byte[] buf = new byte[1024];
        ZipInputStream z = null;
        z = new ZipInputStream(
                is);
        ZipEntry e = z.getNextEntry();
        loop:
        while (e != null)
        {
            //for each entry to be extracted
            String entryName = e.getName();
            int n;
            FileOutputStream fos;
            if (e.isDirectory()) {
                new File(dest+entryName).mkdirs();
            } else  {
                new File(dest + entryName).getParentFile().mkdirs();
                log.info("Writing: "+dest+entryName);
                fos = new FileOutputStream(
                        dest+entryName);
                while ((n = z.read(buf, 0, 1024)) > -1) {
                    fos.write(buf, 0, n);
                }
                fos.close();
            }
            z.closeEntry();
            e = z.getNextEntry();

        }
        z.close();
    }


}


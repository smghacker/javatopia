package com.vmware.sofia.games.javatopia.server.tests.tools;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class OSValidator {
   @Value("#{systemProperties['os.name']}")
   private String osName;

   @PostConstruct
   private void init() {
      osName = osName.toLowerCase();
   }

   public static void main(String[] args) {
      @Configuration
      @EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
      @ComponentScan(basePackageClasses = { OSValidator.class })
      class OsValidatorConfig {

      }
      SpringApplication springApp = new SpringApplicationBuilder(
            OsValidatorConfig.class).showBanner(false).web(false).build();
      ConfigurableApplicationContext ctx = springApp.run(args);
      OSValidator osValidator = ctx.getBean(OSValidator.class);
      System.out.println(osValidator.osName);

      if (osValidator.isWindows()) {
         System.out.println("This is Windows");
      } else if (osValidator.isMac()) {
         System.out.println("This is Mac");
      } else if (osValidator.isUnix()) {
         System.out.println("This is Unix or Linux");
      } else if (osValidator.isSolaris()) {
         System.out.println("This is Solaris");
      } else {
         System.out.println("Your OS is not support!!");
      }
   }

   public boolean isWindows() {
      return (osName.indexOf("win") >= 0);
   }

   public boolean isMac() {
      return (osName.indexOf("mac") >= 0);
   }

   public boolean isUnix() {
      return (osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName
            .indexOf("aix") > 0);
   }

   public boolean isSolaris() {
      return (osName.indexOf("sunos") >= 0);
   }
}
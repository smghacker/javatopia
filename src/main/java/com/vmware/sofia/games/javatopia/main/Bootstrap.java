package com.vmware.sofia.games.javatopia.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vmware.sofia.games.javatopia.server.rest.RootsFacade;
import com.vmware.sofia.games.javatopia.server.tests.tools.OSValidator;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

/**
 * Created by kosio on 7/23/14.
 */
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan(basePackageClasses = { RootsFacade.class, OSValidator.class })
@Import(SecurityConfig.class)
public class Bootstrap {
   static Logger log = Logger.getLogger(Bootstrap.class.getName());
   public static final String BINARY_ROOT_FOLDER = System
         .getProperty("user.home") + "/Software/Graphviz2/";

   public static void main(String[] args) throws Exception {
      unzipGraphiz();
      ConfigurableApplicationContext ctx = SpringApplication.run(
            Bootstrap.class, args);
      OSValidator osValidator = ctx.getBean(OSValidator.class);
      if (osValidator.isWindows()) {
         unzipGraphiz();
      }
      TestSuite t = TestSuite.getInstance();
   }

   public static void unzipGraphiz() throws Exception {
      if (!isUnziped()) {
         try (InputStream st = Bootstrap.class
               .getResourceAsStream("/graphviz.zip")) {
            getZipFiles(st, BINARY_ROOT_FOLDER);
         }
      }
   }

   private static boolean isUnziped() {
      return new File(BINARY_ROOT_FOLDER, "/release/share/Thumbs.db").exists();
   }

   public static void getZipFiles(InputStream is, String dest) throws Exception {
      byte[] buf = new byte[1024];
      ZipInputStream z = null;
      z = new ZipInputStream(is);
      ZipEntry e = z.getNextEntry();
      loop: while (e != null) {
         // for each entry to be extracted
         String entryName = e.getName();
         int n;
         FileOutputStream fos;
         if (e.isDirectory()) {
            new File(dest + entryName).mkdirs();
         } else {
            new File(dest + entryName).getParentFile().mkdirs();
            log.info("Writing: " + dest + entryName);
            fos = new FileOutputStream(dest + entryName);
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

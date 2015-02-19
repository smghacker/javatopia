package com.vmware.sofia.games.javatopia.server.rest;

import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.GARBAGE_CLEANER;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.LINKED_NODE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.sofia.games.javatopia.main.Bootstrap;
import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.rest.exceptions.ResourceNotFoundException;
import com.vmware.sofia.games.javatopia.server.tests.tools.OSValidator;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

@RestController
public class ImageFacade {

   private static final Object SINGLE_EXECUTION_LOCK = new Object();
   private static final int MAX_NODE_CROP_NUMBER = 100;
   private static final int MAX_RELATION = 250;

   private OSValidator osValidator;

   @Autowired
   public ImageFacade(OSValidator osValidator) {
      this.osValidator = osValidator;
   }

   @RequestMapping("/api/sector/{sect}/image.png")
   public byte[] getImage(@PathVariable("sect") int sector) {
      synchronized (SINGLE_EXECUTION_LOCK) {
         try {
            if (sector < 1 || sector > TestSuite.SECTOR_COUNT) {
               throw new ResourceNotFoundException();
            }

            final Graph gr = TestSuite.getInstance().sector(sector);
            File tempFile = File.createTempFile("Grpaph-" + sector, ".gr");
            try (PrintWriter commandInput = new PrintWriter(new BufferedWriter(
                  new FileWriter(tempFile)))) {
               commandInput.print("digraph \"unix\" {");
               commandInput.print("node [shape=circle; color=red];");
               Iterator<Long> it2 = gr.getRootsIterator();
               while (it2.hasNext()) {
                  commandInput.print("" + it2.next());
                  commandInput.print(";");
               }
               commandInput.print("node[shape=circle; color=black];");
               gr.enterSuspendedState();
               try {
                  it2 = gr.getNodeIterator();
                  while (it2.hasNext()) {
                     if (gr.size() > MAX_NODE_CROP_NUMBER)
                        break;
                     Long l = it2.next();
                     boolean linked = gr.getNodeAttribute(l, LINKED_NODE) != null;
                     if (linked) {
                        commandInput.print("" + l);
                        commandInput.print(";");
                     }
                  }
               } finally {
                  gr.leaveSuspendedState();
               }
               commandInput.print("node[shape=box; color=gray];\n");
               int maxElement = MAX_RELATION;
               gr.enterSuspendedState();
               try {
                  Iterator<Long> it = gr.getNodeIterator();
                  label: while (it.hasNext()) {
                     Long node = it.next();
                     String nodeDump = getNodeName(gr, node);
                     java.util.Set<Long> to = gr.getLinks(node);
                     for (Long toCurrent : to) {
                        if (maxElement-- < -1)
                           break label;
                        commandInput.print("" + nodeDump);
                        commandInput.print("-> ");
                        commandInput.print(getNodeName(gr, toCurrent));
                        commandInput.print(";\n");
                     }
                  }
               } finally {
                  gr.leaveSuspendedState();
               }
               commandInput.print("}");
               commandInput.print("\032");
               commandInput.flush();
            }
            System.out.println(getDotApplicationPath() + " -v -Tpng -O "
                  + tempFile.toString());

            ProcessBuilder procBuilder = new ProcessBuilder(getDotApplicationPath(),
                  "-Tpng", "-O", tempFile.toString());
            procBuilder.redirectErrorStream(true);
            Process proc = procBuilder.start();
            proc.waitFor();
            Path filePath = Paths.get(tempFile.toString() + ".png");
            byte[] pngAsByteaArr = Files.readAllBytes(filePath);
            Files.deleteIfExists(tempFile.toPath());
            Files.deleteIfExists(filePath);

            return pngAsByteaArr;
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public String getNodeName(Graph gr, Long t) {
      String hero = (String) gr.getNodeAttribute(t, GARBAGE_CLEANER);
      if (hero != null) {
         return hero.toUpperCase() + "_" + t;
      } else {
         return "" + t;
      }
   }

   private String getDotApplicationPath() {
      return osValidator.isWindows() ? Paths.get(Bootstrap.BINARY_ROOT_FOLDER,
            "release/bin/dot.exe").toString() : "dot";
   }
}

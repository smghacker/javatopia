package com.vmware.sofia.games.javatopia.server.rest;

import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.GARBAGE_CLEANER;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.LINKED_NODE;
import static com.vmware.sofia.games.javatopia.server.tests.tools.IGraphAttributes.ROOT_NODE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.sofia.games.javatopia.server.core.Graph;
import com.vmware.sofia.games.javatopia.server.tests.tools.TestSuite;

@RestController
public class Statistics {

   String[] bg = new String[] {

   "FFECB0", "DD75DD", "CEB98", "A095EE", "99FD77", "E697E6", "FFFF84",
         "8EB4E6", "EAA6EA", "DFB0FF", "CEB98", "A095EE", "99FD77" };

   @RequestMapping("/api/statistics")
   public String returnStatistics() {
      LinkedHashMap<String, Integer> globalObjects = new LinkedHashMap<String, Integer>();
      StringBuffer results = new StringBuffer();
      results
            .append("<html><head><meta http-equiv=\"refresh\" content=\"200\" </title> <body>");
      for (int sector = 1; sector <= 10; sector++) {
         LinkedHashMap<String, Integer> sectorObjects = new LinkedHashMap<String, Integer>();

         Graph gr = TestSuite.getInstance().sector(sector).clone();
         gr.enterSuspendedState();
         try {
            Iterator<Long> it = gr.getNodeIterator();
            while (it.hasNext()) {
               Long n = it.next();
               String hero = (String) gr.getNodeAttribute(n, GARBAGE_CLEANER);
               if (hero == null) {
                  boolean normal = true;
                  if (gr.getNodeAttribute(n, LINKED_NODE) != null) {
                     normal = false;
                  }
                  if (gr.getNodeAttribute(n, ROOT_NODE) != null) {
                     normal = false;
                  }
                  if (normal) {
                     hero = "UNCOLLECTED";
                  }
               }
               if (hero != null) {
                  incPlayer(sectorObjects, hero);
                  incPlayer(globalObjects, hero);
               }
            }
         } finally {
            gr.leaveSuspendedState();
         }
         results.append(dumpTable(sectorObjects, "<b>" + "Sector " + sector
               + "</b><br/>", bg[sector]));
      }
      results.append(dumpTable(globalObjects,
            "<b>" + "Global result </b><br/>", bg[0]));
      results.append("</body>");
      return results.toString();
   }

   public String dumpTable(LinkedHashMap<String, Integer> statistics,
         String title, String bg) {
      if (statistics.size() == 0) {
         return "<br/><Center>No information available yet for " + title
               + "</Center>";
      }
      Set<Entry<String, Integer>> entries = statistics.entrySet();
      Comparator<Entry<String, Integer>> comp = new Comparator<Entry<String, Integer>>() {
         public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
            if (o1.getValue() < o2.getValue())
               return 1;
            if (o1.getValue() > o2.getValue())
               return -1;
            return 0;
         }
      };
      ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
            entries);
      Collections.sort(list, comp);
      StringBuilder result = new StringBuilder();
      result.append("<table align=\"right\" cellspacing=\"10\" border=\"1\" bgcolor=\"#"
            + bg + "\"><tr><td colspan=\"2\">" + title + "</td></tr>");
      for (Entry<String, Integer> ent : list) {
         result.append("<tr><td><FONT COLOR=\"#44000\">" + ent.getKey()
               + "</font></td><td><FONT COLOR=\"#00044\">" + ent.getValue()
               + "</font></td></tr>");
      }
      result.append("</table>");
      return result.toString();
   }

   public void incPlayer(LinkedHashMap<String, Integer> statistics,
         String player) {
      Integer i = statistics.get(player);
      if (i == null)
         i = 0;
      i++;
      statistics.put(player, i);
   }

   @Scheduled(fixedDelay = 1000)
   public void writeToFile() throws IOException {
      Files.write(Paths.get("./statistic.html"), returnStatistics().getBytes());
   }
}
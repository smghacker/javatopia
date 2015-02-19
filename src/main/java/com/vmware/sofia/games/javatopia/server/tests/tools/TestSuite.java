package com.vmware.sofia.games.javatopia.server.tests.tools;

import com.vmware.sofia.games.javatopia.server.core.Graph;

// Referenced classes of package com.vmware.sofia.games.javatopia.server.tests.tools:
//            GraphGenerator

public class TestSuite {

   private static TestSuite currentSuite;

   public static final int SECTOR_COUNT = 10;

   private final Graph graphs[] = {
         GraphGenerator.generateGraph((int) (1030 * Math.random())),
         GraphGenerator.generateGraph((int) (500 * Math.random())),
         GraphGenerator.generateGraph((int) (1050 * Math.random())),
         GraphGenerator.generateGraph((int) (980 * Math.random())),
         GraphGenerator.generateGraph((int) (1100 * Math.random())),
         GraphGenerator.generateGraph((int) (1100 * Math.random())),
         GraphGenerator.generateGraph((int) (280 * Math.random())),
         GraphGenerator.generateGraph((int) (980 * Math.random())),
         GraphGenerator.generateGraph((int) (175 * Math.random())),
         GraphGenerator.generateGraph((int) (980 * Math.random())) };

   public TestSuite() {

   }

   public Graph sector(int number) {
      return graphs[number - 1];
   }

   public static TestSuite getInstance() {
      synchronized (TestSuite.class) {
         if (currentSuite == null) {
            newTestSuite();
         }
      }
      return currentSuite;
   }

   public static void newTestSuite() {
      currentSuite = new TestSuite();
   }

}

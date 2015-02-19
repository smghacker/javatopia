package com.vmware.sofia.games.javatopia.server.tests.tools;

import java.util.HashSet;
import java.util.Random;

import com.vmware.sofia.games.javatopia.server.core.Graph;

public class GraphGenerator {

   public GraphGenerator() {
   }

   public static Graph generateGraph(int elements) {
      return generateGraph(elements, (int) ((double) elements * 1.3D));
   }

   public static Graph generateGraph(int elements, long maxNode) {
      if (maxNode < (long) elements)
         maxNode = elements * 2;
      HashSet<Long> usedRandoms = new HashSet<>();
      Random rnd = new Random();
      Long rootElements[] = new Long[elements / 15 + 1];
      Long linkedElements[] = new Long[elements / 3];
      Long unlinkedElementsCyclic1[] = new Long[elements / 5];
      Long unlinkedElementsCyclic2[] = new Long[elements / 5];
      Long unlinkedElementsRandom[] = new Long[elements - rootElements.length
            - linkedElements.length - unlinkedElementsCyclic1.length
            - unlinkedElementsCyclic2.length];
      for (int i = 0; i < rootElements.length; i++)
         rootElements[i] = getNextUniqueRandom(maxNode, rnd, usedRandoms);

      Graph g = new Graph(rootElements);
      for (int i = 0; i < rootElements.length; i++)
         g.putNodeAttribute(rootElements[i], "root", Boolean.TRUE);

      for (int i = 0; i < linkedElements.length; i++) {
         linkedElements[i] = getNextUniqueRandom(maxNode, rnd, usedRandoms);
         g.putNodeAttribute(linkedElements[i], "linked", Boolean.TRUE);
      }

      for (int i = 0; i < unlinkedElementsCyclic1.length; i++)
         unlinkedElementsCyclic1[i] = getNextUniqueRandom(maxNode, rnd,
               usedRandoms);

      for (int i = 0; i < unlinkedElementsCyclic2.length; i++)
         unlinkedElementsCyclic2[i] = getNextUniqueRandom(maxNode, rnd,
               usedRandoms);

      for (int i = 0; i < unlinkedElementsRandom.length; i++)
         unlinkedElementsRandom[i] = getNextUniqueRandom(maxNode, rnd,
               usedRandoms);

      for (int i = 0; i < linkedElements.length; i++)
         if (i < linkedElements.length / 4 + 2)
            g.addLink(rootElements[i % rootElements.length], linkedElements[i]);
         else
            g.addLink(linkedElements[rnd.nextInt(i - 1)], linkedElements[i]);

      addLinksInArray(linkedElements.length, g, linkedElements);
      addLinksInArray(unlinkedElementsRandom.length * 2, g,
            unlinkedElementsRandom);
      addLinksBetweenArrays(unlinkedElementsRandom.length, g,
            unlinkedElementsRandom, linkedElements);
      addLinksBetweenArrays(rootElements.length * 2, g,
            unlinkedElementsCyclic1, rootElements);
      addLinksBetweenArrays(unlinkedElementsCyclic2.length / 2, g,
            unlinkedElementsCyclic2, unlinkedElementsCyclic1);
      for (int i = 0; i < unlinkedElementsCyclic1.length; i++)
         g.addLink(unlinkedElementsCyclic1[i], unlinkedElementsCyclic1[(i + 1)
               % unlinkedElementsCyclic1.length]);

      for (int i = 0; i < unlinkedElementsCyclic2.length; i++)
         g.addLink(unlinkedElementsCyclic2[i], unlinkedElementsCyclic2[(i + 1)
               % unlinkedElementsCyclic2.length]);

      return g;
   }

   private static Long getNextUniqueRandom(long maxNode, Random rnd,
         HashSet<Long> usedRandoms) {
      Long res;
      do
         res = new Long(Math.abs(rnd.nextLong() % maxNode));
      while (usedRandoms.contains(res));
      usedRandoms.add(res);
      return res;
   }

   private static void addLinksInArray(int linkNumber, Graph g, Long array[]) {
      addLinksBetweenArrays(linkNumber, g, array, array);
   }

   private static void addLinksBetweenArrays(int linkNumber, Graph g,
         Long array[], Long array2[]) {
      Random rnd = new Random();
      for (int i = 0; i < linkNumber; i++) {
         g.addLink(array[rnd.nextInt(array.length)],
               array2[rnd.nextInt(array2.length)]);
      }

   }
}
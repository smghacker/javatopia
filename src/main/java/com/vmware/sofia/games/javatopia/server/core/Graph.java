package com.vmware.sofia.games.javatopia.server.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Maintains the links between the nodes, allows reading and modification. This
 * class is thread-safe.
 */

public class Graph implements Cloneable, Serializable {

   private static final long serialVersionUID = 1L;

   private Graph() {

   }

   public Graph clone() {
      Graph g = new Graph();
      g.links = new HashMap<>(links);
         g.backlinks = new HashMap<>(backlinks);
         g.nodes = new HashSet<>(nodes);
         g.roots = new HashSet<>(roots);
         g.nodeAttributes = new HashMap<>(nodeAttributes);
         g.nodeLock = new ReentrantReadWriteLock();
         return g;
   }
   public Graph(Long[] roots) {
      for (Long l : roots) {
         this.nodes.add(l);
         this.roots.add(l);
      }
   }

   /**
    * Forward direction of the arrow. Of course this link could be in node, but
    * the using of separate Graph class means we have a reason to maintain
    * separation between the node and Graph (some reason, for example Long is in
    * different library and it is not under our control).
    */
   private HashMap<Long, HashSet<Long>> links = new HashMap<Long, HashSet<Long>>();

   /**
    * Keeps the back direction. Some strategies can need this information too.
    */
   private HashMap<Long, HashSet<Long>> backlinks = new HashMap<Long, HashSet<Long>>();

   private HashSet<Long> nodes = new HashSet<Long>();
   private HashSet<Long> roots = new HashSet<Long>();
   private HashMap<Long, HashMap<String, Serializable>> nodeAttributes = new HashMap<Long, HashMap<String, Serializable>>();

   /**
    * Lock for the both links and backlinks
    */
   private ReentrantReadWriteLock nodeLock = new ReentrantReadWriteLock();

   /**
    * Returns the nodes linked from a given node
    * 
    * @param node
    *           Long to get the links from
    * @return The linked nodes as array
    */
   public Set<Long> getLinks(Long node) {
      nodeLock.readLock().lock();
      try {
         HashSet<Long> linksFromNode = links.get(node);
         if (linksFromNode == null)
            return new HashSet<Long>();
         return Collections.unmodifiableSet(linksFromNode);
      } finally {
         nodeLock.readLock().unlock();
      }
   }

   /**
    * Returns the nodes that link to a given node
    * 
    * @param node
    *           The linked node
    * @return The linking nodes as array
    */
   public Set<Long> getBacklinks(Long node) {
      nodeLock.readLock().lock();
      try {
         HashSet<Long> linksToNode = backlinks.get(node);
         if (linksToNode == null)
            return new HashSet<Long>();
         return Collections.unmodifiableSet(linksToNode);
      } finally {
         nodeLock.readLock().unlock();
      }
   }

   /**
    * Creates links and backlinks for two nodes. The adding of already existing
    * link is correct and permitted (important for the random graph generator).
    *
    * @param nodeFrom
    *           Source node
    * @param nodeTo
    *           Target node
    */
   public void addLink(Long nodeFrom, Long nodeTo) {
      nodeLock.writeLock().lock();
      nodes.add(nodeFrom);
      nodes.add(nodeTo);
      try {
         HashSet<Long> nodesToFrom = links.get(nodeFrom);
         if (nodesToFrom == null) {
            nodesToFrom = new HashSet<Long>();
            links.put(nodeFrom, nodesToFrom);
         }
         nodesToFrom.add(nodeTo);
         HashSet<Long> nodesFromTo = backlinks.get(nodeTo);
         if (nodesFromTo == null) {
            nodesFromTo = new HashSet<Long>();
            backlinks.put(nodeTo, nodesFromTo);
         }
         nodesFromTo.add(nodeFrom);
      } finally {
         nodeLock.writeLock().unlock();
      }
   }

   /**
    * Creates links and backlinks for two nodes
    *
    * @param nodeFrom
    *           Source node
    * @param nodeTo
    *           Target node true if and only if the link is successfully
    *           deleted; false otherwise
    */
   public boolean deleteLink(Long nodeFrom, Long nodeTo) {
      nodeLock.writeLock().lock();
      try {
         HashSet<Long> nodesToFrom = links.get(nodeFrom);
         if (nodesToFrom == null) {
            return false;
         }
         boolean removed = nodesToFrom.remove(nodeTo);
         if (!removed)
            return false;
         HashSet<Long> nodesFromTo = backlinks.get(nodeTo);
         if (nodesFromTo == null) {
            return false;
         }
         return nodesFromTo.remove(nodeFrom);
      } finally {
         nodeLock.writeLock().unlock();
      }
   }

   /**
    * Suspend temporary the object modifications. All threads that try to modify
    * the graph will wait until <tt>leaveCriticalSection()</tt> is called. This
    * method used by some GC strategies that require temporal suspension.
    */
   public void enterSuspendedState() {
      nodeLock.writeLock().lock();
   }

   /**
    * Return the graph back to the normal state after suspension.
    */
   public void leaveSuspendedState() {
      nodeLock.writeLock().unlock();
   }

   /**
    * Return and Iterator containing all nodes if the Graph is in suspended
    * state, The iterator should be used only before the the leaving the
    * suspended state, otherwise <tt>ConcurrentModificationException</tt> can
    * happen.
    * 
    * @return Iterator of all nodes
    * @throws IllegalStateException
    *            if the graph is not currently suspended
    */
   public Iterator<Long> getNodeIterator() {
      if (!nodeLock.writeLock().isHeldByCurrentThread()) {
         throw new IllegalStateException(
               "Cannot get the hold iterator, the thread is not in suspended state");
      }
      return nodes.iterator();
   }

   public Iterator<Long> getRootsIterator() {
      return roots.iterator();
   }

   /**
    * Assigning an attribute to a node
    * 
    * @param node
    *           number
    * @param attributeName
    * @param value
    */
   public void putNodeAttribute(Long node, String attributeName,
         Serializable value) {
      try {
         nodeLock.writeLock().lock();
         HashMap<String, Serializable> attributes = nodeAttributes.get(node);
         if (attributes == null) {
            attributes = new HashMap<String, Serializable>();
            nodeAttributes.put(node, attributes);
         }
         attributes.put(attributeName, value);
      } finally {
         nodeLock.writeLock().unlock();
      }
   }

   public Object getNodeAttribute(Long node, String attributeName) {
      try {
         nodeLock.readLock().lock();
         HashMap<String, Serializable> attributes = nodeAttributes.get(node);
         if (attributes == null)
            return null;
         return attributes.get(attributeName);
      } finally {
         nodeLock.readLock().unlock();
      }

   }

   public int size() {
      try {
         nodeLock.readLock().lock();
         return links.size();
      } finally {
         nodeLock.readLock().unlock();
      }
   }

}

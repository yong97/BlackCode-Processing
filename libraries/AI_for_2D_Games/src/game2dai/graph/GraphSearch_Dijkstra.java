/*
 * Copyright (c) 2014 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 */

package game2dai.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class GraphSearch_Dijkstra  implements IGraphSearch {

	protected Graph graph;

	/** 
	 * The settled nodes - the nodes whose shortest distances from 
	 * the source have been found. 
	 */
	protected HashSet<GraphNode> settledNodes;

	/**
	 * The unsettled nodes
	 */
	protected PriorityQueue<GraphNodeCost> unsettledNodes;

	/**
	 * Indicates the predecessor node for a given node.
	 * This is used to store the shortest path.
	 * <node of interest, node where the edge originated>
	 */
	protected HashMap<GraphNode, GraphNode> parent;

	/**
	 * Stores the smallest path cost found so far for a given node
	 */
	protected HashMap<GraphNode, Double> graphCostToNode;

	/**
	 * Used to remember examined edges
	 */
	protected HashSet<GraphEdge> examinedEdges;

	/**
	 * List for routes nodes in order of travel
	 */
	protected LinkedList<GraphNode> route;


	/**
	 * Create a search object that uses Dijkstra's algorithm
	 * for the given graph.
	 * @param graph the graph to use
	 */
	public GraphSearch_Dijkstra(Graph graph) {
		super();
		this.graph = graph;
		int nbrNodes = graph.getNbrNodes();
		// Create the data structures
		settledNodes = new HashSet<GraphNode>(nbrNodes);
		parent = new HashMap<GraphNode, GraphNode>(nbrNodes);
		graphCostToNode = new HashMap<GraphNode, Double>(nbrNodes);
		unsettledNodes = new PriorityQueue<GraphNodeCost>();
		examinedEdges = new HashSet<GraphEdge>();
		route = new LinkedList<GraphNode>();
	}

	/**
	 * Clears all data related to a search so this object can be
	 * reused for another search
	 */
	private void clear(){
		graphCostToNode.clear();
		settledNodes.clear();
		parent.clear();
		unsettledNodes.clear();
		examinedEdges.clear();
		route.clear();
	}

	/**
	 * Search for a route from node startID and ends at targetID. <br>
	 * This will return a linkedlist of the nodes that make up the route
	 * from start to end order. <br>
	 * If either the start or target node does not exist or if a route 
	 * can't be found the returned list is empty.
	 * 
	 * @param startID id of the start node
	 * @param targetID id of the target node
	 * @return the route as a list of nodes
	 */
	public LinkedList<GraphNode> search(int startID, int targetID){
		return search(startID, targetID, false);
	}

	/**
	 * Search for a route from node startID and ends at targetID. <br>
	 * This will return a linkedlist of the nodes that make up the route
	 * from start to end order. <br>
	 * If either the start or target node does not exist or if a route 
	 * can't be found the returned list is empty.
	 * 
	 * @param startID id of the start node
	 * @param targetID id of the target node
	 * @param remember whether to remember the examined edges.
	 * @return the route as a list of nodes
	 */
	public LinkedList<GraphNode> search(int startID, int targetID, boolean remember){
		clear();
		LinkedList<GraphEdge> nextEdges;
		GraphNode next, edgeTo;;
		GraphNodeCost pqNext;
		double newCost;

		GraphNode start = graph.getNode(startID);
		GraphNode target = graph.getNode(targetID);
		if(start == null || target == null)
			return null;

		unsettledNodes.add(new GraphNodeCost(start, 0.0));
		graphCostToNode.put(start, 0.0);

		while(!unsettledNodes.isEmpty()){
			pqNext = unsettledNodes.poll();
			next = pqNext.node;
			if(next == target){
				GraphNode n = target;
				route.addFirst(n);
				while(n != start){
					n = parent.get(n);
					route.addFirst(n);
				}
				return route;
			}
			settledNodes.add(next);
			nextEdges = graph.getEdgeList(next.id());
			// Relax edges
			for(GraphEdge edge : nextEdges){
				newCost = getCost(next) + edge.getCost();
				edgeTo = edge.to();
				if(!settledNodes.contains(edgeTo) && getCost(edgeTo) > newCost){
					graphCostToNode.put(edgeTo, newCost);
					parent.put(edgeTo, next);
					unsettledNodes.add(new GraphNodeCost(edgeTo, newCost));
					if(remember)
						examinedEdges.add(edge);
				}
			}
		}
		return null;
	}

	/**
	 * Used by the search method
	 */
	protected double getCost(GraphNode node){
		Double c = graphCostToNode.get(node);
		if(c == null)
			return Float.MAX_VALUE;
		else
			return c; 
	}

	/**
	 * Get all the edges examined during the search. <br>
	 * 
	 * @return edges examined or array size 0 if none found
	 */
	public GraphEdge[] getExaminedEdges(){
		return getExaminedEdges(new GraphEdge[0]);
	}

	/**
	 * Get all the edges examined during the search. <br>
	 * The type of each element in the array will be of type Object
	 * if the parameter is null otherwise it is T (where T is GraphEdge
	 * or any class that extends GraphEdge.
	 * 
	 * @param array the array to populate
	 * @return edges examined or array size 0 if none found
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getExaminedEdges(T[] array){
		if(array == null)
			return (T[]) examinedEdges.toArray(new Object[0]);
		else
			return examinedEdges.toArray(array);
	}

	/**
	 * Get the path found as an array of GraphNode(s) in start->end
	 * order <br>
	 * @return path found or array size 0 if none found
	 */
	public GraphNode[] getRoute(){
		return route.toArray(new GraphNode[route.size()]);
	}

	/**
	 * Get the path found as an array of T(s) in start->end
	 * order. <br>
	 * The type of each element in the array will be of type Object
	 * if the parameter is null otherwise it is T (where T is GraphNode
	 * or any class that extends GraphNode.
	 * @param array the array to populate
	 * @return path found or array size 0 if none found
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getRoute(T[] array){
		if(array == null)
			return (T[]) route.toArray(new Object[0]);
		else
			return route.toArray(array);
	}



	/**
	 * Objects of this class are created as and when required by the 
	 * path search algorithm
	 * 
	 * @author Peter Lager
	 *
	 */
	private class GraphNodeCost implements Comparable<Object>{
		private GraphNode node;
		private Double cost;

		public GraphNodeCost(GraphNode node, Double cost) {
			super();
			this.node = node;
			this.cost = cost;
		}

		public int compareTo(Object o) {
			GraphNodeCost gnc = (GraphNodeCost)o;
			if(cost == gnc.cost)
				return node.compareTo(gnc.node);
			else
				return cost.compareTo(gnc.cost);
		}
	}
}

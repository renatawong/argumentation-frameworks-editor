
package computations;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;

/**
 * Implementation of the Minimum Cut algorithm by Stoer and Wagner
 * @author Renata Wong
 */
public class StoerWagnerMinimumCut {
    
    
    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();
    private ArrayList<Argument> A;
    private HashSet<Argument> B;
    private Graph<Argument,Attack> uGraph;
    private int minCutValue;
    private Argument minCut;
    private Argument st;


    //Constructor: performs the conversion from directed to undirected graph and removes self-attacks
    public StoerWagnerMinimumCut() {
        
        Attack.resetAttackPaint();
        Argument.resetArgumentDrawColor();
        
        for(Attack at : graph.getEdges()) {
            at.setDeleted(false);
            at.resetWeight();
        }
        
        uGraph = new UndirectedSparseGraph<Argument,Attack>(); 
        minCut = null;
        minCutValue = Integer.MAX_VALUE;
        
        for(Argument arg : graph.getVertices()) {
            uGraph.addVertex(arg);
        }
        
        A = new ArrayList<Argument>();
        B = new HashSet<Argument>(); //contains all arguments from uGraph that are not in A

        Iterator<Attack> it = graph.getEdges().iterator();
        Attack at, back;
        Pair<Argument> endPoints;
        while(it.hasNext()) {
            at = it.next();
            if(!at.isDeleted()) {
                at.setDeleted(true);
                endPoints = graph.getEndpoints(at);
                back = graph.findEdge(endPoints.getSecond(), endPoints.getFirst());
                if(back != null) {
                    back.setDeleted(true);
                    at.setWeight(2);
                }
                else {
                    at.setWeight(1);
                }  
                if(endPoints.getFirst() != endPoints.getSecond()) {
                    uGraph.addEdge(at, endPoints.getFirst(), endPoints.getSecond(), EdgeType.UNDIRECTED); 
                }   
            }
        }


        if(graph.getVertexCount() != 0) {
            Argument a = uGraph.getVertices().iterator().next();
            
            while(uGraph.getVertexCount() > 2) { //maybe 1?
                minimumCutPhase(a); //a can be different each time
            }
        }
        
        
    }
    
    
    /**
     * Computation of the minimum cut phase of the SW algorithm
     * @param a a starting argument
     * @param outEdges set of edges visited that is updated on the fly during the run of the algorithm
     */
    private void minimumCutPhase(Argument a) {

        HashSet<Argument> incidentArgs = new HashSet<Argument>();
           
        for(Argument arg : uGraph.getVertices()) {
            B.add(arg);
        }
        
        A.add(a);
        B.remove(a);
        
        Argument opposite1;
        for(Attack at : uGraph.getIncidentEdges(a)) {
            opposite1 = uGraph.getOpposite(a, at);
            incidentArgs.add(opposite1);
            opposite1.setWeight(at.getWeight());
            
        }      
        int w;
        Argument nextToAdd;
        while(A.size() != uGraph.getVertexCount()) {
            
            if(incidentArgs.isEmpty()) {
                Iterator<Argument> it = B.iterator();
                Argument next = it.next(); 
                incidentArgs.add(next); 
                next.setWeight(0);
            }
            
            w = 0;
            nextToAdd = null;
            
            //add to A the most tightly connected vertex, i.e. the one with largest weight going into A
            for(Argument arg : incidentArgs) {
                if(arg.getWeight() >= w) {
                    w = arg.getWeight();
                    nextToAdd = arg;
                }
            }
            
            A.add(nextToAdd);
            incidentArgs.remove(nextToAdd);
            B.remove(nextToAdd);
                   
            Argument opposite;
            for(Attack at : uGraph.getIncidentEdges(nextToAdd)) { //all edges going out of nextToAdd
                
                opposite = uGraph.getOpposite(nextToAdd, at); 
                
                if(!A.contains(opposite)) {
                    incidentArgs.add(opposite);                                         
                }
                opposite.setWeight(at.getWeight()); 
            }
            
        }
        
        //End of the Phase: Merge the two vertices added last to A
        Argument t = A.get(A.size()-1);
        
        if(t.getWeight() <= minCutValue) {
            minCutValue = t.getWeight(); 
            minCut = t; 
        }
        
        merge(A.get(A.size()-2), t);
        
        for(Argument arg : A) {
            arg.resetWeight();
        }
        
        A.clear();
        B.clear();
    }
    
    
    /**
     * Merges 2 arguments added last to A
     * @param s the argument second to last
     * @param t the last argument
     */
    private void merge(Argument s, Argument t) {
        
        st = new Argument(s.getArgumentName(s)+"+"+t.getArgumentName(t));       
        uGraph.addVertex(st);                                                   
        HashSet<Attack> toRemove = new HashSet<Attack>();
        
        HashSet<Attack> iEs = new HashSet<Attack>(uGraph.getIncidentEdges(s));
        HashSet<Attack> iEt = new HashSet<Attack>(uGraph.getIncidentEdges(t));
        
        for(Attack at : iEs) {
            Argument opposite = uGraph.getOpposite(s, at);
            if(opposite != t) {
                Attack nA = new Attack(at.getAttackName(at));
                uGraph.addEdge(nA, opposite, st);
                nA.setWeight(at.getWeight());
                toRemove.add(at);
            }
        }
        for(Attack at : iEt) {
            Argument opposite = uGraph.getOpposite(t, at);
            if(opposite != s) {
                Attack nA = uGraph.findEdge(opposite, st);
                if(nA != null) {
                    nA.setWeight(at.getWeight());
                }
                else { 
                    nA = new Attack(at.getAttackName(at));
                    uGraph.addEdge(nA, opposite, st);
                    nA.setWeight(at.getWeight());
                }
                toRemove.add(at);
            }
        }
        
        for(Attack at : toRemove) {
            uGraph.removeEdge(at);
        }
        
        toRemove.clear();
        uGraph.removeVertex(s);
        uGraph.removeVertex(t);
        
    }
    
    
    
    /**
     * Extracts the minimum cut from the remaining argument after all merge operations have taken place
     * @return a list of arguments which belong to one part of the cut
     */
    public ArrayList<Argument> getMinimumCut() {
        
        if(minCut == null) {
            return new ArrayList<Argument>();
        }
        else {
            String cut = minCut.getArgumentName(minCut);
            ArrayList<Argument> extractedMinCut = new ArrayList<Argument>();
            ArrayList<String> argNames = new ArrayList<String>();

            int j = 0;

            for(int i = 0; i < cut.length(); i++) {
                if(Character.toString(cut.charAt(i)).equals("+")) {
                    argNames.add(cut.substring(j, i));
                    j = i + 1;
                } 
                if(i == cut.length()-1) {
                    argNames.add(cut.substring(j, i+1));
                }
            }

            for(Argument a : graph.getVertices()) {
                for(String s : argNames) {
                    if(a.getArgumentName(a).equals(s)) {
                        extractedMinCut.add(a);
                    }
                }
            }

            return extractedMinCut;
        }
    }
    
}

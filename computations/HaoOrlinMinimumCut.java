
package computations;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;

/**
 *
 * @author Renata Wong
 * Implementation of the Minimum Cut Algorithm by Hao and Orlin 1994
 */
public class HaoOrlinMinimumCut {
    
    private Collection<Argument> S;
    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();
    private ArrayList<Argument> W;
    private HashSet<Argument> R;
    private ArrayList<Argument> D;
    private Argument sink;
    private int dMax;
    private int bestValue; //contains the minimum cut capacity value
    private ArrayList<HashSet<Argument>> dSet;
    private HashSet<Argument> vertices; //contains all vertices of the graph
    private ArrayList<Argument> cut;

        
    
    /**
     * Constructor
     */
    public HaoOrlinMinimumCut() {
        
        Attack.resetAttackPaint();
        Argument.resetArgumentDrawColor();
        
        S = new HashSet<Argument>();
        W = new ArrayList<Argument>(); 
        D = new ArrayList<Argument>();
        R = new HashSet<Argument>(); 
        bestValue = Integer.MAX_VALUE;
        dSet = new ArrayList<HashSet<Argument>>();
        cut = new ArrayList<Argument>();
        
        vertices = new HashSet<Argument>(graph.getVertices());
        Iterator<Argument> it = vertices.iterator();
        
        int edgeCount = graph.getEdgeCount();
        
        if(edgeCount == 0) {// no need to call minCut
            for(Argument arg : vertices) {
                W.add(arg);
            }
        }
        else { //there is at least one attack in the framework
            if(vertices.size() == 1) { //the only attack is a self-loop
                W.add(it.next());//no need to call minCut
            }
            else {//more than one argument
                initializeResidualNetwork(); 
                Argument s = it.next(); 
                Argument t = it.next();//System.out.println("s "+s+" t "+t);
                findMinCut(s, t);
                reorientFramework();
                findMinCut(t, s);
                disposeOfResidualNetwork();
            }
        }      
    }
    
    
    /**
     * Reorienting the framework
     */
    private void reorientFramework() {
        
        int capacity;
        Attack back;
        for(Attack a : graph.getEdges()) {
            if(!a.isDeleted()) {
                a.setDeleted(true);
                capacity = a.getCapacity();
                back = graph.findEdge(graph.getDest(a), graph.getSource(a));
                back.setDeleted(true);
                a.setCapacity(back.getCapacity());
                back.setCapacity(capacity);
            }
        }
        
        for(Attack at : graph.getEdges()) {
            //setFlow(at,0);
            at.resetFlow();//seems that after adding this it works
        }
        
        for(Argument arg : vertices) {
            arg.setDistance(0);
        }
        W.clear();
        D.clear();
        S.clear();
        R.clear();
        dSet.clear();
        dMax = 0;
    }
    
    
    /**
     * The main method of the Hao Orlin minimum cut algorithm
     * @param s source argument
     * @param t sink argument
     */
    private void findMinCut(Argument s, Argument t) {
                                                                                
        modifiedInitialize(s,t);                                                
        
        Collection<Attack> cutCapacity; //contains the attacks from D to W
        
        while(S.size() != vertices.size()) {                                  
            
            Argument a;
            while((a = getActiveNode()) != null) {                              
                Attack at;
                if((at = getAdmissibleArc(a)) != null) {                        
                    int cap = getResidualCapacity(at); 
                    setFlow(at, Math.min(getExcess(a),cap)); 
                }
                else {
                    modifiedRelabel(a);
                }
            }
            
            int cutValue = 0;
            for(Argument arg : W) {
                cutCapacity = graph.getInEdges(arg);
                for(Attack att : cutCapacity) {
                    if(!W.contains(graph.getSource(att))) {
                        cutValue += att.getCapacity(); 
                    }
                }
            }                                                                   
            if(bestValue > cutValue) { 
                if(!W.isEmpty() && W.size()!=graph.getVertexCount()) {
                    bestValue = cutValue;   
                    cut = (ArrayList)W.clone(); 
                } 
            } 
            
            cutValue = 0;
            for(Argument arg : W) {
                cutCapacity = graph.getOutEdges(arg);
                for(Attack att : cutCapacity) {
                    if(!W.contains(graph.getDest(att))) {
                        cutValue += att.getCapacity();
                    }
                }
            }                                                                                                                
            if(bestValue > cutValue) { 
                //bestValue = cutValue;
                for(Argument arg : graph.getVertices()) {
                    if(!W.contains(arg)) {
                        D.add(arg);
                    }
                } 
                if(!D.isEmpty()) {
                    bestValue = cutValue;
                    cut = (ArrayList)D.clone(); 
                }
                //cut = (ArrayList)D.clone();                                     
            } 
            //System.out.println("bestValue "+bestValue);
            //System.out.println("cut "+cut);
            selectNewSink();                                              
        }                                                                    
        
    }
    
    
    
    /**
     * Initialisation of the computation
     * @param s source argument
     * @param t sink argument
     */
    private void modifiedInitialize(Argument s, Argument t) {
        
        for(Attack at : graph.getOutEdges(s)) {
            setFlow(at, getResidualCapacity(at));
        }

        S.add(s); //newly added command, not in the algo 
        
        HashSet<Argument> car = new HashSet<Argument>();
        car.add(s);
        dSet.add(0, car);
        
        dMax = 0;
        
        Iterator<Argument> it = vertices.iterator();
        while(it.hasNext()) {
            Argument a = it.next();
            W.add(a);
        }
        W.remove(s);                                                            
        
        sink = t;                                                               
        
        for(Argument arg : vertices) {
            arg.setDistance(1);
        }
        sink.setDistance(0);                                                    

    }
    
    
    
    /**
     * Relabeling
     * @param arg argument to be relabeled
     */
    private void modifiedRelabel(Argument arg) {
        
        if(!existsArgWithSameDistanceAs(arg)) {
            
            ++dMax;
            
            R.clear();
            for (Argument a : W) {
                if(a.getDistance() >= arg.getDistance()) {
                    R.add(a);                                                   
                }
            }
            
            if(dSet.size() > dMax) { //clearing the contents of dSet(dMax)
                dSet.remove(dMax);                                             
            }                           
            dSet.add(dMax, (HashSet)R.clone());                                 
            
            for (Argument a : R) {
                if(W.contains(a)) {
                    W.remove(a);
                }
            }                                                                   
        }
        else {
            if(!existsResidualArc(arg)) {               
                ++dMax;
                HashSet<Argument> car = new HashSet<Argument>();//TODO: possibly move up to save number of created objects
                car.add(arg);
                if(dSet.size() > dMax) {
                    dSet.remove(dMax);
                }
                dSet.add(dMax,(HashSet)car.clone());
                if(W.contains(arg)) {
                    W.remove(arg);
                }                               
            }
            else {
                arg.setDistance(getMinDistance(arg));                          
            }
        }
    }
    
    
    
    /**
     * Selection of a new sink
     */
    private void selectNewSink() {
        
        if(W.contains(sink)) {
            W.remove(sink);
        }                                    
        
        S.add(sink);                                                            
                                                                                
        HashSet<Argument> d = (HashSet<Argument>)dSet.get(0);                                      
        d.add(sink);                                                            
        dSet.remove(0);
        dSet.add(0,d);                                                      
        
        if(S.size() == vertices.size()) {                                      
            return;
        }
        
        Argument a;

        for (Attack at : graph.getOutEdges(sink)) {
            a = graph.getDest(at);
            if(!S.contains(a)) {
                setFlow(at, getResidualCapacity(at));                                                
            }
        }
        
        if(W.isEmpty()) {             
            for (Argument arg : (HashSet<Argument>)dSet.get(dMax)) {
                W.add(arg);
            }
            --dMax;
        }
        
        int dist = Integer.MAX_VALUE;
        for (Argument ar : W) {
            if(ar.getDistance() <= dist) {
                dist = ar.getDistance();
                sink = ar;                                                                               
            }
        }                                                                            
    }
    
    
    
    
    /**
     * Checks which argument in W that is attacked by arg has the smallest distance value
     * @param arg the source of the attack
     * @return smallest distance incremented by 1
     */
    private int getMinDistance(Argument arg) {
            
        int min = Integer.MAX_VALUE;
        
        for (Attack at : graph.getOutEdges(arg)) {
            Argument a = graph.getDest(at);
            if(getResidualCapacity(at) > 0) {
                if(W.contains(a)) {
                    if(a.getDistance() < min) {
                        min = a.getDistance();
                    }                              
                }
            }
        }
        return ++min; 
    }
    
    
    
    
    /**
     * Adds an interim, originally non-existing, edge (y,x) of 0 capacity for each existing edge (x,y). 
     */
    private void initializeResidualNetwork() {
        
        Collection<Attack> attacks = new HashSet<Attack>(graph.getEdges());
        
        Attack back;
        
        for(Attack at : attacks) {
            if(graph.findEdge(graph.getDest(at), graph.getSource(at)) == null) { //edge does not exist
                back = new Attack("+");
                graph.addEdge(back, graph.getDest(at), graph.getSource(at), EdgeType.DIRECTED);
                back.setCapacity(0);
                setFlow(back,0);                                                //System.out.println("attack added ("+graph.getDest(at)+" , "+graph.getSource(at)+ ")");
            }
        }
    }
    
    
    
    /**
     * Checks the existence of residual attacks with arg as source
     * @param arg the source of the attack
     * @return true if such a residual attack exists, false otherwise
     */
    private boolean existsResidualArc(Argument arg) {
            
        Argument a;
        for (Attack at : graph.getOutEdges(arg)) {
            a = graph.getDest(at);
            if(getResidualCapacity(at) > 0) {
                if(W.contains(a)) {                              
                    return true;
                }
            }
        }
        return false;
    }
   
    
    
    /**
     * Checks whether the framework contains an argument with the same distance as the given argument
     * @param arg the argument for comparison
     * @return true if there exists a further argument with the same distance as arg, false otherwise
     */
    private boolean existsArgWithSameDistanceAs(Argument arg) {
        
        Argument a;
        for (Iterator<Argument> it = W.iterator(); it.hasNext();) {
            a = it.next();
            if(!a.equals(arg) && (a.getDistance() == arg.getDistance())) {
                return true;
            }
        }
        return false;
    }
    
    
    
    /**
     * Checks whether there is an active argument in the framework
     * @return an active argument, null otherwise
     */
    private Argument getActiveNode() {//synchronized 
        
        Argument arg = null;
        
            for(Argument a : W) { 
                if(!a.equals(sink)) {
                    if(a.getDistance() < graph.getVertexCount()) {
                        if(getExcess(a) > 0) {
                            return a;
                        }
                    }
                }
            }    
        return arg;
    }
    
    
    /**
     * Checks whether there is an admissible attack in the framework
     * @param arg the source of the attack
     * @return an admissible attack, null otherwise
     */
    private Attack getAdmissibleArc(Argument arg) {//synchronized 
        
        Attack at = null;
        
        Iterator<Attack> it = graph.getOutEdges(arg).iterator();
        while(it.hasNext()) {
            Attack att = it.next();
            Argument target = graph.getDest(att);
            if(arg.getDistance() == (target.getDistance()+1)) {
                if(getResidualCapacity(att) > 0) {
                    if(W.contains(target)) {
                        return att;
                    }
                }
            }
        }
        return at;
    }
    
    
    
   
    /**
     * Computes the excess of an argument
     * @param arg the argument
     * @return the excess
     */
    private int getExcess(Argument arg) {//synchronized 
        
        int inFlow = 0;
        for (Attack at : graph.getInEdges(arg)) {
            inFlow += at.getFlow();
        }
        
        int outFlow = 0;
        for (Attack at : graph.getOutEdges(arg)) {
            outFlow += at.getFlow();
        }
                                                                                
        return (inFlow-outFlow);
    }
    
    
    
    
    /**
     * Sets the flow through an attack
     * @param at the attack
     * @param flow the flow value
     */
    private void setFlow(Attack at, int flow) {//synchronized 
        
        int resCap = at.getCapacity() - at.getFlow();
        Attack back;
        //if(graph.getSource(at).equals(graph.getDest(at))) {at.resetFlow();}
        //else {
        if(resCap >= flow) {//i.e. 1
            at.increaseFlow(flow);  
            //back = graph.findEdge(graph.getDest(at), graph.getSource(at));System.out.println("back flow " +back.getFlow());
            //back.decreaseFlow(flow-resCap);
        }
        else if(resCap < flow) {//i.e. 0
            at.increaseFlow(resCap);
            back = graph.findEdge(graph.getDest(at), graph.getSource(at));
            back.decreaseFlow(flow-resCap);
        }
        //}
    }
    
   
    
    
    /**
     * Computes the residual capacity of an attack
     * @param at the attack
     * @return the current residual capacity of the attack
     */
    private int getResidualCapacity(Attack at) {//synchronized 
        
        Attack back = graph.findEdge(graph.getDest(at), graph.getSource(at));   
        return (at.getCapacity() - at.getFlow() + back.getFlow());      
                                                                                
    }
    
    
    
    /**
     * Deletes all the edges with 0 capacity added for the residual network
     */
    private void disposeOfResidualNetwork() {
        
        Collection<Attack> edges = new HashSet<Attack>(graph.getEdges());
        
        for(Attack at : edges) {
            if(at.getAttackName(at).equals("+")) {
                graph.removeEdge(at);
            }
            else {
                at.setCapacity(1);
                //setFlow(at,0);
                at.resetFlow();
            }
        }
        
        for(Argument arg : vertices) {
            arg.setDistance(0);
        }
        
        for(Attack a : graph.getEdges()) {
            a.setDeleted(false);
        }
        
    }
    
    

    /**
     * Public method for the transfer of a minimum cut
     * @return the minimum cut
     */
    public ArrayList<Argument> getMinimumCut() {
        return cut;
    }
            
    
}


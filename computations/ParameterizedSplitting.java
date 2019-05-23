
package computations;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;

/**
 * Computation of Parameterized Splitting
 * @author Renata Wong
 */
public class ParameterizedSplitting {

    private HashSet<Argument> initialArguments; //original args of the graph
    private HashSet<Argument> noninitialArguments; //original args of the graph
    private HashSet<Attack> splittingAttacks;   //attacks between W and D sets
    private HashSet<Argument> initialModifiedArguments; //args modified before computation of semantics
    private HashSet<Argument> noninitialModifiedArguments; //args modified before computation of semantics
    private CopyOnWriteArrayList<Attack> addedRecursiveAttacks = new CopyOnWriteArrayList<Attack>();
    private HashSet<Argument> removedInitialArguments;
    private HashSet<Argument> addedNoninitialArguments;
    private HashSet<Argument> addedInitialArguments;

    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();



    
    
    /**
     * Constructor for k-splitting
     * @param cut the set W of awake arguments, set D of dormant arguments will be computed from it
     * @param compute if compute = true we will add arguments to the framework in preparation for computation of a semantics
     * otherwise the framework will be only initialised for the purpose of visualisation of minimum cut splitting
     */
    public ParameterizedSplitting(ArrayList<Argument> cut, boolean compute) {
        
        initialArguments = new HashSet<Argument>();
        noninitialArguments = new HashSet<Argument>(cut);
        splittingAttacks = new HashSet<Attack>();
        
        noninitialModifiedArguments = new HashSet<Argument>(cut);
        initialModifiedArguments = new HashSet<Argument>();
        
        addedNoninitialArguments = new HashSet<Argument>();
        addedInitialArguments = new HashSet<Argument>();
        removedInitialArguments = new HashSet<Argument>(); //contains args removed because attacked by cond ext
        
        for(Argument arg : graph.getVertices()) {
            if(!noninitialArguments.contains(arg)) {
                initialArguments.add(arg);
                initialModifiedArguments.add(arg);
            }
        }
        
        for(Argument arg : noninitialArguments) {
            for(Attack at : graph.getOutEdges(arg)) {
                if(initialArguments.contains(graph.getDest(at))) {
                    splittingAttacks.add(at);
                }
            }
            for(Attack at : graph.getInEdges(arg)) {
                if(initialArguments.contains(graph.getSource(at))) {
                    splittingAttacks.add(at);
                }
            }
        }
        
        if(compute) {
            addAssArg();
        }
    }
    
    
    /**
     * Constructor for 2 framework splitting
     */
    public ParameterizedSplitting(ArrayList<Argument> frameworkA, HashSet<Attack> splitAttacks) {
                                                            
        Attack.resetAttackPaint();
        Argument.resetArgumentDrawColor();
        
        initialArguments = new HashSet<Argument>();
        noninitialArguments = new HashSet<Argument>(frameworkA);
        splittingAttacks = new HashSet<Attack>(splitAttacks);
        
        noninitialModifiedArguments = new HashSet<Argument>(frameworkA);
        initialModifiedArguments = new HashSet<Argument>();
        
        addedNoninitialArguments = new HashSet<Argument>();
        addedInitialArguments = new HashSet<Argument>();
        removedInitialArguments = new HashSet<Argument>(); //contains args removed because attacked by cond ext
        
        for(Argument arg : graph.getVertices()) {
            if(!noninitialArguments.contains(arg)) {
                initialArguments.add(arg);
                initialModifiedArguments.add(arg);
            }
        }
        
        addAssArg();
    }
    
    
    
    /**
     * Inserts additional arguments before the computation of conditional stable semantics
     */
    private void addAssArg() {
        
        Argument assArg;
        Attack assAt1, assAt2;
        
        /*Argument t;
        for(Attack at : splittingAttacks) {
            t = graph.getDest(at);
            if(noninitialArguments.contains(t)) {
                assArg = new Argument("." + t.getArgumentName(t));
                graph.addVertex(assArg);
                noninitialModifiedArguments.add(assArg);
                addedNoninitialArguments.add(assArg);
                assAt1 = new Attack("*");
                graph.addEdge(assAt1, t, assArg, EdgeType.DIRECTED);
                assAt2 = new Attack("*");
                graph.addEdge(assAt2, assArg, t, EdgeType.DIRECTED); 
                break;
            }
        }*/
        
        for(Argument arg : noninitialArguments) {                               
            for(Attack at : graph.getInEdges(arg)) {
                if(initialArguments.contains(graph.getSource(at))) {
                    assArg = new Argument("." + arg.getArgumentName(arg));
                    graph.addVertex(assArg);
                    noninitialModifiedArguments.add(assArg);
                    addedNoninitialArguments.add(assArg);
                    assAt1 = new Attack("*");
                    graph.addEdge(assAt1, arg, assArg, EdgeType.DIRECTED);
                    assAt2 = new Attack("*");
                    graph.addEdge(assAt2, assArg, arg, EdgeType.DIRECTED);      
                    break; //guarantees that only one additional argument is inserted for an existing argument
                }                                                                                                                           
            }
        }
        
    }
    
    
    
    /**
     * Transfers the modified noninitial arguments
     * @return modified noninitial arguments
     */
    public HashSet<Argument> getNoninitialModifiedArguments() {
        return noninitialModifiedArguments;
    }
    
    /**
     * Transfers the modified noninitial attacks
     * @return modified noninitial attacks
     */
    public HashSet<Attack> getNoninitialModifiedAttacks() {
        
        HashSet<Attack> noninitAttacks = new HashSet<Attack>();
        for(Attack at : graph.getEdges()) {
            if(noninitialModifiedArguments.contains(graph.getSource(at)) && noninitialModifiedArguments.contains(graph.getDest(at))) {
                noninitAttacks.add(at);
            }
        }        
        return noninitAttacks;
    }
    
    /**
     * Transfers additional noninitial arguments
     * @return additional noninitial arguments
     */
    public HashSet<Argument> getAddedNoninitialArguments() {
        return addedNoninitialArguments;
    }
    
    
    
    
    
    
    
    
    
    
    
    
     //below is the code for computations AFTER passing an extension

    
    
    
    /**
     * Modifies the D set with respect to the conditional extension
     * @param conditionalExtension the conditional extension 
     */
    public void modifyInitial(ArrayList<Argument> conditionalExtension) {
        
        resetFramework();
        
        //EA: arguments in noninitialArguments, not attacked by noninitial arguments in the extension
        HashSet<Argument> temp;
        Argument a1;
        for(Argument arg : noninitialArguments) {
            temp = new HashSet<Argument>();
            if(!conditionalExtension.contains(arg)) {
                for(Attack at : graph.getInEdges(arg)) {
                    a1 = graph.getSource(at);
                    if(initialModifiedArguments.contains(a1)) {
                        temp.add(a1);
                    }
                    if(conditionalExtension.contains(a1) && noninitialArguments.contains(a1)) {
                        temp.clear();
                        break;
                    }
                }
                if(!temp.isEmpty()) {
                    Argument nowy = new Argument(":" + arg.getArgumentName(arg));
                    initialModifiedArguments.add(nowy);
                    addedInitialArguments.add(nowy);
                    Attack loop = new Attack("*");
                    graph.addEdge(loop, nowy, nowy, EdgeType.DIRECTED);
                    for(Argument a : temp) {
                        Attack at = new Attack("*");
                        graph.addEdge(at, a, nowy, EdgeType.DIRECTED);
                    }
                }
            }
        }
        
        //removes arguments attacked by conditional extension
        Argument target;
        for(Argument arg : conditionalExtension) {
            for(Attack at : graph.getOutEdges(arg)) {
                if(splittingAttacks.contains(at)) {
                    target = graph.getDest(at);
                    removedInitialArguments.add(target);
                    initialModifiedArguments.remove(target);
                }
            }
            //selfloops initial (non-cut) arguments that attack the conditional extension
            Argument source;
            Attack self;
            for(Attack at : graph.getInEdges(arg)) {
                if(splittingAttacks.contains(at)) {
                    source = graph.getSource(at);
                    self = new Attack("*");
                    graph.addEdge(self, source, source, EdgeType.DIRECTED);
                    addedRecursiveAttacks.add(self);
                }
            }
        }
        
    }
    
    
    
    /**
     * Transfers modified initial arguments
     * @return modified initial arguments
     */
    public HashSet<Argument> getInitialModifiedArguments() {
        return initialModifiedArguments;
    }
    
    
    /**
     * Transfers modified initial attacks
     * @return modified initial attacks
     */
    public HashSet<Attack> getInitialModifiedAttacks() {
        
        HashSet<Attack> initAttacks = new HashSet<Attack>();
        for(Argument arg : initialModifiedArguments) {
            for(Attack at : graph.getOutEdges(arg)) {
                if(initialModifiedArguments.contains(graph.getDest(at))) {
                    initAttacks.add(at);
                }
            }
        }
          
        return initAttacks;
    }
    
    
    /**
     * Internal method for transfering removed initial arguments 
     * @return removed initial arguments
     */
    private HashSet<Argument> getRemovedInitialArguments() {
        return removedInitialArguments;
    }
    
    /**
     * Transfers additional initial arguments
     * @return additional initial arguments
     */
    public HashSet<Argument> getAddedInitialArguments() {
        return addedInitialArguments;
    }
    

    /**
     * Transfers the partition of arguments
     * @return partition of arguments in two sets
     */
    public ArrayList<ArrayList<Argument>> getPartition() {
        
        ArrayList<ArrayList<Argument>> partition = new ArrayList<ArrayList<Argument>>();
        partition.add(new ArrayList(getInitialArguments()));
        partition.add(new ArrayList(getNoninitialArguments()));
        
        return partition;
    }


    
    
    /**
     * Passes the initial arguments of a framework
     * @return initial arguments
     */
    public HashSet<Argument> getInitialArguments() {
        return initialArguments;
    }




    /**
     * Passes the non-initial arguments
     * @return set of the non-initial arguments
     */
    public HashSet<Argument> getNoninitialArguments() {
        return noninitialArguments;
    }




    /**
     * Passes the splitting attacks
     * @return set of splitting attacks
     */
    public HashSet<Attack> getSplittingAttacks() {
        return splittingAttacks;
    }


    /**
     * Returns the number of splitting attacks
     * @return the number of splitting attacks
     */
    public String getSplittingAttacksNumber() {
        return Integer.toString(getSplittingAttacks().size());
    }



    /**
     * Sets the fill colour for arguments based on the given partition
     */
    public void setArgumentFill() {
        Argument.setArgumentFill(getPartition());
    }




    /**
     * Passes the added recursive attacks of the modification
     * @return Collection of recursive attacks
     */
    private Collection<Attack> getRecursiveAttacks() {
        return addedRecursiveAttacks;
    }


    /**
     * Resets the framework after splitting, prepares the program for a next computation
     */
    public void resetFrameworkForSplitting() {

        if(getRecursiveAttacks() != null) {
            Iterator it = getRecursiveAttacks().iterator();
            while(it.hasNext()) {
                Attack at = (Attack)it.next();
                graph.removeEdge(at);
            }
        }
        addedRecursiveAttacks.clear();
        
        for(Argument arg : getAddedNoninitialArguments()) {
            graph.removeVertex(arg);
        }
        addedNoninitialArguments.clear();
        
        for(Argument arg : getAddedInitialArguments()) {
            graph.removeVertex(arg);
        }
        addedInitialArguments.clear();
    }
    
    
    /**
     * Internal reset of the framework for each extension related computation
     */
    private void resetFramework() {
        
        for(Argument arg : removedInitialArguments) {
            initialModifiedArguments.add(arg);
        }
        removedInitialArguments.clear();
        
        for(Argument arg : getAddedInitialArguments()) {
            graph.removeVertex(arg);
            initialModifiedArguments.remove(arg);
        }
        addedInitialArguments.clear();
        
        if(getRecursiveAttacks() != null) {
            Iterator it = getRecursiveAttacks().iterator();
            while(it.hasNext()) {
                Attack at = (Attack)it.next();
                graph.removeEdge(at);
            }
        }
        addedRecursiveAttacks.clear();
    }





    /**
     * Returns the number of initial arguments
     * @return number of initial arguments
     */
    public String getInitArgsNumber() {
        return Integer.toString(initialArguments.size());
    }


    /**
     * Returns the number of non-initial arguments
     * @return number of non-initial arguments
     */
    public String getNoninitArgsNumber() {
        return Integer.toString(noninitialArguments.size());
    }



    /**
     * Method responsible for colouring of the framework after computation
     */
    public void paint() {

        for(Attack at : getSplittingAttacks()) {
            Attack.setAttackPaint(at, Color.LIGHT_GRAY);
        }

        for(Attack at : getRecursiveAttacks()) {
            Attack.setAttackPaint(at, new Color(113, 198, 113));
        }

        for(Argument arg : getRemovedInitialArguments()) {
            Argument.setArgumentFill(arg, Color.WHITE);
            Argument.setArgumentDrawColor(arg, Color.RED);
            for(Attack at : graph.getIncidentEdges(arg)) {
                if(!getSplittingAttacks().contains(at)) {
                    Attack.setAttackPaint(at, Color.RED);
                }
            }
        }
    }




    /**
     * Specifies output of the splitting
     * @return output of splitting result
     */
    public String outputSplitting() {

        String s = "PARAMETERIZED SPLITTING: " + "\n";
        s += "A: ";

        int c;

        c = 0;
        s += "{";
        for(Argument a : getNoninitialArguments()) {
            if(c < getNoninitialArguments().size()-1) {
                s += a + ", ";
            }
            else {
                s += a;
            }
            c++;
        }
        s += "}" + "\n";

        s += "B: ";

        c = 0;
        s += "{";
        for(Argument a : getInitialArguments()) {
            if(c < getInitialArguments().size()-1) {
                s += a + ", ";
            }
            else {
                s += a;
            }
            c++;
        }
        s += "}" + "\n";
        return s;

    }

}

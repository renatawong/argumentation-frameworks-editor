
package computations;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
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
 * Computation of Splitting based on Baumann's "Splitting an Argumentation Framework"
 * @author Renata Wong
 */
public class Splitting {

    private HashSet<Argument> initialArguments;
    private HashSet<Argument> noninitialArguments;
    private HashSet<Attack> splittingAttacks;
    private HashSet<Attack> initialAttacks;
    private ArrayList<ArrayList<Argument>> partition;
    private HashSet<Argument> reduct;
    private HashSet<Argument> undefinedArguments;
    private HashSet<Attack> modificationAttacks;
    private CopyOnWriteArrayList<Attack> addedRecursiveAttacks = new CopyOnWriteArrayList<Attack>();

    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();



    /**
     * Constructor of a splitting
     * @param sccs set of strongly connected components
     * @param optimise true if user requested the optimised version of splitting, false otherwise
     */
    public Splitting(CopyOnWriteArrayList<ArrayList<Argument>> sccs, boolean optimise) {

        initialArguments = new HashSet<Argument>();
        noninitialArguments = new HashSet<Argument>();
        splittingAttacks = new HashSet<Attack>();

        computeInitialArguments(sccs, optimise);
        computeNoninitialArguments();
        computeSplittingAttacks();
    }
    
    


    

    /**
     * Computation of initial arguments
     * @param sccs set of strongly connected components
     * @param optimise indication whether optimisation should be performed
     */
    private void computeInitialArguments(CopyOnWriteArrayList<ArrayList<Argument>> sccs, boolean optimise) {

        if(sccs.size()==1) {
            Iterator<ArrayList<Argument>> it = sccs.iterator();
            while(it.hasNext()){
                initialArguments.addAll(it.next());
            }
        }

        else {
        HashSet<Argument> scc;
        boolean b;

        Iterator<ArrayList<Argument>> it = sccs.iterator();
        while(it.hasNext()) {
            b = false; 
            scc = new HashSet<Argument>(it.next());
            Iterator<Argument> it2 = scc.iterator();

            rout:
            while(it2.hasNext()) {
                Argument argument = it2.next();
                Collection<Attack> inEdges = graph.getInEdges(argument);
                Iterator<Attack> it3 = inEdges.iterator();
                
                while(it3.hasNext()) {
                    Attack attack = it3.next();
                    Argument source = graph.getSource(attack);
                    if(!scc.contains(source)) {
                        b = true;
                        break rout;
                    }
                }
            }
            if(!b) {
                initialArguments.addAll(scc);
            }
        }}
        if(optimise) {
            if(!optimalSize()) {
                optimiseInitialArguments(sccs);
            }
        }
    }



    /**
     * Checks the cardinality of a set
     * @return false if the cardinality is less than 45% of the framework, true otherwise
     */
    private boolean optimalSize() {

        if(initialArguments.size() <= (graph.getVertexCount() * 0.45))
            return false;
        else return true;
    }


    /**
     * Performs optimisation of a set based on the partial order of sccs
     * @param sccs the set of strongly connected components
     */
    private void optimiseInitialArguments(CopyOnWriteArrayList<ArrayList<Argument>> sccs) {

        HashSet<Argument> comparison = new HashSet<Argument>(initialArguments);

        Iterator<ArrayList<Argument>> sccsIt = sccs.iterator();
        boolean attacked = false;

        while(sccsIt.hasNext() && !optimalSize()) {
            ArrayList<Argument> scc = sccsIt.next();
            if(!initialArguments.contains(scc.get(0)) && rightSize(scc)) {
                attacked = false;
                Iterator<Argument> sccIt = scc.iterator();
                rout:
                while(sccIt.hasNext()) {
                    Argument a = sccIt.next();
                        Collection<Attack> inEdges = graph.getInEdges(a);
                        for(Attack at : inEdges) {
                            Argument source = graph.getSource(at);
                            if(!initialArguments.contains(source) && !scc.contains(source)) { //not initial and not in the current scc
                                attacked = true;
                                break rout;
                            }
                        }
                }
                if(!attacked) initialArguments.addAll(scc);
            }
        }
        if(!optimalSize() && !(comparison.size() == initialArguments.size())) optimiseInitialArguments(sccs);
    }


    /**
     * Checks the cardinality of a set of strongly connected components
     * @param scc the strongly connected component
     * @return true if the number of initial argument plus the scc is less than 60% of the framework
     */
    private boolean rightSize(ArrayList<Argument> scc) {

        if((initialArguments.size()+scc.size()) <= (graph.getVertexCount() * 0.6))
            return true;
        else 
            return false;
    }



    /**
     * Passes the initial arguments of a framework
     * @return initial arguments
     */
    public HashSet<Argument> getInitialArguments() {
        //System.out.println(initialArguments);
        return initialArguments;
    }


    /**
     * Computes and passes the attack set related to the initial arguments
     * @return set of attacks
     */
    public HashSet<Attack> getInitialAttacks() {

        initialAttacks = new HashSet<Attack>();

        Iterator<Argument> it = getInitialArguments().iterator();
        while(it.hasNext()) {
            Argument a = it.next();
            Collection outEdges = graph.getOutEdges(a);
            Iterator<Attack> it2 = outEdges.iterator();
            while(it2.hasNext()) {
                Attack at = it2.next();
                Argument destination = graph.getDest(at);
                if(getInitialArguments().contains(destination)) {
                    initialAttacks.add(at);
                }
            }
        }
        return initialAttacks;
    }


    /**
     * Computes the set of non-initial arguments based on the strongly connected components
     */
    private void computeNoninitialArguments() {

        Collection<Argument> argumentSet = graph.getVertices();
        Iterator it = argumentSet.iterator();
        while(it.hasNext()) {
            Argument argument = (Argument)it.next();
            if(!initialArguments.contains(argument)) {
                noninitialArguments.add(argument);
            }
        }
    }


    /**
     * Passes the non-initial arguments
     * @return set of the non-initial arguments
     */
    public HashSet<Argument> getNoninitialArguments() {
        return noninitialArguments;
    }


    /**
     * Computes the splitting attacks
     */
    private void computeSplittingAttacks() {

        HashSet<Argument> argumentSet = getInitialArguments();
        Iterator it = argumentSet.iterator();
        while(it.hasNext()) {
            Argument argument = (Argument)it.next();
            Collection<Attack> attackSet = graph.getOutEdges(argument);
            Iterator it2 = attackSet.iterator();

            while(it2.hasNext()) {
                Attack attack = (Attack)it2.next();
                Argument target = (Argument)graph.getDest(attack);
                if(!argumentSet.contains(target)) {
                    splittingAttacks.add(attack);
                }
            }
        }
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
     * Passes the set of splitting attacks with source in a particular argument
     * @param argument the argument as source of an attack
     * @return the set of splitting attacks for the given argument
     */
    private HashSet<Attack> getSplittingAttacks(Argument argument) {

        HashSet<Attack> sA = new HashSet<Attack>();

        Iterator<Attack> it = getSplittingAttacks().iterator();
        while(it.hasNext()) {
            Attack at = it.next();
            Pair<Argument> endpoints = graph.getEndpoints(at);
            if(endpoints.getFirst() == argument) {
                sA.add(at);
            }
        }
        return sA;
    }




    /**
     * Passes the partition of a framework after splitting
     * @return the set of initial and non-initial arguments
     */
    public ArrayList<ArrayList<Argument>> getPartition() {

        partition = new ArrayList<ArrayList<Argument>>();
        partition.add(new ArrayList(getInitialArguments()));
        partition.add(new ArrayList(getNoninitialArguments()));

        return partition;
    }


    /**
     * Sets the fill colour for arguments based on the given partition
     */
    public void setArgumentFill() {
        
        Argument.setArgumentFill(getPartition());
    }





    ///below is the code for computations AFTER passing an extension


    /**
     * Computes the reduct on the base of given extension
     * @param extension the extension
     */
    private void computeReduct(ArrayList<Argument> extension) {

        reduct = new HashSet<Argument>(getNoninitialArguments());

        Iterator<Argument> it = extension.iterator();
        while(it.hasNext()) {
            Collection outEdges = getSplittingAttacks(it.next());

            Iterator<Attack> it2 = outEdges.iterator();
            while(it2.hasNext()) {
                Argument destination = graph.getDest(it2.next());
                
                if(reduct.contains(destination)) {
                    reduct.remove(destination);
                }
            }
        }
    }


    /**
     * Passes the reduct set
     * @return the reduct set
     */
    public HashSet<Argument> getReduct() {
        return reduct;
    }




    /**
     * Computes the undefined arguments on the basis a the given extension
     * @param extension the extension
     */
    private void computeUndefinedArguments(ArrayList<Argument> extension) {

        undefinedArguments = new HashSet<Argument>();

        Collection sA = getSplittingAttacks();
        Iterator it = sA.iterator();
        while(it.hasNext()) {
            Attack at = (Attack)it.next();
            Argument source = graph.getSource(at);
            if(!extension.contains(source) && !attacked(source, extension)) {
                    undefinedArguments.add(source);
            }
        }
    }


    /**
     * Checks whether a particular argument is attacked by an extension
     * @param argument the argument
     * @param extension the extension
     * @return true if attacked, otherwise false
     */
    private boolean attacked(Argument argument, ArrayList<Argument> extension) {

        Collection inEdges = graph.getInEdges(argument);
        boolean attacked = false;

        if(inEdges.isEmpty())
            return attacked == false;
        else {
            Iterator<Attack> it = inEdges.iterator();
            while(it.hasNext()) {
                Attack at = it.next();
                Argument source = graph.getSource(at);
                boolean u = false;
                if(extension.contains(source)) {
                    u = true;
                }
                else u = false;
                attacked |= u;
            }
        }
        return attacked;
    }


    /**
     * Passes the undefined arguments
     * @return set of undefined arguments
     */
    public HashSet<Argument> getUndefinedArguments() {
        return undefinedArguments;
    }



    /**
     * Computes the modification
     */
    private void computeModification() {

        HashSet<Argument> undefArg = getUndefinedArguments();

        Iterator it = undefArg.iterator();
        while(it.hasNext()) {
            Argument a = (Argument)it.next();
            Collection sA = getSplittingAttacks(a);
            
            Iterator it2 = sA.iterator();
            while(it2.hasNext()) {
                Attack at = (Attack)it2.next();
                Argument b = graph.getDest(at);

                if(getReduct().contains(b)) {
                    Attack newAttack = new Attack("*");
                    graph.addEdge(newAttack, b, b, EdgeType.DIRECTED); 
                    addedRecursiveAttacks.add(newAttack);
                }
            }
        }
    }


    /**
     * Controls the computation of modification
     * @param extension the extension
     */
    public void computeModificationOfReduct(ArrayList<Argument> extension) {

        resetFrameworkForSplitting();
        
        computeReduct(extension);
        computeUndefinedArguments(extension);
        computeModification();
    }


    /**
     * Passes the added recursive attacks of the modification
     * @return Collection of recursive attacks
     */
    private Collection getRecursiveAttacks() {
        return addedRecursiveAttacks;
    }


    /**
     * Resets the framework after splitting
     */
    public void resetFrameworkForSplitting() {

        if(getRecursiveAttacks() != null) {
            Iterator it = getRecursiveAttacks().iterator();
            while(it.hasNext()) {
                Attack at = (Attack)it.next();
                graph.removeEdge(at);
            }
        }
        resetRecursiveAttacks();
    }


    /**
     * Resets the set of recursive attacks
     */
    private void resetRecursiveAttacks() {
        if(getRecursiveAttacks() != null) {
            Iterator it = getRecursiveAttacks().iterator();
            while(it.hasNext()) {
                addedRecursiveAttacks.remove((Attack)it.next());
            }
        }
    }
    

    /**
     * Passes the modification arguments
     * @return set of modification arguments
     */
    public HashSet<Argument> getModificationArguments() {

        //modificationArguments = getReduct();
        return reduct;
    }


    /**
     * Passes the modification attacks
     * @return set of modification attacks
     */
    public HashSet<Attack> getModificationAttacks() {

        modificationAttacks = new HashSet<Attack>();

        Iterator it = getReduct().iterator();
        while(it.hasNext()) {
            Argument a = (Argument)it.next();

            Collection outEdges = graph.getOutEdges(a);
            Iterator it2 = outEdges.iterator();
            while(it2.hasNext()) {
                Attack at = (Attack)it2.next();
                if(getReduct().contains(graph.getDest(at))) {
                    modificationAttacks.add(at);
                }
            }
        }
        return modificationAttacks;
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

        Iterator<Attack> sA = getSplittingAttacks().iterator();
        while(sA.hasNext()) {
            Attack.setAttackPaint(sA.next(), Color.LIGHT_GRAY);
        }

        Iterator<Attack> rA = getRecursiveAttacks().iterator();
        while(rA.hasNext()) {
            Attack.setAttackPaint(rA.next(), new Color(113, 198, 113));
        }

        Iterator<Argument> out = getNoninitialArguments().iterator();
        while(out.hasNext()) {
            Argument a = out.next();
            if(getModificationArguments() != null) {
                if(!getModificationArguments().contains(a)) {
                    Argument.setArgumentFill(a, Color.WHITE);
                    Argument.setArgumentDrawColor(a, Color.RED);
                    Iterator<Attack> outEdges = graph.getOutEdges(a).iterator();
                    while(outEdges.hasNext()) {
                        Attack.setAttackPaint(outEdges.next(), Color.RED);
                    }
                    Iterator<Attack> inEdges = graph.getInEdges(a).iterator();
                    while(inEdges.hasNext()) {
                        Attack att = inEdges.next();
                        if(!getSplittingAttacks().contains(att)) {
                            Attack.setAttackPaint(att, Color.RED);
                        }   
                    }
                }
            }
            else { //in case of non-existing stable labelling
                Argument.setArgumentFill(Color.RED);
                Argument.resetArgumentDrawColor();
            }
        }
    }




    /**
     * Specifies output of the splitting
     * @return output of splitting result
     */
    public String outputSplitting() {

        String s = "SPLITTING: " + "\n";
        s += "A: ";

        int c;

        c = 0;
        s += "{";
        for(Argument a : getInitialArguments()) {
            if(c < getInitialArguments().size()-1) s += a + ", ";
            else s += a;
            c++;
        }
        s += "}" + "\n";

        s += "B: ";

        c = 0;
        s += "{";
        for(Argument a : getNoninitialArguments()) {
            if(c < getNoninitialArguments().size()-1) s += a + ", ";
            else s += a;
            c++;
        }
        s += "}" + "\n";
        return s;

    }

}

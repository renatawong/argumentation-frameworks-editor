
package computations;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;
import visualization.Labelling;



/**
 * An implementation of an algorithm by Modgil, Caminada
 * @author Renata Wong
 */
public class GroundedSemantics {

    private HashSet<Argument> in;
    private HashSet<Argument> out;
    private HashSet<Argument> undec;

    Labelling groundedLabelling;
    Labelling groundedLabellingViaSplitting;
    Labelling groundedLabelling_A;
    Labelling groundedLabelling_B;
    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();
    private int extensionNumber;
    private int counter = 0;

    private HashSet<Argument> argumentSet;
    private HashSet<Attack> attackSet;



    /**
     * Grounded semantics constructor for computations with splitting
     * @param splitting the splitting
     */
    public GroundedSemantics(Splitting splitting) {

        initializeSets();
        argumentSet = new HashSet(splitting.getInitialArguments());
        attackSet = new HashSet(splitting.getInitialAttacks());
        extensionNumber = 1;

        findGroundedLabelling(argumentSet, attackSet);
        
        splitting.computeModificationOfReduct(new ArrayList(in));

        initializeSets();
        argumentSet = new HashSet(splitting.getModificationArguments());
        attackSet = new HashSet(splitting.getModificationAttacks());
        extensionNumber = 2;
        findGroundedLabelling(argumentSet, attackSet);

    }



    /**
     * Initialises the argument sets for computation
     */
    private void initializeSets() {

        in = new HashSet<Argument>();
        out = new HashSet<Argument>();
        undec = new HashSet<Argument>();
        
    }




    /**
     * Grounded semantics constructor for the entire framework (without splitting)
     */
    public GroundedSemantics() {

        argumentSet = new HashSet<Argument>(graph.getVertices());
        attackSet = new HashSet<Attack>(graph.getEdges());
        initializeSets();

        findGroundedLabelling(argumentSet, attackSet);

    }




    /**
     * Computes the grounded extension for a set of arguments
     * @param argumentSet set of arguments
     * @param attackSet set of attacks
     */
    private void findGroundedLabelling(HashSet<Argument> argumentSet, HashSet<Attack> attackSet) {

        Iterator<Argument> itArgSet;
        Labelling labelling_prev = new Labelling(in, out, undec);
        Labelling labelling_next = new Labelling(in, out, undec);
        boolean equalLabellings;

        do {
            ++counter;
            labelling_prev = labelling_next;

            itArgSet = argumentSet.iterator();
            while(itArgSet.hasNext()) {
                Argument a = itArgSet.next();
                if(!labelling_prev.hasLabelling(a, labelling_prev) && allAttackersOut(a, attackSet, labelling_prev)) {
                    labelling_prev.getInSet().add(a);
                    in.add(a);
                }
            }
            itArgSet = argumentSet.iterator();
            while(itArgSet.hasNext()) {
                Argument a = itArgSet.next();
                if(!labelling_prev.hasLabelling(a, labelling_prev) && anAttackerIn(a, attackSet, labelling_prev)) {
                    out.add(a);
                }
            }
            labelling_next = new Labelling(in, out, undec);
            equalLabellings = Labelling.compareLabellings(labelling_next, labelling_prev);

        } while(!equalLabellings);

        if(equalLabellings) {

            Iterator it = argumentSet.iterator();
            while(it.hasNext()) {
                Argument a = (Argument)it.next();
                if(!labelling_next.isIn(a, labelling_next) && !labelling_next.isOut(a, labelling_next)) {
                    undec.add(a);
                }
            }
            groundedLabelling = new Labelling(in, out, undec);
            if(extensionNumber == 1) {
                groundedLabelling_A = groundedLabelling;
            }
            else {
                groundedLabelling_B = groundedLabelling;
            }

        }

    }


    


    /**
     * Checks the labelling of attacking arguments of a given argument
     * @param argument the argument
     * @return true if all attackers OUT, false otherwise
     */
    private boolean allAttackersOut(Argument argument, HashSet<Attack> attackSet, Labelling labelling) {

        boolean sourcesOut = true;
        Collection<Attack> edgesIn = graph.getInEdges(argument);
        if(edgesIn.isEmpty())
            sourcesOut = true;
        else {
            Iterator it = edgesIn.iterator();
            while(it.hasNext()) {
                Attack at = (Attack)it.next();
                boolean sourceOut;
                if(attackSet.contains(at)) {
                    Argument a = (Argument)graph.getSource(at);
                    if(labelling.isOut(a, labelling)) {
                        sourceOut = true;
                    }
                    else sourceOut = false;
                 }
                else sourceOut = true;
                sourcesOut &= sourceOut;
            }
        }
        return sourcesOut;
    }


    /**
     * Checks the labelling of attacking arguments of a given argument
     * @param argument the argument
     * @return true if there is at least one attacker labelled IN, false otherwise
     */
    private boolean anAttackerIn(Argument argument, HashSet<Attack> attackSet, Labelling labelling) {

        Collection<Attack> edgesIn = graph.getInEdges(argument);
        Iterator it = edgesIn.iterator();
        while(it.hasNext()) {
            Attack at = (Attack)it.next();
            if(attackSet.contains(at)) {
                Argument a = (Argument)graph.getSource(at);
                if(labelling.isIn(a, labelling))
                    return true;
            }
        }
        return false;
    }





    /**
     * Returns the grounded labelling without splitting
     * @return the grounded labelling for the entire framework
     */
    public Labelling getGroundedLabelling() {
        return groundedLabelling;
    }



    /**
     * Returns the grounded labelling with splitting
     * @return the grounded labelling with splitting
     */
    private Labelling getGroundedLabellingViaSplitting() {

        groundedLabellingViaSplitting = Labelling.union(getGroundedLabelling_A(), getGroundedLabelling_B());
        return groundedLabellingViaSplitting;
    }


    /**
     * Returns a partial grounded labelling
     * @return the grounded labelling of the first framework
     */
    public Labelling getGroundedLabelling_A() {
        return groundedLabelling_A;
    }


    /**
     * Returns a partial grounded labelling
     * @return the grounded labelling of the second framework
     */
    public Labelling getGroundedLabelling_B() {
        return groundedLabelling_B;
    }


    /**
     * Number of steps
     * @return the number of steps
     */
    public int getNumberOfSteps() {
        return counter;
    }


    /**
     * Specifies the output for splitting
     * @return the grounded labelling without splitting
     */
    public String outputGroundedLabelling() {

        return "GROUNDED LABELLING (IN, OUT, UNDEC): \n" + getGroundedLabelling();

    }


    /**
     * Specifies the output for splitting
     * @return the grounded labelling with splitting
     */
    public String outputGroundedLabellingThroughSplitting() {

        return "GROUNDED LABELLING WITH SPLITTING (IN, OUT, UNDEC): \n" + getGroundedLabellingViaSplitting();
    }
    
}

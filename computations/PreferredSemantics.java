
package computations;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;
import visualization.Labelling;

/**
 * An implementation of an algorithm by Modgil, Caminada
 * @author Renata Wong
 */
public class PreferredSemantics {

    private CopyOnWriteArrayList<Labelling> candidateLabellings;
    private ArrayList<Labelling> preferredLabellings; //originally ArrayList
    private Labelling initialLabelling;

    private HashSet<Argument> argumentSet;
    private HashSet<Attack> attackSet;
    private int counter = 0;

    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();





    /**
     * Constructor for preferred semantics with splitting
     * @param splitting a splitting
     */
     public PreferredSemantics(Splitting splitting) {

        preferredLabellings = new ArrayList<Labelling>();

        argumentSet = new HashSet(splitting.getInitialArguments());
        attackSet = new HashSet(splitting.getInitialAttacks());

        initializeSets();

        findPreferredLabellings(argumentSet, attackSet, initialLabelling);

        for(Labelling labelling : getCandidateLabellings()) {
            splitting.computeModificationOfReduct(new ArrayList(labelling.getInSet()));
            argumentSet = new HashSet(splitting.getModificationArguments());
            attackSet = new HashSet(splitting.getModificationAttacks());
            initializeSets();

            findPreferredLabellings(argumentSet, attackSet, initialLabelling);
            for(Labelling l : getCandidateLabellings()) {
                preferredLabellings.add(Labelling.union(labelling, l));
            }

        }
        candidateLabellings.clear();
        candidateLabellings.addAll(preferredLabellings);

    }


     /**
      * Constructor for preferred semantics
      */
    public PreferredSemantics() {

        argumentSet = new HashSet<Argument>(graph.getVertices());
        attackSet = new HashSet<Attack>(graph.getEdges());

        initializeSets();

        findPreferredLabellings(argumentSet, attackSet, initialLabelling);

    }



    /**
     * Initialises the argument sets for computation
     */
    private void initializeSets() {

        candidateLabellings = new CopyOnWriteArrayList<Labelling>();

        initialLabelling = new Labelling(new HashSet<Argument>(argumentSet), new HashSet<Argument>(), new HashSet<Argument>());

    }



    /**
     * Method for computation of preferred labellings
     * @param argumentSet set of arguments
     * @param attackSet set of attacks
     * @param labelling a labelling
     */
    private void findPreferredLabellings(HashSet<Argument> argumentSet, HashSet<Attack> attackSet, Labelling labelling) {

        Argument argument;
        ++counter;

        if(hasStrictSuperset(labelling)) {
            return; //has to be there, otherwise some strict subsets of labelling can be added to candidateLabellings
        }

        if(!hasArgumentsIllegallyIn(attackSet, labelling)) { 
            for(Labelling lab : candidateLabellings) {
                if(isStrictSubset(lab, labelling)) {
                    candidateLabellings.remove(lab);
                }
            }

            candidateLabellings.add(labelling);
            return;

        }

        else {
            if((argument = superIllegallyIn(labelling)) != null) {
                findPreferredLabellings(argumentSet, attackSet, transitionStep(new Labelling(labelling), argument));
            }
            else {
                Iterator<Argument> it = getArgumentsIllegallyIn(attackSet, labelling).iterator();
                while(it.hasNext()) {
                    argument = it.next();
                    findPreferredLabellings(argumentSet, attackSet, transitionStep(new Labelling(labelling), argument));
                }
            }
        }

    }



    /**
     * Returns an argument that is super-illegally IN in a labelling
     * @param labelling the labelling
     * @return the argument if exists, null otherwise
     */
    private Argument superIllegallyIn(Labelling labelling) {

        Argument argument;

        Iterator<Argument> it = labelling.getInSet().iterator();
        while(it.hasNext()) {
            argument = it.next();
            if(!labelling.legallyIn(argument, attackSet)) {
                Collection inEdges = graph.getInEdges(argument);
                Iterator<Attack> it2 = inEdges.iterator();
                while(it2.hasNext()) {
                    Attack at = it2.next();
                    if(attackSet.contains(at)) {
                        Argument attacker = graph.getSource(at);
                        if(labelling.legallyIn(attacker, attackSet) | labelling.getUndecSet().contains(attacker)) {
                            return argument;
                        }
                    }
                }
            }
        }
        return null;
    }




    /**
     * Performs a check whether a labelling is a strict subset of another labelling
     * @param lab_A first labelling
     * @param lab_B second labelling
     * @return true if lab_A is strict subset of lab_B, false otherwise
     */
     public boolean isStrictSubset(Labelling lab_A, Labelling lab_B) {

        if(lab_B.getInSet().containsAll(lab_A.getInSet())) {
            if(lab_B.getInSet().size() > lab_A.getInSet().size())
                return true;
        }
        return false;

     }



     /**
      * Performs a check whether candidateLabellings contains a labelling which is a superset of the given labelling
      * @param labelling the given labelling
      * @return true if candidateLabellings contains such a labelling, false otherwise
      */
    private boolean hasStrictSuperset(Labelling labelling) {

        for(Labelling lab : candidateLabellings) {
            if(isSuperset(lab, labelling)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks whether a labelling is a superset of another labelling
     * @param lab_A
     * @param lab_B
     * @return true if ti is the case, false otherwise
     */
    private boolean isSuperset(Labelling lab_A, Labelling lab_B) {

        if(lab_A.getInSet().containsAll(lab_B.getInSet()))
                return true;
        else return false;
    }




    /**
     * Transition step
     * @param labelling the labelling
     * @param a the argument
     * @return the new labelling
     */
    private Labelling transitionStep(Labelling labelling, Argument a) {

        labelling.getInSet().remove(a);
        labelling.getOutSet().add(a);

        Iterator<Attack> it = graph.getOutEdges(a).iterator();
        while(it.hasNext()) {
            Attack at = it.next();
            if(attackSet.contains(at)) {

                Argument target = graph.getDest(at);
                if(illegallyOut(labelling, target)) {
                    labelling.getUndecSet().add(target);
                    labelling.getOutSet().remove(target);
                }
            }
        }

        if(illegallyOut(labelling, a)) {
            labelling.getUndecSet().add(a);
            labelling.getOutSet().remove(a);
        }
        return labelling;
    }



    /**
     * Checks whether an argument is illegally OUT in a given labelling
     * @param labelling the labelling to check against
     * @param argument the argument
     * @return true if the argument is illegally OUT, false otherwise
     */
    private boolean illegallyOut(Labelling labelling, Argument argument) {

        boolean isIllOut = true;

        if(labelling.getOutSet().contains(argument)) {
            Collection inEdges = graph.getInEdges(argument);
            if(inEdges.isEmpty())
                isIllOut = false;
            else {
            Iterator it = inEdges.iterator();
            while(it.hasNext()) {
                Attack at = (Attack)it.next();
                if(attackSet.contains(at)) {
                    Argument source = graph.getSource(at);
                    if(labelling.getInSet().contains(source)) {
                        isIllOut &= false;
                    }
                    else isIllOut &= true;
                }
            }
        }
        }

        else isIllOut = false;
        return isIllOut;
    }




    /**
     * Checks whether a labelling has arguments that are illegally IN
     * @param attackSet set of attacks
     * @param labelling the labelling
     * @return true if the labelling has arguments that are illegally IN, false otherwise
     */
    private boolean hasArgumentsIllegallyIn(HashSet<Attack> attackSet, Labelling labelling) {

        Iterator<Argument> it = labelling.getInSet().iterator();
        while(it.hasNext()) {
            Collection inEdges = graph.getInEdges(it.next());
            Iterator<Attack> it2 = inEdges.iterator();
            while(it2.hasNext()) {
                Attack at = it2.next();

                if(attackSet.contains(at)) {
                    Argument source = graph.getSource(at);
                    if(!labelling.getOutSet().contains(source)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Returns set of arguments that are illegally IN in a given labelling
     * @param attackSet set of attacks
     * @param labelling the labelling
     * @return CopyOnWriteArrayList of arguments
     */
    private ArrayList<Argument> getArgumentsIllegallyIn(HashSet<Attack> attackSet, Labelling labelling) {

        ArrayList<Argument> argumentsIllegallyIn = new ArrayList<Argument>();

        Iterator<Argument> it = labelling.getInSet().iterator();
        while(it.hasNext()) {
            Argument argument = it.next();
            if(!labelling.legallyIn(argument, attackSet)) {
                argumentsIllegallyIn.add(argument);
            }
        }
        return argumentsIllegallyIn;
    }




    /**
     * Returns the candidateLabellings
     * @return candidate labellings
     */
    private CopyOnWriteArrayList<Labelling> getCandidateLabellings() {
        return candidateLabellings;
    }


    /**
     * Returns the number of steps
     * @return the number of steps
     */
    public int getNumberOfSteps() {
        return counter;
    }


    /**
     * Output of preferred labellings
     * @return preferred labellings
     */
    public String outputPreferredExtensions() {

        int labNr = 1;
        String result = "PREFERRED LABELLINGS (IN, OUT, UNDEC): \n";

        Iterator it = getCandidateLabellings().iterator();
        while(it.hasNext()) {
            result += " " + labNr++ + " " + it.next().toString() + "\n";
        }
        return result;
    }


    /**
     * Output of preferred labellings with splitting
     * @return preferred labellings with splitting
     */
    public String outputPreferredExtensionsThroughSplitting() {

        int labNr = 1;
        String result = "PREFERRED LABELLINGS WITH SPLITTING (IN, OUT, UNDEC): \n";

        Iterator it = getCandidateLabellings().iterator();
        while(it.hasNext()) {
            result += " " + labNr++ + " " + it.next().toString() + "\n";
        }
        return result;
    }


}

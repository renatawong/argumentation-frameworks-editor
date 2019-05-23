

package computations;

import edu.uci.ics.jung.graph.Graph;
import java.awt.Color;
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
public class StableSemantics {

    private CopyOnWriteArrayList<Labelling> candidateLabellings;
    private ArrayList<Labelling> stableLabellings;
    private Labelling initialLabelling;

    private HashSet<Argument> argumentSet;
    private HashSet<Attack> attackSet;
    private int counter = 0;

    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();


    

    /**
     * Constructor for stable semantics with splitting
     * @param splitting a splitting
     */
    public StableSemantics(Splitting splitting) {

        stableLabellings = new ArrayList<Labelling>();
        
        argumentSet = new HashSet(splitting.getInitialArguments());
        attackSet = new HashSet(splitting.getInitialAttacks());

        initializeSets();

        findStableLabellings(argumentSet, attackSet, initialLabelling);

        for(Labelling labelling : getCandidateLabellings()) {
            splitting.computeModificationOfReduct(new ArrayList(labelling.getInSet()));
            argumentSet = new HashSet(splitting.getModificationArguments());
            attackSet = new HashSet(splitting.getModificationAttacks());
            
            initializeSets();

            findStableLabellings(argumentSet, attackSet, initialLabelling);
            for(Labelling l : getCandidateLabellings()) {
                stableLabellings.add(Labelling.union(labelling, l));
            }

        }
        candidateLabellings.clear();
        candidateLabellings.addAll(stableLabellings);

    }
    
    /**
     * Constructor for stable semantics with parametrised splitting
     * @param parameterizedSplitting a splitting
     */
    public StableSemantics(ParameterizedSplitting parameterizedSplitting) {

        stableLabellings = new ArrayList<Labelling>();
        
        argumentSet = new HashSet(parameterizedSplitting.getNoninitialModifiedArguments());
        attackSet = new HashSet(parameterizedSplitting.getNoninitialModifiedAttacks());

        initializeSets();

        findStableLabellings(argumentSet, attackSet, initialLabelling);

        for(Labelling labelling : getCandidateLabellings()) {
            parameterizedSplitting.modifyInitial(new ArrayList(labelling.getInSet()));
            argumentSet = new HashSet<Argument>(parameterizedSplitting.getInitialModifiedArguments());
            attackSet = new HashSet<Attack>(parameterizedSplitting.getInitialModifiedAttacks());
            
            initializeSets();

            findStableLabellings(argumentSet, attackSet, initialLabelling);
            
            for(Labelling l : getCandidateLabellings()) {
                stableLabellings.add(Labelling.union(labelling, l));
            }

        }
        candidateLabellings.clear();
        candidateLabellings.addAll(stableLabellings);

    }
    


    /**
     * Constructor for stable semantics without splitting
     */
    public StableSemantics() {

        argumentSet = new HashSet<Argument>(graph.getVertices());
        attackSet = new HashSet<Attack>(graph.getEdges());

        initializeSets();

        findStableLabellings(argumentSet, attackSet, initialLabelling);

    }



    /**
     * Initialises the argument sets for computation
     */
    private void initializeSets() {
        
        candidateLabellings = new CopyOnWriteArrayList<Labelling>();

        initialLabelling = new Labelling(new HashSet<Argument>(argumentSet), new HashSet<Argument>(), new HashSet<Argument>());

    }



    /**
     * Computation of stable labellings
     * @param argumentSet set of arguments
     * @param attackSet set of attacks
     * @param labelling a labelling
     */
    private void findStableLabellings(HashSet<Argument> argumentSet, HashSet<Attack> attackSet, Labelling labelling) {

        ++counter;
        Argument argument;

        if(!undecEmpty(labelling)) {
            return;
        }

        if(!hasArgumentsIllegallyIn(attackSet, labelling)) {

            if(!exists(labelling)) {
                candidateLabellings.add(labelling);
            }
            return;
        }

        else {
            if((argument = superIllegallyIn(labelling)) != null) {
                findStableLabellings(argumentSet, attackSet, transitionStep(new Labelling(labelling), argument));
            }
            else {
                ArrayList<Argument> args = getArgumentsIllegallyIn(attackSet, labelling);
                Iterator<Argument> it = args.iterator();
                while(it.hasNext()) {
                    argument = it.next();
                    findStableLabellings(argumentSet, attackSet, transitionStep(new Labelling(labelling), argument));
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
     * Checks whether a particular labelling already exists in candidateLabellings
     * @param labelling the labelling
     * @return true if the labelling is in candidateLabellings, false otherwise
     */
     private boolean exists(Labelling labelling) {

         for(Labelling lab : candidateLabellings) {
             if(labelling.getInSetSize() == lab.getInSetSize()) {         //if(labelling.getInSet().size() == lab.getInSet().size()) {
                 if(labelling.getInSet().containsAll(lab.getInSet())) {
                     return true;
                 }
             }
         }
         return false;
     }



     /**
     * Performs a check whether a labelling is a strict subset of another labelling
     * @param lab_A first labelling
     * @param lab_B sacond labelling
     * @return true if lab_A is strict subset of lab_B, false otherwise
     */
     public boolean isStrictSubset(Labelling lab_A, Labelling lab_B) {

        if(lab_B.getInSet().containsAll(lab_A.getInSet())) {
            if(lab_B.getInSetSize() > lab_A.getInSetSize())
                return true;
        }
        return false;

     }



     /**
      * Checks whether the UNDEC set of a labelling is empty
      * @param labelling the labelling
      * @return true if empty, false if not empty
      */
    private boolean undecEmpty(Labelling labelling) {

        if(labelling.getUndecSet().isEmpty())
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
     * Output of stable labellings
     * @return stable labellings
     */
    public String outputStableLabellings() {

        int labNr = 1;
        String result = "STABLE LABELLINGS (IN, OUT, UNDEC): \n";

        if(getCandidateLabellings().isEmpty()) {
            result += "NO LABELLINGS FOUND";
            Argument.setArgumentFill(Color.RED);
        }
        else {
            Iterator it = getCandidateLabellings().iterator();
            while(it.hasNext()) {
                result += " " + labNr++ + " " + it.next().toString() + "\n";
            }
        }
        return result;
    }


    /**
     * Output of stable labellings with splitting
     * @return stable labellings with splitting
     */
    public String outputStableLabellingsThroughSplitting() {

        int labNr = 1;
        String result = "STABLE LABELLINGS WITH SPLITTING (IN, OUT, UNDEC): \n";

        if(getCandidateLabellings().isEmpty()) {
            result += "NO LABELLINGS FOUND";
            Argument.setArgumentFill(Color.RED);
        }
        else {
            Iterator it = getCandidateLabellings().iterator();
            while(it.hasNext()) {
                result += " " + labNr++ + " " + it.next().toString() + "\n";
            }
        }
        return result;
    }
    
    
    /**
     * Output of stable labellings with parametrised splitting
     * @return stable labellings with parametrised splitting
     */
    public String outputStableLabellingsThroughParSplitting() {

        int labNr = 1;
        String result = "STABLE LABELLINGS WITH PARAMETERIZED SPLITTING (IN, OUT, UNDEC): \n";

        if(getCandidateLabellings().isEmpty()) {
            result += "NO LABELLINGS FOUND";
            Argument.setArgumentFill(Color.RED);
        }
        else {
            Iterator it = getCandidateLabellings().iterator();
            while(it.hasNext()) {
                result += " " + labNr++ + " " + it.next().toString() + "\n";
            }
        }
        return result;
    }
}

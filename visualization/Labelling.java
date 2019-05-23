
package visualization;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Contains the Labelling data for frameworks
 * @author Renata Wong
 */
public class Labelling {

    /**
     * The in set 
     */
    private HashSet<Argument> in;
    
    /**
     * The out set
     */
    private HashSet<Argument> out;
    
    /**
     * The undec set
     */
    private HashSet<Argument> undec;

    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();



    /**
     * Creates a labelling on the basis of the provided sets
     * @param inLabel the in set
     * @param outLabel the out set
     * @param undecLabel the undec set
     */
    public Labelling(HashSet<Argument> inLabel, HashSet<Argument> outLabel, HashSet<Argument> undecLabel) {

        in = new HashSet<Argument>(inLabel);
        out = new HashSet<Argument>(outLabel);
        undec = new HashSet<Argument>(undecLabel);
        
    }


    
    /**
     * Constructor for copying a labelling
     * @param labelling Labelling to be copied
     */
    public Labelling(Labelling labelling) {
        this(labelling.getInSet(), labelling.getOutSet(), labelling.getUndecSet());
    }


    /**
     * Creates an empty labelling
     */
    public Labelling() {

        in = new HashSet<Argument>();
        out = new HashSet<Argument>();
        undec = new HashSet<Argument>();
    }




    //@Override
    /*public Labelling clone() {

        Labelling lab = new Labelling();
        for(Argument a : this.in) {
            lab.getInSet().add(a);
        }
        for(Argument a : this.out) {
            lab.getOutSet().add(a);
        }
        for(Argument a : this.undec) {
            lab.getUndecSet().add(a);
        }
        return lab;
    }*/


    /**
     * Returns the in set
     * @return the in set
     */
    public HashSet<Argument> getInSet() {
        return this.in;
    }
    
    public int getInSetSize() {
        return this.in.size();
    }



    /**
     * Returns the out set
     * @return the out set
     */
    public HashSet<Argument> getOutSet() {
        return this.out;
    }


    /**
     * Returns the undec set
     * @return the undec set
     */
    public HashSet<Argument> getUndecSet() {
        return this.undec;
    }


    /**
     * Checks whether an argument is labeled in a labelling
     * @param argument argument name
     * @param labelling the labelling
     * @return true if labeled, false otherwise
     */
    public boolean hasLabelling(Argument argument, Labelling labelling) {
        if(isIn(argument, labelling) | isOut(argument, labelling) | isUndec(argument, labelling)) {
            return true;
        }
        else return false;
    }


    /**
     * Checks whether an argument is in the in set of a labelling
     * @param argument the argument
     * @param labelling the labelling
     * @return true if the argument is IN, false otherwise
     */
    public boolean isIn(Argument argument, Labelling labelling) {
        if(labelling.getInSet().contains(argument)) {
            return true;
        }
        else return false;
    }


    /**
     * Checks whether an argument is in the out set of a labelling
     * @param argument the argument
     * @param labelling the labelling
     * @return true if the argument is OUT, false otherwise
     */
    public boolean isOut(Argument argument, Labelling labelling) {
        if(labelling.getOutSet().contains(argument)) {
            return true;
        }
        else return false;
    }



    /**
     * Checks whether an argument is in the undec set of a labelling
     * @param argument the argument
     * @param labelling the labelling
     * @return true if the argument is UNDEC, false otherwise
     */
    public boolean isUndec(Argument argument, Labelling labelling) {
        if(labelling.getUndecSet().contains(argument)) {
            return true;
        }
        else return false;
    }


    /**
     * Clears all arguments from a labelling
     * @param labelling the labelling
     */
    public void clear(Labelling labelling) {

        for(Argument a : labelling.getInSet()) {
            labelling.getInSet().remove(a);
        }
        for(Argument a : labelling.getOutSet()) {
            labelling.getOutSet().remove(a);
        }
        for(Argument a : labelling.getUndecSet()) {
            labelling.getUndecSet().remove(a);
        }
    }


    

    /**
     * Performs a check whether an argument is legally IN
     * @param argument the argument
     * @param attackSet the attack set
     * @return true if the argument is legally IN, false otherwise
     */
    public boolean legallyIn(Argument argument, HashSet<Attack> attackSet) {

        if(getInSet().contains(argument)) {

            Collection inEdges = graph.getInEdges(argument);
            Iterator<Attack> it = inEdges.iterator();
            while(it.hasNext()) {
                Attack at = it.next();
                if(attackSet.contains(at)) {
                    Argument attacker = graph.getSource(at);
                    if(!getOutSet().contains(attacker)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }


    /**
     * Checks whether a labelling is a strict subset of another labelling
     * @param lab_A first labelling
     * @param lab_B second labelling
     * @return true for strict subset, false otherwise
     */
    public boolean isStrictSubset(Labelling lab_A, Labelling lab_B) {

        boolean strictSubset = true;

        if(lab_A.getInSet().isEmpty()) {
            strictSubset = true;
        }
        else {
        Iterator it = lab_A.getInSet().iterator();
        while(it.hasNext()) {
            Argument a = (Argument)it.next();
            if(!lab_B.getInSet().contains(a)) {
                strictSubset &= false;
            }
            else strictSubset &= true;
        }}

        return strictSubset;
    }


    /**
     * Checks whether a labelling is a subset of another labelling
     * @param lab_A 
     * @param lab_B
     * @return true if lab_A is a subset of lab_B, false otherwise
     */
    public boolean isSubset(Labelling lab_A, Labelling lab_B) {

        Iterator it = lab_B.getInSet().iterator();
        while(it.hasNext()) {
            if(!lab_A.getInSet().contains((Argument)it.next())) {
                return false;
            }
        }
        return true;
    }

   

    /**
     * Performs the union operation on partial labellings
     * @param labelling_A
     * @param labelling_B
     * @return the complete labelling
     */
    public static Labelling union(Labelling labelling_A, Labelling labelling_B) {

        HashSet<Argument> inSet = new HashSet<Argument>();
        inSet.addAll(labelling_A.getInSet());
        inSet.addAll(labelling_B.getInSet());

        HashSet<Argument> outSet = new HashSet<Argument>();
        outSet.addAll(labelling_A.getOutSet());
        outSet.addAll(labelling_B.getOutSet());

        HashSet<Argument> undecSet = new HashSet<Argument>();
        undecSet.addAll(labelling_A.getUndecSet());
        undecSet.addAll(labelling_B.getUndecSet());

        Labelling labelling = new Labelling(inSet, outSet, undecSet);
        return labelling;
    }



    /**
     * Compares two sets of arguments as to their sizes
     * @param setA the first set
     * @param setB the second set
     * @return true if the sets are of equal size, false otherwise
     */
    private static boolean compareSets(HashSet<Argument> setA, HashSet<Argument> setB) {
        
        int list1size = setA.size();
        int list2size = setB.size();
        boolean equal;
        if(list1size != list2size) {
            equal = false;
        }
        else {
            equal = true;
        }
        return equal;
    }



    /**
     * Compares two labellings
     * @param labelling1
     * @param labelling2
     * @return true if both contains the same elements, false otherwise
     */
    public static boolean compareLabellings(Labelling labelling1, Labelling labelling2) {

        boolean equal_in = compareSets(labelling1.getInSet(), labelling2.getInSet());
        boolean equal_out = compareSets(labelling1.getOutSet(), labelling2.getOutSet());
        boolean equal_undec = compareSets(labelling1.getUndecSet(), labelling2.getUndecSet());

        return equal_in && equal_out && equal_undec;
    }



    /**
     * Specifies the labelling fill colors
     */
    public void setLabellingFill() {

        ArrayList<ArrayList<Argument>> lF = new ArrayList<ArrayList<Argument>>();
        lF.add(new ArrayList(getInSet()));
        lF.add(new ArrayList(getOutSet()));
        lF.add(new ArrayList(getUndecSet()));
        Argument.setLabellingFill(lF);
    }

 

    @Override
    public String toString() {

        setLabellingFill();

        String s = "(" + "{";

        int c = 0;
        for(Argument a : getInSet()) {
            if(c < getInSet().size()-1) s += a + ", ";
            else s += a;
            c++;
        }

        s += "}" + "{";

        c = 0;
        for(Argument a : getOutSet()) {
            if(c < getOutSet().size()-1) s += a + ", ";
            else s += a;
            c++;
        }

        s += "}" + "{";
        
        c = 0;
        for(Argument a : getUndecSet()) {
            if(c < getUndecSet().size()-1) s += a + ", ";
            else s += a;
            c++;
        }

        s += "}" + ")";
        return s;
    }

}

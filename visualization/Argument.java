
package visualization;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 * Specifies the structure of an argument
 * @author Renata Wong
 */
public class Argument implements Serializable {

    /**
     * The name of an argument
     */
    private String argumentName;

    /**
     * Lowlink used for Tarjan's algorithm
     */
    public int lowlink = -1;            // used for Tarjan's algorithm
    
    /**
     * Index used for Tarjan's algorithm
     */
    public int index = -1;              // used for Tarjan's algorithm

    /**
     * The fill colour of an argument
     */
    private Color fillColor = Color.RED;
    
    /**
     * The draw colour of an argument
     */
    private Color drawColor = Color.BLACK;

    /**
     * The x coordinate of an argument
     */
    private Double x = 0.0;
    
    /**
     * The y coordinate of an argument
     */
    private Double y = 0.0;
    
    /**
     * Value required for testing whether an argument was already visited in MidCut
     */
    private boolean wasVisited = false;
  
    /**
     * Contains for a given vertex the sum of weights of edges that go into A, required for Stoer-Wagner
     */
    private int weight = 0;
    
    /**
     * Variable used for directed version of Stoer-Wagner Cut (possibly to be deleted)
     */
    private boolean safe = false;
    
    /**
     * Variable used for Hao-Orlin Mincut
     */
    private int distance = 0;
    
    
    

    
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return this.distance;
    }
    
    public void setSafe(boolean safe) {
        this.safe = safe;
    }
    
    public boolean isSafe() {
        return this.safe;
    }
    
    public void setWeight(int weight) {
        this.weight = this.weight + weight;
    }
    
    public void resetWeight() {
        this.weight = 0;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public void setVisited(boolean visited) {
        this.wasVisited = visited;
    }
    
    public boolean wasVisited() {
        return this.wasVisited;
    }
    
            
    /**
     * Creates a new argument
     * @param argumentName argument name
     */
    public Argument(String argumentName) {
        this.argumentName = argumentName;
    }


    /**
     * Returns argument name
     * @param argument an argument
     * @return the name of the argument
     */
    public String getArgumentName(Argument argument) {
        return argument.argumentName;
    }
    /*
    public Argument getArgument(String argumentName) {
        if(this.argumentName.equals(argumentName))
            return this;
        else return null;
    }*/


    /**
     * Resets the low-link and index values of an argument
     * @param number reset value
     */
    public void resetArgumentIndex(int number) {
        lowlink = number;
        index = number;
    }

    /**
     * Returns the index of an argument
     * @param argumentName argument name
     * @return the index of the argument
     */
    public int getArgumentIndex(int argumentName) {
        return index;
    }

    /**
     * Sets the x coordinate for an argument
     * @param argumentName argument name
     * @param x the x coordinate
     */
    public void setX(Argument argumentName, Double x) {
        argumentName.x = x;
    }

    /**
     * Sets the y coordinate for an argument
     * @param argumentName argument name
     * @param y the y coordinate
     */
    public void setY(Argument argumentName, Double y) {
        argumentName.y = y;
    }

    /**
     * Returns the x coordinate of an argument
     * @param argumentName argument name
     * @return the x coordinate
     */
    public Double getX(Argument argumentName) {
        return argumentName.x;
    }

    /**
     * Returns the y coordinate of an argument
     * @param argumentName argument name
     * @return the y coordinate
     */
    public Double getY(Argument argumentName) {
        return argumentName.y;
    }

    /**
     * Sets the fill colour of an argument
     * @param argumentName argument name
     * @param color the fill colour
     */
    public static void setArgumentFill(Argument argumentName, Color color) {
        argumentName.fillColor = color;
    }

    /**
     * Returns the fill colour for an argument
     * @param argumentName argument name
     * @return the fill colour
     */
    public Color getArgumentFill(Argument argumentName) {
        return argumentName.fillColor;
    }

     /**
     * Specifies colours for arguments
     * @param argumentSets set of sets of arguments
     */
    public static void setArgumentFill(ArrayList<ArrayList<Argument>> argumentSets) {

        for(ArrayList<Argument> argumentSet : argumentSets) {
            Iterator it = argumentSet.iterator();
            Color rgb = getRandomColor();
            while(it.hasNext()) {
                setArgumentFill((Argument)it.next(), rgb);
            }
        }
    }

    /**
     * Specifies colours for arguments
     * @param argumentSets set of sets of arguments
     */
    public static void setArgumentFill(HashSet<HashSet<Argument>> argumentSets) {

        for(HashSet<Argument> argumentSet : argumentSets) {
            Iterator it = argumentSet.iterator();
            Color rgb = getRandomColor();
            while(it.hasNext()) {
                setArgumentFill((Argument)it.next(), rgb);
            }
        }
    }



    /**
     * Specifies the fill color for all arguments
     * @param paint the color
     */
    public static void setArgumentFill(Color paint) {

        Iterator it = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph().getVertices().iterator();
        while(it.hasNext()) {
            setArgumentFill((Argument)it.next(), paint);
        }
    }


    /**
     * Sets the fill colours for a labelling
     * @param labelling the labelling
     */
    public static void setLabellingFill(ArrayList<ArrayList<Argument>> labelling) {

        ArrayList<Argument> inSet = labelling.get(0);
        for(Argument a : inSet) {
            setArgumentFill(a, Color.ORANGE);
        }
        ArrayList<Argument> outSet = labelling.get(1);
        for(Argument a : outSet) {
            setArgumentFill(a, new Color(113, 198, 113));
        }
        ArrayList<Argument> undecSet = labelling.get(2);
        for(Argument a : undecSet) {
            setArgumentFill(a, new Color(125, 158, 192));
        }
    }



     /**
     * Computes a random colour on rgb basis
     * @return random colour
     */
    private static Color getRandomColor() {
        return new Color((int)(Math.random()*256), (int)(Math.random()*256), (int)(Math.random()*256));
    }


    /**
     * Sets the draw color for an argument
     * @param argument argument name
     * @param color the color
     */
    public static void setArgumentDrawColor(Argument argument, Color color) {
        argument.drawColor = color;
    }

    /**
     * Return the draw color of an argument
     * @param argument argument name
     * @return the draw color
     */
    public Color getArgumentDrawColor(Argument argument) {
        return argument.drawColor;
    }

    
    /**
     * Resets the draw color of all arguments
     */
    public static void resetArgumentDrawColor() {

        Iterator it = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph().getVertices().iterator();
        while(it.hasNext()) {
            Argument a = (Argument)it.next();
            setArgumentDrawColor(a, Color.BLACK);
        }
    }


  

    /**
     * Gives back the argument name
     * @return argument name
     */
    @Override
    public String toString() {
        return argumentName;
    }

}

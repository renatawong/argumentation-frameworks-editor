
package visualization;

import java.awt.Color;
import java.util.Iterator;

/**
 * Class for creating attack relations
 * @author Renata Wong
 */
public class Attack {

    /**
     * The name of the attack
     */
    private String attackName;
    
    /**
     * Edge draw colour
     */
    private Color edgeDrawPaint = Color.BLACK;
    
    /**
     * Arrow draw colour
     */
    private Color arrowDrawPaint = Color.BLACK;
    
    /**
     * Arrow fill colour
     */
    private Color arrowFillPaint = Color.BLACK;

    /**
     * Capacity of an attack required for the Hao-Orlin Minimum Cut algorithm 
     */
    private int capacity = 1;
    
    /**
     * The flow through an attack in the Hao Orlin minimum cut algorithm
     */
    private int flow = 0;
    
    private int weight = 0;
    
    private boolean deleted = false;

    
    
    
    
    
    
    
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public boolean isDeleted() {
        return this.deleted;
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
    
    
    public void increaseFlow(int flow) {
        this.flow = this.flow + flow;
    }
    
    public void decreaseFlow(int flow) {
        this.flow = this.flow - flow;
    }
    
    public synchronized int getFlow() {
        return this.flow;
    }
    
    public void resetFlow() {
        this.flow = 0;
    }
    
    
    /**
     * Creates a new attack relation
     * @param attackName name of the attack relation
     */
    public Attack(String attackName) {
        this.attackName = attackName;
    }


    /**
     * Return the name of an attack
     * @param attack the attack
     * @return attack name
     */
    public String getAttackName(Attack attack) {
        return attack.attackName;
    }

    /**
     * Sets the fill colour of an argument
     * @param attackName attack name
     * @param color edge draw colour
     */
    public static void setEdgeDrawPaint(Attack attackName, Color color) {
        attackName.edgeDrawPaint = color;
    }

    /**
     * Returns the fill colour for an attack
     * @param attackName attack name
     * @return edge draw colour
     */
    public Color getEdgeDrawPaint(Attack attackName) {
        return attackName.edgeDrawPaint;
    }

    /**
     * Sets the arrow draw colour of an attack
     * @param attackName attack name
     * @param color arrow draw colour
     */
    public static void setArrowDrawPaint(Attack attackName, Color color) {
        attackName.arrowDrawPaint = color;
    }

    /**
     * Return the arrow draw colour of an attack
     * @param attackName attack name
     * @return arrow draw colour
     */
    public Color getArrowDrawPaint(Attack attackName) {
        return attackName.arrowDrawPaint;
    }

    /**
     * Sets the arrow fill colour of an attack
     * @param attackName attack name
     * @param color arrow fill colour
     */
    public static void setArrowFillPaint(Attack attackName, Color color) {
        attackName.arrowFillPaint = color;
    }

    /**
     * Return the arrow fill colour of an attack
     * @param attackName attack name
     * @return arrow fill colour
     */
    public Color getArrowFillPaint(Attack attackName) {
        return attackName.arrowFillPaint;
    }


    /**
     * Sets edge draw color, arrow draw color and arrow fill color for an attack
     * @param attack attack name
     * @param paint the color
     */
    public static void setAttackPaint(Attack attack, Color paint) {

        Attack.setEdgeDrawPaint(attack, paint);
        Attack.setArrowDrawPaint(attack, paint);
        Attack.setArrowFillPaint(attack, paint);
    }


    /**
     * Resets the default black colour for all attacks
     */
    public static void resetAttackPaint() {

        Iterator it = FrameworkViewer.getGraph().getEdges().iterator();
        while(it.hasNext()) {
            Attack attack = (Attack)it.next();
            setEdgeDrawPaint(attack, Color.BLACK);
            setArrowDrawPaint(attack, Color.BLACK);
            setArrowFillPaint(attack, Color.BLACK);
        }
    }
    
    
    public void setCapacity(int capacity) {       
        this.capacity = capacity;
    }
    
    public int getCapacity() {
        return this.capacity;
    }


    /**
     * Gives back the name of the attack relation
     * @return attack relation name
     */
    @Override
    public String toString() {
        return attackName;
    }

}


package computations;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;
/**
 *
 * @author Renata Wong
 * Computation of a cut in the approximate middle of an argumentation framework
 */
public class MidCut {
    
    /**
     * The present argumentation framework
     */
    private Graph<Argument, Attack> graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();
    private ArrayList<Argument> vertices; 
    /**
     * Set of arguments after cut that will be passed over for parametrized splitting
     */
    private HashSet<Argument> cut;              
    private Collection<Attack> edges;
    private Collection<Argument> nextRound;
    private int half;
    
    /**
     * Computation of a middle cut of an argumentation framework
     */
    public MidCut() {
        
        Attack.resetAttackPaint();
        Argument.resetArgumentDrawColor();
        
        half = graph.getVertexCount()/2;
        
        cut = new HashSet<Argument>();
        
        if(half != 0) { //if half = 0 then the framework is empty
            nextRound = new HashSet<Argument>();

            vertices = new ArrayList<Argument>(graph.getVertices());

            Argument start = vertices.get(0);

            cut.add(start);
            nextRound.add(start); 
            vertices.remove(0);
            start.setVisited(true);

            while(cut.size() < half) {
                runNextRound(nextRound);
            }

            for(Argument a : cut) {
                a.setVisited(false);
            }
        }

    }
    
    /**
     * Computes step after step the middle cut
     * @param thisRound current working set of arguments
     */
    private void runNextRound(Collection<Argument> thisRound) {
        
        nextRound = new HashSet<Argument>(); 
        Argument target;
        
        for(Argument a : thisRound) {
            nextRound.remove(a);//newly added
            edges = graph.getInEdges(a);
            for(Attack at : edges) {
                target = graph.getSource(at);
                if(cut.size() < half && !target.wasVisited()) {
                    cut.add(target);
                    nextRound.add(target);
                    vertices.remove(target);
                    target.setVisited(true);
                }
                else {
                    break;
                }
            }
            /*edges = graph.getOutEdges(a);
            for(Attack at : edges) {
                target = graph.getDest(at);
                if(cut.size() < half && !target.wasVisited()) {
                    cut.add(target);
                    nextRound.add(target);
                    vertices.remove(target);
                    target.setVisited(true);
                }
                else break;
            }*/
            if(cut.size() >= half) {
                break;
            }
        }
        if(nextRound.isEmpty()) {
            target = vertices.get(0);
            cut.add(target);
            nextRound.add(target);
            vertices.remove(0);
            target.setVisited(true);
        }

    }
    
    /**
     * Returns the set A of the cut
     * @return set A of the cut
     */
    public ArrayList<Argument> getFrameworkA() {
        return new ArrayList<Argument>(cut);
    }
    
    public ArrayList<Argument> getMidCut() {
        return new ArrayList<Argument>(cut);
    }
}

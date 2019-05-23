
package computations;

import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;


/**
 * Computation of SCCs using Tarjan's algorithm
 * with an initial vertex added with an attack relation to
 * every other vertex
 *
 * @author Renata Wong
 */
public class StronglyConnectedComponent {

    Graph graph = FrameworkViewer.getVisualizationViewer().getGraphLayout().getGraph();
    private Argument tarjanArgument;
    private int index;
    private ArrayList<Argument> stack;
    private CopyOnWriteArrayList<ArrayList<Argument>> scc;



    /**
     * Constructor
     */
    public StronglyConnectedComponent() {

        Attack.resetAttackPaint();
        Argument.resetArgumentDrawColor();
        runTarjan();

    }



    /**
     * Computes the strongly connected components of a given argumentation framework
     * @param tarjanArgument framework argument on which the algorithm runs
     */
    private void tarjan(Argument tarjanArgument) {

        tarjanArgument.index = index;
        tarjanArgument.lowlink = index;
        index++;

        stack.add(0, tarjanArgument);

        Collection<Attack> edgesOut = graph.getOutEdges(tarjanArgument);

        for(Attack attack : edgesOut) {
            Argument argument = (Argument)graph.getDest(attack);
            if(argument.index == -1) {
                tarjan(argument);
                tarjanArgument.lowlink = Math.min(tarjanArgument.lowlink, argument.lowlink);
            }
            else if(stack.contains(argument)) {
                tarjanArgument.lowlink = Math.min(tarjanArgument.lowlink, argument.index);
            }
        }

        if(tarjanArgument.lowlink == tarjanArgument.index) {
            Argument node;
            ArrayList<Argument> component = new ArrayList<Argument>();

            do {
                node = stack.remove(0);
                component.add(node);
            } while(node != tarjanArgument);

            scc.add(component);
        }
   }



    /**
     * Returns the strongly connected components for the current framework
     * @return list of strongly connected components
     */
    public CopyOnWriteArrayList<ArrayList<Argument>> getStronglyConnectedComponents() {
        return scc;
    }



    /**
     * Method calling the Tarjan algorithm. A new argument is added as source
     * argument to every other existing argument to compute all possible
     * strongly connected components of the given framework.
     */
    private void runTarjan() {

        index = 0;
        stack = new ArrayList<Argument>();
        scc = new CopyOnWriteArrayList<ArrayList<Argument>>();

        Collection<Argument> graphVertices = graph.getVertices();
        Iterator<Argument> it = graphVertices.iterator();
        while(it.hasNext()) {
            Argument a = it.next();
            if(a.index == -1) {
                tarjan(a);
            }
        }
        resetArgumentIndex();
    }



    /**
     * Sends the list of strongly connected components to be displayed
     * @return output for sccs
     */
    public String setOutput() {

        String s = "STRONGLY CONNECTED COMPONENTS: " + "\n";

        s += "{";

        int c;

        Iterator<ArrayList<Argument>> it = getStronglyConnectedComponents().iterator();
        int e = 0;
        while(it.hasNext()) {

            c = 0;
            ArrayList<Argument> al = it.next();
            s += "{";
            for(Argument a : al) {
                if(c < al.size()-1) s += a + ", ";
                else s += a;
                c++;
            }
            if(e < getStronglyConnectedComponents().size()-1) s += "}, ";
            else s += "}";
            e++;

        }
        s += "}";
        return s;
    }



    /**
     * Resets the index and the low-link values of arguments thereby
     * making further computations possible after the framework has been modified
     */
    private void resetArgumentIndex() {
        Collection<Argument> vertices = graph.getVertices();
        Iterator it = vertices.iterator();
        while(it.hasNext()) {
            Argument argument = (Argument)it.next();
            argument.resetArgumentIndex(-1);
        }
    }
}

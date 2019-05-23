
package visualization;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.BasicRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;


/**
 * Creates a framework viewer
 *
 * @author Renata Wong
 */

public class FrameworkViewer {

    private static DirectedGraph<Argument, Attack> graph = null;
    private ArrayList<Argument> argumentSet = new ArrayList<Argument>();
    private Factory<Argument> argumentFactory;
    private Factory<Attack> attackFactory;
    private VertexShapeFactory<Argument> vertexShapeFactory;
    private Transformer<Argument, Integer> vertexSizeTransformer;
    private Transformer<Argument, Shape> vertexShapeTransformer;
    private Transformer<Argument, Float> vertexAspectRatioTransformer;
    private Transformer<Attack, Paint> edgeDrawPaintTransformer;
    private Transformer<Attack, Paint> arrowDrawPaintTransformer;
    private Transformer<Attack, Paint> arrowFillPaintTransformer;
    private Transformer<Argument, Point2D> argumentLocation;
    private static VisualizationViewer visualizationViewer;
    private static Layout<Argument, Attack> layout;
    private static EditingModalGraphMouse graphMouse;
    private Mode mode;
    private BasicRenderer frameworkRenderer;
    private CopyOnWriteArrayList<Argument> shiftedInitials = new CopyOnWriteArrayList<Argument>();
    private CopyOnWriteArrayList<Argument> shiftedNoninitials = new CopyOnWriteArrayList<Argument>();
    private static ArrayList<Argument> indexedArgument;
    PickedState<String> ps;
    private static ArrayList<Argument> frameworkA = new ArrayList<Argument>();
    private ArrayList<Argument> frameworkB = new ArrayList<Argument>();
    private HashSet<Attack> splitAttacks = new HashSet<Attack>();;
    private boolean doubleFramework = false;



    /**
     * Constructor for frameworks loaded from a file
     * @param fileName file name
     */
    public FrameworkViewer(File fileName) {

        graph = getGraphFromFile(fileName);
        String extension = fileName.toString();
        extension = extension.substring(extension.lastIndexOf("."), extension.length());
        
        if(extension.equals(".aaf")) {
            argumentLocation = new Transformer<Argument, Point2D>() {
            @Override
            public Point2D transform(Argument i) {
                Double x = i.getX(i);
                Double y = i.getY(i);
                Point2D p = new Point2D.Double(x, y);
                return p;
            }
        };
        layout = new StaticLayout<Argument, Attack>(graph, argumentLocation);//new AggregateLayout<Argument, Attack>(new StaticLayout<Argument, Attack>(graph, argumentLocation));
        }
        else layout = new FRLayout<Argument, Attack>(graph); //new AggregateLayout<Argument, Attack>(new FRLayout<Argument, Attack>(graph));
        mode = Mode.TRANSFORMING;
        
        //checkForDoubleFramework();
        
        createVisualizationViewer();
    }


    /**
     * Constructor for frameworks with specified argument and attack count
     * @param argumentCount number of arguments
     * @param attackCount number of attacks
     */
    public FrameworkViewer(int argumentCount, int attackCount) {

        if(argumentCount == 0 && attackCount == 0) {
            graph = new DirectedSparseGraph<Argument, Attack>();
            layout = new FRLayout<Argument, Attack>(graph); //new AggregateLayout<Argument, Attack>(new FRLayout<Argument, Attack>(graph));
            mode = Mode.EDITING;
        }
        else {
            graph = getRandomGraph(argumentCount, attackCount);
            layout = new FRLayout<Argument, Attack>(graph); //new AggregateLayout<Argument, Attack>(new FRLayout<Argument, Attack>(graph));
            mode = Mode.TRANSFORMING;
        }
        createVisualizationViewer();
    }
    
    public FrameworkViewer(int argumentCount, int attackCount, boolean shift) {
        
        graph = getRandomGraph(argumentCount, attackCount);
        layout = new FRLayout<Argument, Attack>(graph); //new AggregateLayout<Argument, Attack>(new FRLayout<Argument, Attack>(graph));
        mode = Mode.TRANSFORMING;
        createVisualizationViewer();
        shiftArguments(graph.getVertices(), true);
    }


    private void checkForDoubleFramework() {
        
        HashSet<Argument> dottedArgs = new HashSet<Argument>();
        
        for(Argument arg : graph.getVertices()) {
            if(arg.getArgumentName(arg).startsWith(".")) {
                Collection<Attack> attacking = graph.getOutEdges(arg);
                for(Attack at : attacking) {
                    Argument a = graph.getDest(at);
                    if(a.getArgumentName(a).startsWith(".")) {
                        splitAttacks.add(at);
                        doubleFramework = true;
                    } 
                }
                //dottedArgs.add(arg);
            }
        }
        for(Argument arg : dottedArgs) {
            
        }
    }

    /**
     * Sets up the visualisation viewer for the current framework
     */
    private void createVisualizationViewer() {

        layout.setSize(new Dimension(767,376));

        argumentFactory = new Factory<Argument>() {
            @Override
            public Argument create() {
                String argument = Integer.toString(getGraph().getVertexCount());
                return new Argument(argument);
            }
        };

        attackFactory = new Factory<Attack>() {
            @Override
            public Attack create() {
                String argument = Integer.toString(getGraph().getEdgeCount());
                return new Attack(argument);
            }
        };

        //final VisualizationModel<Argument, Attack> visualizationModel = new DefaultVisualizationModel<Argument, Attack>(
        //layout, new Dimension(800,450));
        //visualizationViewer = new VisualizationViewer<Argument,Attack>(visualizationModel, new Dimension(800,450));
        visualizationViewer = new VisualizationViewer<Argument, Attack>(layout);

        visualizationViewer.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                visualizationViewer.getGraphLayout().setSize(visualizationViewer.getSize());
            }
        });
        
        frameworkRenderer = new BasicRenderer();
        visualizationViewer.setRenderer(frameworkRenderer);
        visualizationViewer.setPreferredSize(new Dimension(767,376));
        visualizationViewer.setBackground(Color.WHITE);

        ps = visualizationViewer.getPickedVertexState();
        visualizationViewer.getRenderContext().setVertexDrawPaintTransformer(
                new PickableVertexPaintTransformer<Argument>(visualizationViewer.getPickedVertexState(),
                Color.BLACK, new Color(255, 255, 0)));

        visualizationViewer.getRenderContext().setVertexDrawPaintTransformer(new Transformer<Argument, Paint>() {
            @Override
            public Paint transform(Argument i) {
                return i.getArgumentDrawColor(i);
            }
        });

        edgeDrawPaintTransformer = new Transformer<Attack, Paint>() {
            @Override
            public Paint transform(Attack i) {
                return i.getEdgeDrawPaint(i);
            }
        };
        visualizationViewer.getRenderContext().setEdgeDrawPaintTransformer(edgeDrawPaintTransformer);

        arrowDrawPaintTransformer = new Transformer<Attack, Paint>() {
            @Override
            public Paint transform(Attack i) {
                return i.getArrowDrawPaint(i);
            }
        };
        visualizationViewer.getRenderContext().setArrowDrawPaintTransformer(arrowDrawPaintTransformer);

        arrowFillPaintTransformer = new Transformer<Attack, Paint>() {
            @Override
            public Paint transform(Attack i) {
                return i.getArrowFillPaint(i);
            }
        };
        visualizationViewer.getRenderContext().setArrowFillPaintTransformer(arrowFillPaintTransformer);
        
        Transformer<Argument, Paint> argumentPaint = new Transformer<Argument, Paint>() {
            @Override
            public Paint transform(Argument i) {
                return i.getArgumentFill(i);
            }
        };
        visualizationViewer.getRenderContext().setVertexFillPaintTransformer(argumentPaint);

        graphMouse = new EditingModalGraphMouse(visualizationViewer.getRenderContext(), argumentFactory, attackFactory);
        visualizationViewer.setGraphMouse(graphMouse);
        visualizationViewer.addKeyListener(graphMouse.getModeKeyListener());
        graphMouse.setMode(mode);

        visualizationViewer.revalidate();
        visualizationViewer.repaint();
    }

    /**
     * Passes over the graph mouse
     * @return graph mouse
     */
    public static EditingModalGraphMouse getGraphMouse() {
        return graphMouse;
    }

    /**
     * Returns the current graph
     * @return current graph
     */
    public static DirectedGraph getGraph() {
        return graph;
    }



    //non-javadoc , yet to be worked on
    public void resetVertices() {

        if(!graph.getVertices().isEmpty()) {
            CopyOnWriteArrayList<Argument> args = new CopyOnWriteArrayList<Argument>(graph.getVertices());
            Iterator it = args.iterator();
            while(it.hasNext()) {
                Argument a = (Argument)it.next();
                    graph.removeVertex(a);
            }
        }
    }

    

    /**
     * Creates a graph from a file
     * @param filename file name
     * @return the graph
     */
    private DirectedGraph getGraphFromFile(File filename) {

        DirectedGraph<Argument, Attack> g = new DirectedSparseGraph<Argument, Attack>();
        int attackCounter = 0;
        
        frameworkA = new ArrayList<Argument>();//added april 6
        frameworkB = new ArrayList<Argument>();//added april 6
        splitAttacks = new HashSet<Attack>();//added newly in april 5.

        String extension = filename.toString();
        extension = extension.substring(extension.lastIndexOf("."), extension.length());
        if(extension.equals(".aaf")) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = null;
            int starNumber = 0;
            int lineNumber = 0;
            while((line = reader.readLine()).trim() != null) {
                
                if(line.equals("*")) {
                    if(lineNumber == 0) {
                        JDialog.setDefaultLookAndFeelDecorated(true);
                        JOptionPane.showMessageDialog(null, "File does not contain any arguments. Cannot proceed");
                        break;
                    }
                    starNumber++;
                    line = reader.readLine().trim();
                }
                if(line.equals("")) {
                    line = reader.readLine().trim(); //solves the problem of an empty row in the file
                }
                lineNumber++;
                
                switch(starNumber) {
                    case 0: g.addVertex(createArgument(line)); break;
                    case 1: ArrayList<String> attackSet = createAttacks(line);
                            if(attackSet.size() >= 2) {
                                Integer sourceArgumentId = Integer.parseInt(attackSet.get(0));
                                for(int i = 1; i < attackSet.size(); i++) {
                                    String targetName = attackSet.get(i).toString();
                                    Integer targetArgumentId;
                                    String attackName;
                                    Attack attack;
                                    
                                    if(targetName.startsWith(".")) {
                                        targetArgumentId = Integer.parseInt(attackSet.get(i).substring(1));
                                        attackName = ".";
                                        attack = new Attack(attackName);
                                        splitAttacks.add(attack);
                                        //doubleFramework = true;
                                    }
                                    else {
                                        targetArgumentId = Integer.parseInt(attackSet.get(i));
                                        attackName = String.valueOf(attackCounter++);
                                        attack = new Attack(attackName);
                                    }

                                    g.addEdge(attack, argumentSet.get(sourceArgumentId),
                                            argumentSet.get(targetArgumentId), EdgeType.DIRECTED);
                                }
                            }
                            break;
                    case 2: specifyLocation(line);
                            break;
                    //case 3: break;
                }
            }
            reader.close();

        } catch (FileNotFoundException ex) {}
        catch (NullPointerException npe) {}
        catch (IOException e) {
            JDialog.setDefaultLookAndFeelDecorated(true);
            JOptionPane.showMessageDialog(null, "Data transfer error. Cannot proceed");
        }
        } //end of .aaf

        else if(extension.equals(".net")) {try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = null;
            int starNumber = 0;
            int lineNumber = 0;
            while((line = reader.readLine()) != null) {
                if(line.trim().split(" ")[0].equals("*Vertices")) {
                    line = reader.readLine().trim();
                }
                if(line.trim().split(" ")[0].equals("*Arcslist")) {
                    starNumber++;
                    line = reader.readLine().trim();
                }
                if(line.trim().equals("")) {
                    line = reader.readLine().trim(); //solves the problem of an empty row in the file
                }
                lineNumber++;
                switch(starNumber) {
                    case 0: g.addVertex(createNetArgument(line)); break;
                    case 1: ArrayList<String> attackSet = createNetAttacks(line);
                            if(attackSet.size() >= 2) {
                                Integer sourceArgumentId = Integer.parseInt(attackSet.get(0));
                                for(int i = 1; i < attackSet.size(); i++) {
                                    String targetName = attackSet.get(i).toString();
                                    Integer targetArgumentId;
                                    String attackName;
                                    Attack attack;
                                    
                                    if(targetName.startsWith(".")) {
                                        targetArgumentId = Integer.parseInt(attackSet.get(i).substring(1));
                                        attackName = ".";
                                        attack = new Attack(attackName);
                                        splitAttacks.add(attack);
                                        //doubleFramework = true;
                                    }
                                    else {
                                        targetArgumentId = Integer.parseInt(attackSet.get(i));
                                        attackName = String.valueOf(attackCounter++);
                                        attack = new Attack(attackName);
                                    }
                                   
                                    g.addEdge(attack, netArgumentSet.get(sourceArgumentId-1),
                                            netArgumentSet.get(targetArgumentId-1), EdgeType.DIRECTED);
                                }
                            }
                            break;
                    case 2: break;
                }
            }
            reader.close();

        } catch (FileNotFoundException ex) {}
        catch (NullPointerException npe) {
            JDialog.setDefaultLookAndFeelDecorated(true);
            JOptionPane.showMessageDialog(null, "File formatting error.", "File formatting error.", JOptionPane.ERROR_MESSAGE);
            //JOptionPane.showMessageDialog(null, "File formatting error.");
        }
        catch (IOException e) {
            JDialog.setDefaultLookAndFeelDecorated(true);
            JOptionPane.showMessageDialog(null, "Data transfer error.", "File transferring error.", JOptionPane.ERROR_MESSAGE);
        }
        catch (IndexOutOfBoundsException iobe) {
            JDialog.setDefaultLookAndFeelDecorated(true);
            JOptionPane.showMessageDialog(null, "File formatting error.", "File formatting error.", JOptionPane.ERROR_MESSAGE);
        }
        } //end of .net

        return g;
    }



    //not used
    //private ArrayList<Pair> indexedArguments = new ArrayList<Pair>();

    //not used
    /*private Argument indexArgument(String line) {

        String[] tokens = line.split("\t");
        String argumentIndex = tokens[0];
        String argumentName = tokens[1];
        Argument argument = new Argument(argumentName);
        Pair indexedArgument = new Pair(argumentIndex, argument);
        indexedArguments.add(indexedArgument);
        return argument;
    }
    //not used
    private ArrayList<Pair> getIndexedArguments() {
        return indexedArguments;
    }*/

    /**
     * Creates an argument from a parsed file line
     * @param line the line
     * @return the argument
     */
    private Argument createArgument(String line) {

        String[] tokens = line.split("\t");
        String argumentName = tokens[1];
        Argument argument;
        
        if(argumentName.startsWith(".")) {//newly added April 5
            argument = new Argument(argumentName.substring(1));
            frameworkA.add(argument);
        }
        else {
            argument = new Argument(argumentName);
            frameworkB.add(argument);
        }

        argumentSet.add(argument);

        return argument;
    }

    ArrayList<Argument> netArgumentSet = new ArrayList<Argument>();

    /**
     * Creates an argument from a net file line
     * @param line the line
     * @return the argument
     */
    private Argument createNetArgument(String line) {

        String[] tokens = line.split(" ");
        String argumentName = tokens[1];
        argumentName = argumentName.substring(1, argumentName.length()-1);
        Argument argument;
        
        if(argumentName.startsWith(".")) {//newly added April 5
            argument = new Argument(argumentName.substring(1));
            frameworkA.add(argument);
        }
        else {
            argument = new Argument(argumentName);
            frameworkB.add(argument);
        }
        
        netArgumentSet.add(argument);
        
        return argument;
    }

    /**
     * Creates attacks from a file line
     * @param line the line
     * @return list of attacks
     */
    private ArrayList<String> createAttacks(String line) {

        String[] tokens = line.split("\t");
        ArrayList<String> attackSet = new ArrayList<String>();
        attackSet.addAll(Arrays.asList(tokens));

        return attackSet;
    }

    /**
     * Creates attacks from a net file line
     * @param line the line
     * @return list of attacks
     */
      private ArrayList<String> createNetAttacks(String line) {

        String[] tokens = line.split(" ");
        ArrayList<String> attackSet = new ArrayList<String>();
        attackSet.addAll(Arrays.asList(tokens));

        return attackSet;
    }

      /**
       * Specifies the location of an argument from a file line
       * @param line the line
       */
    private void specifyLocation(String line) {
        ArrayList location = new ArrayList();
        Double x;
        Double y;
        Point2D xy;

        String[] tokens = line.split("\t");
        String argumentIndex = tokens[2];
        Argument argument = argumentSet.get(Integer.parseInt(argumentIndex));
        location.add(argument);

        x = Double.parseDouble(tokens[0]);
        y = Double.parseDouble(tokens[1]);
        
        argument.setX(argument, x);
        argument.setY(argument, y);
        xy = new Point2D.Double(x, y);
        location.add(xy);
    }

    /**
     * Creates a random graph
     * @param argumentCount number of arguments
     * @param attackCount number of attack relations
     * @return the graph
     */
    private DirectedGraph getRandomGraph(int argumentCount, int attackCount) {

        DirectedGraph<Argument, Attack> g = new DirectedSparseGraph<Argument, Attack>();
        frameworkA = new ArrayList<Argument>();

        for(int nodeCount = 0; nodeCount < argumentCount; nodeCount++) {
            Argument argument = new Argument(Integer.toString(nodeCount));
            g.addVertex(argument);
            frameworkA.add(argument);
        }

        while(g.getEdgeCount() < attackCount) {
            int randomSource = (int)(Math.random() * g.getVertexCount());
            int randomTarget = (int)(Math.random() * g.getVertexCount());
            Attack attack = new Attack(Integer.toString(g.getEdgeCount()));
            ArrayList attacksList = new ArrayList(g.getVertices());
            g.addEdge(attack, (Argument)attacksList.get(randomSource), (Argument)attacksList.get(randomTarget), EdgeType.DIRECTED);
        }
        return g;
    }
    
    /**
     * Adding the framework B to the framework A and shifting it to the right
     */
    public void addFrameworkB(int argumentCount, int attackCount) {
        
        frameworkB = new ArrayList<Argument>();//contains arguments of B
        int nodeCount = graph.getVertexCount();
        
        for(int i = nodeCount; i < nodeCount+argumentCount; i++) {
            Argument argument = new Argument(Integer.toString(i));
            getGraph().addVertex(argument);
            frameworkB.add(argument);
        }
        shiftArguments(frameworkB, false);                                      
        
        int edgeCount = graph.getEdgeCount()+attackCount;
        
        while(graph.getEdgeCount() < edgeCount) {
            int randomSource = (int)(Math.random() * (frameworkB.size()));
            int randomTarget = (int)(Math.random() * (frameworkB.size()));
            Attack attack = new Attack(Integer.toString(graph.getEdgeCount()));
            graph.addEdge(attack, frameworkB.get(randomSource), frameworkB.get(randomTarget), EdgeType.DIRECTED);
        }
        
    }


    /**
     * Adding split attacks between frameworks A and B
     * @param AattB attacks from A to B
     * @param BattA attacks from B to A
     */
    public void addRandomSplitAttacks(int AattB, int BattA) {
        //remove existing splitAttacks
        HashSet<Attack> toRemove = new HashSet<Attack>();
        for(Attack at : graph.getEdges()) {
            if(at.getAttackName(at).equals(".")) {
                toRemove.add(at);
            }
        }
        for(Attack at : toRemove) {
            graph.removeEdge(at);
        }
        
        splitAttacks = new HashSet<Attack>();
        
        while(splitAttacks.size() < AattB) {
            int randomSource = (int)(Math.random() * (frameworkA.size()));
            int randomTarget = (int)(Math.random() * (frameworkB.size()));
            
            Attack attack = new Attack(".");
            if(graph.addEdge(attack, frameworkA.get(randomSource), frameworkB.get(randomTarget), EdgeType.DIRECTED) == true) {
                splitAttacks.add(attack);
            }
        }
        while(splitAttacks.size() < (AattB+BattA)) {
            int randomSource = (int)(Math.random() * (frameworkB.size()));
            int randomTarget = (int)(Math.random() * (frameworkA.size()));
            
            Attack attack = new Attack(".");
            if(graph.addEdge(attack, frameworkB.get(randomSource), frameworkA.get(randomTarget), EdgeType.DIRECTED) == true) {
                splitAttacks.add(attack);
            }
        }
    }
    
    public ArrayList<Argument> getFrameworkB() {
        return frameworkB;
    }
    public HashSet<Attack> getSplitAttacks() {
        return splitAttacks;
    }
    
    /**
     * Updates the framework A dynamically
     * @return framework A
     */
    public ArrayList<Argument> getFrameworkA() {
        for(Argument arg : frameworkA) {
            if(!graph.getVertices().contains(arg)) {
                frameworkA.remove(arg);
            }
        }
        return frameworkA;
    }
    

    /**
     * Returns the VisualizationViewer of the current Framework
     * @return VisualizationViewer
     */
    public static VisualizationViewer getVisualizationViewer() {
        return visualizationViewer;
    }


    /*private static ArrayList<Argument> getIndexedArgument() {
        return indexedArgument;
    }*/



    /**
     * Specifies the content for the file to be saved
     * @param extension file extension (*.aaf, *.net)
     * @return file content as String
     */
    public static String getFileContent(String extension) {

        String fileContent = "";

        ArrayList<String> attacks = new ArrayList<String>();
        ArrayList<String> locations = new ArrayList<String>();

        indexedArgument = new ArrayList<Argument>(graph.getVertices());
        Iterator it = indexedArgument.iterator();

        if(extension.equals(".net")) {
            fileContent += "*Vertices " + indexedArgument.size() + "\n";
            while(it.hasNext()) {
                Argument argument = (Argument)it.next();
                String argumentName = String.valueOf(argument);
                if(frameworkA.contains(argument)) {
                    argumentName = "." + argumentName;
                }
                fileContent += indexedArgument.indexOf(argument)+1 + " " + "\"" + argumentName + "\"" + "\n";
                attacks.add(addNetAttacks(argument));
            }
            fileContent += "*Arcslist" + "\n";
            for(int i = 0; i < attacks.size(); i++) {
                fileContent += attacks.get(i);
            }
        }

        if(extension.equals(".aaf")) {
            while(it.hasNext()) {
                Argument argument = (Argument)it.next();
                String argumentName = String.valueOf(argument);
                if(frameworkA.contains(argument)) {
                    argumentName = "." + argumentName;
                }
                fileContent += indexedArgument.indexOf(argument) + "\t" + argumentName + "\n";
                attacks.add(addAttacks(argument));
                locations.add(getLocation(argument));
            }
            fileContent += "*" + "\n";
            for(int i = 0; i < attacks.size(); i++) {
                fileContent += attacks.get(i);
            }
            fileContent += "*" + "\n";
            for(int i = 0; i < locations.size(); i++) {
                fileContent += locations.get(i);
            }
            fileContent += "*" + "\n";
        }

        return fileContent;
    }


    /**
     * Writes the set of attacks for an argument into a file
     * @param argument the argument
     * @return the line containing attacks
     */
    private static String addAttacks(Argument argument) {

        Collection<Attack> outEdges = graph.getOutEdges(argument);

        String outArguments = "";
        outArguments += String.valueOf(indexedArgument.indexOf(argument));

        Iterator it = outEdges.iterator();
        for(int i = 0; i < outEdges.size(); i++) {
            Attack attack = (Attack)it.next();
            Argument target = graph.getDest(attack);
            String targetArgumentId;
            if(attack.getAttackName(attack).equals(".")) {
                targetArgumentId =  "\t" + "." + String.valueOf(indexedArgument.indexOf(target));
            }
            else {
                targetArgumentId =  "\t" + String.valueOf(indexedArgument.indexOf(target));
            }
            outArguments += targetArgumentId;
        }
        outArguments += "\n";

        return outArguments;
    }

    
    /**
     * Writes the set of attacks for an argument into a net file
     * @param argument the argument
     * @return the line containing attacks
     */
    private static String addNetAttacks(Argument argument) {

        Collection<Attack> outEdges = graph.getOutEdges(argument);

        String outArguments = "";
        outArguments += String.valueOf(indexedArgument.indexOf(argument)+1);

        Iterator it = outEdges.iterator();
        for(int i = 1; i < outEdges.size()+1; i++) {
            Attack attack = (Attack)it.next();
            Argument target = graph.getDest(attack);
            String targetArgumentId;
            if(attack.getAttackName(attack).equals(".")) {
                targetArgumentId =  " " + "." + String.valueOf(indexedArgument.indexOf(target)+1);
            }
            else {
                targetArgumentId =  " " + String.valueOf(indexedArgument.indexOf(target)+1);
            }
            outArguments += targetArgumentId;
        }
        outArguments += "\n";

        return outArguments;
    }



    /**
     * Passes the current position of arguments for writing into a file
     * @param argument the argument
     * @return location as String
     */
    private static String getLocation(Argument argument) {

        String location = "";

        Point2D p = (Point2D)getVisualizationViewer().getGraphLayout().transform(argument);
        double xD = p.getX();
        double yD = p.getY();
        int x = (int)xD;
        int y = (int)yD;

        location += String.valueOf(x) + "\t";
        location += String.valueOf(y) + "\t";
        location += String.valueOf(indexedArgument.indexOf(argument)) + "\n";

        return location;
    }


    /**
     * Control method for shifting
     * @param argumentSets sets of arguments for splitting
     */
    public void cluster(ArrayList<ArrayList<Argument>> argumentSets) {

        shiftArgumentsForSplitting(argumentSets.get(0), true);
        shiftArgumentsForSplitting(argumentSets.get(1), false);
    }


    /**
     * Shifts the initial arguments to the right, the non-initial arguments to the left
     * @param argumentSet set of arguments
     * @param initial specification whether initial or non-initial
     */
    private void shiftArgumentsForSplitting(ArrayList<Argument> argumentSet, boolean initial) {

        Dimension layoutSize = getVisualizationViewer().getGraphLayout().getSize();
        Double layoutX = layoutSize.getWidth();
        Double middleX = layoutX / 2;
        
        Iterator it = argumentSet.iterator();
        while(it.hasNext()) {
            Argument argument = (Argument)it.next();
            Double x = 0.0;
            Double y = 0.0;
                
            Point2D position = (Point2D)getVisualizationViewer().getGraphLayout().transform(argument);
            x = position.getX();
            y = position.getY();

            if(initial) {
                if(x < middleX) {
                    shiftedInitials.add(argument);
                    Double distance = middleX - x;
                    x += 2 * distance;
                    position.setLocation(x, y);
                    getVisualizationViewer().getGraphLayout().setLocation(argument, position);
                }
            }
            else if(!initial) {
                if(x > middleX) {
                    shiftedNoninitials.add(argument);
                    Double distance = x - middleX;
                    x -= 2 * distance;
                    position.setLocation(x, y);
                    getVisualizationViewer().getGraphLayout().setLocation(argument, position);
                }
            }
        }
    }
    
    /**
     * Shifting arguments of the first framework to the left and of the second frameework to the right
     * @param argumentSet the arguments of the framework
     * @param firstFramework information whether framework is first or second
     */
    private void shiftArguments(Collection<Argument> argumentSet, boolean firstFramework) {

        Dimension layoutSize = getVisualizationViewer().getGraphLayout().getSize();
        Double layoutX = layoutSize.getWidth();
        Double middleX = layoutX / 2; 
        
        Iterator it = argumentSet.iterator();
        while(it.hasNext()) {
            Argument argument = (Argument)it.next();
            Double x = 0.0;
            Double y = 0.0;
                
            Point2D position = (Point2D)getVisualizationViewer().getGraphLayout().transform(argument);
            x = position.getX();
            y = position.getY();

            if(firstFramework) {
                if(x > middleX) {
                    Double distance = x - middleX;
                    x -= 2 * distance;
                    position.setLocation(x, y);
                    getVisualizationViewer().getGraphLayout().setLocation(argument, position);
                }
            }
            else if(!firstFramework) {
                if(x < middleX) {
                    Double distance = middleX - x;
                    x += 2 * distance;
                    position.setLocation(x, y); 
                    getVisualizationViewer().getGraphLayout().setLocation(argument, position);
                }
            }
        }
    }

    
    /**
     * Returns the initial arguments that were shifted
     * @return list of shifted arguments
     */
    private CopyOnWriteArrayList<Argument> getShiftedInitials() {
        return shiftedInitials;
    }
    
    /**
     * Returns the non-initial arguments that were shifted
     * @return list of shifted arguments
     */
    private CopyOnWriteArrayList<Argument> getShiftedNoninitials() {
        return shiftedNoninitials;
    }


    /**
     * Shifts back the previously shifted arguments (control method)
     */
    public void uncluster() {

        if(getShiftedInitials() != null & getShiftedNoninitials() != null) {
            shiftArgumentsBack(getShiftedInitials(), true);
            shiftArgumentsBack(getShiftedNoninitials(), false);
        }

        resetClusterArguments();
        
    }


    /**
     * Shifts the arguments back into original positions
     * @param argumentSet set of initial or non-initial arguments
     * @param initial specification whether the set contains initial arguments
     */
    private void shiftArgumentsBack(CopyOnWriteArrayList<Argument> argumentSet, boolean initial) {

        Dimension layoutSize = getVisualizationViewer().getGraphLayout().getSize();
        Double layoutX = layoutSize.getWidth();
        Double middleX = layoutX / 2;

        Iterator it = argumentSet.iterator();
        while(it.hasNext()) {
            Argument argument = (Argument)it.next();
            Double x = 0.0;
            Double y = 0.0;

            Point2D position = (Point2D)getVisualizationViewer().getGraphLayout().transform(argument);
            x = position.getX();
            y = position.getY();

            if(initial) {
                Double distance = x - middleX;
                x -= 2 * distance;
                position.setLocation(x, y);
                getVisualizationViewer().getGraphLayout().setLocation(argument, position);
            }
            else if(!initial) {
                Double distance = middleX - x;
                x += 2 * distance;
                position.setLocation(x, y);
                getVisualizationViewer().getGraphLayout().setLocation(argument, position);
            }
        }
    }


    /**
     * Resets the arguments for clustering
     */
    private void resetClusterArguments() {

        Iterator it = getShiftedInitials().iterator();
        while(it.hasNext()) {
            Argument a = (Argument)it.next();
            shiftedInitials.remove(a);
        }

        Iterator it2 = getShiftedNoninitials().iterator();
        while(it2.hasNext()) {
            Argument a = (Argument)it2.next();
            shiftedNoninitials.remove(a);
        }
    }


    /**
     * Renders no argument label
     */
    public void renderNoArgumentLabel() {

         Transformer<Argument, String> noArgumentLabel = new Transformer<Argument, String>() {
            @Override
            public String transform(Argument i) {
                return "";
            }
        };
        visualizationViewer.getRenderContext().setVertexLabelTransformer(noArgumentLabel);
    }


    /**
     * Renders argument label
     */
    public void renderArgumentLabel() {

        Transformer<Argument, String> argumentLabel = new Transformer<Argument, String>() {
            @Override
            public String transform(Argument i) {
                return i.getArgumentName(i);
            }
        };
        visualizationViewer.getRenderContext().setVertexLabelTransformer(argumentLabel);

    }

    /**
     * Renders attack label
     */
    public void renderAttackLabel() {

        Transformer<Attack, String> attackLabel = new Transformer<Attack, String>() {
            @Override
            public String transform(Attack i) {
                return i.getAttackName(i);
            }
        };
        visualizationViewer.getRenderContext().setEdgeLabelTransformer(attackLabel);
    }

    /**
     * Renders no attack label
     */
    public void renderNoAttackLabel() {

        Transformer<Attack, String> noAttackLabel = new Transformer<Attack, String>() {
            @Override
            public String transform(Attack i) {
                return "";
            }
        };
        visualizationViewer.getRenderContext().setEdgeLabelTransformer(noAttackLabel);
    }

    /**
     * Sets argument label inside vertex
     */
    public void argumentLabelInsideVertex() {

        BasicVertexLabelRenderer bvlr = new BasicVertexLabelRenderer(Position.CNTR);
        visualizationViewer.getRenderer().setVertexLabelRenderer(bvlr);
    }

    /**
     * Sets argument label outside vertex
     */
    public void argumentLabelOutsideVertex() {

        BasicVertexLabelRenderer bvlr = new BasicVertexLabelRenderer(Position.SE);
        visualizationViewer.getRenderer().setVertexLabelRenderer(bvlr);
    }

}


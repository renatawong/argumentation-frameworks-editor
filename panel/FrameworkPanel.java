
package panel;


import computations.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import io.FrameworkFileSelector;
import io.RandomAttackDialog;
import io.RandomFrameworkDialog;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import visualization.Argument;
import visualization.Attack;
import visualization.FrameworkViewer;


/**
 * Creates the panel for framework representation
 *
 * @author Renata Wong
 */
public class FrameworkPanel extends Observable {

    private JPanel controlPanel;
    private JPanel visualPanel;
    private JTextArea outputTextArea;

    JRadioButtonMenuItem circleLayout;
    JRadioButtonMenuItem frLayout;
    JRadioButtonMenuItem isomLayout;
    JRadioButtonMenuItem kkLayout;
    JRadioButtonMenuItem springLayout;
    private LayoutListener layoutListener = new LayoutListener();

    private JButton stableExtension;
    private JButton preferredExtension;
    private JButton groundedExtension;

    private JButton sccs;
    private JButton split;
    private JButton splitSW;
    private JButton splitHO;
    private JButton splitMC;
    private JButton cluster;
    private JButton uncluster;

    private String extension = "";
    private JRadioButton splittingOn;
    private JRadioButton HOSplittingOn;
    private JRadioButton SWSplittingOn;
    private JRadioButton F2SplittingOn;
    private JRadioButton midCutOn;

    private JMenuItem exportImage;
    private JMenuItem saveFile;
    private JMenuItem addAttacks;

    FrameworkViewer frameworkViewer;
    VisualizationViewer visualizationViewer;
    StronglyConnectedComponent scc;
    Splitting splitting;
    ParameterizedSplitting parameterizedSplitting;
    StoerWagnerMinimumCut swmc;
    HaoOrlinMinimumCut homc;

    private AggregateLayout layout;

    private static JTextField timeWithoutSplitting;
    private static JTextField timeRegularSplitting;
    private static JTextField timeHOSplitting;
    private static JTextField timeSWSplitting;
    private static JTextField timeF2Splitting;
    private static JTextField timeMinCut;
    private static JTextField time2MinCut;
    private static JTextField timeMidCutSplitting;

    private HelpListener helpListener = new HelpListener();

    private JRadioButtonMenuItem editing;
    private JRadioButtonMenuItem picking;
    private JRadioButtonMenuItem transforming;
    private ModeListener modeListener = new ModeListener();

    private SplittingListener splittingListener = new SplittingListener();
    private ArrayList<ArrayList<Argument>> argumentSets = new ArrayList<ArrayList<Argument>>();
    private ArrayList<Argument> argumentSet = new ArrayList<Argument>();
    GraphZoomScrollPane gzsp;

    private JCheckBoxMenuItem showArgumentLabel;
    private JCheckBoxMenuItem showAttackLabel;
    private JCheckBoxMenuItem argumentLabelInside;
    private OptionsListener optionsListener = new OptionsListener();

    private ExtensionsListener extensionsListener = new ExtensionsListener();
    private boolean resetExtension = false;
    private boolean resetParExtension = false;
    
    private boolean canAddRandomAttacks = false;
    private int argumentNumberA;
    private int argumentNumberB;

    private Preferences prefs;
    
    private static final boolean SHOW_ARG_LABEL_DEFAULT = true;
    private static final String SHOW_ARG_LABEL = "SHOW_ARG_LABEL";
    private static final boolean SHOW_ARG_LABEL_CNTR_DEFAULT = true;
    private static final String SHOW_ARG_LABEL_CNTR = "SHOW_ATT_LABEL_CNTR";
    private static final boolean SHOW_ATT_LABEL_DEFAULT = false;
    private static final String SHOW_ATT_LABEL = "SHOW_ATT_LABEL";
    private static final boolean SPLITTING_CHOICE_DEFAULT = false;
    private static final String SPLITTING_CHOICE = "SPLITTING_CHOICE";
    private static final boolean HO_SPLITTING_DEFAULT = false;
    private static final String HO_SPLITTING = "HO_SPLITTING";
    private static final boolean SW_SPLITTING_DEFAULT = false;
    private static final String SW_SPLITTING = "SW_SPLITTING";
    private static final boolean PARAMETERIZED_2_SPLITTING_DEFAULT = false;
    private static final String PARAMETERIZED_2_SPLITTING = "PARAMETERIZED_2_SPLITTING";
    private static final boolean MID_CUT_SPLITTING_DEFAULT = false;
    private static final String MID_CUT_SPLITTING = "MID_CUT_SPLITTING";
    private boolean doubleFramework = false;



    /**
     * Constructor: creates a new panel for framework representation
     */
    public FrameworkPanel() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { }
        
        JFrame.setDefaultLookAndFeelDecorated(true);

        Font buttonFont = new Font("Arial", Font.PLAIN, 11);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("Label.font", buttonFont);
        UIManager.put("TextField.font", buttonFont);
        UIManager.put("RadioButton.font", buttonFont);

        JFrame frame = new JFrame("AFE-2 Argumentation Framework Editor v. 2.0");
        Toolkit kit = Toolkit.getDefaultToolkit();
        java.net.URL iconUrl = this.getClass().getResource("circ.gif");
        //Image img = Toolkit.getDefaultToolkit().getImage(iconUrl); 
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("circ.gif")); //TODO: change before uploading
        frame.setSize(new Dimension(1000, 550));
        frame.setMinimumSize(new Dimension(1000, 550));
        frame.setPreferredSize(new Dimension(1000, 550));
        frame.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        /*frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
               exitCheck();
            }
        });*/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(initMenuBarFileMenu());
        menuBar.add(initMenuBarOptionsMenu());
        menuBar.add(initMenuBarFrameworkMenu());
        menuBar.add(initMenuBarHelpMenu());
        menuBar.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.gray));
        frame.setJMenuBar(menuBar);

        controlPanel = new JPanel();
        initControlPanel();
        frame.getContentPane().add(controlPanel, BorderLayout.LINE_START);

        visualPanel = new JPanel(new BorderLayout());
        initVisualPanel();
        frame.getContentPane().add(visualPanel, BorderLayout.CENTER);

        enableSettings(false);
        prefs = Preferences.userNodeForPackage(getClass());
        getStoredPreferences();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    storePreferences();
                } catch(Exception ex) {}
            }
        }));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Initialisation of the "File" menu of the menu bar
     * @return "File" menu of the menu bar
     */
    private JMenu initMenuBarFileMenu() {

        JMenu fileMenu = new JMenu("File");
	fileMenu.setMnemonic(KeyEvent.VK_F);

	JMenuItem newFile = new JMenuItem("New Framework", KeyEvent.VK_N);
	KeyStroke ctrlNKeyStroke = KeyStroke.getKeyStroke("control N");
	newFile.setAccelerator(ctrlNKeyStroke);
        newFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameworkViewer = new FrameworkViewer(0, 0);
                if(gzsp != null) {
                    visualPanel.remove(gzsp);
                }
                visualPanel.add(getGraphZoomScrollPane(), BorderLayout.CENTER);
                canAddRandomAttacks = false;
                setOptions("N");
                visualPanel.validate();
            }
        });

	JMenuItem openFile = new JMenuItem("Open Framework ...", KeyEvent.VK_O);
	KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control O");
	openFile.setAccelerator(ctrlXKeyStroke);
        openFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FrameworkFileSelector fileSelector = new FrameworkFileSelector();
                int status = fileSelector.openFile();
                if(status == 0) {
                    File file = fileSelector.getSelectedFile();
                    frameworkViewer = new FrameworkViewer(file);
                    if(frameworkViewer.getSplitAttacks().size() >= 0) {
                        doubleFramework = true;
                        canAddRandomAttacks = true;
                    } 
                    else {
                        doubleFramework = false;
                        canAddRandomAttacks = false;
                    }
                    if(gzsp != null) {
                        visualPanel.remove(gzsp);
                    }
                    visualPanel.add(getGraphZoomScrollPane(), BorderLayout.CENTER);
                    setOptions(extension);
                    visualPanel.validate();
                }
                else if(status == 2) {
                    JOptionPane.showMessageDialog (null, "Error opening framework!", "File Open Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

	saveFile = new JMenuItem("Save Framework", KeyEvent.VK_S);
	KeyStroke ctrlSKeyStroke = KeyStroke.getKeyStroke("control S");
	saveFile.setAccelerator(ctrlSKeyStroke);
        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FrameworkFileSelector fileSelector = new FrameworkFileSelector();
                int status = fileSelector.saveFile();
                if (status == 2) {
                    JOptionPane.showMessageDialog (null, "Error saving framework!!", "File Save Error", JOptionPane.ERROR_MESSAGE);
                }
                visualPanel.repaint();
            }
        });

        exportImage = new JMenuItem("Export Image", KeyEvent.VK_E);
        KeyStroke ctrlEKeyStroke = KeyStroke.getKeyStroke("control E");
	exportImage.setAccelerator(ctrlEKeyStroke);
        exportImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FrameworkFileSelector fileSelector = new FrameworkFileSelector();
                int status = fileSelector.exportImage(FrameworkViewer.getGraph());
                if(status == 0) {
                    visualizationViewer.setDoubleBuffered(true);
                }
                else if(status == 2) {
                    JOptionPane.showMessageDialog(null, "Error exporting framework!!", "File Export Error", JOptionPane.ERROR_MESSAGE);
                }
                visualizationViewer.setDoubleBuffered(false);
                visualPanel.repaint();
            }
        });

	JMenuItem discardFile = new JMenuItem("Discard Framework", KeyEvent.VK_D);
        KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke("control D");
	discardFile.setAccelerator(ctrlDKeyStroke);
	discardFile.getAccessibleContext().setAccessibleDescription("Discard Framework");
        discardFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(gzsp != null) {
                    visualPanel.remove(gzsp);
                }
                canAddRandomAttacks = false;
                enableSettings(false);
                visualPanel.repaint();
            }
        });

	JMenuItem generateFile = new JMenuItem("Generate Random Framework ...", KeyEvent.VK_G);
        KeyStroke ctrlGKeyStroke = KeyStroke.getKeyStroke("control G");
	generateFile.setAccelerator(ctrlGKeyStroke);
        generateFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRandomFramework();
            }
        });
        
        addAttacks = new JMenuItem("Add Random Attacks ...", KeyEvent.VK_A);
        KeyStroke ctrlAKeyStroke = KeyStroke.getKeyStroke("control A");
	addAttacks.setAccelerator(ctrlAKeyStroke);
        addAttacks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRandomSplitAttacks();
            }
        });


	JMenuItem exitEditor = new JMenuItem("Exit");
	KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
	exitEditor.setAccelerator(escapeKeyStroke);
	exitEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitCheck();
            }
        });

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(discardFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exportImage);
	fileMenu.addSeparator();
        fileMenu.add(generateFile);
        fileMenu.add(addAttacks);
	fileMenu.addSeparator();
        fileMenu.add(exitEditor);

        return fileMenu;
    }

    /**
     * Creates Options Menu
     * @return options menu
     */
    private JMenu initMenuBarOptionsMenu() {

	JMenu optionsMenu = new JMenu("Options");
	optionsMenu.setMnemonic(KeyEvent.VK_O);

	showArgumentLabel = new JCheckBoxMenuItem("argument label");
        showArgumentLabel.addItemListener(optionsListener);

	showAttackLabel = new JCheckBoxMenuItem("attack label");
        showAttackLabel.addItemListener(optionsListener);

        argumentLabelInside = new JCheckBoxMenuItem("argument label (center/out)");
        argumentLabelInside.addItemListener(optionsListener);

	optionsMenu.add(showArgumentLabel);
        optionsMenu.add(argumentLabelInside);
        optionsMenu.add(showAttackLabel);

        return optionsMenu;
    }

    /**
     * Creates the layout menu and handles layout change events
     * @return layout menu
     */
    private JMenu initMenuBarFrameworkMenu() {

        JMenu frameworkMenu = new JMenu("Framework");
        frameworkMenu.setMnemonic(KeyEvent.VK_R);
        ButtonGroup layoutButtonGroup = new ButtonGroup();

        circleLayout = new JRadioButtonMenuItem("circle layout");
        circleLayout.addItemListener(layoutListener);
        layoutButtonGroup.add(circleLayout);
        frameworkMenu.add(circleLayout);

        frLayout = new JRadioButtonMenuItem("Fruchterman-Rheingold layout");
        frLayout.addItemListener(layoutListener);
        layoutButtonGroup.add(frLayout);
        frameworkMenu.add(frLayout);

        isomLayout = new JRadioButtonMenuItem("Isom layout");
        isomLayout.addItemListener(layoutListener);
        layoutButtonGroup.add(isomLayout);
        frameworkMenu.add(isomLayout);

        kkLayout = new JRadioButtonMenuItem("Kamada-Kawai layout");
        kkLayout.addItemListener(layoutListener);
        layoutButtonGroup.add(kkLayout);
        frameworkMenu.add(kkLayout);

        springLayout = new JRadioButtonMenuItem("spring layout");
        springLayout.addItemListener(layoutListener);
        layoutButtonGroup.add(springLayout);
        frameworkMenu.add(springLayout);

        frameworkMenu.addSeparator();

        ButtonGroup modeButtonGroup = new ButtonGroup();

        editing = new JRadioButtonMenuItem("editing mode");
        editing.addItemListener(modeListener);
        modeButtonGroup.add(editing);
        frameworkMenu.add(editing);

        picking = new JRadioButtonMenuItem("picking mode");
        picking.addItemListener(modeListener);
        modeButtonGroup.add(picking);
        frameworkMenu.add(picking);

        transforming = new JRadioButtonMenuItem("transforming mode");
        transforming.addItemListener(modeListener);
        modeButtonGroup.add(transforming);
        frameworkMenu.add(transforming);

        return frameworkMenu;
    }

    /**
     * Initialisation of the "Help" menu of the menu bar
     * @return HELP menu of the menu bar
     */
    private JMenu initMenuBarHelpMenu() {

	JMenu helpMenu = new JMenu("Help");
	helpMenu.setMnemonic(KeyEvent.VK_H);

	JMenuItem helpFile = new JMenuItem("Instructions", KeyEvent.VK_F1);
	KeyStroke f1KeyStroke = KeyStroke.getKeyStroke("F1"); 
	helpFile.setAccelerator(f1KeyStroke);
        helpFile.addActionListener(helpListener);

        JMenuItem marking = new JMenuItem("Reading of results");
        marking.addActionListener(helpListener);

        JMenuItem aafSpecification = new JMenuItem("*.aaf file specification");
        aafSpecification.addActionListener(helpListener);

        JMenuItem netSpecification = new JMenuItem("*.net file specification");
        netSpecification.addActionListener(helpListener);

	JMenuItem aboutFile = new JMenuItem("About");
        aboutFile.addActionListener(helpListener);

        helpMenu.add(helpFile);
        helpMenu.addSeparator();
        helpMenu.add(marking);
        helpMenu.addSeparator();
        helpMenu.add(aafSpecification);
        helpMenu.add(netSpecification);
        helpMenu.addSeparator();
        helpMenu.add(aboutFile);

        return helpMenu;
    }

    /**
     * Initialisation of the control panel
     * @return control panel
     */
    private JPanel initControlPanel() {
        controlPanel.setLayout(null);
        controlPanel.setMinimumSize(new Dimension(200, 550));
        controlPanel.setPreferredSize(new Dimension(200, 550));
        controlPanel.setBackground(new Color(238,224,229));
        controlPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,1, Color.gray));

        controlPanel.add(initExtensionsPanel());
        controlPanel.add(initSplittingPanel());
        controlPanel.add(Box.createVerticalGlue());
        
        return controlPanel;
    }

    /**
     * Initialisation of the extensions panel
     * @return extension panel
     */
    private JPanel initExtensionsPanel() {

        JPanel extensionsPanel = new JPanel();
        extensionsPanel.setBounds(5,5,190,335);
        extensionsPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.gray));
        extensionsPanel.setBackground(new Color(238,224,229));
        extensionsPanel.setLayout(null);

        final JLabel extensionsLabel = new JLabel("LABELLINGS");
        extensionsLabel.setBounds(15,10,150,20);

        stableExtension = new JButton("stable");
        stableExtension.addActionListener(extensionsListener);
        stableExtension.setBounds(15,35,80,20);

        preferredExtension = new JButton("preferred");
        preferredExtension.addActionListener(extensionsListener);
        preferredExtension.setBounds(100,35,80,20);
        
        groundedExtension = new JButton("grounded");
        groundedExtension.addActionListener(extensionsListener);
        groundedExtension.setBounds(100,60,80,20);

        splittingOn = new JRadioButton("splitting (S)");
        splittingOn.setBackground(new Color(238,224,229));
        splittingOn.addItemListener(extensionsListener);
        splittingOn.setBounds(15,60,80,20);
        
        HOSplittingOn = new JRadioButton("mincut par. splitting (HO)");
        HOSplittingOn.setBounds(15,80,160,20);
        HOSplittingOn.setBackground(new Color(238,224,229));
        HOSplittingOn.addItemListener(extensionsListener);
        
        SWSplittingOn = new JRadioButton("mincut par. splitting (SW)");
        SWSplittingOn.setBounds(15,100,160,20);
        SWSplittingOn.setBackground(new Color(238,224,229));
        SWSplittingOn.addItemListener(extensionsListener);
        
        F2SplittingOn = new JRadioButton("2 fram. par. splitting (2F)");
        F2SplittingOn.setBounds(15,120,160,20);
        F2SplittingOn.setBackground(new Color(238,224,229));
        F2SplittingOn.addItemListener(extensionsListener);
        
        midCutOn = new JRadioButton("midcut par. splitting (MC)");
        midCutOn.setBounds(15,140,160,20);
        midCutOn.setBackground(new Color(238,224,229));
        midCutOn.addItemListener(extensionsListener);

        JLabel time = new JLabel("TIME w/o: ");
        time.setBounds(15,170,75,20);
        timeWithoutSplitting = new JTextField(40);
        timeWithoutSplitting.setBounds(85,170,95,18);
        timeWithoutSplitting.setEditable(false);

        JLabel timeS = new JLabel("TIME (S): ");
        timeS.setBounds(15,190,75,20);
        timeRegularSplitting = new JTextField(40);
        timeRegularSplitting.setBounds(85,190,95,18);
        timeRegularSplitting.setEditable(false);
        
        JLabel timeB = new JLabel("TIME (HO): ");
        timeB.setBounds(15,210,75,20);
        timeHOSplitting = new JTextField(40);
        timeHOSplitting.setBounds(85,210,95,18);
        timeHOSplitting.setEditable(false);
        
        JLabel time2Incl = new JLabel("incl.");
        time2Incl.setBounds(15,230,35,20);
        time2MinCut = new JTextField(40);
        time2MinCut.setBounds(37,230,49,18);
        time2MinCut.setEditable(false);
        JLabel time2Cut = new JLabel("ms for HO mincut");
        time2Cut.setBounds(90,230,120,20);
        
        JLabel timeK = new JLabel("TIME (SW): ");
        timeK.setBounds(15,250,75,20);
        timeSWSplitting = new JTextField(40);
        timeSWSplitting.setBounds(85,250,95,18);
        timeSWSplitting.setEditable(false);
        
        JLabel timeIncl = new JLabel("incl.");
        timeIncl.setBounds(15,270,35,20);
        timeMinCut = new JTextField(40);
        timeMinCut.setBounds(37,270,49,18);
        timeMinCut.setEditable(false);
        JLabel timeCut = new JLabel("ms for SW mincut");
        timeCut.setBounds(90,270,120,20);
        
        JLabel time2K = new JLabel("TIME (2F): ");
        time2K.setBounds(15,290,75,20);
        timeF2Splitting = new JTextField(40);
        timeF2Splitting.setBounds(85,290,95,18);
        timeF2Splitting.setEditable(false);
        
        JLabel timeMK = new JLabel("TIME (MC): ");
        timeMK.setBounds(15,310,75,20);
        timeMidCutSplitting = new JTextField(40);
        timeMidCutSplitting.setBounds(85,310,95,18);
        timeMidCutSplitting.setEditable(false);

        extensionsPanel.add(extensionsLabel);
        extensionsPanel.add(stableExtension);
        extensionsPanel.add(preferredExtension);
        extensionsPanel.add(groundedExtension);
        extensionsPanel.add(splittingOn);
        extensionsPanel.add(HOSplittingOn);
        extensionsPanel.add(SWSplittingOn);
        extensionsPanel.add(F2SplittingOn);
        extensionsPanel.add(midCutOn);
        extensionsPanel.add(time);
        extensionsPanel.add(timeWithoutSplitting);
        extensionsPanel.add(timeS);
        extensionsPanel.add(timeRegularSplitting);
        extensionsPanel.add(timeB);
        extensionsPanel.add(timeHOSplitting);
        extensionsPanel.add(timeK);
        extensionsPanel.add(timeSWSplitting);
        extensionsPanel.add(time2K);
        extensionsPanel.add(timeF2Splitting);
        extensionsPanel.add(timeIncl);
        extensionsPanel.add(timeMinCut);
        extensionsPanel.add(timeCut);
        extensionsPanel.add(time2Incl);
        extensionsPanel.add(time2MinCut);
        extensionsPanel.add(time2Cut);
        extensionsPanel.add(timeMK);
        extensionsPanel.add(timeMidCutSplitting);

        return extensionsPanel;
    }

    /**
     * Initialisation of the splitting panel
     * @return splittingPanel
     */
    private JPanel initSplittingPanel() {

        JPanel splittingPanel = new JPanel();
        splittingPanel.setBounds(5,345,190,140);
        splittingPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.gray));
        splittingPanel.setBackground(new Color(238,224,229));
        splittingPanel.setLayout(null);

        final JLabel splittingLabel = new JLabel("SPLITTING");
        splittingLabel.setBounds(15,10,80,20);

        sccs = new JButton("SCCs");
        sccs.setBounds(10,35,80,20);
        sccs.addActionListener(splittingListener);

        split = new JButton("SPLIT S");
        split.setBounds(10,60,80,20);
        split.addActionListener(splittingListener);
        
        splitHO = new JButton("SPLIT HO");
        splitHO.setBounds(95,60,85,20);
        splitHO.addActionListener(splittingListener);

        splitSW = new JButton("SPLIT SW");
        splitSW.setBounds(10,85,80,20);
        splitSW.addActionListener(splittingListener);
        
        splitMC = new JButton("SPLIT MC");
        splitMC.setBounds(95,85,85,20);
        splitMC.addActionListener(splittingListener);

        cluster = new JButton("shift");
        cluster.setBounds(10,110,80,20);
        cluster.addActionListener(splittingListener);

        uncluster = new JButton("shift back");
        uncluster.setBounds(95,110,85,20);
        uncluster.addActionListener(splittingListener);

        splittingPanel.add(splittingLabel);
        splittingPanel.add(sccs);
        splittingPanel.add(split);
        splittingPanel.add(splitHO);
        splittingPanel.add(splitSW);
        splittingPanel.add(splitMC);
        splittingPanel.add(cluster);
        splittingPanel.add(uncluster);

        return splittingPanel;
    }

    /**
     * Initialises the visual panel
     * @return visualPanel
     */
    private JPanel initVisualPanel() {
        
        JScrollPane outputScrollPane = new JScrollPane();
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setBackground(new Color(255,240,245));
        //makes the scrollbars unmovable
        DefaultCaret caret = (DefaultCaret) outputTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        
        outputScrollPane.getViewport().setView(outputTextArea);
        outputScrollPane.setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.gray));
        outputScrollPane.setPreferredSize(new Dimension(800, 100));
	outputScrollPane.setSize(new Dimension(800, 100));
        outputScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        visualPanel.setPreferredSize(new Dimension(800,550));
        visualPanel.add(outputScrollPane, BorderLayout.PAGE_END);
                
        return visualPanel;
    }


    /**
     * Specifies the graph zoom scroll pane for the visualisation viewer
     * @return GraphZoomScrollPane
     */
    public GraphZoomScrollPane getGraphZoomScrollPane() {
        visualizationViewer = FrameworkViewer.getVisualizationViewer();
        visualizationViewer.addKeyListener(modeListener);
        gzsp = new GraphZoomScrollPane(visualizationViewer);
        gzsp.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                //gzsp.setSize(visualizationViewer.getGraphLayout().getSize().width+17, visualizationViewer.getGraphLayout().getSize().height+17);
                gzsp.repaint();
            }
        });
        return gzsp;
    }


    /**
     * Calls a method in StronglyCOnnectedComponent that computes SCCs
     * and then outputs the result
     */
    private void computeSCC() {

        scc = new StronglyConnectedComponent();
        Argument.setArgumentFill(new ArrayList<ArrayList<Argument>>(scc.getStronglyConnectedComponents()));
        setOutput(scc.setOutput());
        visualPanel.repaint();
    }


    /**
     * Performs a regular splitting (not meant for computation of a semantics)
     */
    private void splitFramework() {

        scc = new StronglyConnectedComponent();
        splitting = new Splitting(scc.getStronglyConnectedComponents(), false);
        setArgumentSets(splitting.getPartition());
        setOutput(splitting.outputSplitting());
        visualPanel.repaint();
    }


    /**
     * Performs a size balanced splitting (not meant for computation of a semantics)
     */
    private void splitFrameworkEqually() {

        scc = new StronglyConnectedComponent();
        splitting = new Splitting(scc.getStronglyConnectedComponents(), true);
        setArgumentSets(splitting.getPartition());
        setOutput(splitting.outputSplitting());
        visualPanel.repaint();
    }
    
    
     /**
      * Computation of minimum cut for the purpose of visualisation
      */
     private void computeMidCutForVisualization() {
         
        MidCut midcut = new MidCut(); 
        parameterizedSplitting = new ParameterizedSplitting(midcut.getMidCut(), false);
        setArgumentSets(parameterizedSplitting.getPartition()); 
        setOutput(parameterizedSplitting.outputSplitting());
        visualPanel.repaint();
    }


    /**
     * Performs splitting for the purpose of semantics computation
     * @param optimise true if optimisation requested, false otherwise
     */
    private void splitForComputation(boolean optimise) {

        scc = new StronglyConnectedComponent();
        splitting = new Splitting(scc.getStronglyConnectedComponents(), optimise);
        setArgumentSets(splitting.getPartition());
    }
    
    
    /**
     * Computes the Stoer Wagner minimum cut for the purpose of computing a semantics
     */
    private void computeMinCut() {                                              
        
        swmc = new StoerWagnerMinimumCut(); 
        parameterizedSplitting = new ParameterizedSplitting(swmc.getMinimumCut(), true);
        setArgumentSets(parameterizedSplitting.getPartition());
        visualPanel.repaint();
    }
    
    /**
     * Computes the Hao Orlin minimum cut for the purpose of computing a semantics
     */
    private void computeHaoOrlinMinCut() {                                              
        
        homc = new HaoOrlinMinimumCut();
        parameterizedSplitting = new ParameterizedSplitting(homc.getMinimumCut(), true);
        setArgumentSets(parameterizedSplitting.getPartition());
        visualPanel.repaint();
    }
    
   /**
     * Computes the Stoer Wagner minimum cut for the purpose of visualization
     */
    private void computeMinCutForVisualization() {                                              
        
        swmc = new StoerWagnerMinimumCut();  
        parameterizedSplitting = new ParameterizedSplitting(swmc.getMinimumCut(), false);
        setArgumentSets(parameterizedSplitting.getPartition()); 
        setOutput(parameterizedSplitting.outputSplitting());
        visualPanel.repaint();  
    }
    
    /**
     * Computes the Hao Orlin minimum cut for the purpose of visualization
     */
    private void computeHaoOrlinMinCutForVisualization() {                                              
        
        homc = new HaoOrlinMinimumCut();
        parameterizedSplitting = new ParameterizedSplitting(homc.getMinimumCut(), false);
        setArgumentSets(parameterizedSplitting.getPartition()); 
        setOutput(parameterizedSplitting.outputSplitting());
        visualPanel.repaint();  
    }
    

    /**
     * Calls a random framework generator dialog
     */
    private void generateRandomFramework() {
        
        RandomFrameworkDialog dialog = new RandomFrameworkDialog();

        if(dialog.isValid()) {
            argumentNumberA = dialog.getArgumentCountA();
            int attackNumberA = dialog.getAttackCountA();
            if(dialog.isSingleFramework()) {
                doubleFramework = false;
                canAddRandomAttacks = false;
                frameworkViewer = new FrameworkViewer(argumentNumberA, attackNumberA);
            }
            else if(!dialog.isSingleFramework()) {
                doubleFramework = true;
                canAddRandomAttacks = true;
                frameworkViewer = new FrameworkViewer(argumentNumberA, attackNumberA, true);
                argumentNumberB = dialog.getArgumentCountB();
                int attackNumberB = dialog.getAttackCountB();
                frameworkViewer.addFrameworkB(argumentNumberB, attackNumberB);
                if(dialog.existSplitAttacks()) {
                    frameworkViewer.addRandomSplitAttacks(dialog.getAattB(), dialog.getBattA());
                }
            }
            if(gzsp != null) {
                visualPanel.remove(gzsp);
            }
            visualPanel.add(getGraphZoomScrollPane(), BorderLayout.CENTER);
            setOptions("R");
            optionsListener.loadActions();
            visualPanel.validate();
            visualPanel.repaint();
        }
    }

    private void addRandomSplitAttacks() {
        
        int frA = frameworkViewer.getFrameworkA().size();
        int frB = frameworkViewer.getFrameworkB().size();
        
        RandomAttackDialog dialog = new RandomAttackDialog(frA, frB);
        
        if(dialog.isValid()) {
            
            frameworkViewer.addRandomSplitAttacks(dialog.getAattB(), dialog.getBattA());

            visualPanel.validate();
            visualPanel.repaint();
        }
    } 

    /**
     * Controls the outputs in the output area
     * @param message information to be output
     */
    public void setOutput(String message) {

        outputTextArea.setText(message);
    }


    /**
     * Controls the outputs of time for regular splitting
     * @param time regular splitting time
     */
    public static void setRegularSplittingTimeLabel(String time) {
        timeRegularSplitting.setText(time);
    }
    
    /**
     * Controls the outputs of time for size balanced splitting
     * @param time balanced size splitting time
     */
    public static void setHOSplittingTimeLabel(String time) {
        timeHOSplitting.setText(time);
    }

    /**
     * Controls the outputs of computation time without splitting
     * @param time computation time
     */
    public static void setTimeWithoutSplittingLabel(String time) {
        timeWithoutSplitting.setText(time);
    }
    
    /**
     * Controls the outputs of computation time for parameterised splitting
     * @param time time of parameterised splitting
     */
    public static void setSWSplittingTimeLabel(String time) {
        timeSWSplitting.setText(time);
    }
    
    public static void setF2SplittingTimeLabel(String time) {
        timeF2Splitting.setText(time);
    }
    
    public static void setMidCutTimeLabel(String time) {
        timeMidCutSplitting.setText(time);
    }


    /**
     * Restores user preferences from previous session
     */
    private void getStoredPreferences() {

        showArgumentLabel.setSelected(prefs.getBoolean(SHOW_ARG_LABEL, SHOW_ARG_LABEL_DEFAULT));
        argumentLabelInside.setSelected(prefs.getBoolean(SHOW_ARG_LABEL_CNTR, SHOW_ARG_LABEL_CNTR_DEFAULT));
        showAttackLabel.setSelected(prefs.getBoolean(SHOW_ATT_LABEL, SHOW_ATT_LABEL_DEFAULT));
        splittingOn.setSelected(prefs.getBoolean(SPLITTING_CHOICE, SPLITTING_CHOICE_DEFAULT));
        HOSplittingOn.setSelected(prefs.getBoolean(HO_SPLITTING, HO_SPLITTING_DEFAULT));
        SWSplittingOn.setSelected(prefs.getBoolean(SW_SPLITTING, SW_SPLITTING_DEFAULT));
        F2SplittingOn.setSelected(prefs.getBoolean(PARAMETERIZED_2_SPLITTING, PARAMETERIZED_2_SPLITTING_DEFAULT));
        midCutOn.setSelected(prefs.getBoolean(MID_CUT_SPLITTING, MID_CUT_SPLITTING_DEFAULT));
    }

    /**
     * Stores user preferences between sessions
     * @throws BackingStoreException
     */
    private void storePreferences() throws BackingStoreException {
        
        prefs.putBoolean(SHOW_ARG_LABEL, showArgumentLabel.isSelected());
        prefs.putBoolean(SHOW_ARG_LABEL_CNTR, argumentLabelInside.isSelected());
        prefs.putBoolean(SHOW_ATT_LABEL, showAttackLabel.isSelected());
        prefs.putBoolean(SPLITTING_CHOICE, splittingOn.isSelected());
        prefs.putBoolean(HO_SPLITTING, HOSplittingOn.isSelected());
        prefs.putBoolean(SW_SPLITTING, SWSplittingOn.isSelected());
        prefs.putBoolean(PARAMETERIZED_2_SPLITTING, F2SplittingOn.isSelected());
        prefs.putBoolean(MID_CUT_SPLITTING, midCutOn.isSelected());

        setChanged();
        notifyObservers();
    }


    /**
     * Specifies initial settings for the framework
     * @param feature 
     */
    private void setOptions(String feature) {

        enableSettings(true);
        optionsListener.loadActions(); //do not move!!!
        
        setOutput("");
        setTimeWithoutSplittingLabel("");
        setRegularSplittingTimeLabel("");
        setHOSplittingTimeLabel("");
        setSWSplittingTimeLabel("");
        setF2SplittingTimeLabel("");
        setMidCutTimeLabel("");
        timeMinCut.setText("");
        time2MinCut.setText("");
        
        if(feature.equals(".net") | feature.equals("R") | feature.equals("N")) {
            frLayout.setSelected(true);
        }
        else {
            frLayout.setSelected(false);
        }
        circleLayout.setSelected(false);
        kkLayout.setSelected(false);
        isomLayout.setSelected(false);
        springLayout.setSelected(false);

        picking.setSelected(false);
        if(feature.equals("N")) {
            editing.setSelected(true);
            transforming.setSelected(false);
        }
        else {
            editing.setSelected(false);
            transforming.setSelected(true);
        }

        cluster.setEnabled(false);
        uncluster.setEnabled(false);

    }


    /**
     * Enables / disables settings and items
     * @param enabled true for enable, false for disable
     */
    private void enableSettings(boolean enabled) {

        exportImage.setEnabled(enabled);
        saveFile.setEnabled(enabled);
        addAttacks.setEnabled(canAddRandomAttacks);//TODO after reading from file and saving to file

        showArgumentLabel.setEnabled(enabled);
        showAttackLabel.setEnabled(enabled);
        argumentLabelInside.setEnabled(enabled);

        frLayout.setEnabled(enabled);
        circleLayout.setEnabled(enabled);
        kkLayout.setEnabled(enabled);
        isomLayout.setEnabled(enabled);
        springLayout.setEnabled(enabled);

        picking.setEnabled(enabled);
        editing.setEnabled(enabled);
        transforming.setEnabled(enabled);

        groundedExtension.setEnabled(enabled);
        preferredExtension.setEnabled(enabled);
        stableExtension.setEnabled(enabled);
        
        sccs.setEnabled(enabled);
        split.setEnabled(enabled);
        splitHO.setEnabled(enabled);
        splitSW.setEnabled(enabled);
        splitMC.setEnabled(enabled);
        cluster.setEnabled(enabled);
        uncluster.setEnabled(enabled);
    }



    /**
     * Implements item listener for the layout menu
     */
    private class LayoutListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            JRadioButtonMenuItem mi = (JRadioButtonMenuItem)(e.getSource());
            boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
            mi.setSelected(selected);
            visualPanel.repaint();
            //cluster.setEnabled(true);
            //uncluster.setEnabled(false);
            
            if(visualizationViewer != null) {
                loadActions();
            }
        }

        public void loadActions() {

            if(circleLayout.isSelected()) {
                layout = new AggregateLayout(new CircleLayout(FrameworkViewer.getGraph()));
            }
            else if(frLayout.isSelected()) {
                layout = new AggregateLayout(new FRLayout(FrameworkViewer.getGraph()));
            }
            else if(isomLayout.isSelected()) {
                layout = new AggregateLayout(new ISOMLayout(FrameworkViewer.getGraph()));
            }
            else if(kkLayout.isSelected()) {
                layout = new AggregateLayout(new KKLayout(FrameworkViewer.getGraph()));
            }
            else if(springLayout.isSelected()) {
                layout = new AggregateLayout(new SpringLayout(FrameworkViewer.getGraph()));
            }
            visualizationViewer.setGraphLayout(layout);
        }

    }


    /**
     * Implements item listener and key adapter for the mode menu
     */
    private class ModeListener extends KeyAdapter implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            if(e.getItemSelectable() == editing) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    FrameworkViewer.getGraphMouse().setMode(Mode.EDITING);
                    editing.setSelected(true);
                }
            }
            else if(e.getItemSelectable() == picking) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    FrameworkViewer.getGraphMouse().setMode(Mode.PICKING);
                    picking.setSelected(true);
                }
            }
            else if(e.getItemSelectable() == transforming) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    FrameworkViewer.getGraphMouse().setMode(Mode.TRANSFORMING);
                    transforming.setSelected(true);
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            String letter = KeyEvent.getKeyText(e.getKeyCode());
            if(letter.equals("P") | letter.equals("p")) {
                picking.setSelected(true);
            }
            else if(letter.equals("T")) {
                transforming.setSelected(true);
            }
            else if(letter.equals("E")) {
                editing.setSelected(true);
            }}
    }


    /**
     * Specifies the current partition of arguments
     * @param argumentSets
     */
    public void setArgumentSets(ArrayList<ArrayList<Argument>> argumentSets) {
        this.argumentSets = argumentSets;
    }


    /**
     * Passes the current partition of arguments
     * @return argument sets
     */
    private ArrayList<ArrayList<Argument>> getArgumentSets() {
        return argumentSets;
    }


    /**
     * Implements action listener for the splitting menu
     */
    private class SplittingListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getActionCommand().equals("SCCs")) {
                if(resetExtension) {
                    splitting.resetFrameworkForSplitting();
                }
                if(resetParExtension) {
                    parameterizedSplitting.resetFrameworkForSplitting();
                }
                computeSCC();
                enableClustering(false);
            }
            else if(e.getActionCommand().equals("SPLIT S")) {
                if(resetExtension) {
                    splitting.resetFrameworkForSplitting();
                }
                if(resetParExtension) {
                    parameterizedSplitting.resetFrameworkForSplitting();
                }
                splitFramework();
                splitting.paint();
                splitting.setArgumentFill();
                enableClustering(true);
            }
            else if(e.getActionCommand().equals("SPLIT HO")) {
                if(resetExtension) {
                    splitting.resetFrameworkForSplitting();
                }
                if(resetParExtension) {
                    parameterizedSplitting.resetFrameworkForSplitting();
                }
                computeHaoOrlinMinCutForVisualization();
                parameterizedSplitting.paint();
                parameterizedSplitting.setArgumentFill();
                enableClustering(true);
            }
            else if(e.getActionCommand().equals("SPLIT SW")) {
                if(resetExtension) {
                    splitting.resetFrameworkForSplitting();
                }
                if(resetParExtension) {
                    parameterizedSplitting.resetFrameworkForSplitting();
                }
                computeMinCutForVisualization(); //TODO
                parameterizedSplitting.paint();
                parameterizedSplitting.setArgumentFill();
                enableClustering(true);
            }
            else if(e.getActionCommand().equals("SPLIT MC")) {
                if(resetExtension) {
                    splitting.resetFrameworkForSplitting();
                }
                if(resetParExtension) {
                    parameterizedSplitting.resetFrameworkForSplitting();
                }
                computeMidCutForVisualization();
                parameterizedSplitting.paint();
                parameterizedSplitting.setArgumentFill();
                enableClustering(true);
            }
            if(e.getActionCommand().equals("shift")) {
                frameworkViewer.cluster(getArgumentSets());
                visualPanel.repaint();
                enableClustering(true);
            }
            if(e.getActionCommand().equals("shift back")) {
                frameworkViewer.uncluster();
                visualPanel.repaint(); 
                enableClustering(true);
            }
        }

        private void enableClustering(boolean enabled) {
            cluster.setEnabled(enabled);
            uncluster.setEnabled(enabled);
        }

    }


    /**
     * Implements item listener for the options menu
     */
    private class OptionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            JCheckBoxMenuItem mi = (JCheckBoxMenuItem)(e.getSource());
            boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
            mi.setSelected(selected);
            visualPanel.repaint();
            
            if(visualizationViewer != null) {
                loadActions();
            }
        }

        public void loadActions() {

            if(showArgumentLabel.isSelected()) {
                frameworkViewer.renderArgumentLabel();
            }
            else {
                frameworkViewer.renderNoArgumentLabel();
            }
            if(argumentLabelInside.isSelected()) {
                frameworkViewer.argumentLabelInsideVertex();
            }
            else {
                frameworkViewer.argumentLabelOutsideVertex();
            }
            if(showAttackLabel.isSelected()) {
                frameworkViewer.renderAttackLabel();
            }
            else {
                frameworkViewer.renderNoAttackLabel();
            }
        }
    }


    /**
     * Implements action listener for the extensions menu
     */
    private class ExtensionsListener implements ActionListener, ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {

            if(e.getItemSelectable() == splittingOn) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    splittingOn.setSelected(true);
                    HOSplittingOn.setSelected(false);
                    SWSplittingOn.setSelected(false);
                    F2SplittingOn.setSelected(false);
                    midCutOn.setSelected(false);
                }
            }
            else if(e.getItemSelectable() == HOSplittingOn) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    HOSplittingOn.setSelected(true);
                    splittingOn.setSelected(false);
                    SWSplittingOn.setSelected(false);
                    F2SplittingOn.setSelected(false);
                    midCutOn.setSelected(false);
                }
            }
            else if(e.getItemSelectable() == SWSplittingOn) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    HOSplittingOn.setSelected(false);
                    splittingOn.setSelected(false);
                    SWSplittingOn.setSelected(true);
                    F2SplittingOn.setSelected(false);
                    midCutOn.setSelected(false);
                }
            }
            else if(e.getItemSelectable() == F2SplittingOn) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    HOSplittingOn.setSelected(false);
                    splittingOn.setSelected(false);
                    SWSplittingOn.setSelected(false);
                    F2SplittingOn.setSelected(true);
                    midCutOn.setSelected(false);
                }
            }
            else if(e.getItemSelectable() == midCutOn) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    HOSplittingOn.setSelected(false);
                    splittingOn.setSelected(false);
                    SWSplittingOn.setSelected(false);
                    F2SplittingOn.setSelected(false);
                    midCutOn.setSelected(true);
                }
            }
            visualPanel.repaint();
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {

            if(resetExtension) {
                splitting.resetFrameworkForSplitting();
            }
            if(resetParExtension) {
                parameterizedSplitting.resetFrameworkForSplitting();
            }
            long time = System.currentTimeMillis();
            
            if(e.getSource() == groundedExtension) {
                GroundedSemantics ge;
                if(!splittingOn.isSelected() && !HOSplittingOn.isSelected() && !SWSplittingOn.isSelected() && !F2SplittingOn.isSelected() && !midCutOn.isSelected()) {
                    Attack.resetAttackPaint();
                    Argument.resetArgumentDrawColor();
                    ge = new GroundedSemantics();
                    setOutput(ge.outputGroundedLabelling());
                    resetExtension = false;
                    resetParExtension = false;
                    long computationTime = System.currentTimeMillis() - time;
                    setTimeWithoutSplittingLabel(Long.toString(computationTime) + " / " + ge.getNumberOfSteps());
                }
                else {
                    if(splittingOn.isSelected()) {
                        splitForComputation(false);
                        ge = new GroundedSemantics(splitting);
                        setOutput(ge.outputGroundedLabellingThroughSplitting());
                        resetExtension = true;
                        long totalTime = System.currentTimeMillis() - time;
                        setRegularSplittingTimeLabel(Long.toString(totalTime) + " / " + ge.getNumberOfSteps());
                        splitting.paint();
                    }
                    else {
                        if(HOSplittingOn.isSelected()) {
                            setHOSplittingTimeLabel("not supported");
                        }
                        else {
                            if(SWSplittingOn.isSelected()) {
                                setSWSplittingTimeLabel("not supported");
                            }
                            else if(F2SplittingOn.isSelected()) {
                                setF2SplittingTimeLabel("not supported");
                            }
                            else if(midCutOn.isSelected()) {
                                setMidCutTimeLabel("not supported");
                            }
                        }
                    } 
                }                 
                visualPanel.repaint();
            }
            if(e.getSource() == stableExtension) {
                StableSemantics se;
                if(!splittingOn.isSelected() && !HOSplittingOn.isSelected() && !SWSplittingOn.isSelected() && !F2SplittingOn.isSelected() && !midCutOn.isSelected()) {
                    Attack.resetAttackPaint();
                    Argument.resetArgumentDrawColor();
                    se = new StableSemantics();
                    setOutput(se.outputStableLabellings());
                    resetExtension = false;
                    resetParExtension = false;
                    long computationTime = System.currentTimeMillis() - time;
                    setTimeWithoutSplittingLabel(Long.toString(computationTime) + " / " + se.getNumberOfSteps());
                }
                else {
                    if(splittingOn.isSelected()) {
                        splitForComputation(false);
                        se = new StableSemantics(splitting);
                        setOutput(se.outputStableLabellingsThroughSplitting());
                        resetExtension = true;
                        long totalTime = System.currentTimeMillis() - time;
                        setRegularSplittingTimeLabel(Long.toString(totalTime) + " / " + se.getNumberOfSteps());
                        splitting.paint();
                    }
                    else {
                        if(HOSplittingOn.isSelected()) {
                            computeHaoOrlinMinCut();
                            time2MinCut.setText(Long.toString(System.currentTimeMillis() - time));
                            se = new StableSemantics(parameterizedSplitting);
                            setOutput(se.outputStableLabellingsThroughSplitting());
                            resetParExtension = true;
                            long totalTime = System.currentTimeMillis() - time;
                            setHOSplittingTimeLabel(Long.toString(totalTime) + " / " + se.getNumberOfSteps());
                            parameterizedSplitting.paint();
                        }
                        else {
                            if(SWSplittingOn.isSelected()) {
                                computeMinCut();
                                timeMinCut.setText(Long.toString(System.currentTimeMillis() - time));
                                se = new StableSemantics(parameterizedSplitting);
                                setOutput(se.outputStableLabellingsThroughParSplitting());
                                long totalTime = System.currentTimeMillis() - time;
                                setSWSplittingTimeLabel(Long.toString(totalTime) + " / " + se.getNumberOfSteps());
                                resetParExtension = true;
                                parameterizedSplitting.paint();
                            }
                            else {
                                if(F2SplittingOn.isSelected()) {
                                    if(doubleFramework) {
                                        parameterizedSplitting = new ParameterizedSplitting(frameworkViewer.getFrameworkA(), frameworkViewer.getSplitAttacks());
                                        setArgumentSets(parameterizedSplitting.getPartition());
                                        se = new StableSemantics(parameterizedSplitting);
                                        setOutput(se.outputStableLabellingsThroughParSplitting());
                                        long totalTime = System.currentTimeMillis() - time;
                                        setF2SplittingTimeLabel(Long.toString(totalTime) + " / " + se.getNumberOfSteps());
                                        resetParExtension = true;
                                        parameterizedSplitting.paint();
                                    }
                                    else {
                                        setF2SplittingTimeLabel("not supported");
                                    }
                                }
                                else {
                                    if(midCutOn.isSelected()) {
                                        MidCut midcut = new MidCut();
                                        parameterizedSplitting = new ParameterizedSplitting(midcut.getFrameworkA(), true);
                                        setArgumentSets(parameterizedSplitting.getPartition());
                                        se = new StableSemantics(parameterizedSplitting);
                                        setOutput(se.outputStableLabellingsThroughParSplitting());
                                        long totalTime = System.currentTimeMillis() - time;
                                        setMidCutTimeLabel(Long.toString(totalTime) + " / " + se.getNumberOfSteps());
                                        resetParExtension = true;
                                        parameterizedSplitting.paint();
                                    }
                                }
                            }
                        }
                    }
                }
                visualPanel.repaint();
            }
            if(e.getSource() == preferredExtension) {
                PreferredSemantics pe;
                if(!splittingOn.isSelected() && !HOSplittingOn.isSelected() && !SWSplittingOn.isSelected() && !F2SplittingOn.isSelected() && !midCutOn.isSelected()) {
                    Attack.resetAttackPaint();
                    Argument.resetArgumentDrawColor();
                    pe = new PreferredSemantics();
                    setOutput(pe.outputPreferredExtensions());
                    resetExtension = false;
                    resetParExtension = false;
                    long computationTime = System.currentTimeMillis() - time;
                    setTimeWithoutSplittingLabel(Long.toString(computationTime) + " / " + pe.getNumberOfSteps());
                }
                else {
                    if(splittingOn.isSelected()) {
                        splitForComputation(false);
                        pe = new PreferredSemantics(splitting);
                        setOutput(pe.outputPreferredExtensionsThroughSplitting());
                        resetExtension = true;
                        long totalTime = System.currentTimeMillis() - time;
                        setRegularSplittingTimeLabel(Long.toString(totalTime) + " / " + pe.getNumberOfSteps());
                        splitting.paint();
                    }
                    else {
                        if(HOSplittingOn.isSelected()) {
                        setHOSplittingTimeLabel("not supported");
                        }
                        else {
                            if(SWSplittingOn.isSelected()) {
                                setSWSplittingTimeLabel("not supported");
                            }
                            else if(F2SplittingOn.isSelected()) {
                                setF2SplittingTimeLabel("not supported");
                            }
                            else if(midCutOn.isSelected()) {
                                setMidCutTimeLabel("not supported");
                            }
                        }
                    }
                }
                visualPanel.repaint();
            }
            splittingListener.enableClustering(false);
        }

    }

    /**
     * Implements action listener for the help menu
     */
    private class HelpListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) { }
            JDialog.setDefaultLookAndFeelDecorated(true);

            if(e.getActionCommand().equals("Instructions")) {
                JOptionPane.showMessageDialog(null, HelpInstructions.getHelpInstructions());
            }
            else if(e.getActionCommand().equals("Reading of results")) {
                JOptionPane.showMessageDialog(null, HelpInstructions.getMarkingInformation());
            }
            else if(e.getActionCommand().equals("*.aaf file specification")) {
                JOptionPane.showMessageDialog(null, HelpInstructions.getAafFileSpecification());
            }
            else if(e.getActionCommand().equals("*.net file specification")) {
                JOptionPane.showMessageDialog(null, HelpInstructions.getNetFileSpecification());
            }
            else if(e.getActionCommand().equals("About")) {
                JOptionPane.showMessageDialog(null, HelpInstructions.getAboutInformation());
            }
        }

    }


    /**
     * Prompts the user for confirmation on closing the editor if the framework has at least one argument
     */
    private void exitCheck() {

        if(visualizationViewer != null) {
            int value = visualizationViewer.getGraphLayout().getGraph().getVertexCount();
            if(value != 0) {
                int dialog = JOptionPane.showConfirmDialog(null, "The framework will be deleted. Exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if(dialog == 0) {
                    System.exit(0);
                }
            }
            else {
                System.exit(0);
            }
        }
        else {
            System.exit(0);
        }
    }

}
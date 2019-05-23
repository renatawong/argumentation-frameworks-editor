
package panel;



/**
 * Class containing help information
 * @author Renata Wong
 */
public class HelpInstructions {

    /**
     * Contains explanations of operations on Argumentation Frameworks
     */
    private static String instructions =
            "<html>"+
            "<h3>Operations on AF:</h3>"+
            "<ol>"+
            "<li>transforming mode: "+
            "<ul>"+
            "<li>left mouse button down and drag to PAN</li>"+
            "<li>shift + left mouse button down and drag to ROTATE</li>"+
            "<li>control + left mouse button down and drag to SHEAR</li>"+
            "<li>mouse wheel to ZOOM</li>"+
            "</ul>"+
            "</li>"+
            "<li>all modes: "+
            "<ul>"+
            "<li>click on the framework, then press <i>p</i> for PICKING mode, <i>t</i> for TRANSFORMING and <i>e</i> for EDITING</li>"+
            "</ul>"+
            "</li>"+
            "</ol>"+
            "<h3>Operations on Arguments and Attacks:</h3>"+
            "<ol>"+
            "<li>editing mode: "+
            "<ul>"+
            "<li>click on the framework to CREATE AN ARGUMENT"+
            "<li>shift + left mouse button down on an Argument and drag to another Argument to CREATE AN ATTACK"+
            "<li>right mouse button click on an Argument for DELETE VERTEX popup"+
            "<li>right mouse button click on an Attack for DELETE EDGE popup"+
            "</ul>"+
            "</li><li>"+
            "picking mode:"+
            "<ul>"+
            "<li>select an Argument (Arguments) and right mouse button click for CREATE DIRECTED EDGE menu"+
            "<li>select an Argument (Arguments) and drag to CHANGE POSITION"+
            "</ul>"+
            "</li><li>"+
            "all modes: "+
            "<ul>"+
            "<li>right mouse button click for CREATE VERTEX popup"+
            "<li>right mouse button click on an Argument for DELETE VERTEX popup"+
            "<li>right mouse button click on an Attack for DELETE EDGE popup"+
            "</ul>"+
            "</li></ol>"+
            "</html>";

    /**
     * Returns information about operations on argumentation frameworks
     * @return information about operations as String
     */
    public static String getHelpInstructions() {
        return instructions;
    }

    /**
     * Contains the specification of aaf format
     */
    private static String AafFileSpecification =
            "<html>"+
            "<h3>The *.aaf file extension is specified as follows:</h3>"+
            "only elements in black belong to the code<br><br>"+
            "<p><b>ARGUMENT SPECIFICATION</b>"+
            "<br>"+
            "<font face=Courier New>argument_ID</font> <font color=gray><i>TABULATOR</i></font> "
            + "argument_NAME <font color=gray><i>NEW LINE</i></font></p>"+
            "<p>&nbsp;</p>"+
            "<p><b>ATTACK SPECIFICATION</b>"+
            "<br>"+
            "attacker_ID <font color=gray><i>TABULATOR</i></font> attackedArg1_ID "
            + "<font color=gray><i>TABULATOR</i></font> attackedArg2_ID ...  "
            + "<font color=gray><i>NEW LINE</i></font></p>"+
            "<p>&nbsp;</p>"+
            "<p><b>VERTEX LOCATION SPECIFICATION</b>"+
            "<br>"+
            "coord_X <font color=gray><i>TABULATOR</i></font> coord_Y <font color=gray><i>TABULATOR</i></font> "
            + "argument_ID <font color=gray><i>NEW LINE</i></font></p>"+
            "<br>"+
            "<br>"+
            "<p>Example:</p>"+
            "<br>"+
            "<p bgcolor=white>0 &nbsp;&nbsp;&nbsp; 0<br>"+
            "1 &nbsp;&nbsp;&nbsp; a<br>"+
            "2 &nbsp;&nbsp;&nbsp; b<br>"+
            "*<br>"+
            "0 &nbsp;&nbsp;&nbsp; 2<br>"+
            "1 &nbsp;&nbsp;&nbsp; 0 &nbsp;&nbsp;&nbsp; 1<br>"+
            "2<br>"+
            "<p bgcolor=white>*<br>"+
            "20 &nbsp;&nbsp;&nbsp; 302 &nbsp;&nbsp;&nbsp; 0<br>"+
            "34.67453907 &nbsp;&nbsp;&nbsp; 3 &nbsp;&nbsp;&nbsp; 1<br>"+
            "13.8 &nbsp;&nbsp;&nbsp; 78.9 &nbsp;&nbsp;&nbsp; 2<br>"+
            "<p bgcolor=white>*</p>"+
            "</html>";


    /**
     * Returns the specification of the aaf format
     * @return specification of the aaf format
     */
    public static String getAafFileSpecification() {
        return AafFileSpecification;
    }


    /**
     * Contains the specification of the net format
     */
    private static String NetFileSpecification =
            "<html>"+
            "<h3>The *.net file extension is specified as follows:</h3>"+
            "only elements in black belong to the code<br><br>"+
            "<p><b>ARGUMENT SPECIFICATION</b>"+
            "<br>"+
            "<p><font face=Courier New>argument1_ID</font> <font color=gray>(nonzero) <i>SPACE</i></font> "
            + "argument1_NAME <font color=gray><i>NEW LINE</i></font></p>"+
            "<p>&nbsp;</p>"+
            "<p><b>ATTACK SPECIFICATION</b>"+
            "<br>"+
            "attacker_ID <font color=gray>(nonzero) <i>SPACE</i></font> attackedArg1_ID "
            + "<font color=gray><i>SPACE</i></font> attackedArg2_ID <font color=gray>...  <i>NEW LINE</i></font></p>"+
            "<br>"+
            "<br>"+
            "<p>Example:</p>"+
            "<br>"+
            "<p bgcolor=white>*Vertices 3<br>"+
            "1 &nbsp;&nbsp;&nbsp; \"0\"<br>"+
            "2 &nbsp;&nbsp;&nbsp; \"a\"<br>"+
            "3 &nbsp;&nbsp;&nbsp; \"b\"<br>"+
            "*Arcslist<br>"+
            "1 &nbsp;&nbsp;&nbsp; 2<br>"+
            "2 &nbsp;&nbsp;&nbsp; 0 &nbsp;&nbsp;&nbsp; 1<br>"+
            "3</p>"+
            "</html>";

    
    /**
     * Returns the specification of the net format
     * @return specification of the net format
     */
    public static String getNetFileSpecification() {
        return NetFileSpecification;
    }


    
    /**
     * Contains explanations on reading of results
     */
    private static String marking =
            "<html>"+
            "<h3>Reading of results: </h3>"+
            "<ol>"+
            "<li>Framework: "+
            "<ul>"+
            "<li><font color=#FFD700><b>ORANGE: </b></font> Arguments belonging to an IN set"+"</li>"+
            "<li><font color=green><b>GREEN: </b></font> Arguments belonging to an OUT set<br>and loop attacks added during splitting"+"</li>"+
            "<li><font color=blue><b>BLUE: </b></font> Arguments belonging to a UNDEC set"+"</li>"+
            "<li><font color=red><b>RED: </b></font> Arguments belonging to none of the sets IN, OUT or UNDEC<br>"
            + "and attacks removed from computation after splitting"+"</li>"+
            "<li><font color=white><b>WHITE: </b></font> Arguments removed from computation during splitting"+"</li>"+
            "<li><font color=gray><b>GRAY: </b></font> Attacks which partitioned the framework after splitting</li>"+
            "</ul></li><li>"+
            "Labellings: "
            + "<ul>"
            + "<li><b>TIME w/o: </b>Runtime in ms / Number of steps without Splitting"+"</li>"
            + "<li><b>TIME w/: </b>Runtime in ms / Number of steps with Splitting"+"</li>"+
            "</ul></li><li>"+
            "Splitting :"
            + "<ul><li><b>INITIAL: </b>Number of initial arguments"+"</li>"
            + "<li><b>NONINITIAL: </b>Number of non-initial arguments"+"</li>"
            + "<li><b>ATTACKS: </b>Number of splitting attacks</li>"+
            "</ul></li></ol>"+
            "</html>";

    
    /**
     * Returns information about reading the results
     * @return information about interpretation of results
     */
    public static String getMarkingInformation() {
        return marking;
    }

    
    /**
     * Contains general information about the software
     */
    private static String about =
            "<html>"+
            "<p><b>Project:</b> Master's thesis"+"<br>"+
            "<b>Subject:</b> Parameterized Splitting within Stable Semantics for the Dung-style Argumentation Frameworks: An Implementation"+"<br>"+
            "<b>Copyright 2013 Renata Wong</b>"+"<br>"+
            "<b>Homepage: </b> <a href='http://wwwstud.rz.uni-leipzig.de/~bss01gsc'>http://wwwstud.rz.uni-leipzig.de/~bss01gsc</a></b>"+"<br>"+
            
            "<b>Version:</b> 2.0</p>"+
            "<br>"+
            "<p><b>Acknowledgments:</b><br>"+
            "The graphic representation of frameworks is based on BSD-licensed <br>Java Universal Network/Graph Framework (JUNG) libraries.<br>"+
            "The .aaf file format specification by courtesy of Jochen Tiepmar.</p>"+
            "</html>";

    
    
    /**
     * Returns general inforamtion about the software
     * @return author, version, project information
     */
    public static String getAboutInformation() {
        return about;
    }


}

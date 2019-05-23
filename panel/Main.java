
package panel;


/**
 * Creates an instance of FrameworkPanel
 *
 * @author Renata Wong
 */


public class Main {

    /**
     * Creates an instance of FrameworkPanel
     */
    public static void main(String[] args){

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameworkPanel fp = new FrameworkPanel();
            }
        });
    }
}
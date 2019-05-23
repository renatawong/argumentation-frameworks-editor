
package io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import visualization.FrameworkViewer;

/**
 *
 * @author Renata Wong
 */
public class RandomAttackDialog implements ActionListener {

    private JDialog dialog;
    private final JTextField AtoB;
    private final JTextField BtoA;
    private final JLabel message;
    private final JButton addAttacks;
    private final JButton cancel;
    private int AattB;
    private int BattA;
    private int argumentCountA;
    private int argumentCountB;
    private boolean valid = false;
    private FrameworkViewer frameworkViewer;
    private int frA;
    private int frB;
    
    public RandomAttackDialog(int frA, int frB) {
        
        this.frA = frA;
        this.frB = frB;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        JDialog.setDefaultLookAndFeelDecorated(true);

        dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle("Adding split attacks");
        
        dialog.getContentPane().setLayout(null);
        dialog.setSize(new Dimension(300,170));
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JLabel sAtt = new JLabel("split attacks:");
        sAtt.setBounds(15,15,100,20);
        dialog.getContentPane().add(sAtt);
        
        JLabel ab = new JLabel("A to B: ");
        ab.setBounds(90,15,50,20);
        dialog.getContentPane().add(ab);
        
        AtoB = new JTextField("");
        AtoB.setBounds(90,35,80,18);
        dialog.getContentPane().add(AtoB);
        
        JLabel ba = new JLabel("B to A:");
        ba.setBounds(190,15,50,20);
        dialog.getContentPane().add(ba);
        
        BtoA = new JTextField("");
        BtoA.setBounds(190,35,80,18);
        dialog.getContentPane().add(BtoA);
        
        message = new JLabel("adding new attacks will delete existing attacks");
        message.setBounds(15,70,280,20);
        message.setForeground(new Color(220,20,60));
        dialog.getContentPane().add(message);
        
        addAttacks = new JButton("add attacks");
        addAttacks.setBounds(15,100,140,25);
        addAttacks.setActionCommand("add");
        
        addAttacks.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSplitAttacks();
            }
        });

        dialog.getContentPane().add(addAttacks);
        dialog.getRootPane().setDefaultButton(addAttacks);

        cancel = new JButton("cancel");
        cancel.setBounds(170,100,100,25);

        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               valid = false;
               dialog.setVisible(false);
               dialog.dispose();
            }
        });

        dialog.getContentPane().add(cancel);
        dialog.setVisible(true);
    }
    
    
    private void checkSplitAttacks() {
        
        AattB = validateInput(AtoB.getText());
        BattA = validateInput(BtoA.getText());
        int allowed = frA * frB;
        if(AattB <= allowed) {
            if(BattA <= allowed) {
                valid = true;
                message.setText("");
                dialog.setVisible(false);
            }
            else {
                message.setText("maximum number of attacks from B to A: " + allowed);
            }
        }
        else {
            message.setText("maximum number of attacks from A to B: " + allowed);
        }
    }
    
    /**
     * Validation of user input
     * @param string the input
     * @return 0 if format incorrect or no input, length of the input otherwise
     */
    private int validateInput(String string) {
        String inputValue = string;
        int parsedInputValue = 0;
        if(inputValue.length() == 0) {
            message.setText("a value is missing");
        }
        else {
            try {
                parsedInputValue = Integer.parseInt(inputValue);
            } catch (NumberFormatException nfe) {
                message.setText("please input numerical values only");
                parsedInputValue = 0;
            }
        }
        return parsedInputValue;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Returns the number of attacks
     * @return number of attacks
     */
    public int getAattB() {
        return AattB;
    }
    
    public int getBattA() {
        return BattA;
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) { }
    
    
}

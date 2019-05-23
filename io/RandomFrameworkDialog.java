
package io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Dialog for user input of argument and attack number for random framework
 * @author Renata Wong
 */
public class RandomFrameworkDialog implements ActionListener {

    private JDialog dialog;

    private static JLabel arguments;
    private static JLabel attacks;
    private JTextField argumentInputA;
    private JTextField argumentInputB;
    private JTextField attackInputA;
    private JTextField attackInputB;
    private JTextField AtoB;
    private JTextField BtoA;
    private int attackCountA;
    private int attackCountB;
    private int argumentCountA;
    private int argumentCountB;
    private int AattB;
    private int BattA;
    private JLabel message;
    private boolean singleFramework = true;

    private JButton createFramework;
    private JButton cancel;
    private boolean valid = false;



    /**
     * Constructor: creates a new dialog for random graph values
     */
    public RandomFrameworkDialog() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        JDialog.setDefaultLookAndFeelDecorated(true);

        dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle("Specify input for a random framework");

        dialog.getContentPane().setLayout(null);
        dialog.setSize(new Dimension(300,270));
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel fA = new JLabel("Framework A:");
        fA.setBounds(90,15,80,20);
        dialog.getContentPane().add(fA);
        
        JLabel fB = new JLabel("Framework B:");
        fB.setBounds(190,15,80,20);
        dialog.getContentPane().add(fB);
        
        
        arguments = new JLabel("arguments: ");
        arguments.setBounds(15,45,80,20);
        dialog.getContentPane().add(arguments);

        argumentInputA = new JTextField("");
        argumentInputA.setBounds(90,45,80,18);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
             public void windowOpened(WindowEvent e) {
                 argumentInputA.requestFocus();
                 argumentInputA.setNextFocusableComponent(attackInputA);
                 attackInputA.setNextFocusableComponent(argumentInputB);
                 argumentInputB.setNextFocusableComponent(attackInputB);
                 attackInputB.setNextFocusableComponent(AtoB);
                 AtoB.setNextFocusableComponent(BtoA);
                 BtoA.setNextFocusableComponent(argumentInputA);
             }
         });

        dialog.getContentPane().add(argumentInputA);
                
        argumentInputB = new JTextField("");
        argumentInputB.setBounds(190,45,80,18);
        dialog.getContentPane().add(argumentInputB);

        attacks = new JLabel("attacks: ");
        attacks.setBounds(15,65,80,20);
        dialog.getContentPane().add(attacks);

        attackInputA = new JTextField("");
        attackInputA.setBounds(90,65,80,18);
        dialog.getContentPane().add(attackInputA);
        
        attackInputB = new JTextField("");
        attackInputB.setBounds(190,65,80,18);
        dialog.getContentPane().add(attackInputB);

        JLabel sAtt = new JLabel("split attacks:");
        sAtt.setBounds(15,105,100,20);
        dialog.getContentPane().add(sAtt);
        
        JLabel ab = new JLabel("A to B: ");
        ab.setBounds(90,105,50,20);
        dialog.getContentPane().add(ab);
        
        AtoB = new JTextField("");
        AtoB.setBounds(90,125,80,18);
        dialog.getContentPane().add(AtoB);
        
        JLabel ba = new JLabel("B to A:");
        ba.setBounds(190,105,50,20);
        dialog.getContentPane().add(ba);
        
        BtoA = new JTextField("");
        BtoA.setBounds(190,125,80,18);
        dialog.getContentPane().add(BtoA);
        
        message = new JLabel("");
        message.setBounds(15,170,280,20);
        message.setForeground(new Color(220,20,60));
        dialog.getContentPane().add(message);
        
        createFramework = new JButton("create framework");
        createFramework.setBounds(15,200,140,25);
        createFramework.setActionCommand("create");

        createFramework.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                argumentCountA = validateInput(argumentInputA.getText());
                attackCountA = validateInput(attackInputA.getText());

                if(attackCountA > argumentCountA * argumentCountA) {            
                    setMessage("maximum number of attacks for A: " + argumentCountA * argumentCountA);
                }
                else if(argumentCountA >= 1 && attackCountA >= 0 && attackCountA > argumentCountA * argumentCountA) {
                    setMessage("cannot process the inputs for framework A");
                }
                else if(argumentCountA >= 1 && attackCountA >= 0 && attackCountA <= argumentCountA * argumentCountA) {
                    checkFrameworkB();
                }
                else if(argumentCountA == 0 && attackCountA == 0) {
                    setMessage("at least 1 argument is required to create framework A");
                }
            }
        });

        dialog.getContentPane().add(createFramework);
        dialog.getRootPane().setDefaultButton(createFramework);

        cancel = new JButton("cancel");
        cancel.setBounds(170,200,100,25);

        cancel.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               setValid(false);
               dialog.setVisible(false);
               dialog.dispose();
            }
        });

        dialog.getContentPane().add(cancel);
        dialog.setVisible(true);

    }


    private void checkFrameworkB() {
        argumentCountB = validateInput(argumentInputB.getText());
        attackCountB = validateInput(attackInputB.getText());

        if(attackCountB > argumentCountB * argumentCountB) {
            setMessage("maximum number of attacks for B: " + argumentCountB * argumentCountB);
        }
        else if(argumentCountB >= 1 && attackCountB >= 0 && attackCountB > argumentCountB * argumentCountB) {
            setMessage("cannot process the inputs for framework B");
        }
        else if(argumentCountB >= 1 && attackCountB >= 0 && attackCountB <= argumentCountB * argumentCountB) {
            //here check of the values of splitting attacks is needed
            checkSplitAttacks();
        }
        else if(argumentCountB == 0 && attackCountB == 0) {//fine if the splitting attacks are also 0 and 0
            if(validateInput(AtoB.getText()) == 0 && validateInput(BtoA.getText()) == 0) {
                setValid(true);
                setMessage("");
                singleFramework = true;
                dialog.setVisible(false);
            }
            else {
                message.setText("no split attacks allowed because B is empty");
            }
        }
    }
    
    
    private void checkSplitAttacks() {
        
        AattB = validateInput(AtoB.getText());
        BattA = validateInput(BtoA.getText());
        int allowed = argumentCountA * argumentCountB;
        if(AattB <= allowed) {
            if(BattA <= allowed) {
                setValid(true);
                setMessage("");
                singleFramework = false;
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
    
    
    private void setMessage(String string) {
        message.setText(string);
    }
    
    public boolean isSingleFramework() {
        return singleFramework;
    }
    
    public boolean existSplitAttacks() {
        if(AattB > 0 || BattA > 0) {
            return true;
        }
        else return false;
    }
    
    public int getAattB() {
        return AattB;
    }
    
    public int getBattA() {
        return BattA;
    }


    /**
     * Validation of user input
     * @param string the input
     * @return -1 if format incorrect or no input, length of the input otherwise
     */
    private int validateInput(String string) {
        String inputValue = string;
        int parsedInputValue = 0;
        if(inputValue.length() == 0) {
            setMessage("a value is missing");
        }
        else {
            try {
                parsedInputValue = Integer.parseInt(inputValue);
            } catch (NumberFormatException nfe) {
                setMessage("please input numerical values only");
                parsedInputValue = 0;
            }
        }
        return parsedInputValue;
    }


    /**
     * Returns the number of arguments
     * @return number of arguments
     */
    public int getArgumentCountA() {
        return argumentCountA;
    }
    
    public int getArgumentCountB() {
        return argumentCountB;
    }


    /**
     * Returns the number of attacks
     * @return number of attacks
     */
    public int getAttackCountA() {
        return attackCountA;
    }
    
    public int getAttackCountB() {
        return attackCountB;
    }


    /**
     * Sets the validity of the input
     * @param valid true for valid, false for not valid
     */
    private void setValid(boolean valid) {
        this.valid = valid;
    }


    /**
     * Gives the information whether an input is valid
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return this.valid;
    }

    
    @Override
    public void actionPerformed(ActionEvent e) { }

}

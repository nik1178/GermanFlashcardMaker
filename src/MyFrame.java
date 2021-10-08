import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import lc.kra.system.keyboard.event.*;
import lc.kra.system.keyboard.*;

public class MyFrame extends JFrame {
    
    static boolean run = true;
    MyFrame(){

        //this.addKeyListener(this);
        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyReleased(GlobalKeyEvent e) {

                if(!run){
                    if (e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                        run = true;
                    }
                } else{
                    if (e.getVirtualKeyCode() == GlobalKeyEvent.VK_ESCAPE) {
                        run = false;
                    }
                }
                if(!run) return;


                /* System.out.println(e.getVirtualKeyCode()); */
                if(e.getVirtualKeyCode()==13){
                    try{
                        new MakeFlashcard();
                    }catch(Exception ex){
                        System.out.println("Something went wrong when creating the flashcard.");
                    }
                }
                
            }
        });

        this.setSize(0,0);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

}
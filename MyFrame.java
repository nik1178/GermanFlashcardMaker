import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class MyFrame extends JFrame implements KeyListener{
    
    static String gerWord;
    static String sloWord;
    MyFrame(){

        this.addKeyListener(this);

        this.setSize(0,0);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        System.out.println(e.getKeyCode());
        if(e.getKeyCode()==10){
            makeFlashcard();
        }
    }

    int mouseStartX, mouseStartY;
    int mouseEndX, mouseEndY;
    public void  makeFlashcard(){
        System.out.println("\n\n\n\nNew word---------------------------------");
        PointerInfo pointer = java.awt.MouseInfo.getPointerInfo();
        mouseStartX = (int)pointer.getLocation().getX();
        mouseStartY = (int)pointer.getLocation().getY();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mouseEndX = (int) screenSize.getWidth() - mouseStartX;
        mouseEndY = mouseStartY;

        //copyToClipboard();
        
        sloWord = getFromClipboard();
        if(sloWord.equals("")){
            System.out.println("Failed to make flashcard");
            return;
        }

        sloWord = removeSumniki(sloWord);
        System.out.println("SloWord: " + sloWord);
        gerWord = TranslateWord.translateWord(sloWord);
        System.out.println(gerWord);
        TranslateWord.foldWord();

        pasteFromClipboard();
    }

    String clipboardResult = "";
    String getFromClipboard(){
        int repCounter = 0;
        while(repCounter<10){
            try{
                Thread.sleep(100);
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                String result = (String) clipboard.getData(DataFlavor.stringFlavor);
                if(result.length()<20) return result;
                else {
                    System.out.println("Read wrong result from clipboard");
                }
            }catch(Exception e){
                System.out.println("Couldn't read from clipboard");
                System.out.println(e);
            }
            repCounter++;
        }
        return "";
    }

    public String removeSumniki(String sloWord){
        for(int currentCharIndex=0; currentCharIndex<sloWord.length(); currentCharIndex++){
            sloWord = sloWord.toLowerCase();
            char currentChar = sloWord.charAt(currentCharIndex);
            if(currentChar=='č'){
                sloWord = sloWord.substring(0, currentCharIndex) + "%C4%8D" + sloWord.substring(currentCharIndex+1);
            }
            if(currentChar=='š'){
                sloWord = sloWord.substring(0, currentCharIndex) + "%C5%A1" + sloWord.substring(currentCharIndex+1);
            }
            if(currentChar=='ž'){
                sloWord = sloWord.substring(0, currentCharIndex) + "%C5%BE" + sloWord.substring(currentCharIndex+1);
            }
        }
        return sloWord;
    }

    public void copyToClipboard(){
        try{
            Robot robot = new Robot();
            //triple click to select word --> one click to focus word, another two to select
            clickLeftMouse(3);
            Thread.sleep(400);

            //ctrl+c to copy word
            pressCopyButtons();

            Thread.sleep(200);
            

        }catch(Exception e){
            System.out.println("Couldn't get slo word");
        }
    }
    void clickLeftMouse(int amount){
        try{
            Robot robot = new Robot();
            for(int i=0; i<amount; i++){
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            }
        }catch(Exception e){
            System.out.println("Failed pressing left mouse button");
        }
    }
    void pressCopyButtons(){
        try{
            Robot robot = new Robot();
            robot.keyPress(17);
            Thread.sleep(20);
            robot.keyPress(67);
            Thread.sleep(20);
            robot.keyRelease(67);
            Thread.sleep(20);
            robot.keyRelease(17);
        }catch(Exception e){
            System.out.println("Failed pressing copy buttons");
        }
    }

    public void pasteFromClipboard(){
        try{
            Robot robot = new Robot();

            //move to other side of screen
            robot.mouseMove(mouseEndX, mouseEndY);
            Thread.sleep(20);

            //click once
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            Thread.sleep(20);

            //ctrl+v to paste result
            robot.keyPress(17);
            robot.keyPress(86);
            robot.keyRelease(17);
            robot.keyRelease(86);

        }catch(Exception e){
            System.out.println("Couldn't paste from clipboard");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}

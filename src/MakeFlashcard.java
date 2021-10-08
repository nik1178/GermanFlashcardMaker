import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.datatransfer.*;
import lc.kra.system.keyboard.event.*;
import lc.kra.system.keyboard.*;

public class MakeFlashcard {
    
    static String gerWord;
    static String sloWord;
    int mouseStartX, mouseStartY;
    int mouseEndX, mouseEndY;
    public MakeFlashcard(){
        pressCtrlplus('z');
        pressCtrlplus('c');
        
        System.out.println("\n\n\n\nNew word---------------------------------");
        PointerInfo pointer = java.awt.MouseInfo.getPointerInfo();
        mouseStartX = (int)pointer.getLocation().getX();
        mouseStartY = (int)pointer.getLocation().getY();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mouseEndX = (int) screenSize.getWidth() - mouseStartX;
        mouseEndY = mouseStartY;

        //copyToClipboard();
        
        if(mouseStartX<screenSize.getWidth()/2){
            sloWord = getFromClipboard();
            if(sloWord.equals("")){
                System.out.println("Failed to make flashcard");
                return;
            }

            sloWord = removeSumniki(sloWord);
            System.out.println("SloWord: " + sloWord);
            gerWord = TranslateWord.translateWord(sloWord);
        }else{
            pressCtrlplus('y');
            pressBackspace();
            mouseEndX = mouseStartX;
            gerWord = getFromClipboard();
            System.out.println("Gor ger word: " + gerWord);
        }


        System.out.println(gerWord);
        TranslateWord.foldWord(gerWord);

        pasteFromClipboard();
        moveMouse(mouseStartX, mouseStartY);

    }

    String clipboardResult = "";
    String getFromClipboard(){
        int repCounter = 0;
        while(repCounter<1){
            try{
                Thread.sleep(100);
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                String result = (String) clipboard.getData(DataFlavor.stringFlavor);
                if(result.length()<20) return result;
                else {
                    System.out.println("Read wrong result from clipboard: " + result.substring(0, 10) + "...");
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
            pressCtrlplus('c');

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
    void pressCtrlplus(char letter){
        String tempString = letter+"";
        tempString = tempString.toUpperCase();
        letter = tempString.charAt(0);
        System.out.println(letter+ " <----this");
        try{
            Robot robot = new Robot();
            robot.keyPress(17);
            Thread.sleep(20);
            robot.keyPress((int) letter);
            Thread.sleep(20);
            robot.keyRelease((int) letter);
            Thread.sleep(20);
            robot.keyRelease(17);
        }catch(Exception e){
            System.out.println("Failed pressing ctrl + letter");
        }
    }

    public void pasteFromClipboard(){
        try{
            Robot robot = new Robot();

            //move to other side of screen
            moveMouse(mouseEndX, mouseEndY);
            Thread.sleep(20);

            //click once
            clickLeftMouse(1);
            Thread.sleep(20);

            //ctrl+v to paste result
            pressCtrlplus('v');

        }catch(Exception e){
            System.out.println("Couldn't paste from clipboard");
        }
    }

    public void moveMouse(int x, int y){
        try{
            Robot robot = new Robot();
            robot.mouseMove(x, y);

        }catch(Exception e){
            System.out.println("Couldn't paste from clipboard");
        }
    }

    public void pressBackspace(){
        try{
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_BACK_SPACE);
            robot.keyRelease(KeyEvent.VK_BACK_SPACE);
        }catch(Exception e){
            System.out.println("Failed pressing backspace");
        }
    }
}

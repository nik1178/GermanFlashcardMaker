import java.net.*;  
import java.io.*;  
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;

public class TranslateWord{  

    TranslateWord(){
        
    }

    static String foldWord(){
        String siteURL = "https://konjugator.reverso.net/konjugation-deutsch-verb-" + gerWord + ".html";
        String output  = getUrlContents(siteURL); 
        printToFile(output);
        searchForFold(output);

        return "";
    }

    static void searchForFold(String output){
        String keyword = "\"verbtxt\">";
        String[] splitOutput = output.split(keyword);
        System.out.println(splitOutput[1]);


        int prasenStartsAt = 1; //(first object in array is null)
        int prateriumStartsAt = 7;
        int perfektStartsAt = 13;

        //get all prasens and prateriums + perfekt without ge
        int forLoopLength = splitOutput.length;
        if(forLoopLength>30) forLoopLength = 30; //avoid any data after the folds that we are looking for
        String[] allUsableWords = new String[forLoopLength];
        
        
        for(int i=1; i<forLoopLength; i++){

            //check index for praterium------------------------------------
            if(splitOutput[i].contains("Präteritum")){
                prateriumStartsAt=i+1;
            }
            if(splitOutput[i].contains("Perfekt")){
                perfektStartsAt=i+1;
            }

            //form words from ---------------------------------------------
            char currentChar = splitOutput[i].charAt(0);
            int currentCharIndex = 1;
            String currentWord = "";
            while(currentChar!='<'){
                currentWord+=currentChar;
                currentChar = splitOutput[i].charAt(currentCharIndex);
                currentCharIndex++;
            }
            allUsableWords[i]=currentWord;
        }

        //add the folds to logical arrays--------------------------------
        String[] prasenFolds = new String[6];
        String[] prateriumFolds = new String[6];
        String perfekt = "";
        for(int i = 0; i<6; i++){
            prasenFolds[i] = allUsableWords[prasenStartsAt+i];
            prateriumFolds[i] = allUsableWords[prasenStartsAt+i];
        }
        perfekt = allUsableWords[perfektStartsAt];

        //habe or sind for perfekt--------------------------------------
        String habeOrSind = "";
        String[] habeOrSindSplit = splitOutput[perfektStartsAt-1].split("\"auxgraytxt\">");
        //if first letter is h it's habe, else if it's b it is bin aka. sind
        if(habeOrSindSplit[1].charAt(0)=='h'){
            habeOrSind = "haben ";
        } else if(habeOrSindSplit[1].charAt(0)=='b'){
            habeOrSind = "sind ";
        }
        

        //check for perfekt particle-------------------------------------------
        String perfektParticle = "";
        if(splitOutput[perfektStartsAt-1].contains("<i class=\"particletxt\">")){
            perfektParticle = "ge";
        }
        perfekt = perfektParticle+perfekt;

        System.out.println(prasenStartsAt);
        System.out.println(prateriumStartsAt);
        System.out.println(perfektStartsAt);
        System.out.println(Arrays.toString(allUsableWords));
        String pasteReady = gerWord + "\t\t\t" + habeOrSind + perfekt + "\n" + 
                            "\n" + 
                            "ich "  + "\t" + prasenFolds[0] + "\t\t" + prateriumFolds[0] + "\n" +
                            "du "   + "\t" + prasenFolds[1] + "\t\t" + prateriumFolds[1]  + "\n" +
                            "er "   + "\t" + prasenFolds[2] + "\t\t" + prateriumFolds[2]  + "\n" +
                            "wir "  + "\t" + prasenFolds[3] + "\t\t" + prateriumFolds[3] + "\n" +
                            "ihr "  + "\t" + prasenFolds[4] + "\t\t" + prateriumFolds[4] + "\n" +
                            "sind " + "\t" + prasenFolds[5] + "\t\t" + prateriumFolds[5];

        pushToClipboard(pasteReady);
    }

    static String gerWord;
    static String translateWord(String sloWord){
        String siteURL = "https://sl.pons.com/prevod/sloven%C5%A1%C4%8Dina-nem%C5%A1%C4%8Dina/"+sloWord;
        String output  = getUrlContents(siteURL); 
        printToFile(output); 
        gerWord = searchForGerTranslation(output);
        return gerWord;
    }

    static void pushToClipboard(String output){
        try{
            StringSelection stringSelection = new StringSelection(output);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        }catch(Exception e){
            System.out.println("Couldn't push to clipboard");
        }
    }

    static void printToFile(String output){
        try{
            File file = new File("output.txt");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fw);
            printWriter.print(output);
        }catch(Exception e){
            System.out.println("Failed print to file");
        }
    }

    private static String getUrlContents(String theUrl)  {  
        StringBuilder content = new StringBuilder();  
        // Use try and catch to avoid the exceptions  
        boolean gotData = false;
        try {  
            URL url = new URL(theUrl); // creating a url object  
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object  

            // wrapping the urlconnection in a bufferedreader  
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));  
            String line;  
            // reading from the urlconnection using the bufferedreader  
            while ((line = bufferedReader.readLine()) != null)  
            {  
            content.append(line + "\n");  
            }  
            bufferedReader.close();  
            gotData = true;
        }catch(Exception e) {  
            System.out.println("Failed getting website data");
        }  
        return content.toString();  
    }

    static String searchForGerTranslation(String output){
        String keyword = "<a href='/prevod/nem%C5%A1%C4%8Dina-sloven%C5%A1%C4%8Dina/";
        String[] splitOutput = output.split(keyword);
        char currentChar = ' ';
        String gerWord = "";


        // pleskati
        // kidati
        int getFind = 1;
        if(splitOutput[0].charAt(splitOutput[0].length()-1)=='(') {
            getFind = 2;
        }

        //System.out.println(output);
        int counter = 0;
        while(currentChar!='\''){
            currentChar = splitOutput[getFind].charAt(counter);
            gerWord+=currentChar;
            counter++;
            currentChar = splitOutput[getFind].charAt(counter);
        }
        System.out.println(gerWord);

        return gerWord;
    }
}  
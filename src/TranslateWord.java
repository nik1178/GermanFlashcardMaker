import java.net.*;  
import java.io.*;  
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;

public class TranslateWord{  

    TranslateWord(){
        
    }

    static String foldWord(String gerWord){
        TranslateWord.gerWord = gerWord;
        String siteURL = "https://konjugator.reverso.net/konjugation-deutsch-verb-" + gerWord + ".html";
        String output  = getUrlContents(siteURL); 
        printToFile(output, "Konjugation");
        searchForFold(output);

        return "";
    }

    static void searchForFold(String output){
        String keyword = "\"verbtxt\">";
        String[] splitOutput = output.split(keyword);
        //System.out.println(splitOutput[1]);
        System.out.println("Split the website into " + splitOutput.length + " pieces.");


        int prasenStartsAt = 1; //(first object in array is null)
        int prateriumStartsAt = splitOutput.length-1;
        int perfektStartsAt = splitOutput.length-1;

        //get all prasens and prateriums + perfekt without ge
        int forLoopLength = splitOutput.length;
        //if(forLoopLength>30) forLoopLength = 30; //avoid any data after the folds that we are looking for
        String[] allUsableWords = new String[forLoopLength];
        
        
        for(int i=1; i<forLoopLength; i++){

            //check index for praterium----------------- \/ - this checks if it's the first match it found, so it doesn't accidentally go too far
            if(splitOutput[i].contains("Präteritum") && i<prateriumStartsAt){
                prateriumStartsAt=i+1;
            }
            if(splitOutput[i].contains("Perfekt") && i<perfektStartsAt){
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
        System.out.println("Got all the folds. There are " + allUsableWords.length + " usable words.");

        //add the folds to logical arrays--------------------------------
        String[] prasenFolds = new String[6];
        String[] prateriumFolds = new String[6];
        String perfekt = "";
        for(int i = 0; i<6; i++){
            prasenFolds[i] = allUsableWords[prasenStartsAt+i];
            prateriumFolds[i] = allUsableWords[prasenStartsAt+i];
        }
        perfekt = allUsableWords[perfektStartsAt];
        System.out.println("Made arrays of correct words");

        //habe or sind for perfekt--------------------------------------
        String habeOrSind = "";
        String[] habeOrSindSplit = splitOutput[perfektStartsAt-1].split("\"auxgraytxt\">");
        System.out.println("Split the split website to find where habe or sind are located. The length of the new array is: " + habeOrSindSplit.length);
        //if first letter is h it's habe, else if it's b it is bin aka. sind
        try{
            if(habeOrSindSplit[1].charAt(0)=='h'){
                habeOrSind = "haben ";
            } else if(habeOrSindSplit[1].charAt(0)=='b'){
                habeOrSind = "sind ";
            }
            System.out.println("Found habe or sind");
        }catch(Exception e){
            System.out.println("Failed getting habe or sind.");
            habeOrSind = "N/A";
        }

        //check for perfekt particle-------------------------------------------
        String perfektParticle = "";
        if(splitOutput[perfektStartsAt-1].contains("<i class=\"particletxt\">")){
            perfektParticle = "ge";
        }
        perfekt = perfektParticle+perfekt;
        System.out.println("Found perfekt particle");

        /* System.out.println(prasenStartsAt);
        System.out.println(prateriumStartsAt);
        System.out.println(perfektStartsAt); */
        System.out.println(Arrays.toString(allUsableWords));
        //Old system:
        /* String pasteReady = gerWord + "\t\t\t" + habeOrSind + perfekt + "\n" + 
                            "\n" + 
                            "ich "  + "\t" + prasenFolds[0] + "\t\t" + prateriumFolds[0] + "\n" +
                            "du "   + "\t" + prasenFolds[1] + "\t\t" + prateriumFolds[1]  + "\n" +
                            "er "   + "\t" + prasenFolds[2] + "\t\t" + prateriumFolds[2]  + "\n" +
                            "wir "  + "\t" + prasenFolds[3] + "\t\t" + prateriumFolds[3] + "\n" +
                            "ihr "  + "\t" + prasenFolds[4] + "\t\t" + prateriumFolds[4] + "\n" +
                            "sind " + "\t" + prasenFolds[5] + "\t\t" + prateriumFolds[5]; */

        //new system:
        String pasteReady = gerWord + "\t\t\t" + habeOrSind + perfekt + "\n\n";
        for(int i=0; i<prasenFolds.length; i++){
            switch(i){
                case 0: pasteReady+="ich"; break;
                case 1: pasteReady+="du"; break;
                case 2: pasteReady+="er"; break;
                case 3: pasteReady+="wir"; break;
                case 4: pasteReady+="ihr"; break;
                case 5: pasteReady+="sind"; break;
                default: pasteReady+="Something went wrong when finding folds."; break;
            }
            pasteReady+="\t" + prasenFolds[i] + "\t\t" + prasenFolds[i];
            if(i<5) pasteReady+="\n";
        }
        System.out.println("Made paste-ready string");

        pushToClipboard(pasteReady);
    }

    static String gerWord;
    static String translateWord(String sloWord){
        String siteURL = "https://sl.pons.com/prevod/sloven%C5%A1%C4%8Dina-nem%C5%A1%C4%8Dina/"+sloWord;
        String output  = getUrlContents(siteURL); 
        printToFile(output, "PONS"); 
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

    static void printToFile(String output, String websiteName){
        try{
            File file = new File(websiteName + ".txt");
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
        System.out.println("Attempting download website: " + theUrl);
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
            System.out.println("Downloaded website: " + theUrl);

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

        //Sometimes nouns have examples before they have their actual translation. This skips the examples:
        int getFind = 1;

        for(int i=0; i<splitOutput.length; i++){
            if(splitOutput[i].contains("\"entry")){
                if(!splitOutput[i].contains("Geslo uporabnika")){
                    getFind = i+1;
                    break;
                }
            }
        }

        // pleskati
        // kidati
        if(splitOutput[getFind-1].charAt(splitOutput[getFind-1].length()-1)=='(') {
            getFind++;
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
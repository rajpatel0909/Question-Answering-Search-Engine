/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//this is this
package irp4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import javax.servlet.ServletContext;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import simplenlg.features.Feature;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.english.Realiser;

public class QuestionAnsweringAPI {

    public static ArrayList<String> ids = new ArrayList<>();

    //public static void main(String[] args) {
    public static void main(String args[]) {
        Map<Integer, List<String>> finalAnswers = new HashMap<>();
        POSModel model = new POSModelLoader().load(new File("en-pos-maxent.zip"));
        String query = "When was currency banned in India";
        System.out.println("Question is : " + query);
        HashMap<String, ArrayList<String>> qHm = new HashMap<>();
        HashMap<String, HashMap<String, ArrayList<String>>> docsNER = new HashMap<String, HashMap<String, ArrayList<String>>>();
        HashMap<String, String> docsText = new HashMap<>();//should in cases
        HashMap<String, String> originaldocsText;
        String str = "";
        List<String> calculatedResult = null;
        String solrQuery = "";
        ArrayList<String> tweetTxt = new ArrayList<>();
        try {
            if(query.contains("?")){
                query = query.replace("?", "");
            }
            //System.out.println("docs NOW are " + docsNER);
            qHm = POSTag(query, model);
            String qsType = "";
            if (qHm.containsKey("WP")) {
                qsType = qHm.get("WP").toString();
            } else if (qHm.containsKey("WRB")) {
                qsType = qHm.get("WRB").toString();
            }

            System.out.println("question type " + qsType + " with tags as " + qHm.toString());
            switch (qsType) {
                case "[who]":

                case "[Who]":
                    int numberOfTopResults = 5;
                    String[] checkSingularity = {"CM", "Chief Minister", "chief minister", "PM", "Prime Minister",
                                                "HM", "home minister", "Governor"};
                    for(String checker: checkSingularity){
                        if(query.substring(3).contains("is " +checker)){
                            System.out.println(" Total NUmber set to 1");
                            numberOfTopResults = 1;
                        }
                    }
                    ArrayList<String> dataNNP = new ArrayList<>();
                    ArrayList<String> dataNN = new ArrayList<>();
                    ArrayList<String> dataIN = new ArrayList<>();
                    if (qHm.containsKey("NNP")) {
                        dataNNP = qHm.get("NNP");
                    }
                    if (qHm.containsKey("NN")) {
                        dataNN = qHm.get("NN");
                    }
                    if (qHm.containsKey("IN")) {
                        dataIN = qHm.get("IN");
                    }

                    solrQuery = "";
                    for (String qtext : dataNNP) {
                        solrQuery += qtext + " ";
                    }
                    for (String qtext : dataNN) {
                        solrQuery += qtext + " ";
                    }
                    for (String qtext : dataIN) {
                        solrQuery += qtext + " ";
                    }
                    System.out.println(" solrQuery is " + solrQuery);
                    docsText = getDataFromSolr(solrQuery);
                    removeMentionsandHashtags(docsText);
                    Iterator it1 = docsText.entrySet().iterator();
                    while (it1.hasNext()) {
                        Map.Entry pair = (Map.Entry) it1.next();
                        HashMap<String, ArrayList<String>> hm = POSTag(pair.getValue().toString(), model);
                        docsNER.put(pair.getKey().toString(), hm);
                        //it1.remove();
                    }
                    ArrayList<String> data = new ArrayList<>();
                    data.addAll(dataNNP);
                    data.addAll(dataNN);
                    //System.out.println("ids before calculation " + ids.toString());
                    calculatedResult = whoCategory(data, docsNER, numberOfTopResults);
                    str = data.toString();

                    break;
                    
                case "[when]":
                case "[When]":
                    if (qHm.containsKey("VBD")) {
                        ArrayList<String> vbdList = qHm.get("VBD");
                        if (vbdList.contains("did") && qHm.containsKey("VB")) {
                            ArrayList<String> vbList = qHm.get("VB");
                            ArrayList<String> newVbList = new ArrayList<>();
                            for (String verb : vbList) {
                                Lexicon lexicon = Lexicon.getDefaultLexicon();
                                WordElement word = lexicon.getWord(verb, LexicalCategory.VERB);
                                InflectedWordElement infl = new InflectedWordElement(word);
                                infl.setFeature(Feature.TENSE, Tense.PAST);
                                Realiser realiser = new Realiser(lexicon);
                                verb = realiser.realise(infl).getRealisation();
                                newVbList.add(verb);
                            }
                            qHm.put("VBD", newVbList);
                        }
                    }

                    String qNoun = "";
                    String qVerb = "";
                    if (qHm.containsKey("NN")) {
                        qNoun = qHm.get("NN").toString();
                        qNoun = qNoun.substring(1, qNoun.length() - 1);
                        solrQuery += qNoun + " ";
                    }
                    if (qHm.containsKey("VBN")) {
                        qVerb = qHm.get("VBN").toString();
                        qVerb = qVerb.substring(1, qVerb.length() - 1);
                        solrQuery += qVerb + " ";
                    } else if (qHm.containsKey("VBD")) {
                        if (qHm.get("VBD").toString().contains("did")) {

                        }
                        qVerb = qHm.get("VBD").toString();
                        qVerb = qVerb.substring(1, qVerb.length() - 1);
                        solrQuery += qVerb + " ";
                    } else if (qHm.containsKey("VB")) {
                        qVerb = qHm.get("VB").toString();
                        qVerb = qVerb.substring(1, qVerb.length() - 1);
                        solrQuery += qVerb + " ";
                    }
                    solrQuery += "on";
                    //System.out.println(" constructed query is " + solrQuery);

                    docsText = getDataFromSolr(solrQuery);
                    removeMentionsandHashtags(docsText);
                    Iterator it2 = docsText.entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry pair = (Map.Entry) it2.next();
                        HashMap<String, ArrayList<String>> hm = POSTag(pair.getValue().toString(), model);
                        docsNER.put(pair.getKey().toString(), hm);
                        //it2.remove();
                    }

                    calculatedResult = whenCategory(qNoun, qVerb, docsText, docsNER);
                    //str = data1.toString();

                    break;
                case "[what]":
                case "[What]":
                    docsText = getDataFromSolr(query);
                    Iterator it3 = docsText.entrySet().iterator();
                    while (it3.hasNext()) {
                        Map.Entry pair = (Map.Entry) it3.next();
                        tweetTxt.add(pair.getValue().toString());
                        HashMap<String, ArrayList<String>> hm = POSTag(pair.getValue().toString(), model);
                        docsNER.put(pair.getKey().toString(), hm);
                    }
                    System.out.println("tweetTxt:" + tweetTxt);

                    break;
                case "[where]":
                case "[Where]":
                    docsText = getDataFromSolr(query);
                    removeMentionsandHashtags(docsText);
                    Iterator it5 = docsText.entrySet().iterator();
                    while (it5.hasNext()) {
                        Map.Entry pair = (Map.Entry) it5.next();
                        tweetTxt.add(pair.getValue().toString());
                        HashMap<String, ArrayList<String>> hm = POSTag(pair.getValue().toString(), model);
                        docsNER.put(pair.getKey().toString(), hm);
                    }
                    calculatedResult = whereCategory(docsNER);
                    System.out.println("where answers:" + calculatedResult);

                    break;
                case "[how]":
                case "[How]":
                    ArrayList<String> resultText = new ArrayList<>();
                    String[] queryArray = query.split(" ");
                    String secondWord = queryArray[1];
                    if (secondWord.toLowerCase().equals("many")) {
                        query = "";
                        for (String qWord : queryArray) {
                            if (!((qWord.toLowerCase().equals("how")) || (qWord.toLowerCase().equals("many")))) {
                                query += " " + qWord;
                            }
                        }
                    }
                    // System.out.println("*************"+query);
                    docsText = getDataFromSolr(query);
                    Iterator it4 = docsText.entrySet().iterator();
                    while (it4.hasNext()) {
                        Map.Entry pair = (Map.Entry) it4.next();
                        tweetTxt.add(pair.getValue().toString());
                        HashMap<String, ArrayList<String>> hm = POSTag(pair.getValue().toString(), model);
                        docsNER.put(pair.getKey().toString(), hm);
                    }

                    Integer getCardinalNumber = getCardinalNumber(docsNER);
                    if (getCardinalNumber == 0) {
                        resultText = tweetTxt;
                    } else {
                        resultText.add(getCardinalNumber.toString());
                    }
                    System.out.println(getCardinalNumber);
                    List<String> finalResult = new ArrayList<>();
                    finalResult.add(getCardinalNumber.toString());
                    calculatedResult = finalResult;
                    break;
                default:
                    docsText = getDataFromSolr(query);
                    Iterator itBrk = docsText.entrySet().iterator();
                    while (itBrk.hasNext()) {
                        Map.Entry pair = (Map.Entry) itBrk.next();
                        tweetTxt.add(pair.getValue().toString());
                        HashMap<String, ArrayList<String>> hm = POSTag(pair.getValue().toString(), model);
                        docsNER.put(pair.getKey().toString(), hm);
                    }
                    System.out.println("tweetTxt:" + tweetTxt);
                    break;
            }
            System.out.println(str);

        } catch (Exception e) {
            System.out.println("exception " + e.getMessage() + " " + e);
        }
        //System.out.println("docs NOW are " + docsNER.keySet());
//                List<String> keys = new ArrayList<String>(docsNER.keySet());
//                if(keys.isEmpty()){
//                    System.out.println("Answer is " + docsText.get(keys.get(0)));
//                }else{
//                    System.out.println("sorry we don't have answer for this question");
//                }
        originaldocsText = docsText;
        System.out.println("Answer is " + calculatedResult);
        System.out.println("qhm:" + qHm);
        finalAnswers.put(0, calculatedResult);
        List<String> relevantTweets = new ArrayList<>();
        for (Entry<String, String> entry : originaldocsText.entrySet()) {
            relevantTweets.add(entry.getValue());
        }
        finalAnswers.put(1, relevantTweets);
        //return finalAnswers;

    }

    public static List<String> whenCategory(String qNoun, String qVerb, HashMap<String, String> docsText, HashMap<String, HashMap<String, ArrayList<String>>> docsNER) {
        System.out.println("Inside whenCategory ");
        //String searchString = qNoun.toLowerCase() + " " + qVerb.toLowerCase() + " on";
        String searchString = qNoun.toLowerCase() + " " + qVerb.toLowerCase();
        System.out.println("current query = " + searchString);
        //Iterator it = docsText.entrySet().iterator();
        System.out.println(" first doc text " + ids.get(0) + " = " + docsText.get(ids.get(0)));
        List<String> answers = new ArrayList<>();
        String result = "";
        for (int j = 0; j <= 10; j++) {
            boolean found = false;
            System.out.println("counter = " + j + " found = " + found);
            String firstText = docsText.get(ids.get(j));
            if (docsNER.get(ids.get(j)).containsKey("CD")) {
                ArrayList<String> cdList = docsNER.get(ids.get(j)).get("CD");
                for (String cdStr : cdList) {
                    if (cdStr.contains("/")) {

                        result += cdStr;
                        found = true;
                    }
                }
            }
            System.out.println("First string = " + firstText);
            if (!found) {
                String[] mon = {" jan ", " feb ", " mar ", " apr ", " may ", " jun ", " jul ", " aug ", " sep ", " oct ", " nov ", " dec "};
                String[] months = {" january ", " february ", " march ", " april ", " may ", " june ", " july ", " august ", " september ",
                    " november ", " december "};
                String[] day = {" mon ", " tue ", " wed ", " thu ", " fri "};
                String[] days = {" monday ", " tuesday ", " wednesday ", " thursday ", " friday ", " saturday ", " sunday "};
                //firstText = "aanan aodfnaonfa faonfaondf second sdgd 8 nov, aojsnfajna oansdjoan";
                firstText = firstText.replaceAll("[^a-zA-Z0-9 ]", "");
                System.out.println(" filter comma first text " + firstText);
                System.out.println("Look between these ");
                ArrayList<String[]> times = new ArrayList<>(Arrays.asList(mon, months, day, days));
                String nextWord = "";
                for (String[] time : times) {
                    for (String timeString : time) {
                        if (found) {
                            break;
                        }
                        //System.out.println(" monthString " + timeString);
                        if (firstText.toLowerCase().contains(timeString)) {
                            result += timeString;
                            //System.out.println("first contains " + timeString);
                            int index = firstText.indexOf(timeString);
                            char[] firstTextArray = firstText.toCharArray();
                            //System.out.println("index in string is " + index + " with char at that index as " + firstTextArray[index + timeString.length()]);
                            nextWord = "";
                            if ('0' < firstTextArray[index + timeString.length()] && firstTextArray[index + timeString.length()] < '9') {
                                for (int i = index + timeString.length(); i < firstTextArray.length && firstTextArray[i] != ' '; i++) {
                                    nextWord += firstTextArray[i];
                                }
                            } else if ('0' < firstTextArray[index - 1] && firstTextArray[index - 1] < '9') {
                                for (int i = index - 1; i >= 0 && firstTextArray[i] != ' '; i--) {
                                    nextWord += firstTextArray[i];
                                }
                                StringBuilder tempsb = new StringBuilder(nextWord);
                                nextWord = tempsb.reverse().toString();
                            }
                            //System.out.println("nextword = " + nextWord);
                        }
                    }
                }
                result += " " + nextWord;
            }
//                if(!result.equals(" ")){
//                    break;
//                }
        }
        answers.add(result);

        //if(!found){
        //    System.out.println("result = " + (firstText.substring(firstText.indexOf(" on ") + 3 , firstText.length())).toString());
        //}
        //System.out.println(" result = " + result);
        return answers;

        //ArrayList<string> days = new
    }

    public static List<String> whoCategory(ArrayList<String> data, HashMap<String, HashMap<String, ArrayList<String>>> docsNER, int numberOfTopResults) throws FileNotFoundException, IOException {
        System.out.println("Inside whoCategory " + data.toString());
        HashMap<String, Integer> wordsRank = new HashMap<>();
        Iterator it = docsNER.entrySet().iterator();
        BufferedReader br = new BufferedReader(new FileReader("whoStopWords.txt"));
        //String[] stopwords = {"demonetization","#demonetisation","#demonetization", "demonetisation","co","t","chief",
        //    "center","PM","mandate","election","victory","BJP4Gujarat","support"};
        String line = "";
        while ((line = br.readLine()) != null) {
            data.add(line);
        }
//            for(String tempStopWords: stopwords){
//                data.add(tempStopWords);
//            }

        //System.out.println("data words are " + data.toString());
        //remove docs without query's NNP and NN 
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            HashMap<String, ArrayList<String>> eachDocNER = (HashMap< String, ArrayList<String>>) pair.getValue();
            boolean take[] = new boolean[data.size()];
            ArrayList<String> allTempTags = new ArrayList<>();
            if (eachDocNER.containsKey("NNP")) {
                allTempTags.addAll(eachDocNER.get("NNP"));
            }
            if (eachDocNER.containsKey("NN")) {
                allTempTags.addAll(eachDocNER.get("NN"));
            }
            for (String temptag : allTempTags) {
                if (!data.contains(temptag.toLowerCase()) && !data.contains(temptag)) {
                    //System.out.println(" current temptag is "+temptag);
                    if (wordsRank.containsKey(temptag)) {
                        wordsRank.put(temptag, wordsRank.get(temptag) + 1);
                    } else {
                        wordsRank.put(temptag, 1);
                    }
                }
            }
        }

        //System.out.println(" who words ranking " + wordsRank.toString());
        ArrayList<Integer> valuesOfWordsRank = new ArrayList<>();
        for (Entry<String, Integer> entry : wordsRank.entrySet()) {
            valuesOfWordsRank.add(entry.getValue());
        }
        Collections.sort(valuesOfWordsRank, Collections.reverseOrder());
        //System.out.println("values of words rank " + valuesOfWordsRank.toString());
        
        List<String> topNResults = new ArrayList<>();
        for (int i = 0; i < numberOfTopResults; i++) {
            Iterator it2 = wordsRank.entrySet().iterator();
            int max = 0;
            String maxValue = "";
            boolean found = false;
            while (it2.hasNext()) {
                Map.Entry pair = (Map.Entry) it2.next();
                if (max < (int) pair.getValue()) {
                    max = (int) pair.getValue();
                    maxValue = pair.getKey().toString();
                }
            }
            wordsRank.remove(maxValue);
            //System.out.println(" added tag is " + maxValue + " with count = " + max);
            topNResults.add(maxValue);
            System.out.println("");
        }

        System.out.println(" top N values = " + topNResults.toString());
        return topNResults;
    }

    public static ArrayList<String> whereCategory(HashMap<String, HashMap<String, ArrayList<String>>> docsNER) {
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> addWords = new ArrayList<>();
        String queryWords = "";
        for (int i = 0; i < 10; i++) {
            HashMap<String, ArrayList<String>> documentTagging = new HashMap<>();
            if (docsNER.containsKey(ids.get(i))) {
                documentTagging = docsNER.get(ids.get(i));
            }
            if (documentTagging.containsKey("NNP") && documentTagging.containsKey("CD")) {
                for (String word : documentTagging.get("NNP")) {
                    if (!(addWords.contains(word))) {
                        addWords.add(word);
                    }
                }
            }
        }

        for (String word : addWords) {
            queryWords += " " + word;
        }

        return addWords;
    }

    public static String rerunQuery(HashMap<String, HashMap<String, ArrayList<String>>> docsNER) {
        ArrayList<String> addWords = new ArrayList<>();
        String queryWords = "";
        for (int i = 0; i < 5; i++) {
            HashMap<String, ArrayList<String>> documentTagging = new HashMap<>();
            if (docsNER.containsKey(ids.get(i))) {
                documentTagging = docsNER.get(ids.get(i));
            }
            if (documentTagging.containsKey("NNP")) {
                for (String word : documentTagging.get("NNP")) {
                    if (!(addWords.contains(word))) {
                        addWords.add(word);
                    }
                }
            }
            if (documentTagging.containsKey("NN")) {
                for (String word : documentTagging.get("NN")) {
                    if (!(addWords.contains(word))) {
                        addWords.add(word);
                    }
                }
            }
        }

        for (String word : addWords) {
            queryWords += " " + word;
        }

        return queryWords;
    }

    public static Integer getCardinalNumber(HashMap<String, HashMap<String, ArrayList<String>>> docsNER) {
        ArrayList<Integer> cardinalValueArray = new ArrayList<>();
        String cardinalValue = "";
        for (int i = 0; i < 10; i++) {
            HashMap<String, ArrayList<String>> documentTagging = new HashMap<>();
            if (docsNER.containsKey(ids.get(i))) {
                documentTagging = docsNER.get(ids.get(i));
            }
            if (documentTagging.containsKey("CD")) {
                for (String word : documentTagging.get("CD")) {
                    word = word.replaceAll("[^0-9]", "");
                    cardinalValueArray.add(Integer.parseInt(word));
                }
            }
        }

        int count = 1, tempCount;
        int popular = 0;
        if (cardinalValueArray.size() != 0) {
            popular = cardinalValueArray.get(0);
            int temp = 0;
            for (int i = 0; i < (cardinalValueArray.size() - 1); i++) {
                temp = cardinalValueArray.get(i);
                tempCount = 0;
                for (int j = 1; j < cardinalValueArray.size(); j++) {
                    if (temp == cardinalValueArray.get(j)) {
                        tempCount++;
                    }
                }
                if (tempCount > count) {
                    popular = temp;
                    count = tempCount;
                }
            }
        }

        return popular;
    }

    public static HashMap<String, String> getDataFromSolr(String solrQuery) {
        HashMap<String, String> textData = new HashMap<>();
        //String query = "demonetisation%20implemented%20on";
        //String query = "%22demonetisation%20implemented%22";
        try {
            String query = URLEncoder.encode(solrQuery, "UTF-8");
            System.out.println(" constructed query is " + query);
            JSONArray array = new JSONArray();
            URL oracle = new URL("http://52.43.144.199:8983/solr/IRF16P4/select?indent=on&q=" + query + "&rows=100&wt=json");
            //URL oracle = new URL("http://10.84.55.157:8983/solr/IRF16P4/select?indent=on&q=favour+demonetisation+&wt=json");
            System.out.println(oracle);
            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            Object obj = JSONValue.parse(sb.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject obj222 = (JSONObject) jsonObject.get("response");
            array = (JSONArray) obj222.get("docs");
            if (array.size() != 0) {
                for (int i = 0; i < array.size(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    JSONArray textArray = (JSONArray) object.get("text");
                    String text = (String) textArray.get(0);
                    if (text == null) {
                        text = "";
                    }
                    if (object.get("entities.user_mentions.name") != null) {
                        JSONArray textArray2 = (JSONArray) object.get("entities.user_mentions.name");
                        System.out.println("textarray2 " + textArray2);
                        text += (String) textArray2.get(0);
                        text += " ";
                    }
                    if (object.get("entities.hashtags.text") != null) {
                        JSONArray textArray2 = (JSONArray) object.get("entities.hashtags.text");
                        System.out.println("textarray2 " + textArray2);
                        text += (String) textArray2.get(0);
                        text += " ";
                    }
                    //JSONArray textArray3 = (JSONArray) object.get("entities.hashtags.text");
                    //text += (String) textArray3.get(0);
                    //text += " ";
                    String id = (String) object.get("id");
                    textData.put(id, text);
                    ids.add((String) object.get("id"));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return textData;
    }

    public static ArrayList<String> parseData() {
        ArrayList<String> textData = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("C:/Users/divya/workspace/IRProject4/demonetization.json"));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray array = (JSONArray) jsonObject.get("statuses");
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);

                String text = (String) object.get("text");
                textData.add(text);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return textData;
    }

    private static HashMap<String, ArrayList<String>> POSTag(String string, POSModel model) throws IOException {
        HashMap<String, ArrayList<String>> SentenceTags = new HashMap<String, ArrayList<String>>();

        //POSModel model = new POSModelLoader().load(new File("en-pos-maxent.zip"));
        //PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);

        String input = string;

        ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(input));

        //perfMon.start();
        String line;

        while ((line = lineStream.read()) != null) {

            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);

            String[] tags = tagger.tag(whitespaceTokenizerLine);

            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

            //System.out.println(sample.toString());
            ArrayList<String> tagArr = new ArrayList<String>(Arrays.asList(sample.getTags()));

            ArrayList<String> sentArr = new ArrayList<String>(Arrays.asList(sample.getSentence()));
            for (int i = 0; i < tagArr.size(); i++) {
                if (SentenceTags.containsKey(tagArr.get(i))) {
                    SentenceTags.get(tagArr.get(i)).add(sentArr.get(i));
                } else {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(sentArr.get(i));
                    SentenceTags.put(tagArr.get(i), tmp);
                }
            }
            //perfMon.incrementCounter();
        }
        //perfMon.stopAndPrintFinalResult();
        return SentenceTags;
    }

    public static HashMap<String, String> removeMentionsandHashtags(HashMap<String, String> docsText) {
        String text;
        for (Entry<String, String> entry : docsText.entrySet()) {
            text = entry.getValue();
            text = text.replaceAll("[^a-zA-Z0-9]", " ");
            text = text.replaceAll("\\s+", " ");
            docsText.put(entry.getKey(), text);
        }
        return docsText;
    }
}

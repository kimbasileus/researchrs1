package hyu.kskim.research.rs.metadata.tools.nlp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;

/**
 *
 * @author KyungSoo Kim, Ph.D. Candidate, Hanyang University
 */
public class Parser {
	private String parserModel = null;
    private LexicalizedParser lp = null;
    private TokenizerFactory<CoreLabel> tokenizerFactory = null;

    public Parser(){
        try{
        	parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
            lp = LexicalizedParser.loadModel(parserModel);
      		tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        }catch(Exception e){
            System.out.println("Parser() Exception: "+e.getMessage());
        }
    }

    	
    public String parsing(String sentence){
        try{
            String parsedSentence = null;
            if(sentence == null || sentence.length()==0) return null;
            
            System.out.println("Sentence will be parsed: "+sentence);
            
            // This option shows loading and using an explicit tokenizer
      //      TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), ""); // See global variable...
            Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));
            List<CoreLabel> rawWords = tok.tokenize();
            
            Tree parse = lp.apply(rawWords);
            parsedSentence = parse.toString();
            int sentLength = parsedSentence.length();
            
            parsedSentence = parsedSentence.substring(6, sentLength-1).replace("(","<").replace(")", ">").replace("\n", "");

            System.out.println("Sentence parsing is completed: "+parsedSentence);

            return parsedSentence;
        }catch(Exception e){
            System.out.println("parsing() Exception: "+e.getMessage());
            return null;
        }
    }


/*
    public String parsing(ArrayList<String> tokenizedList){
        try{
            String parsedSentence = null;

            // This option shows loading and using an explicit tokenizer
            TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), ""); // See global variable...

            Tokenizer<CoreLabel> tok =
                tokenizerFactory.getTokenizer(new StringReader(sentence));
            List<CoreLabel> rawWords = tok.tokenize();

            //////////////////////////////////////////////////////////////////////////////////////////////
            //All words tokenized by the tokenizer tok will be added into the array list "tokenizedList"
            int rawWordsize = rawWords.size();
            for (int i=0; i < rawWordsize; i++) 
                tokenizedList.add(rawWords.get(i).toString());
            //////////////////////////////////////////////////////////////////////////////////////////////

            Tree parse = lp.apply(rawWords);

            parsedSentence = parse.toString();
            int sentLength = parsedSentence.length();

            parsedSentence = parsedSentence.substring(6, sentLength-1).replace("(","<").replace(")", ">").replace("\n", "");

        //    parsedSentence = parse.toString().substring(6, parse.toString().length()).replace("(","<").replace(")", ">").replace("\n", "");

            System.out.println("Sentence: "+sentence);
            System.out.println("Parsed Sentence: "+parsedSentence);

            return parsedSentence;
        }catch(Exception e){
            System.out.println("parsing() Exception: "+e.getMessage());
            return null;
        }
    }
*/


    public ArrayList<String> tokenization(String sentence){
        try{
            String parsedSentence = null;
            if(sentence == null || sentence.length()==0) return null;
            
            ArrayList<String> list = new ArrayList<String> ();

            // This option shows loading and using an explicit tokenizer
            // TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), ""); // See global variable...

            Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));
            List<CoreLabel> rawWords = tok.tokenize();

          /*  if(tok==null || rawWords==null ) {
            	System.out.println("Tokenization Error: "+sentence);
            	System.exit(0);
            }  */
            
            int rawWordsize = rawWords.size();
            for (int i=0; i < rawWordsize; i++){
            	list.add(rawWords.get(i).toString());
            }
            //System.out.println(list.size());
            return list;
        }catch(Exception e){
            System.out.println("tokenization() Exception: "+e.getMessage());
            return null;
        }
    }
    

    public void getPosList(String sentence){
    	try{
    		
    		
    		
    	}catch(Exception e){
    		//return null;
    	}
    }

    
}
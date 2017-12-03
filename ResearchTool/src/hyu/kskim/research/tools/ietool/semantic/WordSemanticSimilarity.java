package hyu.kskim.research.tools.ietool.semantic;

import java.util.*;

import hyu.kskim.research.tools.mathtool.*;
import hyu.kskim.research.tools.ietool.dic.WordNetInterface;
import hyu.kskim.research.tools.ietool.dic.WordNet_JWI;
import hyu.kskim.research.tools.ietool.nlp.Parser;
import hyu.kskim.research.tools.ietool.nlp.PorterStemmer;
import hyu.kskim.research.tools.ietool.nlp.StopWordEliminator;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

class WS4J {
    private static ILexicalDatabase db = new NictWordNet();
	private int methodID = 3; // Basic setting option
	
	public WS4J(){};
	public WS4J(int methodID){
		this.methodID = methodID;
	}
	
	//available options of metrics
	private static RelatednessCalculator[] rcs = { new HirstStOnge(db),
		new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
		new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
	
    // Source site: https://www.programcreek.com/2014/01/calculate-words-similarity-using-wordnet-in-java/
	public double semanticSimilarity2(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
        double s = 0;
		//double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
        s = rcs[methodID].calcRelatednessOfWords(word1, word2);
        return s;
	}
        
         
    // Source site: https://www.programcreek.com/java-api-examples/index.php?api=edu.cmu.lti.ws4j.RelatednessCalculator
    public double semanticSimilarity(String inputStr, String decompString) {
        WS4JConfiguration.getInstance().setMFS(true);

        List<POS[]> posPairs = rcs[methodID].getPOSPairs();
        double maxScore = -1D;

        for (POS[] posPair : posPairs) {
        	List<Concept> synsets1 = (List<Concept>) db.getAllConcepts(inputStr, posPair[0].toString());
        	List<Concept> synsets2 = (List<Concept>) db.getAllConcepts(decompString, posPair[1].toString());

            for (Concept synset1 : synsets1) {
                for (Concept synset2 : synsets2) {
                    Relatedness relatedness = rcs[methodID].calcRelatednessOfSynset(synset1, synset2);
                    double score = relatedness.getScore();
                    if (score > maxScore) {
                            maxScore = score;
                    }
                }
            }
        }

        if (maxScore == -1D) maxScore = 0.0;
        return maxScore;
    }
 
	public void test() {
		String[] words = {"add", "get", "filter", "remove", "check", "find", "collect", "create"};
 
		for(int i=0; i<words.length-1; i++){
			for(int j=i+1; j<words.length; j++){
				//double distance = compute(words[i], words[j]);
				double distance = semanticSimilarity(words[i], words[j]);
                System.out.println(words[i] +" -  " +  words[j] + " = " + distance);
			}
		}
	}
}




class GlossOverlap{
	WordNet_JWI dic = new WordNet_JWI(null);
	Parser parser = new Parser();
	StopWordEliminator se = new StopWordEliminator();
	PorterStemmer ps = new PorterStemmer();
	
	public double semanticSimilarity(String word1, String word2){
		try{
			double sim = 0;
			// 1.
			Queue<String> readyW1 = new LinkedList<String>();
			Queue<String> readyW2 = new LinkedList<String>();
			
			readyW1.add(word1);
			readyW2.add(word2);
			
			
			// 2. 
			ArrayList<String> listW1 = new ArrayList<String>();
			ArrayList<String> listW2 = new ArrayList<String>();
			
			//listW1 <- synonym(w1) + hypernyms(w1) + hyponyms(w1)
			
			
			//listW2 <- synonym(w2) + hypernyms(w2) + hyponyms(w2)
			
			
			// 3. 
			readyW1.addAll(listW1);
			readyW2.addAll(listW2);
			
			
			//4. 
			StringBuffer gloss1 = new StringBuffer();
			StringBuffer gloss2 = new StringBuffer();
			
			while(!readyW1.isEmpty())
			{
				// gloss1.append( glossary(readyW1.remove()) ).append(" ");
			}
			
			while(!readyW2.isEmpty())
			{
				// gloss2.append( glossary(readyW2.remove()) ).append(" ");
			}
			
			
			// 5. 
			listW1.clear(); listW2.clear();
			listW1 = se.getStopWordEliminatedSentence( parser.tokenization(gloss1.toString()) );
			listW2 = se.getStopWordEliminatedSentence( parser.tokenization(gloss2.toString()) );
			
			
			// 6. 
			HashSet<String> set1 = makeSet(listW1);
			HashSet<String> set2 = makeSet(listW2);
			
			
			// 7.
			// sim = jaccardSim(set1, set2);
			
			
			return sim;
		}catch(Exception e){
			System.err.println("semanticSimilarity() Exception: "+e.getMessage());
			return -1;
		}
	}
	
	
	public HashSet<String> makeSet(ArrayList<String> list){
		try{
			if(list==null) return null;
			int n = list.size();
			HashSet<String> set = new HashSet<String>();
			
			for(int i=0; i < n ; i++) {
				set.add( ps.stemming_word(list.get(i)) );
				// set.add( list.get(i) );
			}
			
			return set;
		}catch(Exception e){
			System.err.println("makeSet() Exception: "+e.getMessage());
			return null;
		}
	}
}


// Main Class
public class WordSemanticSimilarity {
	WS4J ws4j = null;
	GlossOverlap glo = null;
	
	public WordSemanticSimilarity(int methodID){
		this.ws4j = new WS4J(methodID);
		this.glo = new GlossOverlap();
	}
	
	public double similarity_ws4j(String word1, String word2){
		return ws4j.semanticSimilarity(word1, word2);
	}
	
	public double similarity_gloss(String word1, String word2) {
		return glo.semanticSimilarity(word1, word2);
	}
}

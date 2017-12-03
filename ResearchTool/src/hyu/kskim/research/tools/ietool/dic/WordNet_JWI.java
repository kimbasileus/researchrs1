/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.ietool.dic;

import java.io.File;
import java.util.ArrayList;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.mit.jwi.*;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * 0: Noun, 1: Verb, 2: Adjective, 3: Adverb
 */

/**
 * JWI (MIT) library interface
 * @author KyungSoo Kim, Ph.D. Candidate, Hanyang University
 */
public class WordNet_JWI implements WordNetInterface{
	String wnHome = "C:\\WordNet-3.0\\"; // WordNet directory
	String path = null;
	IDictionary dict = null;
	
	/**
	 * Constructor
	 * @param dir Wordnet program directory such as "C:\\WordNet-3.0\\". If this value is null, it may be set as default directory.
	 */
	public WordNet_JWI(String dir){
		try{
			if(dir!=null) wnHome = dir;
			path = wnHome + File . separator + "dict";
			
			URL url = new URL("file", null, path);
	        dict = new Dictionary ( url);
	        dict . open ();
	            
		}catch(Exception e){
			System.err.println("WordNet_JWI Exception: "+e.getMessage());
		}
	}
	
    @Override
    public ArrayList<String>  getGlossary(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size();
            if(n<=0) return null;
            
            ArrayList<String> glossary = new ArrayList<String>();
            
            for(int i=0; i < n ;i++){
            	wordID = idxWord . getWordIDs ().get (i) ;        
                wordEntity = dict . getWord ( wordID );	
                glossary.add( wordEntity.getSynset().getGloss() );
            }
            
            return glossary;
    	}catch(Exception e) {
    		System.err.println("getGlossary() Exception: "+e.getMessage());
    		return null;
    	}
    }

    
    
    public ArrayList<String>  getGlossary_typical(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size();
            if(n<=0) return null;
            
            ArrayList<String> glossary = new ArrayList<String>();
            
        	wordID = idxWord . getWordIDs ().get (0) ;        
            wordEntity = dict . getWord ( wordID );	
            glossary.add( wordEntity.getSynset().getGloss() );

            return glossary;
    	}catch(Exception e) {
    		System.err.println("getGlossary() Exception: "+e.getMessage());
    		return null;
    	}
    }
    
    
    @Override
    public ArrayList<String>[] getSynonyms(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size(); // The number of meanings
            if(n<=0) return null;
            
            ArrayList<String>[] synList = new ArrayList[n];
            ISynset synset = null;
            for(int i=0; i < n; i++) {
            	synList[i] = new ArrayList<String>(); // For i'th meaning
            	
            	wordID = idxWord . getWordIDs ().get (i) ;        
                wordEntity = dict . getWord ( wordID );	
            	
                synset = wordEntity.getSynset(); // Get synonyms
                
                for( IWord w : synset . getWords ()) { // Add the synonyms into the i'th list
                	synList[i].add(w.getLemma());
                }
            }
        
            return synList;
    	}catch(Exception e) {
    		System.err.println("getSynonyms() Exception: "+e.getMessage());
    		return null;
    	}

    }
    
    
    public ArrayList<String> getSynonyms_typical(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
        	
        	wordID = idxWord . getWordIDs ().get (0) ; // First meaning
        	wordEntity = dict . getWord ( wordID );
        	ISynset synset = wordEntity . getSynset ();
        	
        	ArrayList<String> synList = new ArrayList<String>();
        	// iterate over words associated with the synset
        	for( IWord w : synset . getWords ()) {
        		synList.add(w.getLemma());
        	}
        	
        	return synList;
    	}catch(Exception e) {
    		System.err.println("getSynonyms_typical() Exception: "+e.getMessage());
    		return null;
    	}

    }

    
    @Override
    public ArrayList<String>[] getHypernyms(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size(); // The number of meanings
            if(n<=0) return null;
            
            ArrayList<String>[] hypernymList = new ArrayList[n];
            ISynset synset = null;
            List < ISynsetID > hypernyms = null;
            
            for(int i=0; i < n; i++) {
            	hypernymList[i] = new ArrayList<String>(); // For i'th meaning
            	
            	wordID = idxWord . getWordIDs ().get (i) ;        
                wordEntity = dict . getWord ( wordID );	
                synset = wordEntity.getSynset(); // Get synset

                hypernyms = synset . getRelatedSynsets ( Pointer . HYPERNYM );
                
                List <IWord > words ;
                for( ISynsetID sid : hypernyms ){
    	            words = dict . getSynset (sid). getWords ();
    	            //System .out . print (sid + " {");
    	            for( Iterator <IWord > i1 = words . iterator (); i1. hasNext () ;){
    	            	//System .out . print (i1. next (). getLemma ());
    	            	hypernymList[i].add(i1.next().getLemma());
    	            }
    	            //System .out . println ("}");
                }
            }
        
            return hypernymList;
    	}catch(Exception e) {
    		System.err.println("getHypernyms() Exception: "+e.getMessage());
    		return null;
    	}
    }
    
    
    public ArrayList<String> getHypernyms_typical(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size(); // The number of meanings
            if(n<=0) return null;
            
            ArrayList<String> hypernymList = new ArrayList<String>();
            ISynset synset = null;
            List < ISynsetID > hypernyms = null;
            
            	
        	wordID = idxWord . getWordIDs ().get (0) ;  // First meaning      
            wordEntity = dict . getWord ( wordID );	
            synset = wordEntity.getSynset(); // Get synset

            hypernyms = synset . getRelatedSynsets ( Pointer . HYPERNYM );
            
            List <IWord > words ;
            for( ISynsetID sid : hypernyms ){
                words = dict . getSynset (sid). getWords ();
                //System .out . print (sid + " {");
                for( Iterator <IWord > i1 = words . iterator (); i1. hasNext () ;){
                	//System .out . print (i1. next (). getLemma ());
                	hypernymList.add(i1.next().getLemma());
                }
            }
        
            return hypernymList;
    	}catch(Exception e) {
    		System.err.println("getHypernyms_typical() Exception: "+e.getMessage());
    		return null;
    	}
   }

    
    @Override
    public ArrayList<String>[] getHyponyms(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size(); // The number of meanings
            if(n<=0) return null;
            
            ArrayList<String>[] hyponymList = new ArrayList[n];
            ISynset synset = null;
            List < ISynsetID > hyponyms = null;
            
            for(int i=0; i < n; i++) {
            	hyponymList[i] = new ArrayList<String>(); // For i'th meaning
            	
            	wordID = idxWord . getWordIDs ().get (i) ;        
                wordEntity = dict . getWord ( wordID );	
                synset = wordEntity.getSynset(); // Get synset

                hyponyms = synset . getRelatedSynsets ( Pointer.HYPONYM );
                
                List <IWord > words ;
                for( ISynsetID sid : hyponyms ){
    	            words = dict . getSynset (sid). getWords ();
    	            //System .out . print (sid + " {");
    	            for( Iterator <IWord > i1 = words . iterator (); i1. hasNext () ;){
    	            	//System .out . print (i1. next (). getLemma ());
    	            	hyponymList[i].add(i1.next().getLemma());
    	            }
    	            //System .out . println ("}");
                }
                
            }
        
            return hyponymList;	
    	}catch(Exception e) {
    		System.err.println("getHyponyms() Exception: "+e.getMessage());
    		return null;
    	}        
    }

    
    public ArrayList<String> getHyponyms_typical(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
            
            int n = idxWord.getWordIDs().size(); // The number of meanings
            if(n<=0) return null;
            
            ArrayList<String> hyponymList = new ArrayList<String>();
            ISynset synset = null;
            List < ISynsetID > hyponyms = null;
            
        	wordID = idxWord . getWordIDs ().get (0) ;        
            wordEntity = dict . getWord ( wordID );	
            synset = wordEntity.getSynset(); // Get synset

            hyponyms = synset . getRelatedSynsets ( Pointer.HYPONYM );
            
            List <IWord > words ;
            for( ISynsetID sid : hyponyms ){
                words = dict . getSynset (sid). getWords ();
                //System .out . print (sid + " {");
                for( Iterator <IWord > i1 = words . iterator (); i1. hasNext () ;){
                	//System .out . print (i1. next (). getLemma ());
                	hyponymList.add(i1.next().getLemma());
                }
                //System .out . println ("}");
            }
            
            return hyponymList; 	
    	}catch(Exception e) {
    		System.err.println("getHyponyms_typical() Exception: "+e.getMessage());
    		return null;
    	}
    }
    
    
    
    
    @Override
    public String getRootExpression(String word, int pos) {
    	try {
        	if(word==null) return null;
        	
        	IIndexWord idxWord = null;
        	if(pos==0)  // Noun
        		idxWord = dict.getIndexWord (word, POS.NOUN );
        	else if(pos==1) // Verb
        		idxWord = dict . getIndexWord (word, POS.VERB );
        	else if(pos==2) // Adjective
        		idxWord = dict . getIndexWord (word, POS.ADJECTIVE );
        	else if(pos==3) // Adverb
        		idxWord = dict . getIndexWord (word, POS.ADVERB );
        	
        	if(idxWord == null) return null;
        	
        	IWordID wordID = null;
            IWord wordEntity = null;
        	
        	wordID = idxWord . getWordIDs ().get (0) ; // First meaning
        	wordEntity = dict . getWord ( wordID );
        	
        	String lemma = wordEntity.getLemma();
        	
        	return lemma;	
    	}catch(Exception e) {
    		System.err.println("getRootExpression() Exception: "+e.getMessage());
    		return null;
    	}
    }

    
    @Override
    public boolean isWord(String word, int pos) {
    	try {
        	if(pos==0 && dict.getIndexWord (word, POS.NOUN )!=null )
        		return true;
        	else if(pos==1 && dict . getIndexWord (word, POS.VERB )!=null ) 
        		return true;
        	else if(pos==2 && dict . getIndexWord (word, POS.ADJECTIVE )!=null) 
        		return true;
        	else if(pos==3 && dict . getIndexWord (word, POS.ADVERB )!=null) 
        		return true;
        	else
        		return false;	
    	}catch(Exception e) {
    		System.err.println("isWord() Exception: "+e.getMessage());
    		return false;
    	}
    }
    
    
    public boolean isWord(String word) {
    	try {
        	if(this.isNoun(word) || this.isVerb(word) || this.isAdjective(word) || this.isAdverb(word))
        		return true;
        	else
        		return false;
    	}catch(Exception e) {
    		System.err.println("isWord() Exception: "+e.getMessage());
    		return false;
    	}
    }

    @Override
    public boolean isNoun(String word) {
    	try {
        	if(dict.getIndexWord (word, POS.NOUN )!=null )
        		return true;
        	else 
        		return false;	
    	}catch(Exception e) {
    		System.err.println("isNoun() Exception: "+e.getMessage());
    		return false;
    	}
    }

    @Override
    public boolean isVerb(String word) {
    	try {
        	if(dict.getIndexWord (word, POS.VERB )!=null )
        		return true;
        	else 
        		return false;    		
    	}catch(Exception e) {
    		System.err.println("isVerb() Exception: "+e.getMessage());
    		return false;
    	}
    }

    @Override
    public boolean isAdjective(String word) {
    	try {
        	if(dict.getIndexWord (word, POS.ADJECTIVE )!=null )
        		return true;
        	else 
        		return false;    		
    	}catch(Exception e) {
    		System.err.println("isAdjective() Exception: "+e.getMessage());
    		return false;
    	}
    }

	@Override
	public boolean isAdverb(String word) {
    	try {
    		if(dict.getIndexWord (word, POS.ADVERB )!=null )
        		return true;
        	else 
        		return false;	
    	}catch(Exception e) {
    		System.err.println("isAdverb() Exception: "+e.getMessage());
    		return false;
    	}
	}
    
    @Override
    public int getNumOfMeanings(String word, int pos) {
    	try {
    		return getGlossary(word, pos).size();	
    	}catch(Exception e) {
    		System.err.println("getNumOfMeanings() Exception: "+e.getMessage());
    		return -999;
    	}
    }

    
    // This method is used to test this module.
    public void testModule() {
    	String word = "abstraction";
    	
    	
    	System.out.println("1. Get glossary...");
    	ArrayList<String> gloss = this.getGlossary(word, 0);
    	
    	for(int i=0; gloss!=null && i<gloss.size(); i++)
    		System.out.println("\t"+(i+1)+"'th meaning: "+gloss.get(i));
    	
    	System.out.println("\tRepresentative glossary: "+this.getGlossary_typical(word, 0));
    	//System.exit(0);
    	
    	System.out.println("\n\n2. Get Synsets...");
    	ArrayList<String>[] synset = this.getSynonyms(word, 0);
    	for(int i=0 ; synset!=null && i < synset.length; i++) {
    		System.out.print("\t"+(i+1)+"'th meaning: ");
    		if(synset[i]==null) continue;
    		for(int j=0; j < synset[i].size(); j++)
    			System.out.print(synset[i].get(j)+", ");
    		System.out.println();;
    	}
    	
    	System.out.println("\tRepresentative synonyms: "+this.getSynonyms_typical(word, 0));
    	//System.exit(0);
    	
    	System.out.println("\n\n3. Get Hypernyms...");
    	ArrayList<String>[] hypernym = this.getHypernyms(word, 0);
    	for(int i=0 ; hypernym!=null && i < hypernym.length; i++) {
    		System.out.print("\t"+(i+1)+"'th meaning: ");
    		if(hypernym[i]==null) continue;
    		for(int j=0; j < hypernym[i].size(); j++)
    			System.out.print(hypernym[i].get(j)+", ");
    		System.out.println();;
    	}
    	
    	System.out.println("\tRepresentative hypernyms: "+this.getHypernyms_typical(word, 0));
    	//System.exit(0);
    	
    	System.out.println("\n\n4. Get Hyponyms...");
    	ArrayList<String>[] hyponym = this.getHyponyms(word, 0);
    	for(int i=0 ; hyponym!=null && i < hyponym.length; i++) {
    		System.out.print("\t"+(i+1)+"'th meaning: ");
    		if(hyponym[i]==null) continue;
    		for(int j=0; j < hyponym[i].size(); j++)
    			System.out.print(hyponym[i].get(j)+", ");
    		System.out.println();;
    	}
    	
    	System.out.println("\tRepresentative hyponyms: "+this.getHyponyms_typical(word, 0));
    	
    	String word1 = "compact disk";
    	
    	System.out.println("\n\n5. Stemming...");
    	System.out.println("\t"+word1+" -> "+this.getRootExpression(word1, 1));
    	
    	System.out.println("\n\n6. Existing test... word is "+word1);
    	System.out.println("\t word? -> "+this.isWord(word1));
    	System.out.println("\t noun? -> "+this.isNoun(word1));
    	System.out.println("\t verb? -> "+this.isVerb(word1));
    	System.out.println("\t adjective? -> "+this.isAdjective(word1));
    	System.out.println("\t adverb? -> "+this.isAdverb(word1));
    }
}

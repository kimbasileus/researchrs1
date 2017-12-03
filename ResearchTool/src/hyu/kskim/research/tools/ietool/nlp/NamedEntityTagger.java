/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.ietool.nlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.ArrayList;

public class NamedEntityTagger {
	String serializedClassifier = null;
	AbstractSequenceClassifier<CoreLabel> classifier = null;
	
	public NamedEntityTagger(String dir){
		try{
			if (dir == null)
				serializedClassifier = "D:\\ResearchDevelopment\\Library\\stanford-ner-2016-10-31\\classifiers\\english.muc.7class.distsim.crf.ser.gz";
			else
				serializedClassifier = dir;
			
			// Define NEtagging classifier...
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		}catch(Exception e){
			System.out.println("NETagger() Exception: "+e.getMessage());
		}
	}
	
	
	// XML Tag Type...
	public String taggedSentence(String sentence){ 
		try{
			String taggedSen = null;
			taggedSen = this.classifier.classifyWithInlineXML(sentence);
			
			if(taggedSen!=null) System.out.println(taggedSen);
			
			return taggedSen;
		}catch(Exception e){
			
			return null;
		}
	}
	
	
	// XML Tag List...
	public ArrayList<NETagPair> taggedArrayListSentence(String sentence){ 
		try{
			ArrayList<NETagPair> list = new ArrayList<NETagPair>();
			String taggedSen = this.classifier.classifyToString(sentence, "slashTags", false);
			
			String str[] = null;
			str = taggedSen.split(" ");
			
			String str2[] = null;
			for(int i= 0 ; i < str.length; i++){
				str2 = str[i].split("/");
				if(str2.length!=2) continue;
				
				if(!(str2[1].equals("O")) ) {
				//	System.out.println(str2[0]+", "+str2[1]);
					list.add(new NETagPair(str2[0], str2[1]));
				}
				
			}
			
			//str = null; str2 = null;
			return list;
		}catch(Exception e){
			System.err.println("taggedArrayListSentence() Exception: "+e.getMessage()); 
			return null;
		}
	}
}

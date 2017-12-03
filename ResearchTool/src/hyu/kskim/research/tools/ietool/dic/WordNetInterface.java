/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.ietool.dic;

import java.util.*;
/**
 *
 * @author KyungSoo Kim, Ph.D. Candidate, Hanyang University
 */
public interface WordNetInterface {
    public final String noun = "NOUN";
    public final String verb = "VERB";
    public final String adj = "ADJ";

    public ArrayList<String>  getGlossary(String word, int pos);
    public ArrayList<String>[] getSynonyms(String word, int pos);
    public ArrayList<String>[] getHypernyms(String word, int pos);
    public ArrayList<String>[] getHyponyms(String word, int pos);
    public String getRootExpression(String word, int pos);
    
    public boolean isWord(String word, int pos);
    public boolean isNoun(String word);
    public boolean isVerb(String word);
    public boolean isAdjective(String word);
    public boolean isAdverb(String word);
    
    public int getNumOfMeanings(String word, int pos);
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyu.kskim.research.tools.ietool.nlp;

/**
 *
 * @author KyungSoo Kim
 */
public class POS {
    public String posTag;
    public String word;
    
    public POS(String posTag, String word){
        this.posTag = posTag;
        this.word = word;
    }
}

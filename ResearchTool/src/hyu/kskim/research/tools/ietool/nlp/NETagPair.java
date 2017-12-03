package hyu.kskim.research.tools.ietool.nlp;

public class NETagPair {
	private String word;
	private String neTag;
	
	
	public NETagPair(String word, String neTag) {
		super();
		this.word = word;
		this.neTag = neTag;
	}


	public String getWord() {
		return word;
	}


	public void setWord(String word) {
		this.word = word;
	}


	public String getNeTag() {
		return neTag;
	}


	public void setNeTag(String neTag) {
		this.neTag = neTag;
	}
}

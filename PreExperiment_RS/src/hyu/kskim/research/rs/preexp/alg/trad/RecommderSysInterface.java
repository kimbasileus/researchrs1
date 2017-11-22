package hyu.kskim.research.rs.preexp.alg.trad;

import java.util.ArrayList;

public interface RecommderSysInterface {
	public void initRecommenderEngine();
	public double getRating(int userID, int itemID);
}

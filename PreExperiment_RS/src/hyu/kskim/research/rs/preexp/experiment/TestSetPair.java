package hyu.kskim.research.rs.preexp.experiment;

public class TestSetPair{
	public int userID;
	public int itemID;
	public double actual_rating;
	public double predicted_rating;
	public double prediction_error;
	
	
	public TestSetPair(int userID, int itemID, double rating) {
		this.userID = userID;
		this.itemID = itemID;
		this.actual_rating = rating;
		this.predicted_rating = -1;
		this.prediction_error = -1;
	}
	
	public TestSetPair(int userID, int itemID, double rating, double predicted_rating, double prediction_error) {
		this.userID = userID;
		this.itemID = itemID;
		this.actual_rating = rating;
		this.predicted_rating = predicted_rating;
		this.prediction_error = prediction_error;
	}
}
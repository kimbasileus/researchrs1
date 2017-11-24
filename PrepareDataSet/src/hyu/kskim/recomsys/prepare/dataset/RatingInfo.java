package hyu.kskim.recomsys.prepare.dataset;

public class RatingInfo{
	public int id;
	public int userID;
	public int itemID;
	public int oldItemID;
	public double rating;
	public int timestamp;
	
	public RatingInfo(int no, int userID, int itemID, int movielensID, double rating, int timestamp) {
		super();
		id = no;
		this.userID = userID;
		this.itemID = itemID;
		this.oldItemID = movielensID;
		this.rating = rating;
		this.timestamp = timestamp;
	}
}
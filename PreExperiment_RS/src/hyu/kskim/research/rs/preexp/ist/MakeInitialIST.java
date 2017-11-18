package hyu.kskim.research.rs.preexp.ist;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import hyu.kskim.research.rs.preexp.ds.ISTPair_ADC;
import hyu.kskim.research.rs.utils.DBManager;
import hyu.kskim.research.rs.utils.FileIO;

class Node{
	int index;
	double value;
	
	public Node(int index, double value) {
		this.index = index;
		this.value = value;
	}
}

public class MakeInitialIST {
	FileIO file = new FileIO();
	Node[][] itemList = null; // 각 아이템별로 평점들이 배열 타입으로 연결되어 있는 자료구조
	double[] userAvg = null; // 각 사용자별 평점 Average 값들이 캐싱되어 있는 자료구조
	String dbSchema = null; 
	DBManager db = null;
	
	
	int numOfItems = 0; // 9125
	int numOfUsers = 0; // 671
	
	double threshold = 0.3; // 아이템 유사도의 임계값 (기본값 0.3)
	
	public MakeInitialIST(String dbSchema, int numOfUsers, int numOfItems, double sim_threshold) {
		
		this.itemList = new Node[numOfItems+1][];
		this.userAvg = new double[numOfUsers+1];
		
		this.dbSchema = dbSchema;
		this.db = new DBManager(null, null, null);
		
		
		this.numOfItems = numOfItems;
		this.numOfUsers = numOfUsers;
		this.threshold = sim_threshold;
		
		for(int i=0; i < numOfItems+1; i++)	this.itemList[i] = null;
		
		this.db.connectDB("root", "kyungsookim");
		
	}	
	
	// 주 실행 함수로 이 함수 하나만 실행하면 된다.
	public void run(int testSetRatio) { // ratio =20, 30, 40
		try {
			// 1. User-Item Rating을 Item Vector 타입으로 메모리로 로딩
			for(int item=1; item <=numOfItems; item++) loadUserRatings_from_DB(item);
						
			// 2. User Average 값들을 메모리로 로딩
			loadUserAvg_from_DB();
						
			
			// 3. 각 아이템 쌍에 대하여 Item to Item Similarity를 계산 (Adjusted Cosine Similarity)
			for(int item1=1; item1 <=numOfItems; item1++) {
				DBManager db2 = new DBManager(null, null, null);
				db2.connectDB("root", "kyungsookim");
								
				//ArrayList<String> list = new ArrayList<String>();
				for(int item2=item1+1; item2<=numOfItems; item2++) {
					ISTPair_ADC pair = null;
					pair = getSim_ADC(item1, item2, userAvg, this.threshold);
					// getSim_ADC_2(item1, item2, userAvg, this.threshold);
					
					if(pair==null) continue;
					
					writeIST(pair, 0, db2); // DB에 유사도 및 세부 계산값을 캐싱한다.
				}
				
				db2.getStmt().executeBatch();
				db2.getStmt().close();
				db2.closeDB();
				
				/*
				int n = list.size();
				StringBuffer sb = new StringBuffer();
				for(int j=0; j<n ; j++) {
					if(j!=n-1) sb.append(list.get(j)).append("\n");
					else sb.append(list.get(j));
				}
				
				this.file.writer(dir, sb.toString());
				*/
			}
			
			
		}catch (Exception e) {
			System.err.println("getSim_ADC Exception: "+e.getMessage()+" // "+e.getStackTrace());
		}
	}
	
	// 
	public void loadUserRatings_from_DB(int itemID) {
		// TODO Auto-generated method stub
		String sql = "select userID, rating from `"+this.dbSchema+"`.`ratings_trainset` "
				+ "where itemID = '"+itemID+"' order by (userID) asc;"; // userID에 대한 정렬 반드시 필요
		try {
			ArrayList<Node> list = new ArrayList<Node>();
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int userID; double rating;
			while(rs0.next()) {
				userID = rs0.getInt(1);
				rating = rs0.getDouble(2);
				
				list.add(new Node(userID, rating));
			}
			rs0.close();
			
			this.itemList[itemID] = list.toArray(new Node[list.size()]);
			
			System.out.println("Item "+itemID+" 에 대한 메모리 로딩 완료. "+this.itemList[itemID].length);
		} catch (SQLException e) {
			System.err.println("loadUserRatings_from_DB Exception: "+e.getMessage()+" // "+e.getSQLState());
		}
	}
	
	
	// DB로부터 사용자들의 평점 평균값들을 메모리로 로딩하는 함수
	public void loadUserAvg_from_DB() {
		String sql = "SELECT userID, average FROM "+this.dbSchema+".user_avg order by (userID) asc;"; // userID에 대한 정렬 반드시 필요
		try {
			ResultSet rs0 = this.db.getStmt().executeQuery(sql);
			int userID; double avg;
			while(rs0.next()) {
				userID = rs0.getInt(1);
				avg = rs0.getDouble(2);
				
				this.userAvg[userID] = avg;
				System.out.println("User "+userID+" 평균값 메모리 로딩 완료: "+avg);
			}
			rs0.close();
		} catch (SQLException e) {
			System.err.println("loadUserAvg_from_DB Exception: "+e.getMessage()+" // "+e.getSQLState());
		}
	}
	
	
	// 계산된 아이템 간의 유사도 및 유사도 계산에 사용된 Term들을 DB에 캐싱하는 함수
	public void writeIST(ISTPair_ADC pair, int count, DBManager dm) {
		String sql = "INSERT INTO `"+this.dbSchema+"`.`ist` (`ID`, `item1ID`, `item2ID`, `similarity`, `A`, `B`, `C`, `D`, `E`, `F`, `G`) "
				+ "VALUES ('1', '"+pair.item1ID+"', '"+pair.item2ID+"', '"+pair.sim+"', "
						+ "'"+pair.A+"', '"+pair.B+"', '"+pair.C+"', '"+pair.D+"', '"+pair.E+"', '"+pair.F+"', '"+pair.G+"');"; // userID에 대한 정렬 반드시 필요
		
		try {
			dm.getStmt().addBatch(sql);
			//this.db.getStmt().executeUpdate(sql);
		} catch (Exception e) {
			System.err.println("writeIST Exception: "+e.getMessage());
		}
	}
	
	
	// 두 아이템 간의 유사도를 계산하는 함수 (Adjusted Cosine Similarity)
	public ISTPair_ADC getSim_ADC(int item1ID, int item2ID, double[] userAvg, double threshold) {
		try {
			ISTPair_ADC pair = null;
			
			int n1 = this.itemList[item1ID].length;
			int n2 = this.itemList[item2ID].length;
			
			if(n1==0 || n2==0) return null;
			
			int i =0; int j=0;
			
			double A = 0; double B=0; double C=0; double D=0; double E=0; double F=0; int numOfCommonRatings=0;
			double x = 0; double y=0; int u1;
			do {
				while(i<n1 && j<n2 && (itemList[item1ID][i].index < itemList[item2ID][j].index) ) i++;
				while(i<n1 && j<n2 && (itemList[item1ID][i].index > itemList[item2ID][j].index) ) j++;
				
				// Computation Part......
				if( (i<n1 && j<n2) &&  itemList[item1ID][i].index==itemList[item2ID][j].index) {
					x = itemList[item1ID][i].value;
					y = itemList[item2ID][j].value;
					
					u1 = itemList[item1ID][i].index;
					
					A += x*y;
					B += x*userAvg[u1];
					C += y*userAvg[u1];
					D += userAvg[u1]*userAvg[u1];
					E += x*x;
					F += y*y;	
					numOfCommonRatings++;
					
					i++; j++;
				//	System.out.println(userAvg[u1]);					
				}
				
			}while(i < n1 && j < n2);
			
			double L1 = E-2*B+D;
			double L2 = F-2*C+D;
			
			if(L1<=0 || L2<=0) return null;
			
			L1 = Math.sqrt(L1);
			L2 = Math.sqrt(L2);
			
			double H = A-B-C+D;
			double sim = H/(L1*L2);
			sim = Math.round(sim*100000000.0)/100000000.0; // 소수점 여덟째 자리에서 반올림한다.
			
			//System.out.println("ADC1: <"+item1ID+", "+item2ID+"> sim: "+sim);
			
			if(sim < threshold) return null;
			
			if(sim > 1 && sim < 1.1) sim = 1.0;
			
			pair = new ISTPair_ADC(item1ID, item2ID, A, B, C, D, E, F, numOfCommonRatings, sim);
			
			System.out.println("ADC1: <"+item1ID+", "+item2ID+"> sim: "+sim);
			
			return pair;
		} catch (Exception e) {
			System.err.println("getSim_ADC Exception: "+e.getMessage()+" // "+e.getStackTrace());
			return null;
		}
	}
	
	
	// 두 아이템 간의 유사도를 계산하는 함수 (Adjusted Cosine Similarity)
		public ISTPair_ADC getSim_ADC_2(int item1ID, int item2ID, double[] userAvg, double threshold) {
			try {
				ISTPair_ADC pair = null;
				
				int n1 = this.itemList[item1ID].length;
				int n2 = this.itemList[item2ID].length;
				
				if(n1==0 || n2==0) return null;
				
				
				double x1[] = new double[this.numOfItems+1];
				double y1[] = new double[this.numOfItems+1];
				
				for(int i=0; i < this.numOfItems+1; i++)
				{
					x1[i] = y1[i] = -1;
				}
				
				for(int i=0; i <n1; i++)
				{
					int index = this.itemList[item1ID][i].index;
					double value = this.itemList[item1ID][i].value;
					
					x1[index] = value;
				}
				
				for(int i=0; i <n2; i++)
				{
					int index = this.itemList[item2ID][i].index;
					double value = this.itemList[item2ID][i].value;
					
					y1[index] = value;
				}
				
				
				double A = 0; double B=0; double C=0; double D=0; double E=0; double F=0; int count=0;
				double x = 0; double y=0; int u1;
				for(int i=1; i <= this.numOfItems; i++)
				{
					if(x1[i]==-1 || y1[i]==-1) continue;
					
					x = x1[i];
					y = y1[i];
					
					A += x*y;
					B += x*userAvg[i];
					C += y*userAvg[i];
					D += userAvg[i]*userAvg[i];
					E += x*x;
					F += y*y;	
					count++;
				}
				
				
				double L1 = E-2*B+D;
				double L2 = F-2*C+D;
				
				if(L1<=0 || L2<=0) return null;
				
				L1 = Math.sqrt(L1);
				L2 = Math.sqrt(L2);
				
				double H = A-B-C+D;
				double sim = H/(L1*L2);
				sim = Math.round(sim*100000000.0)/100000000.0; // 소수점 여덟째 자리에서 반올림한다.
				
				System.out.println("ADC2: <"+item1ID+", "+item2ID+"> sim: "+sim);
				
				if(sim < threshold) return null;
				
				if(sim > 1 && sim < 1.1) sim = 1.0;
				
				pair = new ISTPair_ADC(item1ID, item2ID, A, B, C, D, E, F, count, sim);
				return pair;
			} catch (Exception e) {
				System.err.println("getSim_ADC2 Exception: "+e.getMessage()+" // "+e.getStackTrace());
				return null;
			}
		}
}

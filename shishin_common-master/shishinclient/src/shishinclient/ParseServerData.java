package shishinclient;

public class ParseServerData {
	private int[][] myUnit		= new int[4][2];
	private int[][] enemyUnit	= new int[4][2];
	private int[][] obstacle	= new int[6][2];
	private int[]   tower		= new int[5];
	private int[]   score		= new int[2];
	private int teamId;

	public ParseServerData(int team){
		//init
		for(int i = 1;i < 4;i++){
			for(int j = 0;j < 2;j++){
				myUnit[i][j] = -1;
				enemyUnit[i][j] = -1;
				obstacle[i][j] = -1;
			}
		}
		for(int i = 0;i < 5;i++){
			tower[i] = -1;
		}
		score[0] = -1;
		score[1] = -1;
		teamId = team;
	}
	
	public Boolean Parse(String[] data){
		for(int i = 0;i < data.length;i++){
			//System.out.println(data[i]);
			String[] splitData = data[i].split(" ");
			if(splitData[0] == null){
				System.out.println("data is null");
			}else switch(splitData[0]){
			case "401":
				if(splitData[2].equals("0")){
					if(teamId == 0){
						myUnit[Integer.valueOf(splitData[3])][0] = Integer.valueOf(splitData[4]);
						myUnit[Integer.valueOf(splitData[3])][1] = Integer.valueOf(splitData[5]);
					}else{
						enemyUnit[Integer.valueOf(splitData[3])][0] = Integer.valueOf(splitData[4]);
						enemyUnit[Integer.valueOf(splitData[3])][1] = Integer.valueOf(splitData[5]);
					}
				}else if(splitData[2].equals("1")){
					if(teamId == 1){
						myUnit[Integer.valueOf(splitData[3])][0] = Integer.valueOf(splitData[4]);
						myUnit[Integer.valueOf(splitData[3])][1] = Integer.valueOf(splitData[5]);
					}else{
						enemyUnit[Integer.valueOf(splitData[3])][0] = Integer.valueOf(splitData[4]);
						enemyUnit[Integer.valueOf(splitData[3])][1] = Integer.valueOf(splitData[5]);
					}
				}else{
					return false;
				}
				break;
			case "402": //tower
						tower[Integer.valueOf(splitData[2])] = Integer.valueOf(splitData[3]);				
				break;
			case "403":
				
				break;
			case "406": //obstacle
						obstacle[Integer.valueOf(splitData[2])][0] = Integer.valueOf(splitData[3]);
						obstacle[Integer.valueOf(splitData[2])][1] = Integer.valueOf(splitData[4]);				
				break;
			case "202":
				
				break;
			}
		}
		return true;
	}
	
	public int GetUnitPos(int team){
		
		return -1;
	}
	
	public int[] GetMyUnitPos(int num){
		int[] pos = new int[2];
		pos[0] = myUnit[num][0];
		pos[1] = myUnit[num][1];
		return pos;
	}
	
	public int[] GetEnemyUnitPos(int num){
		int[] pos = new int[2];
		pos[0] = enemyUnit[num][0];
		pos[1] = enemyUnit[num][1];
		return pos;
	}
	
	public int[][] GetObstaclePos(){
		return obstacle;
	}
	
	public int[] GetTowerInfo(){
		return tower;
	}
	
	public int[] GetScore(){
		return score;
	}
	
	public int IsFirst(){
		return teamId;
	}
}

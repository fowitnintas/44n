package shishinclient;

import java.util.Random;

/**
 * 
 * @author shogo
 *         
 *This program needs ParseServerData.java.
 *
 */
public class Ai {
	private int[][][] myUnitStatus = new int[4][8][8];
	private int[][][] enemyUnitStatus = new int[4][8][8];
	ParseServerData ParseData = null;
	
	public Ai(int isFirst){
		ParseData = new ParseServerData(isFirst);
	}
	
	
	private int[][] CreateMap(){ //9x9で0を敷き詰めたマップデータ生成。岩は-1。
		
		int[][] obstacle = ParseData.GetObstaclePos();		
		int map[][] = new int[9][9];
		for(int i=0;i<9;i++) { //初期化
			for(int n=0;n<9;n++) map[n][i] = 0;
		}
		for(int i=0;i<4;i++) {
			map[ obstacle[i][0] ][ obstacle[i][1] ] = -1; //岩(-1)配置
		}
/*		System.out.println("ob:"+obstacle[0][0]+obstacle[0][1]);						
		System.out.println("ob:"+obstacle[1][0]+obstacle[1][1]);						
		System.out.println("ob:"+obstacle[2][0]+obstacle[2][1]);						
		System.out.println("ob:"+obstacle[3][0]+obstacle[3][1]);						


		System.out.println("map created-----");						
		for(int x=0;x<9;x++){
			for(int y=0;y<9;y++){
				System.out.print(map[y][x]);						
			}
			System.out.print("\n");						
		}
*/	
		/*
		map[0][2] = -2;
		map[0][6] = -2;
		map[4][4] = -2;
		map[8][2] = -2;
		map[8][6] = -2; //towers
		*/
		
		
		return map;
	}
	
	
	private int[] SearchNearTower(int unitNum){ //[0]にID、[1]に距離を入れて返す
		int map[][] = CreateMap();
		int pos[] = ParseData.GetMyUnitPos(unitNum);
		int cnt = 1;
		int[] towerid = {-1,0};
		int[] towernow = ParseData.GetTowerInfo(); 
		int isFirst = ParseData.IsFirst();
		
		map[pos[0]][pos[1]] = 1; //うにっと初期座標配置
		
		while(true){ //最寄り塔を探索する
			for(int i=0;i<9;i++) {
				for(int n=0;n<9;n++) {
					if(map[n][i] == cnt) { //周囲マスを埋めていく
						if(n>0){ //左端回避
							if(i>0){ //上端回避
								if(map[n-1][i-1] == 0) map[n-1][i-1] = cnt+1;								
							}
							if(i<8){ //下端
								if(map[n-1][i+1] == 0) map[n-1][i+1] = cnt+1;															
							}
								if(map[n-1][i] == 0) map[n-1][i] = cnt+1;
						}
						if(n<8){ //右端
							if(i>0){ //上端回避
								if(map[n+1][i-1] == 0) map[n+1][i-1] = cnt+1;								
							}
							if(i<8){ //下端
								if(map[n+1][i+1] == 0) map[n+1][i+1] = cnt+1;															
							}
								if(map[n+1][i] == 0) map[n+1][i] = cnt+1;							
						}
						
						if(i>0){ //上端回避
							if(map[n][i-1] == 0) map[n][i-1] = cnt+1;								
						}
						if(i<8){ //下端
							if(map[n][i+1] == 0) map[n][i+1] = cnt+1;															
						}
					}
				}
			}
//			if(removeMytower){ //相手or空白塔の中での最寄りを探す
				if (!(map[0][2] == 0) && !(towernow[0]==isFirst)) {
					towerid[0] = 1;
					towerid[1] = cnt; 
					break;
				}else if(!(map[0][6] == 0)&& !(towernow[2]==isFirst)){
					towerid[0] = 3;
					towerid[1] = cnt; 
					break;
				}else if(!(map[4][4] == 0)&& !(towernow[4]==isFirst)){
					towerid[0] = 5;
					towerid[1] = cnt; 
					break;
				}else if(!(map[8][2] == 0)&& !(towernow[1]==isFirst)){
					towerid[0] = 2;
					towerid[1] = cnt; 
					break;
				}else if(!(map[8][6] == 0)&& !(towernow[3]==isFirst)){
					towerid[0] = 4;
					towerid[1] = cnt; 
					break;  //塔来たらbreak吐く		
				}
/*			}else{ // とにかく最寄りを探す
				if (!(map[0][2] == 0) ) {
					towerid[0] = 1;
					towerid[1] = cnt; 
					break;
				}else if(!(map[0][6] == 0)){
					towerid[0] = 3;
					towerid[1] = cnt; 
					break;
				}else if(!(map[4][4] == 0)){
					towerid[0] = 5;
					towerid[1] = cnt; 
					break;
				}else if(!(map[8][2] == 0)){
					towerid[0] = 2;
					towerid[1] = cnt; 
					break;
				}else if(!(map[8][6] == 0)){
					towerid[0] = 4;
					towerid[1] = cnt; 
					break;  //塔来たらbreak吐く
				}
				*/
				cnt++;
			}
/*		System.out.println("SNT-loopend"+ cnt );
		for(int x=0;x<9;x++){
			for(int y=0;y<9;y++){
				System.out.print(map[y][x]);						
			}
			System.out.print("\n");						
		}
*/

		return towerid;
	}

	public int[] SearchNearEnemyUnit(int unitNum){
		
		return null;
	}
	
	public int[] GotoNearTower(String[] serverRes){
		ParseData.Parse(serverRes);
		int[] intower = {-1,-1,-1,-1}; //-1自陣　1塔
		int[] towernow = ParseData.GetTowerInfo(); 
		int map[][] = CreateMap();
		int[] pos = new int[2];
		int[][] nearTower = new int[4][2];
		int[] snt = new int[2];
		

//		System.out.println("Tower:" + towernow[0] + towernow[1] + towernow[2] + towernow[3] + towernow[4]);
		
		int[] nearestUnit={-1,-1,10}; //[0:unitID][1:towerID][2:dist]
		int[][] equalDistUnit = new int[4][3]; //[unitID][0:flag-1:towerID-2:dist]
		
		System.out.println("dataset ok");

		for(int i=0;i<4;i++){
			snt = SearchNearTower(i);
//			System.out.println("SNT ok");
			nearTower[i][0] = snt[0]; //ID
			nearTower[i][1] = snt[1]; //距離
			
			if(nearestUnit[2] > nearTower[i][1]){ //最も塔に近いユニット選択
				nearestUnit[0] = i;
				nearestUnit[1] = nearTower[i][0];
				nearestUnit[2] = nearTower[i][1]; 
				for(int n=0;n<4;n++) equalDistUnit[n][0] = 0;	//同距離消			
			}else if(nearestUnit[2] == nearTower[i][1]){ //同距離ユニットがいたら保持する
				equalDistUnit[i][0] = 1;
				equalDistUnit[i][1] = nearTower[i][0];
				equalDistUnit[i][2] = nearTower[i][1];
			}else{
				equalDistUnit[i][0] = 0;			//同距離フラグ折り
			}
		} 
		System.out.println("nearestUnit ok ID:"+nearestUnit[0]+"tow:"+nearestUnit[1]+"dis:"+nearestUnit[2]);
		
		int[] data = {0,0,0};
		data[0] = -1;
		
		boolean onlydist = true;
		for(int i=0;i<4;i++){
				pos = ParseData.GetMyUnitPos(i); //ユニットが塔にいるかどうか見る
				if( (pos[0]==0 && pos[1]==2) ||
					(pos[0]==0 && pos[1]==6) ||
					(pos[0]==4 && pos[1]==4) ||
					(pos[0]==8 && pos[1]==2) ||
					(pos[0]==8 && pos[1]==6) )
					intower[i] = 1; //とうのなかにいます
				else if ((pos[0]==4 && pos[1]==8) ||
						 (pos[0]==4 && pos[1]==0) )
					intower[i] = -1;
			if(equalDistUnit[i][0] == 1) onlydist = false;
		}
		
		
///////////////////////ai
		
		if(onlydist) { //同距離一つだけなら確定できる
			
		} else { //二つ以上あるなら動かす駒を考える
			for(int i=0;i<4;i++){
				if(equalDistUnit[i][0] ==1){ //競合している相手と比較して
					if(intower[i] < intower[ nearestUnit[0] ]){ //強豪相手の方が優先度で勝るなら採用
																//優先度：塔＜場＜自陣
						nearestUnit[0] = i;
						nearestUnit[1] = equalDistUnit[i][1];
						nearestUnit[2] = equalDistUnit[i][2]; 						
					}
					else if(intower[i] < intower[ nearestUnit[0] ]){ //同じ優先度なら
						//同じ優先度での駒処理
					}
				}
			}
			
		}
		
///////////////////////
		System.out.println("unitselect ok-ID:"+nearestUnit[0]);
		//nearestUnitで指定したユニットを指定塔まで動かす。
		pos = ParseData.GetMyUnitPos(nearestUnit[0]);
		data[0] = nearestUnit[0];
				switch(nearestUnit[1]){
					case 3:	//左下tower
						if(pos[0] == 1 && pos[1]==6){
							data[1] = pos[0]- 1;
							data[2] = pos[1]; //左へ							
						}
						else if(pos[0] > 0 && pos[1]<=6){
							data[1] = pos[0] - 1;
							data[2] = pos[1] + 1; //左下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] -= 1; //左直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] += 1;
										data[2] += 1; //下直進
									}
							}
						}
						else if(pos[0] > 0 && pos[1]>6 ){
							data[1] = pos[0] - 1;
							data[2] = pos[1] - 1; //左上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] += 1; //左直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] += 1;
										data[2] -= 1; //上直進
									}
							}
						}
						else if(pos[0] == 0 && pos[1]==7){
							data[1] = pos[0];
							data[2] = pos[1] - 1 ; //上へ							
						}
						else if(pos[0] == 0 && pos[1]==5){
							data[1] = pos[0];
							data[2] = pos[1] + 1 ; //下へ							
						}
						else if(pos[0] == 0 && pos[1]<=6){
							data[1] = pos[0] + 1;
							data[2] = pos[1] + 1; //右下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] -= 1; //下直進
							}
						}
						else if(pos[0] == 0 && pos[1]>6 ){
							data[1] = pos[0] + 1;
							data[2] = pos[1] - 1; //右上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] -= 1; //上直進
							}
						}
						break;
					case 1:	//左上tower
						if(pos[0] == 1 && pos[1]==2){
							data[1] = pos[0]- 1;
							data[2] = pos[1]; //左へ							
						}
						else if(pos[0] > 0 && pos[1]<=2){
							data[1] = pos[0] - 1;
							data[2] = pos[1] + 1; //左下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] -= 1; //左直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] += 1;
										data[2] += 1; //下直進
									}
							}
						}
						else if(pos[0] > 0 && pos[1]>2 ){
							data[1] = pos[0] - 1;
							data[2] = pos[1] - 1; //左上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] += 1; //左直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] += 1;
										data[2] -= 1; //上直進
									}
							}
						}
						else if(pos[0] == 0 && pos[1]==3){
							data[1] = pos[0];
							data[2] = pos[1] - 1 ; //上へ							
						}
						else if(pos[0] == 0 && pos[1]==1){
							data[1] = pos[0];
							data[2] = pos[1] + 1 ; //下へ							
						}
						else if(pos[0] == 0 && pos[1]<=2){
							data[1] = pos[0] + 1;
							data[2] = pos[1] + 1; //右下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] -= 1; //下直進
							}
						}
						else if(pos[0] == 0 && pos[1]>2 ){
							data[1] = pos[0] + 1;
							data[2] = pos[1] - 1; //右上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] -= 1; //上直進
							}
						}
						break;
						
					case 5://中央
						if(pos[0] == 4 && pos[1]==3){ //特例４つ
							data[1] = pos[0];
							data[2] = pos[1] + 1; //下へ							
						}
						else if(pos[0] == 4 && pos[1]==5){
							data[1] = pos[0];
							data[2] = pos[1] - 1; //上へ							
						}
						else if(pos[0] == 3 && pos[1]==4){
							data[1] = pos[0] + 1; //右へ							
							data[2] = pos[1];
						}
						else if(pos[0] == 5 && pos[1]==4){
							data[1] = pos[0] - 1; //左へ							
							data[2] = pos[1];
						}
						else if(pos[0] > 4 && pos[1]<=4){
							data[1] = pos[0] - 1;
							data[2] = pos[1] + 1; //左下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] -= 1; //左直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] += 1;
										data[2] += 1; //下直進
									}
							}
						}
						else if(pos[0] > 4 && pos[1]>4 ){
							data[1] = pos[0] - 1;
							data[2] = pos[1] - 1; //左上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] += 1; //左直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] += 1;
										data[2] -= 1; //上直進
									}
							}
						}
						else if(pos[0] <= 4 && pos[1]<=4){
							data[1] = pos[0] + 1;
							data[2] = pos[1] + 1; //右下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] -= 1; //右直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] -= 1;
										data[2] += 1; //下直進
									}
							}
						}
						else if(pos[0] <= 4 && pos[1]>4 ){
							data[1] = pos[0] + 1;
							data[2] = pos[1] - 1; //右上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] += 1; //右直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] -= 1;
										data[2] -= 1; //上直進
									}
							}
						}
						break;
						
					case 4: //右下tw
						if(pos[0] == 7 && pos[1]==6){
							data[1] = pos[0] + 1; //R
							data[2] = pos[1];
						}						
						else if(pos[0] == 8 && pos[1]==5){
							data[1] = pos[0];
							data[2] = pos[1] + 1; //D
						}						
						else if(pos[0] == 8 && pos[1]==7){
							data[1] = pos[0];
							data[2] = pos[1] - 1; //U
						}						
						else if(pos[0] == 8 && pos[1]<6){
							data[1] = pos[0] - 1;
							data[2] = pos[1] + 1; //左下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] += 1; //下直進
							}
						}
						else if(pos[0] == 8 && pos[1]>6 ){
							data[1] = pos[0] - 1;
							data[2] = pos[1] - 1; //左上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] += 1; //上直進
							}
						}
						else if(pos[0] < 8 && pos[1]<=6){
							data[1] = pos[0] + 1;
							data[2] = pos[1] + 1; //右下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] -= 1; //右直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] -= 1;
										data[2] += 1; //下直進
									}
							}
						}
						else if(pos[0] < 8 && pos[1]>6 ){
							data[1] = pos[0] + 1;
							data[2] = pos[1] - 1; //右上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] += 1; //右直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] -= 1;
										data[2] -= 1; //上直進
									}
							}
						}
						break;
					case 2: //右上tw
						if(pos[0] == 7 && pos[1]==2){
							data[1] = pos[0] + 1; //R
							data[2] = pos[1];
						}						
						else if(pos[0] == 8 && pos[1]==1){
							data[1] = pos[0];
							data[2] = pos[1] + 1; //D
						}						
						else if(pos[0] == 8 && pos[1]==3){
							data[1] = pos[0];
							data[2] = pos[1] - 1; //U
						}						
						else if(pos[0] == 8 && pos[1]<2){
							data[1] = pos[0] - 1;
							data[2] = pos[1] + 1; //左下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] += 1; //下直進
							}
						}
						else if(pos[0] == 8 && pos[1]>2 ){
							data[1] = pos[0] - 1;
							data[2] = pos[1] - 1; //左上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[1] += 1; //上直進
							}
						}
						else if(pos[0] < 8 && pos[1]<=2){
							data[1] = pos[0] + 1;
							data[2] = pos[1] + 1; //右下へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] -= 1; //右直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] -= 1;
										data[2] += 1; //下直進
									}
							}
						}
						else if(pos[0] < 8 && pos[1]>2 ){
							data[1] = pos[0] + 1;
							data[2] = pos[1] - 1; //右上へ
							if(map[data[1]] [data[2]] == -1){ //岩なら
								data[2] += 1; //右直進
									if(map[data[1]] [data[2]] == -1){ //まだ岩なら
										data[1] -= 1;
										data[2] -= 1; //上直進
									}
							}
						}
						break;
					
					
				}				
							
		return data;
	
	}
	
	public int[] RandomMoveUnit(String[] serverRes){
		ParseData.Parse(serverRes);
		Random rnd = new Random();
		int[] data = {0,0,0};
		data[0] = rnd.nextInt(4);
		int[] pos = ParseData.GetMyUnitPos(rnd.nextInt(4));
		data[1] = pos[0];
		data[2] = pos[1];
		switch(rnd.nextInt(4)){
		case 0:
			data[1]++;
			data[2]--;
			break;
		case 1:
			data[1]++;
			data[2]++;
			break;
		case 2:
			data[1]--;
			data[2]++;
			break;
		case 3:
			data[1]--;
			data[2]--;
			break;
		default:
			break;
		}
		return data;
	}
	
	
	/**
	 * @param myUnit :unit number
	 * @param enemyUnit :unit number
	 * @return 
	 * 1:win
	 * 0:draw
	 * -1:lose
	 * -2:missing arguments
	 */
	/**memo green:0 gray:1 red:2 yellow:3 
	 * 0->1
	 * 1->2
	 * 2->3
	 * 3->0
	 */
	public int IsWinnable(int myUnit,int enemyUnit){
		if(3 < myUnit || 3 < enemyUnit)return -2;
		switch(myUnit){
		case 0:
			if((myUnit + 3) == enemyUnit)return -1;
		case 1:
		case 2:
			if((myUnit + 1) == enemyUnit)return 1;	
		case 3:
			if((myUnit - 3) == enemyUnit)return 1;
			if((myUnit - 1) == enemyUnit)return -1;
			return 0;
		default:
			return -2;
		}
	}
}

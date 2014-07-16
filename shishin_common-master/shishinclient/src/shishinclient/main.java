package shishinclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
Shishin Server ConneSct Example
*/

public class main {
	//デフォルトポートの設定。
	//public static String serverAddress	= "192.168.43.79";
	public static String serverAddress	= "localhost";
	public static int serverPort		= 13306;
	public static String userName		= "Hakanai03";
	
	public static void main(String[] args){
		//コマンドライン引数からポート・アドレス・ユーザーネームの書き換えを行えるように。
		for(int i = 0;i < args.length;i++){
			System.out.println(i + ":" + args[i]);
			if((i+1) < args.length){
				if(args[i] == null){
					System.out.println("Unknown error.");
				}else switch(args[i]){
					case "-p":
						try{
							serverPort = Integer.valueOf(args[i+1]);
							System.out.println("");
						} catch(NumberFormatException e){
							System.out.println("Invalid port \"" + args[i+1] + "\"");
							System.exit(-1);
						}
						i++;
						break;
					case "-a":
						serverAddress = args[i+1];
						i++;
						break;
					case "-u":
						userName = args[i+1];
						i++;
						break;
					default:
						System.out.println("Invalid argument \"" + args[i] + "\"\n"
								+ "-p:port\n-a:address\n-u:username");
						System.exit(-1);
						break;
				}
			}
		}
		ShishinCommonClass SCClass = new ShishinCommonClass(userName,serverAddress,serverPort);
		//不要な場合、ポートを省略することが可能、省略した場合デフォルトのポートである13306に接続される。
		//また、ポートが不正な値の範囲の場合デフォルトのポートに設定される。

		if(SCClass.Login()){
			System.out.println("login success");
			switch(SCClass.IsFirst()){
			case 1:
				System.out.println("enemy waiting...");
				SCClass.WaitMyTurn();
			case 0:
				//サーバーデータをパースするクラスをインスタンス化
				ParseServerData ParseData = new ParseServerData(SCClass.IsFirst());
				//AIクラスをインスタンス化
				Ai Ai = new Ai(SCClass.IsFirst());
				while(true){
					System.out.print("Waiting Command:");
					String Command = WaitCommand();
					if(Command == null){
						
					}else switch(Command){
					case "q":
						SCClass.Logout();
						System.exit(0);
						break;
					case "c":
						System.out.print("SendMsg:");
						SCClass.SendMsg(WaitCommand());
						break;
					case "m":
						System.out.print("moveUnitNumber:");
						int unitNum = Integer.valueOf(WaitCommand());
						if(unitNum < 0|| 4 < unitNum)break;
						System.out.print("move direction(w,a,s,d):");
						String direction = WaitCommand();
						String data[] = SCClass.GetData();
						//データをパース
						if(ParseData.Parse(data))System.out.println("parse success");
						else System.out.println("parse failed");						
						int myUnitPos[] = ParseData.GetMyUnitPos(unitNum);
						if(direction == null){
							break;
						}else switch(direction){
						case "w":
							myUnitPos[1]--;
							break;
						case "a":
							myUnitPos[0]--;
							break;
						case "s":
							myUnitPos[1]++;
							break;
						case "d":
							myUnitPos[0]++;
							break;
						case "wd":
						case "dw":
							myUnitPos[0]++;
							myUnitPos[1]--;
							break;
						case "ds":
						case "sd":
							myUnitPos[0]++;
							myUnitPos[1]++;
							break;
						case "sa":
						case "as":
							myUnitPos[0]--;
							myUnitPos[1]++;
							break;
						case "aw":
						case "wa":
							myUnitPos[0]--;
							myUnitPos[1]--;
							break;
						default:
							break;
						}
						System.out.println("Unit:" + unitNum + " X:" + myUnitPos[0] + " Y:" + myUnitPos[1]);
						if(SCClass.MoveUnit(unitNum, myUnitPos[0], myUnitPos[1])){
							System.out.println("unit move success");
						}else{
							System.out.println("unit move failed");
						}
						
						System.out.println("enemy waiting...");
						SCClass.WaitMyTurn();
						break;
					case "iswin":
						System.out.println(Ai.IsWinnable(Integer.valueOf(WaitCommand()), Integer.valueOf(WaitCommand())));
						break;
					case "dr":
						SCClass.DebugReadData();
						break;
					case "ai":
						//AIを実行します。
						while(true){
							System.out.println("Getdatacheck");
							String serverRes[] = SCClass.GetData();
							System.out.println("Ai Start");
							int[] randData = Ai.GotoNearTower(serverRes);
							System.out.println(" Unit:" + randData[0] + " X:" + randData[1] + " Y:" + randData[2]);
							SCClass.MoveUnit(randData[0], randData[1], randData[2]);
/*							if(!(SCClass.MoveUnit(randData[0], randData[1], randData[2]))) {
								randData = Ai.RandomMoveUnit(serverRes);
								System.out.println("*Ran* Unit:" + randData[0] + " X:" + randData[1] + " Y:" + randData[2]);
							}
*/
							SCClass.WaitMyTurn();
						}
					case "airandom":
						//AI-randomを実行します。
						while(true){
							String serverRes[] = SCClass.GetData();
							int[] randData = Ai.RandomMoveUnit(serverRes);
							System.out.println(" Unit:" + randData[0] + " X:" + randData[1] + " Y:" + randData[2]);
							SCClass.MoveUnit(randData[0], randData[1], randData[2]);
							SCClass.WaitMyTurn();
						}
					default:
						
						break;
					}
				}
			case -1:
			default:
				System.out.println("unknown error");
				break;
			}
		}else{
			System.out.println("login failed");
		}
	}
	
	public static String WaitCommand(){
		String str = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
			str = br.readLine();
		}catch(IOException e){
			return null;
		}
	    return str;
	}
}

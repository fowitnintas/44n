package shishinclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class ShishinCommonClass {
	//class default variable
	public static final String LOG_HEAD		= "[ShishinCommonClass] ";
	public static final String HEAD_SPACE	= "                     ";
	public static final String STR_RED		= "\u001b[31m";
	public static final String STR_YELLOW	= "\u001b[33m";
	public static final String STR_DEFAULT	= "\u001b[m";
	
	//network
	TCPSocketStream SStream;
	
	//variable
	private String userName;
	private int teamId = -1;
	private Boolean isLogin = false;
	
	/**
	 * ShishinCommonClass
	 * @param userName
	 * @param serverAddress
	 */
	public ShishinCommonClass(String name,String address){
		System.out.println(LOG_HEAD + "ShishinCommonClass was loaded successfully.");
		SStream = new TCPSocketStream(address,13306);
		userName = name;
	}
	
	/**
	 * 
	 * @param userName
	 * @param serverAddress
	 * @param serverPort
	 */
	public ShishinCommonClass(String name,String address,int port){
		System.out.println(LOG_HEAD + "ShishinCommonClass was loaded successfully.");
		if(port > 65535 || port <= 0){
			System.out.println(STR_YELLOW + LOG_HEAD + "Warning:Change to the default port, because the port is invalid." + STR_DEFAULT);
			SStream = new TCPSocketStream(address,13306);
		}else{
			SStream = new TCPSocketStream(address,port);
		}
		userName = name;
	}
	
	//login
	public Boolean Login(){
		if(SStream != null){
			if(SStream.Connection()){
				if(SStream.Send("101 NAME " + userName)){
					if(SStream.Read(1).equals("200 OK")){
						System.out.print(LOG_HEAD + "Login successful. ");
						String teamIdStr = SStream.Read(1);
						if(teamIdStr.equals("102 TEAMID 0")){
							System.out.println("Go First.");
							teamId = 0;
							System.out.print(LOG_HEAD + "Waiting for a enemy... ");
							String[] enemyName = SStream.Read(1).split(" ");
							System.out.println("Enemy name is " + enemyName[2] + ".");
							if(!SStream.Read(1).equals("404 DOPLAY"))return false;
						}else if(teamIdStr.equals("102 TEAMID 1")){
							System.out.println("Go Second.");
							teamId = 1;
							System.out.print(LOG_HEAD + "Enemy name is ");
							String[] enemyName = SStream.Read(1).split(" ");
							System.out.println(enemyName[2] + ".");
						}else{
							System.out.println(LOG_HEAD + "Unknown response from Shishin server.");
							return false;
						}
						isLogin = true;
						return true;
					}else{
						System.out.println(LOG_HEAD + "Login failed.");
						return false;
					}
				}else{
					System.out.println(STR_RED + LOG_HEAD + "OutputStream has not been initialized." + STR_DEFAULT);
					return false;
				}
			}else{
				System.out.println(STR_RED + LOG_HEAD + "Connection failed.");
				System.out.println(LOG_HEAD + "Please Check the server port and address."
						+ " After that, Try to running the following function.\n"
						+ HEAD_SPACE + "int RewriteServerAddress(String serverAddress)\n"
						+ HEAD_SPACE + "int RewriteServerPort(int serverPort)" + STR_DEFAULT);
				return false;
			}
		}else{
			System.out.println(STR_RED + LOG_HEAD + "The instance for connection was destroyed by an unknown error." + STR_DEFAULT);
			return false;
		}
	}
	
	//logout
	public Boolean Logout(){
		isLogin = false;
		if(SStream.Close()){
			System.out.println(LOG_HEAD + "Logout successful.");
			return true;
		}else{
			System.out.println(STR_YELLOW + LOG_HEAD + "Warning:Some error might have occurred while logged out." + STR_DEFAULT);
			return false;
		}
	}
	
	//Login Status
	public Boolean IsLogin(){
		return isLogin;
	}
	
	//first or second
	public int IsFirst(){
		return teamId;
	}
	
	//Send Message
	public Boolean SendMsg(String text){
		return SStream.Send("600 " + "[" + userName + "] " + text);
	}
	
	//get board data
	public String[] GetData(){
		SStream.Send("400 GETBORD");
		String returnData[] = new String[20];
		String readString = "";
		do {
			readString = SStream.Read(1);
			String[] dataSplit = readString.split(" ");
			if(dataSplit[0].equals("600")){
				System.out.println(LOG_HEAD + "Chat:" + readString);
			}else break;
		}while(true);
		if(readString.equals("201 MULTILINE")){
			String data;
			int i = 0;
			do{
				data = SStream.Read(1);
				/*String[] dataSplit = data.split(" ");
				switch(dataSplit[0]){
				case "401":
					
					break;
				case "402":
					
					break;
				case "403":
					
					break;
				case "406":
					
					break;
				case "202":
					
					break;
				default:
					System.out.println(STR_YELLOW + LOG_HEAD + "Warning:Unknown Text Format:" + data + STR_DEFAULT);
					break;
				}*/
				returnData[i] = data;
				i++;
			}while(!data.equals("202 LINEEND"));
			if(data.equals("")){
				return null;
			}else{
				return returnData;
			}
		}else{
			System.out.println(STR_YELLOW + LOG_HEAD + "Warning:Unknown Text Format:" + readString + STR_DEFAULT);
			return null;
		}
	}
	
	//send request and get return data
	public String SendRequest(String req){
		SStream.Send(req);
		return null;
	}
	
	//move Unit
	public Boolean MoveUnit(int unitNum,int posX,int posY){
		SStream.Send("405 PLAY " + unitNum + " " + posX + " " + posY);
		String res = SStream.Read(1);
		while(true){
			if(res.equals("200 OK")){
				return true;
			}else if(res.equals("304 NOT YOURE TURN")||res.equals("303 UNIT COULD NOT MOVE")||res.equals("300 MESSAGE SYNTAX ERROR")){
				System.out.println(STR_YELLOW + LOG_HEAD + "SERVER:" + res + STR_DEFAULT);
				return false;
			}else{
				System.out.println(STR_YELLOW + LOG_HEAD + "SERVER:" + res + STR_DEFAULT);
				res = SStream.Read(1);
			}
		}
	}
	
	public void WaitPhaseEnd(){
		while(true){
			String res = SStream.Read(1);
			if(res.equals("501 PHASEEND")){
				return;
			}
		}
	}
	
	public void WaitEnemyPlayEnd(){
		while(true){
			String res = SStream.Read(1);
			if(res.equals("500 PLAYED")){
				return;
			}
		}
	}
	
	public void WaitMyTurn(){
		while(true){
			String res = SStream.Read(1);
			if(res.equals("404 DOPLAY")){
				return;
			}
		}
	}
	
	//Debug Read Stream func
	public void DebugReadStream(int readLine){
		String readData = SStream.Read(readLine);
		if(readData != null){
			System.out.println("Readed success.");
			System.out.println(readData);
		}else{
			System.out.println("Readed failed.");
		}
	}
	
	//debug read data
	public void DebugReadData(){
		String res = SStream.Read(1);
		System.out.println(LOG_HEAD + "DEBUG:" + res);
	}
	
	/********************Socket Stream Connection class********************/
	/**
	 * 
	 * @author shogo
	 *
	 */
	private class TCPSocketStream{
		private int serverPort;
		private String serverAddress;
		private Boolean isStarted = false;
		
		//private OutputStream serverOutput;
		
		//instance
		private Socket socket;
		private BufferedReader inputStream;
		private PrintWriter outputStream;
		
		TCPSocketStream(String address,int port){
			System.out.print(LOG_HEAD + "Socket Connecting...");
			serverAddress	= address;
			serverPort		= port;
		}
		
		Boolean Connection(){
			if(!isStarted){
				try {
					System.out.print(".");
					socket = new Socket(serverAddress, serverPort);
					System.out.print(".");
					//serverOutput = serverSocket.getOutputStream();
					outputStream	= new PrintWriter(socket.getOutputStream(), true);
					inputStream		= new BufferedReader(new InputStreamReader(socket.getInputStream()));
				} catch(SocketException e){
					System.out.println(STR_RED + "TCP Connection Error." + STR_DEFAULT);
					return false;
				} catch(UnknownHostException e){ 
					System.out.println(STR_RED + "The host \"" + serverAddress + ":" + serverPort + "\" could not be found." + STR_DEFAULT);
					return false;
				} catch(IOException e){
					System.out.println(STR_RED + "Unknown Error." + STR_DEFAULT);
					return false;
				}
				String res = SStream.Read(1);
				if(res == null)return false;
				if(res.equals("100 HELLO")){
					System.out.println("Successful.");
					isStarted = true;
					return true;
				}else{
					System.out.println("failed.");
					return false;
				}
			}else{
				return false;
			}
		}
		
		Boolean Send(String SendText){
			if(outputStream == null){
				return false;
			}else{
				outputStream.println(SendText);
				outputStream.flush();
				return true;
			}
		}
		
		String Read(int loop){
			if(loop == 0)return null;
			try {
				String line = "";
				String multiLine = "";
				int i = 1;
				if(loop == 1){
					line = inputStream.readLine();
					return line;
				}else{
					while((line = inputStream.readLine()) != null){
						if(i == loop){
							StringBuilder buf = new StringBuilder();
							buf.append(multiLine);
							buf.append(line);
							multiLine = buf.toString();
							return multiLine;
						}else{
							StringBuilder buf = new StringBuilder();
							buf.append(multiLine);
							buf.append(line);
							multiLine = buf.toString();
							i++;
						}
					}
				}
				return line;
			} catch(IOException e){
				System.out.println(STR_RED + LOG_HEAD + "Failed to connect to the InputStream." + STR_DEFAULT);
				return null;
			}
		}
		
		
		Boolean Close(){
			if(isStarted){
				Send("203 EXIT");
				isStarted = false;
				try {
					outputStream.close();
					inputStream.close();
					socket.close();
				} catch(IOException e){
					return false;
				}
				return true;
			}else{
				return false;
			}
		}
	}
	
	
	public int RewriteServerAddress(String serverAddress){
		System.out.println("This feature is not implemented yet.");
		//System.out.println(LOG_HEAD + "Rewriting the server address was successfully.");
		return -1;
	}
	
	public int RewriteServerPort(int serverPort){
		System.out.println("This feature is not implemented yet.");
		//System.out.println(LOG_HEAD + "Rewriting the server port was successfully.");
		return -1;
	}
}

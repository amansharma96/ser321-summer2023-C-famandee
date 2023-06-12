package Assign32starter;
import java.net.*;
import java.util.Base64;
import java.util.Set;
import java.util.Stack;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;
import java.io.*;
import org.json.*;


public class SockServer {

	public static int seconds = 0;
	public static int q_num = 0;
	public static Timer game_timer;

	private static Map<String, Integer> scores = new HashMap<>();


	public static void main (String args[]) {


		Socket sock;
		try {

			ServerSocket serv = new ServerSocket(9000);
			System.out.println("Server ready for connetion");

			String name = "";
			int points = 0;
			String num_q = "";
			int turn = 0;
			int internal_count = 0;
			boolean game_started = false;
			int more_count = 0;
			boolean end_game = false;

			boolean game_check;


			while(true) {
				sock = serv.accept();

				ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
				OutputStream out = sock.getOutputStream();

				String s = (String) in.readObject();
				JSONObject json = new JSONObject(s);

				JSONObject response = new JSONObject();

				if (json.getString("type").equals("start")){

					System.out.println("- Got a start");
					//signals for name collection
					response.put("type","hello" );
					response.put("value","Hello, please tell me your name..." );
					sendPic("img/hi.png", response);
					scores.put(name, 0);


				}
				else if (json.getString("type").equals("name")){
					response.put("type", "name");
					response.put("name", json.getString("name"));
					name = json.getString("name");
					System.out.println("Users name is: " + name);
				}
				else if (json.getString("type").equals("leaderboard")) {
					System.out.println("Received leaderboard request");
					response.put("type", "leaderboard");
					System.out.println(json.getString("check"));
					if (json.getString("check").equals("no")) {
						response.put("score", "");
					} else {
						JSONArray leaderboard = new JSONArray();
						for (Map.Entry<String, Integer> entry : scores.entrySet()) {
							JSONObject userScore = new JSONObject();
							userScore.put("username", entry.getKey());
							userScore.put("score", entry.getValue());
							leaderboard.put(userScore);
						}
						String result = leaderboard.toString();
						response.put("score", result);
					}
				}
				else if (json.getString("type").equals("n_question")){
					System.out.println("here in q");
					response.put("type", "n_question");
					response.put("q_num", json.getString("q_num"));
					System.out.println( "receiving----> " + response);
					boolean test = true;

					num_q = response.getString("q_num");

					for(int i = 0; i < response.getString("q_num").length();i++){
						if(!Character.isDigit(response.getString("q_num").charAt(i))){
							test = false;
							System.out.println("fails digit check");
						}
					}

					try{
						if(response.get("q_num").equals("")){
							System.out.println("In try, failure - > not a string");
							response.put("type", "error1");
							response.put("message", "You didnt enter in anything");


						}
						else if(test == false){
							System.out.println("failure -->  digit check failed earlier");
							response.put("type", "error1");
							response.put("message", "must enter a digit");

						}
						else if (Integer.parseInt((String) response.get("q_num")) > 7) {
							System.out.println("In try, failure - > greater than 7");
							response.put("type", "error1");
							response.put("message", "Input must be 7 or less");

						}
						else{
							q_num = Integer.parseInt((String) response.get("q_num"));
							response.put("q_num", "Alright " + name + ", " + num_q + " questions it is! Type \"start\" when you are ready to play.");

						}}catch (NumberFormatException e){
						response.put("type", "error1");
						response.put("message", "You didnt enter any thing in? must enter a integer");
					}
					if(q_num == 0){
						response.put("type", "error1");
						response.put("message", "must enter value larger than 0");
					}
					System.out.println("q_num = " + q_num);
				}

				else if (json.getString("type").equals("begin")){
					String[] photo_arr = new String[] {"img/berlin1.jpg","img/berlin2.jpg","img/berlin3.jpg",
							"img/berlin4.jpg","img/ireland1.jpg","img/ireland2.jpg",
							"img/ireland3.jpg","img/ireland4.jpg","img/paris1.jpg",
							"img/paris2.jpg","img/paris3.jpg","img/paris4.jpg",
							"img/phoenix1.jpg","img/phoenix2.jpg","img/phoenix3.jpg",
							"img/phoenix4.jpg","img/rome1.jpg","img/rome2.jpg", "img/rome3.jpg",
							"img/rome4.jpg", "img/san fransisco1.jpg", "img/san fransisco2.jpg", "img/san fransisco3.jpg",
							"img/san fransisco4.jpg", "img/switzerland1.jpg", "img/switzerland2.jpg",
							"img/switzerland3.jpg", "img/switzerland4.jpg"};

					response.put("new_game", "old");

					int[] startingPositions = {4, 8, 12, 16, 20, 24};
					int start_of_ireland = startingPositions[0];
					int start_of_paris = startingPositions[1];
					int start_of_phoenix = startingPositions[2];
					int start_of_rome = startingPositions[3];
					int start_of_san_fransisco = startingPositions[4];
					int start_of_switzerland = startingPositions[5];

					response.put("type", "begin");
					response.put("payload", json.getString("payload"));
					System.out.println( "receiving----> " + response);
					String user_input = response.getString("payload");
					System.out.println(user_input);

					if(!user_input.equals("start") && turn == 0){
						System.out.println("In try, failure - > not a string");
						response.put("type", "error2");
						response.put("message", "enter start when ready to start");
					}
					if(user_input.equals("start")){

						TimerTask task = new TimerTask() {
							@Override
							public void run() {
								seconds++;
								System.out.println("seconds passed: " + seconds);
								if(seconds > q_num*30){
									game_timer.cancel();
									return;
								}
							}
						};

						game_timer = new Timer();
						internal_count = 0;
						seconds = 0;
						response.put("Info!", "good luck, timer has now stared!");
						sendPic(photo_arr[internal_count],response);
						game_started = true;
						game_timer.scheduleAtFixedRate(task,1000,1000);
					}
					if(user_input.equals("more")){
						if(more_count < 2){
							more_count++;
							internal_count++;
							sendPic(photo_arr[internal_count],response);
						} else if(more_count == 2){
							response.put("type", "error2");
							response.put("message", "Cannot get any more \"more's\" for this round");
						}

					}
					if(user_input.equals("next")){
						if(turn == 0){
							response.put("Info!","Easy now");
						}
						else if(turn == 1){
							response.put("Info!","You lose a point for using \"next\"");
							internal_count = start_of_ireland;
							sendPic(photo_arr[internal_count],response);
							turn++;
							points--;
						}
						else if(turn == 2){
							response.put("Info!","You lose a point for using \"next\"");
							internal_count = start_of_paris;
							sendPic(photo_arr[internal_count],response);
							turn++;
							points--;
						}
						else if(turn == 3){
							response.put("Info!","You lose a point for using \"next\"");
							internal_count = start_of_phoenix;
							sendPic(photo_arr[internal_count],response);
							turn++;
							points--;
						}
						else if(turn == 4){
							response.put("Info!","You lose a point for using \"next\"");
							internal_count = start_of_rome;
							sendPic(photo_arr[internal_count],response);
							turn++;
							points--;
						}
						else if(turn == 5){
							response.put("Info!","You lose a point for using \"next\"");
							internal_count = start_of_san_fransisco;
							sendPic(photo_arr[internal_count],response);
							turn++;
							points--;
						}
						else if(turn == 6){
							response.put("Info!","You lose a point for using \"next\"");
							internal_count = start_of_san_fransisco;
							sendPic(photo_arr[internal_count],response);
							turn++;
							points--;
						}

						else if(turn == 7){
							response.put("Info!","End");
						}
					}
					if(game_started == true && q_num != 0){
						if (turn == 1) {
							if (!user_input.equals("berlin")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\". You can enter \"next\" but you will lose a point --> 1");
							} else if (user_input.equals("berlin")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;
								}
								points++;
								response.put("Info!", "Good job, you got it! Total points = " + points);
								turn++;
								internal_count = start_of_ireland;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);

							}
						} else if (turn == 2) {
							if (!user_input.equals("ireland")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\" --> 2");
							} else if (user_input.equals("ireland")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;
									System.out.println("timer is up");
								}
								points++;
								response.put("Info!", "Good job, you got it! Total points = " + Integer.toString(points));
								turn++;
								internal_count = start_of_paris;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);

							}
						} else if (turn == 3) {
							if (!user_input.equals("paris")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\"");
							} else if (user_input.equals("paris")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;
								}

								points++;
								response.put("Info!", "Good job, you got it! Total points = " + Integer.toString(points));
								turn++;
								internal_count = start_of_phoenix;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);

							}
						} else if (turn == 4) {
							if (!user_input.equals("phoenix")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\"");
							} else if (user_input.equals("phoenix")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;

								}
								points++;
								response.put("Info!", "Good job, you got it! Total points = " + Integer.toString(points));
								turn++;
								internal_count = start_of_rome;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);

							}
						} else if (turn == 5) {
							if (!user_input.equals("rome")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\"");
							} else if (user_input.equals("rome")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;
								}
								points++;
								response.put("Info!", "Good job, you got it! Total points = " + Integer.toString(points));
								turn++;
								internal_count = start_of_san_fransisco;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);
							}
						} else if (turn == 6) {
							if (!user_input.equals("san fransisco")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\"");
							} else if (user_input.equals("san fransisco")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;
								}
								points++;
								response.put("Info!", "Good job, you got it! Total points = " + Integer.toString(points));
								turn++;
								internal_count = start_of_switzerland;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);
							}
						} else if (turn == 7) {
							if (!user_input.equals("switzerland")) {
								response.put("Info!", "Try Again! If you cannot figure it out, enter \"more\"");
							} else if (user_input.equals("switzerland")) {
								if (seconds >= (30 * q_num)) {
									end_game = true;
									game_check = false;
								}
								points++;
								response.put("Info!", "Good job, you got it! Total points = " + Integer.toString(points));
								turn++;
								internal_count = start_of_switzerland;
								sendPic(photo_arr[internal_count], response);
								more_count = 0;
								q_num--;
								game_check = true;
								scores.put(name, points);
							}
						} else if (turn == 8) {
							if (seconds >= (30 * q_num)) {
								end_game = true;
								game_check = false;
							}
							response.put("Info!", "SUCCESS! You made it to 8!!! Total points = " + Integer.toString(points));

							q_num--;
							game_check = true;

							scores.put(name, points);
						}
					}

					if(end_game == true){
						response.put("new_game", "new");
						sendPic("img/lose.jpg",response);
						response.put("Info!","GAME OVER - > YOU HAVE RUN OUT OF TIME" + "Play again? just enter your name again!");
						internal_count = 0;
						game_timer.cancel();
						turn = 0;
						seconds = 0;
						points = 0;
						end_game = false;

					}
					else if(q_num == 0 && end_game == false ){
						response.put("new_game", "new");
						internal_count = 0;
						turn = 0;
						response.put("Info!", "You win! " + "Play again? just enter your name again " + "Total score = " + points);
						sendPic("img/win.jpg",response);
						game_timer.cancel();
						seconds = 0;
						points = 0;
					}

					if(turn == 0){
						turn++;
					}

				}

				else {
					System.out.println("not sure what you meant");
					response.put("type","error" );
					response.put("message","unknown response" );
				}
				PrintWriter outWrite = new PrintWriter(sock.getOutputStream(), true); // using a PrintWriter here, you could also use and ObjectOutputStream or anything you fancy
				outWrite.println(response.toString());
			}

		} catch(Exception e) {e.printStackTrace();}
	}


	public static void sendPic(String filename, JSONObject obj) throws Exception {
		File file = new File(filename);
		if (file.exists()) {
			obj.put("payload", filename);
		}
	}
}

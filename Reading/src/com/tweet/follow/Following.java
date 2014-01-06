package com.tweet.follow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tweet.read.Reading;

public class Following {

	java.sql.Connection conn = null;
	private Statement stmt = null;

	private static String dbURL = Messages.getString("Following.DB_URL"); //$NON-NLS-1$

	/**
	 * Creates Connection to the derby TweetDB database
	 */
	public void createConnection() {
		try {
			Class.forName(Messages.getString("Following.DERBY_DRIVER")).newInstance(); //$NON-NLS-1$
			// Get a connection
			conn = DriverManager.getConnection(dbURL);
		} catch (Exception except) {
			except.printStackTrace();
		}
	}

	public void insertRecords(String userId, String follows) {
		createConnection();
		try {
			stmt = conn.createStatement();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("insert into FollowsTable  values ('");
			stringBuilder.append(userId);
			stringBuilder.append("','");
			stringBuilder.append(follows);
			stringBuilder.append("')");
			stmt.execute(stringBuilder.toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			stmt.close();
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		close();
	}

	public List<String> selectRecords(String sql) {
		createConnection();
		ArrayList<String> followsList = new ArrayList<String>();

		try {
			stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(sql);

			while (results.next()) {
				followsList.add(results.getString(1));

			}
			results.close();
			stmt.close();
		} catch (SQLException sqlExcept) {
			sqlExcept.printStackTrace();
		}
		close();
		return followsList;
	}

	public void close() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				DriverManager.getConnection(dbURL + ";shutdown=true"); //$NON-NLS-1$
				conn.close();
			}
		} catch (SQLException sqlExcept) {

		}

	}

	/**
	 * prints the param to the standard output console
	 * 
	 * @param msg
	 */
	public static void printToConsole(String msg) {
		// TODO Auto-generated method stub
		System.out.println(msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Following followingObj = new Following();
		printToConsole(Messages.getString("Following.USER_PROMPT")); //$NON-NLS-1$

		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String user_input_string;
		try {
			user_input_string = r.readLine();
			if (user_input_string != null && !user_input_string.equals("")) { //$NON-NLS-1$
				String username = user_input_string.substring(0,
						user_input_string.indexOf(" ")); //$NON-NLS-1$
				String userToFollow = user_input_string
				.substring(
						user_input_string.lastIndexOf(" ") + 1, user_input_string.length()); //$NON-NLS-1$
				followingObj.insertRecords(username, userToFollow);
				List<String> followingUsersList = (List<String>) followingObj
				.selectRecords(Messages.getString("Following.SELECT") + username + "'"); //$NON-NLS-1$ //$NON-NLS-2$
				if (!followingUsersList.isEmpty()) {
					if (!followingUsersList.contains(username)) {
						followingUsersList.add(username);
					}
					if (!followingUsersList.contains(userToFollow)) {
						followingUsersList.add(userToFollow);
					}
				}

				String[] queueNames = followingUsersList
				.toArray(new String[] {});

				new Reading(queueNames).start();

				// String [] readPosts ={username,userToFollow};
				// new Reading(readPosts).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}

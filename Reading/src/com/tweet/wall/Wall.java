package com.tweet.wall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.naming.InitialContext;

import com.tweet.follow.Following;
import com.tweet.read.MapMessageListener;

public class Wall {
	private static final String USER_WALL_PROMPT = "Enter <user name> wall ";

	/**
	 * Browse the Messages from the QueueBrowser and print it to the console
	 * 
	 * @param mConsumerQueueNames
	 */
	@SuppressWarnings("unchecked")
	public void browseMessages(String[] mConsumerQueueNames) {
		Connection connection = null;
		Queue q = null;
		Enumeration<MapMessage> messageEnumeration = null;
		MapMessage mapMessage = null;
		Session session = null;
		MapMessageListener mapMsgListener = new MapMessageListener();

		try {
			for (int index = 0; index < mConsumerQueueNames.length; ++index) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("jms/");
				stringBuilder.append(mConsumerQueueNames[index]);
				stringBuilder.append("Queue");
				String qJNDI = stringBuilder.toString();
				try {
					InitialContext ctx = new InitialContext();
					q = (Queue) ctx.lookup(qJNDI);

					ConnectionFactory cf = (ConnectionFactory) ctx
							.lookup("jms/ConnectionFactory");
					connection = cf.createConnection();
					session = connection.createSession(true,
							Session.AUTO_ACKNOWLEDGE);
					QueueBrowser browser = session.createBrowser(q);
					messageEnumeration = browser.getEnumeration();
					if (messageEnumeration != null) {
						if (!messageEnumeration.hasMoreElements()) {
							System.out.println("There are no messages "
									+ "in the queue.");

						} else {
							while (messageEnumeration.hasMoreElements()) {
								mapMessage = (MapMessage) messageEnumeration
										.nextElement();
								mapMsgListener.onMessage(mapMessage);
							}
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println(e.getMessage());
				}
			}

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Following followingObj = new Following();
		Wall wallObj = new Wall();

		System.out.println(USER_WALL_PROMPT);
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String user_input_string;

		try {
			user_input_string = r.readLine();
			if (user_input_string != null && !user_input_string.equals("")) {
				String username = user_input_string.substring(0,
						user_input_string.indexOf(" "));
				StringBuilder stringBuilder
						 = new StringBuilder();
				stringBuilder
						.append("select distinct follows from FollowsTable where userId='");
				stringBuilder
						.append(username);
				stringBuilder
						.append("'");
				@SuppressWarnings("unchecked")
				ArrayList<String> followingUsersList = (ArrayList<String>) followingObj
						.selectRecords(stringBuilder
						.toString());
				if (!followingUsersList.isEmpty()) {
					if (!followingUsersList.contains(username)) {
						followingUsersList.add(username);
					}

					String[] usersToFollow = followingUsersList
							.toArray(new String[] {});
					wallObj.browseMessages(usersToFollow);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}

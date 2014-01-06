package com.tweet.read;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MapMessageListener implements MessageListener {
	/**
	 * Calculates the elapsed time since the posted message
	 * 
	 * @param timePostedInMilliseconds
	 * @return
	 */
	private long timeElapsedSincePost(long timePostedInMilliseconds) {

		long currentTime = System.currentTimeMillis();
		long timeElapsed = (currentTime - timePostedInMilliseconds) / 60000;
		return timeElapsed;
	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			try {
				long msgReceivedTime = mapMessage.getJMSTimestamp();
				long timeElapsed = timeElapsedSincePost(msgReceivedTime);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("> ");
				stringBuilder.append(mapMessage.getString("UserName"));
				stringBuilder.append("\n");
				stringBuilder.append(mapMessage.getString("UserTweet"));
				stringBuilder.append("(");
				stringBuilder.append(timeElapsed);
				stringBuilder.append(" minutes ago)");
				stringBuilder.append("\n");
				String messageOutput = stringBuilder.toString();
				System.out.println(messageOutput);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println(e.getMessage());
			}

		} else {

			System.out.println("Invalid Msg Received");
		}

	}

}

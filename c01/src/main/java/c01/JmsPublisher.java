package c01;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class JmsPublisher {

    private ConnectionFactory connectionFactory;
    private Topic topic;

    public JmsPublisher() {
        this.connectionFactory = new ActiveMQConnectionFactory("tcp://c02:3002");
        try {
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.topic = session.createTopic("ImageTopic");
        } catch (JMSException e) {
            throw new RuntimeException("Error initializing JmsPublisher" + e.getMessage());
        }
    }

    public void publish(byte[] pictureData, int percent) {
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            BytesMessage message = session.createBytesMessage();
            message.writeBytes(pictureData);
            message.setIntProperty("zoomParameters", percent);

            MessageProducer producer = session.createProducer(topic);
            producer.send(message);

            System.out.println("Picture uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

}

package c02;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;

public class ActiveMQBroker {

    public static void main(String[] args) {
        try {
            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://0.0.0.0:3002");
            String topicName = "ImageTopic";
            ActiveMQTopic topic = new ActiveMQTopic(topicName);
            broker.setDestinations(new ActiveMQTopic[]{topic});
            broker.start();
            System.out.println("ActiveMQ Broker Started on tcp://c02:3002");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

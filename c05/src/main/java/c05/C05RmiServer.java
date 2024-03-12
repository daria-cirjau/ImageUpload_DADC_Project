package c05;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class C05RmiServer {

    public static void main(String[] args) {
        try {
            ImageProcessorIncrease imageProcessor = new ImageProcessorIncreaseImpl();
            Registry registry = LocateRegistry.createRegistry(1098);
            registry.rebind("ImageProcessorIncrease", imageProcessor);

            System.out.println("C05 RMI Server is running");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
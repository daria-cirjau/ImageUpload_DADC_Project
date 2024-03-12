package c04;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class C04RmiServer {


    public static void main(String[] args) {
    	if (System.getSecurityManager() == null) {
    		System.setSecurityManager(new RMISecurityManager());
    	}
        try {
            ImageProcessorDecrease imageProcessor = new ImageProcessorDecreaseImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("ImageProcessorDecrease", imageProcessor);

            System.out.println("C04 RMI Server is running");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

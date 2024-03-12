package c03;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.BytesMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import java.rmi.Naming;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "ImageTopic"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class ImageProcessingMDB implements MessageListener {

    public ImageProcessingMDB() {
        System.out.println("Started ImageProcessingMDB");
    }
    
    @Override
    public void onMessage(Message message) {
        try {
        	System.out.println("Message received");
            if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] pictureData = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(pictureData);

                String zoomParameters = bytesMessage.getStringProperty("zoomParameters");
                processImage(pictureData, Integer.parseInt(zoomParameters));
            }
        } catch (Exception e) {
        	System.out.println("Error while receiving image: " + e.getMessage());
        }
    }

    private void processImage(byte[] pictureData, int zoomParameters) {
        try {
            ImageProcessorDecrease imageProcessorDecrease = (ImageProcessorDecrease) Naming.lookup("rmi://c04:1099/ImageProcessorDecrease");
            System.out.println("ImageProcessorDecrease found on port 1099");
            ImageProcessorIncrease imageProcessorIncrease = (ImageProcessorIncrease) Naming.lookup("rmi://c05:1098/ImageProcessorIncrease");
            System.out.println("ImageProcessorIncrease found on port 1098");

            if (zoomParameters < 100) {
                imageProcessorDecrease.processImage(pictureData, zoomParameters);
            } else {
                imageProcessorIncrease.processImage(pictureData, zoomParameters);
            }
        } catch (Exception e) {
        	System.out.println("Error while processing image: " + e.getMessage());
        }
    }
}

package c05;

import javax.imageio.ImageIO;

import com.sun.management.OperatingSystemMXBean;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ImageProcessorIncreaseImpl extends UnicastRemoteObject implements ImageProcessorIncrease {
	   
	private String osName;
	private String cpuUsage;
	private String ramUsage;

    protected ImageProcessorIncreaseImpl() throws RemoteException {
        super();
    }

    @Override
    public Image processImage(byte[] imageData, int zoomParameters) throws RemoteException {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(input);

            int newWidth = (image.getWidth(null) * zoomParameters) / 100;
            int newHeight = (image.getHeight(null) * zoomParameters) / 100;

            image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "bmp", outputStream);
            byte[] processedImageData = outputStream.toByteArray();

            sendToC06(processedImageData);

            return image;
        } catch (Exception e) {
            throw new RemoteException("Error resizing image in C05." + e.getMessage());

        }
    }

    private void sendToC06(byte[] imageData) {
        try {
            String nodejsUrl = "http://c06:3006/api/store-data";
            URL url = null;
            try {
                url = new URL(nodejsUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String imageBLOB = new String(imageData, StandardCharsets.ISO_8859_1);
                String jsonInputString = String.format(
                        "{\"image\": \"%s\", \"snmpData\": {\"osName\": \"%s\", \"cpuUsage\": %d, \"ramUsage\": %d}}",
                        imageBLOB, osName, cpuUsage, ramUsage
                );

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("Data sent to C06 Node.js server.");
                } else {
                    System.err.println("Error sending data to C06 Node.js server: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void getSnmpData() {
        osName = System.getProperty("os.name");

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        cpuUsage = String.valueOf(osBean.getSystemCpuLoad() * 100);
        long totalMemory = osBean.getTotalPhysicalMemorySize();
        long freeMemory = osBean.getFreePhysicalMemorySize();
        ramUsage = String.valueOf(totalMemory - freeMemory);
    }
}



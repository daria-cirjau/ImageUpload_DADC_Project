package c05;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ImageProcessorIncrease extends Remote {
    Image processImage(byte[] imageData, int zoomParameters) throws RemoteException;
}

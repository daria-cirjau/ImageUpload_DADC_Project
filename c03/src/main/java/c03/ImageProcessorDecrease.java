package c03;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ImageProcessorDecrease extends Remote {
    Image processImage(byte[] imageData, int zoomParameters) throws RemoteException;
}
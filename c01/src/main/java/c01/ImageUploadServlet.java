package c01;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/imageUpload/*")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
	    maxFileSize = 1024 * 1024 * 10,      // 10MB
	    maxRequestSize = 1024 * 1024 * 50    // 50MB
	)
public class ImageUploadServlet extends HttpServlet {

    private JmsPublisher jmsPublisher;

    private String imageDownloadLink;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if ("/upload".equals(path)) {
            uploadImage(request, response);
        } else if ("/imageStored".equals(path)) {
            notifyImageStored(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("/".equals(request.getPathInfo()) || request.getPathInfo() == null) {
            response.sendRedirect("filechooser.html");
        } else if ("/downloadLink".equals(request.getPathInfo())) {
            getDownloadLink(response);
        }
    }

    private void uploadImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Part imagePart = request.getPart("image");
            String percentString = request.getParameter("percent");
            int percent = Integer.parseInt(percentString);
            byte[] imageBytes = imagePart.getInputStream().readAllBytes();
            jmsPublisher = new JmsPublisher();
            jmsPublisher.publish(imageBytes, percent);
            writeResponse(response, "Image uploaded and message sent to topic");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeResponse(response, e.getMessage());
        }
    }


    private void notifyImageStored(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageId = request.getParameter("imageId");
        imageDownloadLink = "http://c06:3006/api/download-image/" + imageId;
        writeResponse(response, "Notification received for image ID: " + imageId);
    }

    private void getDownloadLink(HttpServletResponse response) throws IOException {
        if (imageDownloadLink == null) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            writeResponse(response, "No image available");
        } else {
            writeResponse(response, imageDownloadLink);
        }
    }

    private void writeResponse(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        out.println(message);
    }
}

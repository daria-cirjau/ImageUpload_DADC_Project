# ImageUpload_DADC_Project

Requirement: 
Create 6 Docker containers for zooming in/out a large BMP picture.

The front-end sends images to the first container, which hosts a back-end powered by Jakarta EE Servlet REST API.
From the back-end container, images are forwarded as binary messages to a JMS Topic hosted on the second container, acting as a Message-Oriented Middleware (MoM) JMS Broker.
A Jakarta EE EJB Message-Driven Bean (MDB) deployed in the third container subscribes to the JMS Topic. Additionally, it acts as a Java RMI client for two Java RMI servers deployed in the fourth and fifth containers.
Processed images are stored as Binary Large Objects (BLOBs) in a MySQL database hosted on the sixth container. 
The sixth container exposes REST endpoints using Node.js Express. 
Upon insertion of zoomed BMP images into MySQL, the first container notifies the front-end via REST API/WebSocket. The front-end is then redirected to the appropriate Node.js URL for downloading the image.

* C01: Jakarta EE Servlet, Apache Tomcat 10 Microserv + JMS Client Publisher
* C02: Apache TomEE 9 w JMS Broker - Topic & Queues
* C03: Apache TomEE 9 w EJB Client MDB & RMI Client <- zoom pics united into DB via REST API & JMS Client Publisher in Topic/Queue that the job is done
* C04, C05: Apache TomEE 9 w RMI Server objects
* C01: Notification that the pic is ready => in the front-end with link for download from node.js C06
* C06: REST API in node.js for DB access

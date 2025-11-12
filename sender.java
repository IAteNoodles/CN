import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
// import java.util.Random; // Removed as it was unused in the original sender code
import java.util.Arrays; // Added for shorter array initialization

class Sender {
    public static final int WINDOW_SIZE = 4;
    private static final int TOTAL_FRAMES = 10;
    // private static final Random random = new Random(); // Deleted: Unused

    public static void main(String[] args) throws IOException, InterruptedException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress receiverAddress = InetAddress.getLocalHost();
        int frameNumber = 0; // Next frame to send
        int nextAck = 0; // Oldest unacknowledged frame (window base)
        
        // boolean[] ackReceived = new boolean[TOTAL_FRAMES]; // Replaced by one-line initialization
        boolean[] ackReceived = new boolean[TOTAL_FRAMES]; 
        
        while (nextAck < TOTAL_FRAMES) {
            // Send frames within the window size
            for (int j = 0; j < WINDOW_SIZE && (frameNumber < TOTAL_FRAMES); j++) {
                if (!ackReceived[frameNumber]) {
                    String message = "Frame " + frameNumber;
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, 9876);
                    socket.send(packet);
                    System.out.println("Sent: " + message);
                    frameNumber++;
                }
            }
            
            socket.setSoTimeout(2000); // 2 seconds timeout for ACKs
            try {
                // Try to receive ACKs for frames within the current window size
                for (int j = nextAck; j < frameNumber; j++) {
                    if (!ackReceived[j]) {
                        byte[] ackBuffer = new byte[1024];
                        DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                        socket.receive(ackPacket);

                        String ackMessage = new String(ackPacket.getData()).trim();
                        // int ackNum = Integer.parseInt(ackMessage.split(" ")[1]); // Retained due to necessary parsing
                        int ackNum = Integer.parseInt(ackMessage.split(" ")[1]); 
                        
                        if (ackNum == j) {
                            System.out.println("Received ACK for Frame " + ackNum);
                            ackReceived[j] = true;
                            
                            if (j == nextAck) {
                                // while (nextAck < TOTAL_FRAMES && ackReceived[nextAck]) { nextAck++; } // Consolidated loop body
                                while (nextAck < TOTAL_FRAMES && ackReceived[nextAck]) nextAck++;
                            }
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout: Resending frames from " + nextAck);
                // Resend frames from the next acknowledgment needed (Go-Back-N strategy)
                frameNumber = nextAck; 
            }
        }
        System.out.println("All frames sent successfully.");
        socket.close();
    }
}

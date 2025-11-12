import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;

class Receiver {
    private static final int TOTAL_FRAMES = 10;
    // private static final Random random = new Random(); // Retained as it's used for simulation
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(9876);
        int expectedFrame = 0;
        
        // Removed: while (true) loop logic moved into the 'for' loop for cleaner exit
        for (;;) { 
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String message = new String(packet.getData()).trim();
            int frameNumber = Integer.parseInt(message.split(" ")[1]);

            // Randomly decide whether to acknowledge the frame to simulate packet loss
            if (random.nextInt(10) < 8) { // 80% chance to ACK
                if (frameNumber == expectedFrame) {
                    System.out.println("Received: " + message);
                    expectedFrame++;
                } else {
                    System.out.println("Received out-of-order frame: " + frameNumber);
                }
                
                String ackMessage = "ACK " + frameNumber;
                byte[] ackBuffer = ackMessage.getBytes();
                
                DatagramPacket ackPacket = new DatagramPacket(
                    ackBuffer, ackBuffer.length, packet.getAddress(),
                    packet.getPort());
                socket.send(ackPacket);
                System.out.println("Sent: " + ackMessage);
            } else {
                System.out.println("Simulated loss for Frame " + frameNumber);
            }
            
            // Simplified exit check
            // if (frameNumber == TOTAL_FRAMES - 1) { break; } // Replaced by a simpler check
            if (expectedFrame == TOTAL_FRAMES) break; 
        }
        socket.close();
    }
}

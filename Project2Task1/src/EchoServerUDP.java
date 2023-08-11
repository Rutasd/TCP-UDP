import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class EchoServerUDP{
    public static void main(String args[]){
        DatagramSocket aSocket = null; // declare a datagram socket variable
        byte[] buffer = new byte[1000]; // declare a byte array of size 1000
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader object to read from the input stream
            System.out.print("Enter the server port number: "); // print message to enter server port number
            int serverPort = Integer.parseInt(reader.readLine()); // read the server port number from the input stream and convert to an integer
            aSocket = new DatagramSocket(serverPort); // create a datagram socket with the given server port number
            DatagramPacket request = new DatagramPacket(buffer, buffer.length); // create a datagram packet with the buffer array and its length
            while(true){ // start an infinite loop
                aSocket.receive(request); // receive a datagram packet on the socket
                byte[] requestData = Arrays.copyOf(request.getData(), request.getLength()); // copy the received packet data into a new byte array of the correct length
                String requestString = new String(requestData); // convert the byte array to a string
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort()); // create a datagram packet to send back to the client with the received data, the client address, and the client port number
                aSocket.send(reply); // send the datagram packet back to the client
                System.out.println("Echoing: "+requestString); // print a message indicating that the server is echoing the client's message
                if(requestString.equals("halt!")) // if the client sent the message "halt!"
                {
                    System.out.println("UDP Server quitting"); // print a message indicating that the server is quitting
                    break; // exit the infinite loop
                }
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage()); // handle socket exceptions by printing an error message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage()); // handle I/O exceptions by printing an error message
        }finally {if(aSocket != null) aSocket.close();} // close the socket if it is not null
    }
}

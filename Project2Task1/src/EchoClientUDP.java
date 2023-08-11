import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class EchoClientUDP{
    public static void main(String args[]){
        DatagramSocket aSocket = null;  // create a datagram socket
        try {
            System.out.println("The UDP client is running");// print a message indicating that the client is running
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader to read input from the console
            System.out.print("Enter the server port number: ");// ask the user to enter the server port number
            InetAddress aHost = InetAddress.getByName("localhost");// get the hostname of the server
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader to read input from the console
            int serverPort = Integer.parseInt(serverInput.readLine());// get the server port number from the user input
            aSocket = new DatagramSocket(); // create a new datagram socket
            String nextLine;// create a buffered reader to read input from the console
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // read input from the console until the user enters null
            while ((nextLine = typed.readLine()) != null) {

                byte [] m = nextLine.getBytes();// convert the input string to a byte array
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);// create a datagram packet containing the input string
                aSocket.send(request); // send the datagram packet to the server
                byte[] buffer = new byte[1000];// create a buffer to receive the reply from the server
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);// create a datagram packet to receive the reply from the server
                aSocket.receive(reply);// receive the reply from the server
                byte[] replyData = Arrays.copyOf(reply.getData(), reply.getLength());// copy the reply data into a new byte array
                System.out.println("Reply from server: " + new String(replyData));// print the reply from the server
                if(new String(replyData).equals("halt!"))// check if the reply from the server is "halt!"
                {
                    System.out.println("UDP Client quitting");
                    break;
                }
            }
        }catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());// print an error message if a socket exception occurs
        }catch (IOException e){
            System.out.println("IO Exception: " + e.getMessage());// print an error message if an I/O exception occurs
        }finally {
            if(aSocket != null) aSocket.close();// close the socket if it is not null
        }
    }
}

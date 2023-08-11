import java.net.*;
import java.io.*;
import java.util.Arrays;
/**
 * The `EchoClientUDP` class is a simple UDP client that sends text messages to a server and receives
 * echo replies. The client prompts the user for the server port number and sends each message entered
 * by the user to the server. The server echos the message back to the client, which then displays the
 * echoed message on the console. The client will continue to send messages until the user enters "halt!"
 * as a message. At that point, the client will print a message indicating that it is quitting and exit.
 *
 *
 @author: Ruta Deshpande
 @andrew id: rutasurd
 @email id: rutasurd@andrew.cmu.edu
 @date: 21st Feb 2023
 *
 * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class EchoClientUDP{
    public static void main(String args[]){

        // create a datagram socket
        DatagramSocket aSocket = null;

        try {
            // print a message indicating that the client is running
            System.out.println("The UDP client is running");

            // create a buffered reader to read input from the console
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // ask the user to enter the server port number
            System.out.print("Enter the server port number: ");

            // get the hostname of the server
            InetAddress aHost = InetAddress.getByName("localhost");

            // create a buffered reader to read input from the console
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in));

            // get the server port number from the user input
            int serverPort = Integer.parseInt(serverInput.readLine());

            // create a new datagram socket
            aSocket = new DatagramSocket();

            // create a buffered reader to read input from the console
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            // read input from the console until the user enters null
            while ((nextLine = typed.readLine()) != null) {
                // convert the input string to a byte array
                byte [] m = nextLine.getBytes();

                // create a datagram packet containing the input string
                DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);

                // send the datagram packet to the server
                aSocket.send(request);

                // create a buffer to receive the reply from the server
                byte[] buffer = new byte[1000];

                // create a datagram packet to receive the reply from the server
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

                // receive the reply from the server
                aSocket.receive(reply);

                // copy the reply data into a new byte array
                byte[] replyData = Arrays.copyOf(reply.getData(), reply.getLength());

                // print the reply from the server
                System.out.println("Reply from server: " + new String(replyData));
                // check if the reply from the server is "halt!"
                if(new String(replyData).equals("halt!"))
                {
                    // print a message indicating that the client is quitting
                    System.out.println("UDP Client quitting");
                    // break out of the loop
                    break;
                }
            }
        }catch (SocketException e) {
            // print an error message if a socket exception occurs
            System.out.println("Socket Exception: " + e.getMessage());
        }catch (IOException e){
            // print an error message if an I/O exception occurs
            System.out.println("IO Exception: " + e.getMessage());
        }finally {
            // close the socket if it is not null
            if(aSocket != null) aSocket.close();
        }
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * This program is a UDP client that sends an integer to a server and receives a response from the server.
 * The user is prompted to enter the server port number and then asked to enter integers to send to the server.
 * If the server responds with "halt!", the client will exit the loop and print a message indicating that it is quitting.
 * The add() method takes an integer parameter, sends it to the server in a datagram packet, receives a response from the server, and returns the response as an integer.
 * If an error occurs, the program will print an error message and exit.
 *
 *  *  @author: Ruta Deshpande
 *  *  @andrew id: rutasurd
 *  *  @email id: rutasurd@andrew.cmu.edu
 *  *  @date: 21st Feb 2023
 *  *  Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class AddingClientUDP{
    static DatagramSocket aSocket = null;  // create a datagram socket
    static InetAddress aHost;
    static int serverPort;

    public static void main(String args[]){

        try {
            System.out.println("The Client is running");// print a message indicating that the client is running
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader to read input from the console
            System.out.print("Enter the server port number: ");// ask the user to enter the server port number
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader to read input from the console
            System.out.println();
            aHost = InetAddress.getByName("localhost");// get the hostname of the server
            serverPort = Integer.parseInt(serverInput.readLine());// get the server port number from the user input
            String nextLine;// create a buffered reader to read input from the console
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // read input from the console until the user enters null
            while ((nextLine = typed.readLine()) != null) {
                if(nextLine.equals("halt!"))// check if the reply from the server is "halt!"
                {
                    System.out.println("UDP Client quitting");
                    break;
                }
                int value = Integer.parseInt(nextLine);
                add(value);
            }
        }catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());// print an error message if a socket exception occurs
        }catch (IOException e){
            System.out.println("IO Exception: " + e.getMessage());// print an error message if an I/O exception occurs
        }finally {
            if(aSocket != null) aSocket.close();// close the socket if it is not null
        }
    }
    public static int add(int i)
    {
        int result;
        try {
            aSocket = new DatagramSocket(); // create a new datagram socket
            byte [] m = String.valueOf(i).getBytes();// convert the int to a byte array
            DatagramPacket request = new DatagramPacket(m,  m.length, aHost, serverPort);// create a datagram packet containing the input string
            aSocket.send(request); // send the datagram packet to the server
            byte[] buffer = new byte[1000];// create a buffer to receive the reply from the server
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);// create a datagram packet to receive the reply from the server
            aSocket.receive(reply);// receive the reply from the server
            byte[] replyData = Arrays.copyOf(reply.getData(), reply.getLength());// copy the reply data into a new byte array
            System.out.println("The server returned " + new String(replyData));// print the reply from the server
            result = Integer.parseInt(new String(replyData));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}

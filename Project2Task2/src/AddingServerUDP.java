import java.net.*;
import java.io.*;
import java.util.Arrays;
/**
 * This is a Java program that implements a simple UDP server
 * that receives integer numbers from clients and returns the
 * sum of all received numbers back to the client.
 * The server runs on an infinite loop, listening for incoming
 * packets on a specified port, and then sends a response packet to
 * the client with the sum of all received numbers.
 * This program calculates the sum by adding each new number to the previous sum and then returns it
 *
 *  *  @author: Ruta Deshpande
 *  *  @andrew id: rutasurd
 *  *  @email id: rutasurd@andrew.cmu.edu
 *  *  @date: 21st Feb 2023
 *  *  Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class AddingServerUDP{
    static int sum = 0;
    public static void main(String args[]){

        DatagramSocket aSocket = null; // declare a datagram socket variable
        byte[] buffer = new byte[1000]; // declare a byte array of size 1000
        try{
            System.out.println("Server started");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader object to read from the input stream
            System.out.print("Enter the server port number: "); // print message to enter server port number
            int serverPort = Integer.parseInt(reader.readLine()); // read the server port number from the input stream and convert to an integer
            aSocket = new DatagramSocket(serverPort); // create a datagram socket with the given server port number
            DatagramPacket request = new DatagramPacket(buffer, buffer.length); // create a datagram packet with the buffer array and its length
            while(true){ // start an infinite loop
                aSocket.receive(request); // receive a datagram packet on the socket
                byte[] requestData = Arrays.copyOf(request.getData(), request.getLength()); // copy the received packet data into a new byte array of the correct length
                String requestString = new String(requestData); // convert the byte array to a string
                int num = Integer.parseInt(requestString);
                int res = add(num);
                String replyString = String.valueOf(res);
                DatagramPacket reply = new DatagramPacket(replyString.getBytes(), replyString.length(), request.getAddress(), request.getPort()); // create a datagram packet to send back to the client with the received data, the client address, and the client port number

                aSocket.send(reply);
                System.out.println("Returning sum of " +res+ " to client "); // print a message indicating that the server is echoing the client's message
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage()); // handle socket exceptions by printing an error message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage()); // handle I/O exceptions by printing an error message
        }finally {if(aSocket != null) aSocket.close();} // close the socket if it is not null
    }

    public static int add(int i)
    {
    System.out.println("\nAdding: "+i+" to "+sum);
    sum = sum + i;
    return sum;
    }
}

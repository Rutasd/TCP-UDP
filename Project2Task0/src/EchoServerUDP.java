import java.net.*;
import java.io.*;
import java.util.Arrays;
/**
*
 * *
 EchoServerUDP class is a simple implementation of a UDP server that receives messages
 from clients and echoes them back to the clients.
 The server listens on a specified port number for incoming packets, and when it receives
 a packet, it copies the packet data into a new byte array, converts the byte array to a string,
 creates a new datagram packet with the received data, the client address, and the client port number,
 and sends the packet back to the client. The server continues to listen for incoming packets
 until it receives a packet containing the message "halt!", at which point it terminates.
 This class uses the java.net and java.io packages to create and manipulate sockets, datagram packets,
 and input/output streams.
 *
 *
 @author: Ruta Deshpande
 @andrew id: rutasurd
 @email id: rutasurd@andrew.cmu.edu
 @date: 21st Feb 2023
 *
 * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class EchoServerUDP{
    public static void main(String args[]){
        DatagramSocket aSocket = null; // declare a datagram socket variable
        byte[] buffer = new byte[1000]; // declare a byte array of size 1000
        try{
            System.out.println("UDP Server is running!");
            // create a buffered reader object to read from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // print message to enter server port number
            System.out.print("Enter the server port number: ");

            // read the server port number from the input stream and convert to an integer
            int serverPort = Integer.parseInt(reader.readLine());

            // create a datagram socket with the given server port number
            aSocket = new DatagramSocket(serverPort);

            // create a datagram packet with the buffer array and its length
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            while(true){ // start an infinite loop

                // receive a datagram packet on the socket
                aSocket.receive(request);

                // copy the received packet data into a new byte array of the correct length
                byte[] requestData = Arrays.copyOf(request.getData(), request.getLength());

                // convert the byte array to a string
                String requestString = new String(requestData);

                // create a datagram packet to send back to the client with the received data, the client address, and the client port number
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());     aSocket.send(reply); // send the datagram packet back to the client

                // print a message indicating that the server is echoing the client's message
                System.out.println("Echoing: "+requestString);

                // if the client sent the message "halt!"
                if(requestString.equals("halt!"))
                {
                    // print a message indicating that the server is quitting
                    System.out.println("UDP Server quitting");
                    break; // exit the infinite loop
                }
            }
            // handle socket exceptions by printing an error message
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());

            // handle I/O exceptions by printing an error message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());

            // close the socket if it is not null
        }finally {if(aSocket != null) aSocket.close();}
    }
}

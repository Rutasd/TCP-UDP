import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
/**
*
The EavesdropperUDP class is a program that acts as a middleman between a client and a server.
It listens to the server on one port and masquerades as the server on another port.
It intercepts messages from the client, echoes them back to the server, receives a reply from the server, and sends it back to the client.
If the client sends the message "halt!", the program terminates.
 *
 *
 *  @author: Ruta Deshpande
 *  @andrew id: rutasurd
 *  @email id: rutasurd@andrew.cmu.edu
 *  @date: 21st Feb 2023
 *  * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 *  *
*/
public class EavesdropperUDP {
    public static void main(String args[])
    {
        // declare a datagram socket variable for client
        DatagramSocket aSocketClient = null;

        // declare a datagram socket variable for server
        DatagramSocket aSocketServer = null;

        // declare a byte array of size 1000
        byte[] buffer = new byte[1000];

        // create a buffered reader object to read from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            // print message to enter server port number
            System.out.print("Enter the server port number that the eavesdropper is listening to: ");

            // read the server port number from the input stream and convert to an integer
            int serverPort1 = Integer.parseInt(reader.readLine());

            System.out.println("Enter the server port number that the eavesdropper is masquerading as: ");

            // read the server port number from the input stream and convert to an integer
            int serverPort2 = Integer.parseInt(reader.readLine());

            //Creating Datagram sockets
            aSocketClient = new DatagramSocket(serverPort1);
            aSocketServer = new DatagramSocket();
            while(true)
            {
                // create a datagram packet with the buffer array and its length
                DatagramPacket receivedData = new DatagramPacket(buffer, buffer.length);

                // receive a datagram packet on the socket
                aSocketClient.receive(receivedData);

                // copy the received packet data into a new byte array of the correct length
                byte[] dataReceived = Arrays.copyOf(receivedData.getData(), receivedData.getLength());

                // convert the byte array to a string
                String input = new String(dataReceived);

                // print a message indicating that the server is echoing the client's message
                System.out.println("Echoing: " + input);

                if(!input.equals("halt!"))
                {
                    input = input + "!";
                    // create a datagram packet to send back to the client with the received data, the client address, and the client port number
                    DatagramPacket sendData = new DatagramPacket(input.getBytes(), receivedData.getLength() + 1, receivedData.getAddress(), serverPort2);

                    // send the datagram packet back to the client
                    aSocketServer.send(sendData);
                }
                else if(input.equals("halt!"))
                {
                    // create a datagram packet to send back to the client with the received data, the client address, and the client port number
                    DatagramPacket sendData = new DatagramPacket(input.getBytes(), receivedData.getLength() , receivedData.getAddress(), serverPort2);

                    // send the datagram packet back to the client
                    aSocketServer.send(sendData);
                }
                // create a datagram packet to receive the reply from the server
                DatagramPacket replyFromServer = new DatagramPacket(buffer, buffer.length);

                // receive the reply from the server
                aSocketServer.receive(replyFromServer);

                // copy the reply data into a new byte array
                byte[] replyData = Arrays.copyOf(replyFromServer.getData(), replyFromServer.getLength());

                //converting replydata to string
                String clientData = new String(replyData);

                // print the reply from the server
                System.out.println("Reply from server: " + clientData);

                clientData.replaceAll("!","");

                // create a datagram packet to send back to the client with the received data, the client address, and the client port number
                DatagramPacket sendDatatoClient = new DatagramPacket(clientData.getBytes(), receivedData.getLength(), receivedData.getAddress(), receivedData.getPort());
                // send the datagram packet back to the client
                aSocketServer.send(sendDatatoClient);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

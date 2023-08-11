import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;

/**
Class for implementing client side using UDP protocol
Takes input from user about actions to be performed for a particular client id
Addition, Subtraction, Retrieval can be performed
Takes user input and sends it to server
 *
 *  @author: Ruta Deshpande
 *  @andrew id: rutasurd
 *  @email id: rutasurd@andrew.cmu.edu
 *  @date: 21st Feb 2023
 *  * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
*/

public class RemoteVariableClientUDP {
    static DatagramSocket aSocket = null;  // create a datagram socket
    static InetAddress aHost;
    static int serverPort;
    static Scanner scanner;
    public static void main(String args[]){
        try {
            Boolean flag = true;
            scanner = new Scanner(System.in);
            System.out.println("The Client is running");// print a message indicating that the client is running
            System.out.print("Enter the server port number: ");// ask the user to enter the server port number
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader to read input from the console
            serverPort = Integer.parseInt(serverInput.readLine());// get the server port number from the user input
            aHost = InetAddress.getByName("localhost");// get the hostname of the server

            while (true) {
                System.out.println("1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client.");

                //switch case for selecting operation to be performed
                int choice = scanner.nextInt();
                switch(choice)
                {
                    case 1:
                        add();
                        break;

                    case 2:
                        subtract();
                        break;
                    case 3:
                        get();
                        break;
                    case 4:
                        flag = false;
                        System.out.println("Client side quitting. The remote variable server is still running.!");
                        break;
                }
                if(flag==false)
                    break;
                receivePacket();
            }
        }catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());// print an error message if a socket exception occurs
        }catch (IOException e){
            System.out.println("IO Exception: " + e.getMessage());// print an error message if an I/O exception occurs
        }finally {
            if(aSocket != null) aSocket.close();// close the socket if it is not null
        }
    }

    /**
     Adds a number to the sum for the specified client ID by creating a datagram packet with the client ID, operation type "add",
     and the number to add, and sending it to the server.
     */
    public static void add()
    {
        init();
        int num,id;
        String combinedString = null;
        DatagramPacket sendPacket = null;
        System.out.println("Enter the number to add");
        num = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        System.out.print("Enter your ID: ");
        id = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = id+","+"add"+","+num;
        sendPacket = new DatagramPacket(combinedString.getBytes(), combinedString.length(), aHost, serverPort);
        try {
            aSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     Method to subtract a given number from the current value associated with a client ID on the server.
     Prompts the user to enter the value to subtract and the client ID, creates a message with the data, and sends it to the server via a datagram packet.
     */
    public static void subtract()
    {
        init();
        int num,id;
        String combinedString = null;
        DatagramPacket sendPacket = null;
        System.out.println("Enter value to subtract");
        num = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        System.out.print("Enter your ID: ");
        id = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = id+","+"subtract"+","+num;
        sendPacket = new DatagramPacket(combinedString.getBytes(), combinedString.length(), aHost, serverPort);
        try {
            aSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     If the client ID is found in the server's map, prints the retrieved value to the console and returns it.
     */
    public static void get()
    {
        init();
        int num,id;
        String combinedString = null;
        DatagramPacket sendPacket = null;
        System.out.print("Enter your ID: ");
        id = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = id+","+"get";
        sendPacket = new DatagramPacket(combinedString.getBytes(), combinedString.length(), aHost, serverPort);
        try {
            aSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     Initializes the datagram socket by creating a new DatagramSocket object.
     */
    public static void init()
    {
        try {
            aSocket = new DatagramSocket(); // create a new datagram socket
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     Receives a packet from the server and prints the reply to the console.
     */
    public static void receivePacket()
    {
        byte[] buffer = new byte[1000];// create a buffer to receive the reply from the server
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);// create a datagram packet to receive the reply from the server
        try {
            aSocket.receive(reply);// receive the reply from the server
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] replyData = Arrays.copyOf(reply.getData(), reply.getLength());// copy the reply data into a new byte array
        System.out.println("The result is: " + new String(replyData));// print the reply from the server
    }
}
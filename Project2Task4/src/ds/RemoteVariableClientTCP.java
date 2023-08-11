package ds;

import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Class used to select operation and send the data required for that operation to the server
 * this uses TCP protocol for transmission
 *
 * @author: Ruta Deshpande
 * @andrew id: rutasurd
 * @email id: rutasurd@andrew.cmu.edu
 * @date: 21st Feb 2023
 * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */

public class RemoteVariableClientTCP {
    static Scanner scanner;
    static BufferedReader in;
    static PrintWriter out;
    static Socket clientSocket = null;
    public static void main(String args[]) {
        // arguments supply hostname

        try {
            int serverPort = 7777;
            clientSocket = new Socket("localhost", serverPort);
            //BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            String m;
            Boolean flag = true;
            int choice;
            String passString = "";
            scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client.");
                choice = scanner.nextInt();
                //switch case for choosing the operation to be performed
                switch(choice)
                {
                    case 1:
                        passString = add();
                        break;

                    case 2:
                        passString = subtract();
                        break;

                    case 3:
                        passString = get();
                        break;

                    case 4:
                        flag = false;
                        System.out.println("TCP Client is quitting!");
                        break;
                }
                if(flag==false)
                    break;
                sendToServer(passString);
                receiveFromServer();

            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }

    public static void receiveFromServer()
    {
        String data = null; // read a line of data from the stream
        try {
            data = in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("The result is: " + data);
    }

    /**
     * Method to form the string when the operation chosen is add
     * concatenates id,"add", number to be added
     * @return concatenated string is returned
     */
    public static String add()
    {
        int num,id;
        String combinedString = null;
        System.out.println("Enter the number to add");
        num = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        System.out.print("Enter your ID: ");
        id = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = id+","+"add"+","+num;
        return combinedString;
    }

    /**
     * Method to form the string when the operation chosen is subtract
     * concatenates id,"subtract", number to be subtracted
     * @return concatenated string is returned
     */
    public static String subtract()
    {
        int num,id;
        String combinedString = null;
        System.out.println("Enter the number to subtract");
        num = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        System.out.print("Enter your ID: ");
        id = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = id+","+"subtract"+","+num;
        return combinedString;
    }


    /**
     * Method to form the string when the operation chosen is get
     * concatenates id,"get"
     * @return concatenated string is returned
     */
    public static String get()
    {
        int id;
        String combinedString = null;
        System.out.print("Enter your ID: ");
        id = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = id+","+"get";
        return combinedString;
    }

    /**
     * Method for sending the generated string to the server
     * @param s string to be sent to the server
     */
    public static void sendToServer(String s)
    {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            out.println(s);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
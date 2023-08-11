package ds;

import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * This class computers the result based on the input provided by the client
 * Logic for Addition, Subtraction and retrieval opeartions
 *
 * @author: Ruta Deshpande
 * @andrew id: rutasurd
 * @email id: rutasurd@andrew.cmu.edu
 * @date: 21st Feb 2023
 * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class RemoteVariableServerTCP {
    static Scanner in;
    static PrintWriter out;
    static Map<Integer, Integer> resultMap = new TreeMap<>();
    public static void main(String args[]) {
        Socket clientSocket = null;
        int num = 0;
        int id;
        String operation;
        try {
            int result =0;
            int serverPort = 7777; // the server port we are using

            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();

            // Set up "in" to read from the client socket
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */
            while (true) {
                //getting line of data
                String data = in.nextLine();

                //splitting data to get id, number and operation
                id = Integer.parseInt(data.split(",")[0]);
                operation = data.split(",")[1];
                if(data.split(",").length>2)
                {
                    num = Integer.parseInt(data.split(",")[2]);
                }
                if(operation.equalsIgnoreCase("add"))
                {
                    //if the operation string is add, call the add method
                    result = add(id,num);
                }
                else if(operation.equalsIgnoreCase("subtract"))
                {
                    //if the operation string is subtract, call the subtract method
                    result = subtract(id,num);
                }
                else if(operation.equalsIgnoreCase("get"))
                {
                    //if the operation string is get, call the get method
                    result = get(id);
                }
                sendToClient(String.valueOf(result));

            }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
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

    /**
     * Sends the string passed back to the client
     * @param result
     */
    public static void sendToClient(String result)
    {
        out.println(result);
        out.flush();
    }

    /**
     Adds the given integer to the value associated with the given ID in the resultMap.
     @param id The ID associated with the value to be added.
     @param num The integer to be added to the value associated with the ID.
     @return The new value associated with the ID in the resultMap.
     */
    public static int add(int id, int num)
    {
        int sum;
        if(resultMap.containsKey(id))
        {
            System.out.println("Adding: "+num+" to "+resultMap.get(id)+" for client "+id);
            sum = resultMap.get(id) + num;
            resultMap.put(id,sum);
        }
        else {
            System.out.println("Adding: "+num+" to "+0+" for client "+id);
            resultMap.put(id,num);
        }
        return resultMap.get(id);
    }

    /**
     Subtracts the specified number from the value associated with the specified ID in the resultMap.
     @param id the ID whose value is to be subtracted
     @param num the number to be subtracted from the value associated with the specified ID
     @return the result of the subtraction operation
     */
    public static int subtract(int id, int num)
    {
        int subtraction;
        if(resultMap.containsKey(id))
        {
            System.out.println("Subtracting: "+num+" from "+resultMap.get(id)+" for client "+id);
            subtraction = resultMap.get(id) - num;
            resultMap.put(id,subtraction);
        }
        else {
            System.out.println("Subtracting: "+num+" from "+0+" for client "+id);
            resultMap.put(id,num);
        }
        return resultMap.get(id);
    }

    /**
     Returns the current value associated with the specified ID in the resultMap.
     @param id the ID of the result value to retrieve
     @return the current value associated with the specified ID
     */
    public static int get(int id)
    {
        if(resultMap.containsKey(id))
        {
            System.out.println("Getting:  value for "+id+ " is "+resultMap.get(id)+" for client "+id);
            return resultMap.get(id);
        }
        else
        {
            System.out.println("Getting:  value for "+id+ " is 0"+" for client "+id);
            return 0;
        }
    }
}
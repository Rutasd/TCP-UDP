import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Performs operations based on input received from client
 * Logic for addition, subtraction and retrieval operations
 *
 *  *  @author: Ruta Deshpande
 *  *  @andrew id: rutasurd
 *  *  @email id: rutasurd@andrew.cmu.edu
 *  *  @date: 21st Feb 2023
 *  *  * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class RemoteVariableServerUDP {
    static Map<Integer, Integer> resultMap = new TreeMap<>();
    static DatagramSocket aSocket;
    static DatagramPacket request;

    public static void main(String args[]){


        byte[] buffer = new byte[1000]; // declare a byte array of size 1000
        try{
            System.out.println("Server started");
            int result=0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // create a buffered reader object to read from the input stream
            System.out.print("Enter the server port number: "); // print message to enter server port number
            int serverPort = Integer.parseInt(reader.readLine()); // read the server port number from the input stream and convert to an integer
            aSocket = new DatagramSocket(serverPort); // create a datagram socket with the given server port number
            request = new DatagramPacket(buffer, buffer.length); // create a datagram packet with the buffer array and its length
            while(true){ // start an infinite loop
                aSocket.receive(request); // receive a datagram packet on the socket
                byte[] requestData = Arrays.copyOf(request.getData(), request.getLength()); // copy the received packet data into a new byte array of the correct length
                String requestString = new String(requestData); // convert the byte array to a string
                int id = 0,num;
                String operation = null;
                id = Integer.parseInt(requestString.split(",")[0]);
                operation = requestString.split(",")[1];
                if(requestString.split(",").length>2) {
                    num = Integer.parseInt(requestString.split(",")[2]);
                    if (operation.equals("add"))
                        result = add(id, num);
                    else if (operation.equals("subtract"))
                        result = subtract(id, num);
                }
                else {
                    if(operation.equals("get"))
                        result = get(id);
                }
                String replyString = String.valueOf(result);
                sendToClient(replyString);
                     }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage()); // handle socket exceptions by printing an error message
        }catch (IOException e) {System.out.println("IO: " + e.getMessage()); // handle I/O exceptions by printing an error message
        }finally {if(aSocket != null) aSocket.close();} // close the socket if it is not null
    }


    /**
     This method adds the passed integer number to the sum of the specified client id in the resultMap.
     @param id the client id for which the number is to be added
     @param num the integer number to be added
     @return the updated sum of the specified client id in the resultMap
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
        System.out.println();
        return resultMap.get(id);
    }

    /**
     Subtracts the given number from the result associated with the given client id in the result map.
     @param id the client id for which we want to subtract the number from the result
     @param num the number to subtract from the result
     @return the new result for the client after subtracting the given number from the previous result
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
        System.out.println();
        return resultMap.get(id);
    }

    /**
     This method retrieves the value associated with the specified client ID from the resultMap
     and returns it. If the ID does not exist in the resultMap, the method returns 0.
     @param id the client ID whose value is to be retrieved from the resultMap
     @return the value associated with the specified client ID, or 0 if the ID does not exist in the resultMap
     */
    public static int get(int id)
    {
        if(resultMap.containsKey(id))
        {
            System.out.println("Getting:  value for "+id+ " is "+resultMap.get(id)+" for client "+id);
            System.out.println();
            return resultMap.get(id);
        }
        else
        {
            System.out.println("Getting:  value for "+id+ " is 0"+" for client "+id);
            return 0;
        }
    }

    /**
     Sends the specified reply string back to the client using the DatagramSocket and DatagramPacket objects.
     @param replyString the string to be sent back to the client
     */
    public static void sendToClient(String replyString)
    {
        DatagramPacket reply = new DatagramPacket(replyString.getBytes(), replyString.length(), request.getAddress(), request.getPort()); // create a datagram packet to send back to the client with the received data, the client address, and the client port number
        try {
            aSocket.send(reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

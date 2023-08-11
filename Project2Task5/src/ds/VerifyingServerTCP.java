package ds;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
/**
 * This class takes the gets the input from the client,
 * It verifies the hashed client id and also the signed string and then performs the operations
 * It calculates the client id using public key, signs it and send it to the server
 *
 *  * @author: Ruta Deshpande
 *  * @andrew id: rutasurd
 *  * @email id: rutasurd@andrew.cmu.edu
 *  * @date: 21st Feb 2023
 *  * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class VerifyingServerTCP {
    static Scanner in;
    static PrintWriter out;
    static Map<String, Integer> resultMap = new TreeMap<>();
    static BigInteger e;
    BigInteger d;
    static BigInteger n;
    public static void main(String args[]) {
        Socket clientSocket = null;
        int num = 0;
        String id;
        String operation;
        String signedMsg;
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
            // If we get here, then we are now connected to a client.

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
            while (in.hasNextLine()) {
                //taking string input
                String data = in.nextLine();
                //Printing the enti data from client
                System.out.println("From Client "+data);
                //splitting the string based on ; to separate from signed part
                String[] parts = data.split(";");
                //splitting first part to get value of e and n
                e = new BigInteger(parts[0].split(",")[0]);
                n = new BigInteger(parts[0].split(",")[1]);
                System.out.println("e+n: "+(String.valueOf(e)+","+String.valueOf(n)));
                //getting client id
                id  = parts[0].split(",")[2];
                //getting operation
                operation = parts[0].split(",")[3];
                System.out.println("Operation : "+operation);
                if(parts[0].split(",").length>4)
                {
                    //getting number
                    num = Integer.parseInt(parts[0].split(",")[4]);
                }
                //if verified perform operations
                if(checkClientID(id) && verify(parts[0],parts[1]))
                {
                    //adding the number
                    if(operation.equalsIgnoreCase("add"))
                    {
                        result = add(id,num);
                    }
                    //subtraction
                    else if(operation.equalsIgnoreCase("subtract"))
                    {
                        result = subtract(id,num);
                    }
                    //retrieving results
                    else if(operation.equalsIgnoreCase("get"))
                    {
                        result = get(id);
                    }
                    sendToClient(String.valueOf(result));
                }
                else
                {
                    sendToClient("Error in request");
                }
            }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } catch (Exception ex) {
            throw new RuntimeException(ex);
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
     * Method to send result to client
     * @param result
     */
    public static void sendToClient(String result)
    {
        System.out.println("Sending " + result+ " to client");
        out.println(result);
        out.flush();

    }

    /**
     * Method to add a number for the client id
     * @param id
     * @param num
     * @return result
     */
    public static int add(String id, int num)
    {
        int sum;
        if(resultMap.containsKey(id))
        {
            sum = resultMap.get(id) + num;
            resultMap.put(id,sum);
        }
        else {
            resultMap.put(id,num);
        }
        return resultMap.get(id);
    }

    /**
     * Method to subtract a number for the client id
     * @param id
     * @param num
     * @return result
     */
    public static int subtract(String id, int num)
    {
        int subtraction;
        if(resultMap.containsKey(id))
        {
            subtraction = resultMap.get(id) - num;
            resultMap.put(id,subtraction);
        }
        else {
            resultMap.put(id,num);
        }
        return resultMap.get(id);
    }
    /**
     * Method to get the sum for the client id
     * @param id
     * @return result
     */
    public static int get(String id)
    {
        if(resultMap.containsKey(id))
        {
            return resultMap.get(id);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Method for checking whether e+n hashes to client id
     * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
     * @param clientId
     * @return
     * @throws UnsupportedEncodingException
     */
    public static boolean checkClientID(String clientId) throws UnsupportedEncodingException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update((String.valueOf(e)+","+String.valueOf(n)).getBytes());
        byte[] hashValue = md.digest();
        byte[] idByte = new byte[20];
        //byte[] idByte = Arrays.copyOfRange(hashValue, hashValue.length - 20, hashValue.length);
        System.arraycopy(hashValue, hashValue.length - 20, idByte, 0, 20);
        if(new String(idByte,"UTF-8").equals(clientId))
            return true;
        else
        {
            System.out.println(" Client and public key check failed");
            return false;
        }

    }

    /**
     * Method to check whether the unsigned and signed parts match
     * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
     * @param messageToCheck
     * @param encryptedHashStr
     * @return
     * @throws Exception
     */
    public static boolean verify(String messageToCheck, String encryptedHashStr)throws Exception  {

        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(encryptedHashStr);
        // Decrypt it
        BigInteger decryptedHash = encryptedHash.modPow(e, n);

        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");

        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);

        // messageToCheckDigest is a full SHA-256 digest
        // take two bytes from SHA-256 and add a zero byte
        byte[] extraByte = new byte[messageToCheckDigest.length+1];
        extraByte[0] = 0;

        for(int i = 0; i < messageToCheckDigest.length; i++){
            extraByte[i+1] = messageToCheckDigest[i];
        }
        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);

        // inform the client on how the two compare
        if(bigIntegerToCheck.compareTo(decryptedHash) == 0) {

            return true;
        }
        else {
            System.out.println("Verification failed");
            return false;
        }
    }

}
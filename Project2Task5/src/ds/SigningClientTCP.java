package ds;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * This class takes the operation as input
 * It calculates the client id using public key, signs it and send it to the server
 *
 *  * @author: Ruta Deshpande
 *  * @andrew id: rutasurd
 *  * @email id: rutasurd@andrew.cmu.edu
 *  * @date: 21st Feb 2023
 *  * Reference - https://github.com/CMU-Heinz-95702/Project-2-Client-Server
 */
public class SigningClientTCP {
    static Scanner scanner;
    static BufferedReader in;
    static PrintWriter out;
    static Socket clientSocket = null;
    static String publicKey;
    static String privateKey;
    static BigInteger n; // n is the modulus for both the private and public keys
    static BigInteger e; // e is the exponent of the public key
    static BigInteger d; // d is the exponent of the private key
    static byte[] idByte = new byte[20];
    public static void main(String args[]) {
        // arguments supply hostname
        try {

            generateKeys();
            createID();
            int serverPort = 7777;
            clientSocket = new Socket("localhost", serverPort);
            //BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            String m;
            Boolean flag = true;
            int choice;
            String passString = "";
            String finalString = "";
            scanner = new Scanner(System.in);
            while (true) {
                //getting choice from user
                System.out.println("1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client.");
                choice = scanner.nextInt();
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
                try {
                    finalString = sign(passString);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                sendToServer(passString+";"+finalString);
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

    /**
     * Method to receive from server
     */
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
     * Method to return string for addition operation
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String add() throws UnsupportedEncodingException {
        int num;
        String combinedString = null;
        System.out.println("Enter the number to add");
        num = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = publicKey+","+new String(idByte, "UTF-8")+","+"add"+","+num;
        return combinedString;
    }

    /**
     * Mwthod to return string for subtraction
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String subtract() throws UnsupportedEncodingException {
        int num;
        String combinedString = null;
        System.out.println("Enter the number to subtract");
        num = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        combinedString = publicKey+","+new String(idByte, "UTF-8")+","+"subtract"+","+num;
        return combinedString;
    }

    /**
     * Method to return string for get
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String get() throws UnsupportedEncodingException {
        String combinedString = null;
        combinedString = publicKey+","+new String(idByte, "UTF-8")+","+"get";
        return combinedString;
    }

    /**
     * Method for sending string to server
     * @param s
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

    /**
     * Method for generating  e and n
     */
    public static void generateKeys()
    {
        Random rnd = new Random();
        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again and you will
        // notice it takes much longer to do the math with that many bits.
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);

        publicKey = String.valueOf(e) +","+ String.valueOf(n);
        privateKey = String.valueOf(d) +","+ String.valueOf(n);
        System.out.println("Public key is: "+publicKey);
        System.out.println("Private key is: "+privateKey);
    }

    /**
     * Method for generating client id from e and n
     */
    public static void createID()
    {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update((String.valueOf(e) +","+ String.valueOf(n)).getBytes());
        byte[] hashValue = md.digest();
        System.arraycopy(hashValue, hashValue.length - 20, idByte, 0, 20);
    }
    public static String sign(String message) throws Exception {

        // compute the digest with SHA-256
        byte[] bytesOfMessage = message.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        // we only want two bytes of the hash for ShortMessageSign
        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        byte[] messageDigest = new byte[bigDigest.length+1];
        messageDigest[0] = 0;   // most significant set to 0
        for(int i = 0; i < bigDigest.length; i++){
            messageDigest[i+1] = bigDigest[i];
        } // The message digest now has three bytes. Two from SHA-256
        // and one is 0.
        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);

        // encrypt the digest with the private key
        BigInteger c = m.modPow(d, n);

        // return this as a big integer string
        return c.toString();
    }
}
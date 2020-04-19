/**
 *
 *  @author Wałachowski Marcin S19541
 *
 */

package zad1;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client{
    Socket clientSocket;
    SocketChannel client;
    String host;
    int port;
    String id;
    public Client(String host, int port, String id){
        this.host=host;
        this.port=port;
        this.id=id;
    }
    public void connect(){
        try {
            //clientSocket = new Socket(host, port);
            client = SocketChannel.open(new InetSocketAddress(host, port));
        }catch(IOException e){
            System.out.println("Problem with creating client socket");
        }
    }
    public String send(String req){
        try {
            //DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            //outToServer.writeBytes(req + '\n');
            //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //String answerFromServer = inFromServer.readLine();
            //return answerFromServer;
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put((req+'\n').getBytes());
            buffer.flip();
            client.write(buffer);
            ByteBuffer buffer2 = ByteBuffer.allocate(1024);
            client.read(buffer2);
            Charset charset  = Charset.forName("ISO-8859-2");
            return new String(buffer2.array()).trim();
        }catch(IOException e){
            System.out.println("Problem with sending request to server");
        }
        return null;
    }
}

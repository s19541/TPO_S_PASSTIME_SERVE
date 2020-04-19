/**
 *
 *  @author Wałachowski Marcin S19541
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Server implements Runnable{
    String host;
    int port;
    private ServerSocketChannel ssc = null;
    boolean running;
    Map<SocketChannel,String> clientNames;
    Map<SocketChannel,String> clientLogs;
    private Selector selector = null;
    String log;
    Server(String host,int port){
        this.host=host;
        this.port=port;
        log="";
        clientNames = new HashMap<>();
        clientLogs = new HashMap<>();

    }
    public void startServer(){
        try {
            running=true;
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.socket().bind(new InetSocketAddress(host, port));
            selector = Selector.open();
            ssc.register(selector,SelectionKey.OP_ACCEPT);
            new Thread() {
                @Override
                public void run() {
                    startS();
                }
            }.start();
        }
        catch(IOException ex){}
    }
    public void startS(){
        run();
    }
    public void stopServer() {
        new Thread() {
            @Override
            public void run() {
                try {
                    this.sleep(1000);
                }catch(InterruptedException e){}
                running = false;
                try {
                    ssc.close();
                } catch (IOException e) { }
            }
        }.start();
    }
    String getServerLog(){
        try {
            Thread.sleep(1000);
        }catch(InterruptedException e){}
        return log;
    }
    @Override
    public void run() {
        while(running){
            try {
                selector.select();
                Set keys = selector.selectedKeys();
                Iterator iter = keys.iterator();
                while(iter.hasNext()) {
                    SelectionKey key = (SelectionKey) iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        SocketChannel cc = ssc.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ);
                        continue;
                    }

                    if (key.isReadable()) {
                        SocketChannel cc = (SocketChannel) key.channel();
                        serviceRequest(cc);
                        continue;
                    }
                }

            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }
    Charset charset  = Charset.forName("ISO-8859-2");
    private void serviceRequest(SocketChannel sc) {
        if (!sc.isOpen()) return;
        StringBuffer reqString = new StringBuffer();
        ByteBuffer bbuf = ByteBuffer.allocate(1024);
        reqString.setLength(0);
        bbuf.clear();
        try {
            readLoop:
            while (true) {
                int n = sc.read(bbuf);
                if (n > 0) {
                    bbuf.flip();
                    CharBuffer cbuf = charset.decode(bbuf);
                    while(cbuf.hasRemaining()) {
                        char c = cbuf.get();
                        if (c == '\r' || c == '\n') break readLoop;
                        reqString.append(c);
                    }
                }
            }
            if (reqString.toString().contains("login")) {
                writeResp(sc, "logged in");
                clientNames.put(sc,reqString.toString().substring(reqString.toString().indexOf(' ')+1));
                log+=clientNames.get(sc)+" logged in at "+ LocalTime.now()+'\n';
                clientLogs.put(sc,"=== "+clientNames.get(sc)+ " log start ==="+'\n'+"logged in"+'\n');
            }
            else if (reqString.toString().equals("bye")) {
                writeResp(sc, "logged out");
                sc.close();
                sc.socket().close();
                log+=clientNames.get(sc)+" logged out at "+ LocalTime.now()+'\n';
                clientLogs.put(sc,clientLogs.get(sc)+"=== "+clientNames.get(sc)+ " log end ==="+'\n');
            }
            else if (reqString.toString().contains("bye and log transfer")) {
                clientLogs.put(sc,clientLogs.get(sc)+"=== "+clientNames.get(sc)+ " log end ==="+'\n');
                writeResp(sc, clientLogs.get(sc));
                log+=clientNames.get(sc)+" logged out at "+ LocalTime.now()+'\n';
            }
            else {
                String result = Time.passed(reqString.toString().substring(0,reqString.toString().indexOf(' ')),reqString.toString().substring(reqString.toString().indexOf(' ')+1));
                writeResp(sc, result);
                log+=clientNames.get(sc)+" request at "+ LocalTime.now()+": \""+reqString.toString()+"\""+'\n';
                clientLogs.put(sc,clientLogs.get(sc)+"Request: "+reqString.toString()+'\n'+"Result: "+'\n'+result+'\n');
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            try { sc.close();
                sc.socket().close();
            } catch (Exception e) {}
        }
    }
    private void writeResp(SocketChannel sc, String msg)
            throws IOException {
        //StringBuffer remsg = new StringBuffer();
        //remsg.setLength(0);
        //remsg.append(msg);
        //ByteBuffer buf = charset.encode(CharBuffer.wrap(remsg));
        //sc.write(buf);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(msg.getBytes());
        buffer.flip();
        sc.write(buffer);
    }
}

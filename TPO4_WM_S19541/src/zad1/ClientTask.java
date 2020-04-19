/**
 *
 *  @author Wałachowski Marcin S19541
 *
 */

package zad1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ClientTask implements Runnable {
    Client c;
    List<String> reqs;
    boolean showSendRes;
    String log="";
    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes){
        ClientTask task =  new ClientTask();
        task.c=c;
        task.reqs=reqs;
        task.showSendRes=showSendRes;
        return task;
    }

    @Override
    public void run() {

            c.connect();
            c.send("login " + c.id);
            for(String req : reqs) {
                String res = c.send(req);
                if (showSendRes) System.out.println(res);
            }
        log = c.send("bye and log transfer");


        }

    public String get() throws InterruptedException, ExecutionException {
        try{
            Thread.sleep(200);
        }catch(InterruptedException e){}
        return log;
    }
}

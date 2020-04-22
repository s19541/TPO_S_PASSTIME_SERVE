/**
 *
 *  @author Wałachowski Marcin S19541
 *
 */

package zad1;



import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {
    Client c;
    List<String> reqs;
    boolean showSendRes;
    static StringBuffer log = new StringBuffer();
    public ClientTask(Callable<String> callable) {
        super(callable);
    }
    public static ClientTask create(Client c, List<String> reqs, boolean showSendRes){
        return new ClientTask(()->{

            c.connect();
            c.send("login " + c.id);
            reqs.forEach((string)->{

                String response = c.send(string);

                if(showSendRes)
                    System.out.println(response);


            });

            log.append(c.send("bye and log transfer"));

            return log.toString();

        });
    }

}

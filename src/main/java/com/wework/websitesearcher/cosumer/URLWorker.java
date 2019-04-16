package com.wework.websitesearcher.cosumer;

import com.wework.websitesearcher.queue.Task;
import com.wework.websitesearcher.queue.TaskQueue;
import com.wework.websitesearcher.queue.URLTask;
import com.wework.websitesearcher.util.Constants;
import com.wework.websitesearcher.util.PCCoordinator;
import com.wework.websitesearcher.util.ResultAggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class URLWorker implements Worker {
    private final TaskQueue queue;
    private final PCCoordinator pc;
    private final ResultAggregator aggregator ;
    private final int Id ;

    public URLWorker(TaskQueue queue, PCCoordinator pc, ResultAggregator aggregator, int Id){
        this.queue = queue;
        this.pc = pc;
        this.aggregator = aggregator;
        this.Id = Id;
    }

    private void connectURLAndParse(String url){
        try {
            int len = url.length();
            if (url.charAt(len-1)=='"') {
                url =url.substring(1, len-1);
            }

            if(!url.startsWith("https") || !url.startsWith("http"))
                url = "https://"+url;
            URL website = new URL(url);
            URLConnection con = website.openConnection();
            con.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
            con.setReadTimeout(Constants.READ_TIMEOUT);
            InputStream is = con.getInputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(is));
            aggregator.parseStream(in, Id);

        } catch (MalformedURLException e) {
            aggregator.errorPlusOne();
            System.out.println("MalformedURLException  : " + e.getMessage());
        } catch (IOException e) {
            aggregator.errorPlusOne();
            System.out.println("Connection Error : " + e.getMessage());
        }
    }


    @Override
    public void work() {
        while(true){
            try {
                if (queue.isEmpty()) {
                    if(pc.isProducerDepleted()){
                        pc.workerDone();
                        System.out.println("Worker #"+Id+" is done.");
                        return;
                    }
                    else{
                        System.out.println("Worker #"+Id+" is waiting.");
                        pc.consumerWait();
                        System.out.println("Worker #"+Id+" is waken up.");
                    }
                }
                Task t = queue.poll();
                if(t == null) continue;

                URLTask task = (URLTask)t;
                pc.notifyProducer();
                String url = task.getUrl();
                String taskId = task.getId();
                System.out.println("Worker #"+ Id + " is working on task# " + taskId +", url:" + url);
                aggregator.totalPlusOne();
                connectURLAndParse(url);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

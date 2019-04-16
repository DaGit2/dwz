package com.wework.websitesearcher.producer;

import com.wework.websitesearcher.queue.TaskQueue;
import com.wework.websitesearcher.queue.TaskQueueManager;
import com.wework.websitesearcher.queue.URLTask;
import com.wework.websitesearcher.util.Constants;
import com.wework.websitesearcher.util.PCCoordinator;

import java.io.*;

public class URLTaskProducer implements Producer {
    final private TaskQueue<URLTask> queue;
    final private PCCoordinator pc;
    final private String file;
    private boolean isCompleted = false;

    public URLTaskProducer(String file, PCCoordinator pc) {
        this.queue = TaskQueueManager.getInstance().retrieveQueue(Constants.URL_TASK_QUEUE);
        this.pc = pc;
        this.file = file;


    }
    private String[] filterURL(String line){
        //TODO : validate line is correctly format, or ignore the line.
        String[] inputs = line.split(",");
        String[] res = new String[]{inputs[0],inputs[1]};
        //this is just to check if URL is a valid one by checking the string contains a dot or not.
        return res[1].contains(".") ? res : null;
    }

    @Override
    public void produce(){
        try {
            BufferedReader reader;
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while ( !isCompleted ) {

                System.out.println("Producer is loading : " + line);
                if(queue.isFull()){
                    pc.producerWait();
                }
                String[] url = filterURL(line) ;
                if(url != null){
                    queue.offer(new URLTask(url[1], url[0]));
                    pc.notifyConsumer();
                }
                line = reader.readLine();
                if(line == null){
                    isCompleted = true;
                }
            }
        } catch (IOException e) {
            throw new ReadFileException(e.getMessage());
        } catch (InterruptedException e){
            e.printStackTrace();
        } finally {
            System.out.println("Producer completed loading : ");
            pc.producerDone();
        }
    }
}

package com.wework.websitesearcher.cosumer;

import com.wework.websitesearcher.queue.TaskQueue;
import com.wework.websitesearcher.queue.TaskQueueManager;
import com.wework.websitesearcher.queue.URLTask;
import com.wework.websitesearcher.util.Constants;
import com.wework.websitesearcher.util.PCCoordinator;
import com.wework.websitesearcher.util.ResultAggregator;


public class URLTaskConsumer implements Consumer {
    final private PCCoordinator pc;
    final private TaskQueue<URLTask> queue;
    final private int workerCount;
    final private ResultAggregator aggregator;

    public URLTaskConsumer(int workerCount, PCCoordinator pc, ResultAggregator aggregator){
        this.pc = pc;
        this.queue = TaskQueueManager.getInstance().retrieveQueue(Constants.URL_TASK_QUEUE);
        this.workerCount = workerCount;
        this.aggregator = aggregator;
    }

    @Override
    public void consume() {
        for(int i = 0; i< this.workerCount ; ++i){
            URLWorker worker = new URLWorker(queue, pc, aggregator, i );
            new Thread(()-> worker.work()).start();
        }
    }

}

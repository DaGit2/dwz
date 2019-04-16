package com.wework.websitesearcher;

import com.wework.websitesearcher.cosumer.Consumer;
import com.wework.websitesearcher.cosumer.URLTaskConsumer;
import com.wework.websitesearcher.producer.Producer;
import com.wework.websitesearcher.producer.URLTaskProducer;
import com.wework.websitesearcher.queue.TaskQueue;
import com.wework.websitesearcher.queue.TaskQueueManager;
import com.wework.websitesearcher.queue.URLTask;
import com.wework.websitesearcher.util.Constants;
import com.wework.websitesearcher.util.PCCoordinator;
import com.wework.websitesearcher.util.ResultAggregator;

class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {

        TaskQueueManager qm = TaskQueueManager.getInstance();
        int task_queue_size = 100;
        TaskQueue<URLTask> q = new TaskQueue<>(task_queue_size);
        qm.manageQueue(Constants.URL_TASK_QUEUE, q);

        int worker_count = 20;
        PCCoordinator pcCoordinator = new PCCoordinator(worker_count);
        ResultAggregator aggregator = new ResultAggregator(".*[H|h]tml.*");

        Producer p = new URLTaskProducer("urls.txt", pcCoordinator);
        Consumer c = new URLTaskConsumer(worker_count, pcCoordinator, aggregator);

        new Thread(()-> p.produce()).start();
        new Thread(()-> c.consume()).start();

        pcCoordinator.waitForProducerDone();
        pcCoordinator.waitForConsumerDone();
        System.out.println(aggregator.summarize());
    }

}


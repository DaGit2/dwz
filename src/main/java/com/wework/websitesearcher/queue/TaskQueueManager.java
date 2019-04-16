package com.wework.websitesearcher.queue;

import java.util.HashMap;
import java.util.Map;

public class TaskQueueManager {
    private final Map<String, TaskQueue> queuesMap = new HashMap<>();
    private static TaskQueueManager single_instance = null;

    private TaskQueueManager(){

    }

    public static TaskQueueManager getInstance()
    {
        if (single_instance == null)
            single_instance = new TaskQueueManager();

        return single_instance;
    }

    public void manageQueue(String name, TaskQueue q){
        queuesMap.put(name,q);
    }

    public TaskQueue retrieveQueue(String name){
        TaskQueue q = queuesMap.get(name);
        if(q == null){
            throw new TaskQueueException("NO QUEUE FOUND for name: " + name);
        }
        return q;
    }
}

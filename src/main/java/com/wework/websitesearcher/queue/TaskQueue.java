package com.wework.websitesearcher.queue;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue<T extends Task> {
    private Queue<T> blockingQueue;
    private int MAX_SIZE;

    @SuppressWarnings("unused")
    public TaskQueue(){
        int DEFAULT_SIZE = 100;
        init(DEFAULT_SIZE);
    }

    public TaskQueue(int queue_size){
        init(queue_size);
    }

    private void init(int queue_size){
        MAX_SIZE = queue_size;
        blockingQueue = new LinkedBlockingQueue<>()  ;
    }
    public boolean isFull(){
        return blockingQueue.size() >= MAX_SIZE;
    }

    public boolean isEmpty(){
        return blockingQueue.size() ==0 ;
    }

    public void offer(T task){
        blockingQueue.offer(task);
    }

    public T poll(){
        return  blockingQueue.poll();
    }
}

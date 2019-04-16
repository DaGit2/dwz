package com.wework.websitesearcher.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PCCoordinator {
    private final Lock queueLock = new ReentrantLock();
    private final Condition notFull  = queueLock.newCondition();
    private final Condition notEmpty = queueLock.newCondition();

    private final Lock producerLock = new ReentrantLock();
    private final Condition producerDone  = producerLock.newCondition();

    private final CountDownLatch countDownLatch ;
    private final AtomicBoolean producerDepleted = new AtomicBoolean(false);

    public PCCoordinator(int workerCount){

        countDownLatch = new CountDownLatch(workerCount);
    }
    public void producerWait() throws InterruptedException{
        internalWait(queueLock, notFull);
    }

    public void notifyProducer(){
        internalSignal(queueLock, notFull);
    }

    public void consumerWait() throws InterruptedException{
        internalWait(queueLock, notEmpty);
    }

    public void notifyConsumer() {
        internalSignal(queueLock, notEmpty);
    }

    public void waitForProducerDone() throws InterruptedException{
        internalWait(producerLock, producerDone);
    }

    public void producerDone(){
        producerDepleted.set(true);
        System.out.println("-----------Producer is done.");
        internalSignal(producerLock, producerDone);
        notifyConsumer();
    }

    public boolean isProducerDepleted(){
        return producerDepleted.get();
    }

    public void waitForConsumerDone() throws InterruptedException{
        countDownLatch.await();
        System.out.println("-----------Consumer is done. all workers thread completed");
    }

    public void workerDone() {
        countDownLatch.countDown();
    }

    private void internalWait(Lock lock , Condition condition ) throws InterruptedException{
        lock.lock();
        try {
            condition.await();
        } finally {
            lock.unlock();
        }
    }

    private void internalSignal(Lock lock , Condition condition ){
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}

package Game;

import java.util.LinkedList;

 //Based on sample code provided on Brightspace and show in the lecture

public class ThreadPool extends ThreadGroup {

    private boolean isAlive;
    private LinkedList<Runnable> taskQueue;
    private int threadID;
    private static int threadPoolID;

    /**
        Creates a new ThreadPool.
        @param numThreads The number of threads in the pool.
    */
    public ThreadPool(int numThreads) {
        super("ThreadPool-" + (threadPoolID++));
        setDaemon(true);

        isAlive = true;

        taskQueue = new LinkedList<>();
        for (int i=0; i<numThreads; i++) {
            new PooledThread().start();
        }
    }

    /**
     * pass in a runnable and add it to the queue
     * @param task
     */
    public synchronized void runTask(Runnable task) {
        if (!isAlive) {
            throw new IllegalStateException();
        }
        if (task != null) {
            taskQueue.add(task);
            notify();
        }
    }

    /**
     * blocks until there is a task to do, once there is a task return that task
     * @return runnable task
     * @throws InterruptedException
     */
    protected synchronized Runnable getTask()
        throws InterruptedException
    {
        while (taskQueue.size() == 0) {
            if (!isAlive) {
                return null;
            }
            wait();
        }
        return taskQueue.removeFirst();
    }


    /**
        Closes this ThreadPool and waits for all running threads
        to finish. Any waiting tasks are executed.
    */
    public void join() {
        // notify all waiting threads that this ThreadPool is no longer alive
        synchronized (this) {
            isAlive = false;
            notifyAll();
        }

        // wait for all threads to finish
        Thread[] threads = new Thread[activeCount()];
        int count = enumerate(threads);
        for (int i=0; i<count; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException ex) { }
        }
    }


    /**
        A PooledThread is a Thread in a ThreadPool group, designed to run tasks (Runnables).
    */
    private class PooledThread extends Thread {

        public PooledThread() {
            super(ThreadPool.this, "PooledThread-" + (threadID++));
        }

        public void run() {

            while (true) {

                // get a task to run
                Runnable task = null;
                try {
                    task = getTask();
                } catch (InterruptedException ex) { }

                // if getTask() returned null or was interrupted, close this thread.
                if (task == null) {
                    break;
                }

                try {
                    task.run();
                } catch (Exception e) {}
            }
        }
    }
}
package Test2;

import Test.CFuture;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class ThreadPool {
    //Fixed thread pool implementation!
    volatile boolean stop;
    List<Thread> l = new LinkedList<>();
    Queue<Runnable> q = new LinkedList<>();

    public ThreadPool(int num){
        stop=false;
        for(int i=0;i<num;++i){
            l.add(new Thread(()->{
                while(!stop){
                    Runnable temp= null;
                    synchronized (q) {
                        try {
                            if (q.isEmpty())
                                q.wait();
                            temp = q.poll();
                        } catch (InterruptedException e) { }
                    }
                    if (temp!=null)
                        temp.run();
                }
            }));
            l.get(i).start();
        }
    }

    public void execute(Runnable r)  {
        synchronized (q) {
            try {
                q.add(r);
                q.notify();
            } catch (Exception e){}
        }
    }

    public <V> CFuture<V> submit(Callable<V> c){
        CFuture<V> f = new CFuture<>();
        execute(()->{ try { f.set(c.call()); } catch (Exception e) {} });
        return f;
    }

    //public
    public void shutdown() {
        System.out.println("shutingdown is called");
        synchronized (q) {
            try {
                q.add(() -> { stop = true; System.out.println("shutingdown mission has finished");});
                q.notifyAll();
            } catch (Exception e){}
        }
    }




    public static void main(String[] args) {
        //Sanity check: check whether two threads are being created by waiting 5 second to the two prints will be in the same time
        ThreadPool p = new ThreadPool(2);
        System.out.println("tttt");
        p.execute(()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(1);
        });
        p.execute(()->{
           try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(2);
        });
        //end of sanity check

        p.submit(()->{Thread.sleep(1000); return "42"; })
                .thenApply((answer)->{try{Thread.sleep(5000);}catch (Exception e){}return Integer.parseInt(answer);})
                .thenApply((answer)->{return answer*100;})
                .thenAccept((answer)->{System.out.println(answer);});
        p.shutdown();

    }
}

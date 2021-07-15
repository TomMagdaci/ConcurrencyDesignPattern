package Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class CompleteFuture<V> {
    V value;
    Runnable r;

    public V get(){
        //implemented as double check locking
        if(value==null){
            synchronized (this){
                while(value==null){
                    try{wait();}catch (Exception e){};
                }
            }
        }
        return value;
    }

    public void set(V v){
        synchronized (this) {
            value = v;
            notifyAll();
        }
        if(r!=null)
            r.run();
    }

    public static <V> CompleteFuture<V> supplyAsync(Callable<V> c){
        CompleteFuture<V> cf = new CompleteFuture<>();
        new Thread(()->{cf.set(c.call());}).start();
        return cf;
    }

    public <R> CompleteFuture<R> thenApply(Function<V,R> f){
        CompleteFuture<R> cf = new CompleteFuture<>();
        if(value==null)
            r=()->{cf.set(f.apply(value));};
        else
            new Thread(()->{cf.set(f.apply(value));}).start();
        return cf;
    }

    public void thenAccept(Consumer<V> c){
        if(value==null)
            r=()->{c.accept(value);};
        else
            new Thread(()->{c.accept(value);}).start();
    }


/*    public static void main(String[] args){

        CompleteFuture.supplyAsync(()->{try{Thread.sleep(4000);}catch (Exception e){}; return "20"; })
                .thenApply((answer)->{return Integer.parseInt(answer);})
                .thenApply((answer)->answer*2).thenAccept((answer)->{System.out.println(answer);});

        CompleteFuture.supplyAsync(()->{try{Thread.sleep(10000);}catch (Exception e){}; return 2;}).get();
        System.out.println("Finally main thread finished");
    }*/

}

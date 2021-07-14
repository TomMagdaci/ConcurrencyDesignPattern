package Test;

import java.util.function.Consumer;
import java.util.function.Function;

public class CFuture<V> {
    public V value;
    Runnable r;

    public void set(V v){
        this.value = v;
        if (r!=null)
            r.run();
    }
    public static <V> CFuture<V> supplyAsync(Callable<V> c){
        CFuture<V> cf = new CFuture<>();
        new Thread(() -> { cf.set(c.call()); }).start();
        return cf;
    }

    public <R> CFuture<R> thenApply(Function<V,R> fci){
        CFuture<R> newF = new CFuture<>();
        if (value==null)
            r = () -> { newF.set(fci.apply(this.value));};
        else
            new Thread(() -> { newF.set(fci.apply(this.value));}).start();
        return newF;
    }

    public void thenAccept(Consumer<V> con){
        if (value==null)
            r = () -> { con.accept(this.value); };
        else
            new Thread(() -> { con.accept(this.value); }).start();
    }
}

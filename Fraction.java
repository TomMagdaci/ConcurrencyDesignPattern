package Test3;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Fraction extends RecursiveTask<Integer> {
    int num;
    public Fraction(int n){this.num=n;}
    public Integer compute() {
        if (this.num<=1)
            return 1;
        Fraction f = new Fraction(this.num-1);
        f.fork();
        return this.num*f.join();
    }

    public static void main(String[] args){
        Fraction f = new Fraction(10);
        ForkJoinPoolTry q = new ForkJoinPoolTry();
        System.out.println(q.invoke(f));

    }
}

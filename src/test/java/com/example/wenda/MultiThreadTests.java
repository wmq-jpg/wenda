package com.example.wenda;

import org.springframework.web.servlet.mvc.condition.ConsumesRequestCondition;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadTests {

    public  static class MyThread extends Thread {
        private int tid;

        public MyThread(int tid) {
            this.tid = tid;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("%d:%d", tid, i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public  static void TestThread()
    {
        for (int i = 0; i <10 ; i++) {
            new MyThread(i).start();

        }
    }
    public static void TestThread2()
    {
        for (int i = 0; i <10 ; i++) {
           int flag=i;
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        for (int k= 0; k< 10; k++) {
                            Thread.sleep(1000);
                            System.out.println(String.format("%d:%d",flag,k));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            ).start();
        }
    }
    public static Object obj=new  Object();
    public static void Synchoronized1()
    {
        synchronized(obj)
        {
            try {
                for (int k= 0; k< 10; k++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T3 %d",k));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void Synchoronized2()
    {
        synchronized(new Object())
        {
            try {
                for (int k= 0; k< 10; k++) {
                    Thread.sleep(1000);
                    System.out.println(String.format("T4 %d",k));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   public static class Consumer implements Runnable
    {
        BlockingQueue<String> q;
        public Consumer(BlockingQueue<String> q)
        {
              this.q=q;
        }
        @Override
        public void run()
        {
            try{
                while(true)
                {
                    System.out.println(Thread.currentThread().getName()+" " +q.take());
                }


            }
            catch(Exception e)
            {
                e.printStackTrace();
            }


        }

    }
public static class Producer implements Runnable
{   private BlockingQueue<String > q;
   public Producer(BlockingQueue<String >q)
   {
       this.q=q;
   }
    @Override
    public void run() {
        try{
            for (int i = 0; i <100 ; i++) {
                Thread.sleep(1000);
                q.put(String.valueOf(i));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }

}
    public static void testBlockingQueue()
    {
        BlockingQueue<String> q=new ArrayBlockingQueue<String>(10);
        new Thread(new Producer(q)).start();
        new Thread(new Consumer(q),"Consumer1").start();
        new Thread(new Consumer(q),"Consumer2").start();


    }
    private static ThreadLocal<Integer> threadLocalUserIds=new ThreadLocal<>();
    private static int userId;
    public static void testThreadLocal()
    {
        for (int i = 0; i <10 ; i++) {
            final int finalI=i;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        threadLocalUserIds.set(finalI);
                        Thread.sleep(1000);
                        System.out.println("ThreadLocal:" +threadLocalUserIds.get());
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }

            }).start();

        }

        for (int i = 0; i <10 ; i++) {
            final int finalI=i;
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                       userId=finalI;
                        Thread.sleep(1000);
                        System.out.println("UserID" + userId);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    public static void testExcutor()
    {
        //ExecutorService service= Executors.newSingleThreadExecutor();
        ExecutorService service=Executors.newFixedThreadPool(2);
        service.submit(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        System.out.println("Executor1:"+i);

                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        service.submit(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        System.out.println("Executor2:"+i);

                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        service.shutdown();
        while(!service.isTerminated())
        {
            try{

                Thread.sleep(1000);
                System.out.println("wait for Termination");

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    private static int counter=0;
    private static AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void testWithoutAtomic()
    {
        for (int i = 0; i <10 ; i++) {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try{
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++) {
                            counter++;
                            System.out.println(counter);
                        }

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
    public static void testWithAtomic()
    {
        for (int i = 0; i <10 ; i++) {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try{
                        Thread.sleep(1000);
                        for (int j = 0; j < 10; j++) {

                            System.out.println(atomicInteger.incrementAndGet());
                        }

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
public  static void testFuture()
{
    ExecutorService service=Executors.newSingleThreadExecutor();
    Future future=service.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            Thread.sleep(1000);
            //throw new IllegalArgumentException();
             return 1;

        }

    });
    service.shutdown();
    try
    {
        System.out.println(future.get());
      //  System.out.println(future.get(100,TimeUnit.MILLISECONDS));


    }catch(Exception e)
    {
        e.printStackTrace();
    }


}



    public static void main(String[] args) {
        // TestThread();
           //     TestThread2();
        /*for (int i = 0; i <10 ; i++) {
            new Thread(new Runnable()
            {
                public void run()
                {
                    Synchoronized1();
                    Synchoronized2();
                }
            }).start();

        }
        */
      //  testBlockingQueue();
       // testThreadLocal();
        //testExcutor();
      //  testWithoutAtomic();
       // testWithAtomic();
        testFuture();
    }


}

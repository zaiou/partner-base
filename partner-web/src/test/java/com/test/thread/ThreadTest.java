package com.test.thread;

/**
 * @Description:
 * @auther: LB 2018/12/21 15:41
 * @modify: LB 2018/12/21 15:41
 */
public class ThreadTest {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
            if (i == 30) {
                Thread myThread1 = new MyThread();     // 创建一个新的线程  myThread1  此线程进入新建状态
                Thread myThread2 = new MyThread();     // 创建一个新的线程 myThread2 此线程进入新建状态
                myThread1.start();                     // 调用start()方法使得线程进入就绪状态
                myThread2.start();                     // 调用start()方法使得线程进入就绪状态
            }
        }
        System.out.println("==============线程执行完成==================");
    }
}

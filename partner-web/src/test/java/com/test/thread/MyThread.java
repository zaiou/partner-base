package com.test.thread;

/**
 * @Description:
 * @auther: LB 2018/12/21 15:41
 * @modify: LB 2018/12/21 15:41
 */
class MyThread extends Thread {

    private int i = 0;

    @Override
    public void run() {
        for (i = 0; i < 100; i++) {
            System.out.println("当前线程"+Thread.currentThread().getName() + " " + i);
        }
    }
}

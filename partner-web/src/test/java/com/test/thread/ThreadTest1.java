package com.test.thread;

/**
 * @Description:
 * @auther: LB 2018/12/26 16:36
 * @modify: LB 2018/12/26 16:36
 */
public class ThreadTest1 {
    public static void main(String[] args) {
        Account account = new Account("123456", 0);

        Thread drawMoneyThread = new DrawMoneyThread("取钱线程", account, 700);
        Thread depositeMoneyThread = new DepositeMoneyThread("存钱线程", account, 700);

        drawMoneyThread.start();
        depositeMoneyThread.start();
    }
}

package com.test.thread;

/**
 * @Description:
 * @auther: LB 2018/12/26 16:37
 * @modify: LB 2018/12/26 16:37
 */
public class DrawMoneyThread extends Thread {

    private Account account;
    private double amount;

    public DrawMoneyThread(String threadName, Account account, double amount) {
        super(threadName);
        this.account = account;
        this.amount = amount;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            account.draw(amount, i);
        }
    }
}

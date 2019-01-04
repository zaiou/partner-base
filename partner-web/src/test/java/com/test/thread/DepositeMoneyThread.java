package com.test.thread;

/**
 * @Description:
 * @auther: LB 2018/12/26 16:37
 * @modify: LB 2018/12/26 16:37
 */
public class DepositeMoneyThread extends Thread {

    private Account account;
    private double amount;

    public DepositeMoneyThread(String threadName, Account account, double amount) {
        super(threadName);
        this.account = account;
        this.amount = amount;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            account.deposite(amount, i);
        }
    }
}

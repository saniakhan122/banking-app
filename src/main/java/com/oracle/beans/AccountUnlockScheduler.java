package com.oracle.beans;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.oracle.dao.CustomerLoginDAO;

public class AccountUnlockScheduler {

    private final CustomerLoginDAO customerLoginDAO;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AccountUnlockScheduler(CustomerLoginDAO customerLoginDAO) {
        this.customerLoginDAO = customerLoginDAO;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::unlockExpiredAccounts, 0, 5, TimeUnit.MINUTES);
    }

    private void unlockExpiredAccounts() {
        try {
            List<CustomerLogin> lockedAccounts = customerLoginDAO.findAllLockedAccounts();
            LocalDateTime now = LocalDateTime.now();

            for (CustomerLogin cl : lockedAccounts) {
                if (cl.getLockedUntil() != null && cl.getLockedUntil().isBefore(now)) {
                    customerLoginDAO.unlockAccount(cl.getCustomerId());
                    System.out.println("Unlocked account: " + cl.getCustomerId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        scheduler.shutdown();
    }
}

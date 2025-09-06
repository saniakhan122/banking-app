// com.oracle.dao.BankAccountDAOJDBCImpl.java
package com.oracle.dao.impl;

import com.oracle.beans.BankAccount;
import com.oracle.dao.BankAccountDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class BankAccountJPAImpl implements BankAccountDAO {
    
    private static final Logger logger = Logger.getLogger(BankAccountJPAImpl.class.getName());
    
    @Override
    public boolean createAccount(BankAccount account) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            account.setOpenedDate(LocalDateTime.now());
            em.persist(account);
            em.getTransaction().commit();
            logger.info("Bank account created successfully: " + account.getAccountNumber());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error creating bank account: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public BankAccount findAccountByNumber(String accountNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BankAccount account = em.find(BankAccount.class, accountNumber);
            return account;
        } catch (Exception e) {
            logger.severe("Error finding account by number: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public BankAccount findAccountByAccountNumber(String accountNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.accountNumber = :accountNumber",
                BankAccount.class);
            query.setParameter("accountNumber", accountNumber);

            // getSingleResult() throws NoResultException if not found
            return query.getSingleResult();
        } catch (NoResultException e) {
            // No account matches
            return null;
        } catch (Exception e) {
            logger.severe("Error finding account by account number: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    @Override
    public List<BankAccount> findAccountsByCustomerId(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.customerId = :customerId ORDER BY ba.openedDate DESC", 
                BankAccount.class);
            query.setParameter("customerId", customerId);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding accounts by customer ID: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BankAccount> findAllAccounts() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba ORDER BY ba.openedDate DESC", BankAccount.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all accounts: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BankAccount> findAccountsByType(String accountType) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.accountType = :accountType ORDER BY ba.openedDate DESC", 
                BankAccount.class);
            query.setParameter("accountType", accountType);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding accounts by type: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BankAccount> findActiveAccounts() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.isActive = 'Y' ORDER BY ba.openedDate DESC", 
                BankAccount.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding active accounts: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BankAccount> findInactiveAccounts() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.isActive = 'N' ORDER BY ba.openedDate DESC", 
                BankAccount.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding inactive accounts: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean updateAccount(BankAccount account) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
            logger.info("Account updated successfully: " + account.getAccountNumber());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error updating account: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean updateAccountBalance(String accountNumber, BigDecimal newBalance) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount account = em.find(BankAccount.class, accountNumber);
            if (account != null) {
                account.setBalance(newBalance);
                account.setLastTransactionDate(LocalDateTime.now());
                em.merge(account);
                em.getTransaction().commit();
                logger.info("Account balance updated: " + accountNumber + " to " + newBalance);
                return true;
            }
            em.getTransaction().rollback();
            logger.warning("Account not found for balance update: " + accountNumber);
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error updating account balance: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean debitAccount(String accountNumber, BigDecimal amount) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount account = em.find(BankAccount.class, accountNumber);
            if (account != null) {
                BigDecimal currentBalance = account.getBalance();
                if (currentBalance.compareTo(amount) >= 0) {
                    BigDecimal newBalance = currentBalance.subtract(amount);
                    account.setBalance(newBalance);
                    account.setLastTransactionDate(LocalDateTime.now());
                    em.merge(account);
                    em.getTransaction().commit();
                    logger.info("Account debited: " + accountNumber + " amount: " + amount);
                    return true;
                } else {
                    em.getTransaction().rollback();
                    logger.warning("Insufficient balance for debit: " + accountNumber);
                    return false;
                }
            }
            em.getTransaction().rollback();
            logger.warning("Account not found for debit: " + accountNumber);
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error debiting account: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean creditAccount(String accountNumber, BigDecimal amount) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount account = em.find(BankAccount.class, accountNumber);
            if (account != null) {
                BigDecimal currentBalance = account.getBalance();
                BigDecimal newBalance = currentBalance.add(amount);
                account.setBalance(newBalance);
                account.setLastTransactionDate(LocalDateTime.now());
                em.merge(account);
                em.getTransaction().commit();
                logger.info("Account credited: " + accountNumber + " amount: " + amount);
                return true;
            }
            em.getTransaction().rollback();
            logger.warning("Account not found for credit: " + accountNumber);
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error crediting account: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean activateAccount(String accountNumber) {
        return updateAccountStatus(accountNumber, "Y");
    }
    
    @Override
    public boolean deactivateAccount(String accountNumber) {
        return updateAccountStatus(accountNumber, "N");
    }
    
    private boolean updateAccountStatus(String accountNumber, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount account = em.find(BankAccount.class, accountNumber);
            if (account != null) {
                account.setIsActive(status);
                em.merge(account);
                em.getTransaction().commit();
                logger.info("Account status updated: " + accountNumber + " to " + status);
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error updating account status: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean deleteAccount(String accountNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount account = em.find(BankAccount.class, accountNumber);
            if (account != null) {
                em.remove(account);
                em.getTransaction().commit();
                logger.info("Account deleted: " + accountNumber);
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error deleting account: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean accountExists(String accountNumber) {
        return findAccountByNumber(accountNumber) != null;
    }
    
    @Override
    public BigDecimal getAccountBalance(String accountNumber) {
        BankAccount account = findAccountByNumber(accountNumber);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }
    
    @Override
    public boolean hasInsufficientBalance(String accountNumber, BigDecimal amount) {
        BigDecimal balance = getAccountBalance(accountNumber);
        return balance.compareTo(amount) < 0;
    }
    
    @Override
    public boolean isAccountActive(String accountNumber) {
        BankAccount account = findAccountByNumber(accountNumber);
        return account != null && "Y".equals(account.getIsActive());
    }
    
    @Override
    public long getTotalAccountsCount() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(ba) FROM BankAccount ba", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.severe("Error getting total accounts count: " + e.getMessage());
            return 0;
        } finally {
            em.close();
        }
    }
    
    @Override
    public BigDecimal getTotalBankBalance() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BigDecimal> query = em.createQuery(
                "SELECT COALESCE(SUM(ba.balance), 0) FROM BankAccount ba WHERE ba.isActive = 'Y'", 
                BigDecimal.class);
            BigDecimal result = query.getSingleResult();
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.severe("Error getting total bank balance: " + e.getMessage());
            return BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BankAccount> findAccountsWithBalanceGreaterThan(BigDecimal amount) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.balance > :amount ORDER BY ba.balance DESC", 
                BankAccount.class);
            query.setParameter("amount", amount);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding accounts with balance greater than: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<BankAccount> findAccountsWithBalanceLessThan(BigDecimal amount) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BankAccount> query = em.createQuery(
                "SELECT ba FROM BankAccount ba WHERE ba.balance < :amount ORDER BY ba.balance ASC", 
                BankAccount.class);
            query.setParameter("amount", amount);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding accounts with balance less than: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

//    @Override
//    public List<BankAccount> findAccountsByUserId(String userId) {
//        EntityManager em = JPAUtil.getEntityManager();
//        try {
//            TypedQuery<BankAccount> query = em.createQuery(
//                "SELECT ba FROM BankAccount ba WHERE ba.userId = :userId ORDER BY ba.openedDate DESC",
//                BankAccount.class);
//            query.setParameter("userId", userId);
//            return query.getResultList();
//        } catch (Exception e) {
//            logger.severe("Error finding accounts by user ID: " + e.getMessage());
//            return null;
//        } finally {
//            em.close();
//        }
//    }

}
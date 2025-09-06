package com.oracle.dao.impl;

import com.oracle.beans.Transaction;
import com.oracle.dao.TransactionDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionJPAImpl implements TransactionDAO {

    private EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }
    public static class TransactionWithType {
        private Transaction transaction;
        private String transactionType; // "DEBIT" or "CREDIT"
        private String counterpartyAccount;
        
        // constructors, getters, setters...
        // No-args constructor
        public TransactionWithType() {
        }

        // All-args constructor
        public TransactionWithType(Transaction transaction, String transactionType, String counterpartyAccount) {
            this.transaction = transaction;
            this.transactionType = transactionType;
            this.counterpartyAccount = counterpartyAccount;
        }

        // Getters and Setters
        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getCounterpartyAccount() {
            return counterpartyAccount;
        }

        public void setCounterpartyAccount(String counterpartyAccount) {
            this.counterpartyAccount = counterpartyAccount;
        }

        @Override
        public String toString() {
            return "TransactionWithType{" +
                    "transaction=" + transaction +
                    ", transactionType='" + transactionType + '\'' +
                    ", counterpartyAccount='" + counterpartyAccount + '\'' +
                    '}';
        }
    }
    // ------------------- CREATE -------------------
    @Override
    public Transaction save(Transaction transaction) {
        generateUniqueIds(transaction);
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
            return transaction;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error saving transaction", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean createTransaction(Transaction transaction) {
        generateUniqueIds(transaction);
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error creating transaction", e);
        } finally {
            em.close();
        }
    }

    // Ensures no duplicate PK or reference
    private void generateUniqueIds(Transaction transaction) {
        if (transaction.getTransactionId() == null || transactionExists(transaction.getTransactionId())) {
            transaction.setTransactionId("TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        }
        if (transaction.getTransactionRefNo() == null || transactionRefExists(transaction.getTransactionRefNo())) {
            transaction.setTransactionRefNo("REF" + System.currentTimeMillis());
        }
    }

    // ------------------- READ -------------------
    @Override
    public Transaction findById(String transactionId) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Transaction.class, transactionId);
        } finally {
            em.close();
        }
    }

    @Override
    public Transaction findByTransactionRefNo(String transactionRefNo) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.transactionRefNo = :transactionRefNo",
                    Transaction.class);
            query.setParameter("transactionRefNo", transactionRefNo);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.fromAccount.accountNumber = :accountNumber " +
                            "OR t.toAccount.accountNumber = :accountNumber ORDER BY t.transactionDate DESC",
                    Transaction.class);
            query.setParameter("accountNumber", accountNumber);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

//    @Override
//    public List<Transaction> findTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate) {
//        EntityManager em = getEntityManager();
//        try {
//            TypedQuery<Transaction> query = em.createQuery(
//                "SELECT t FROM Transaction t WHERE " +
//                "((t.fromAccount.accountNumber = :accountNumber) OR (t.toAccount.accountNumber = :accountNumber)) " +
//                "AND t.valueDate BETWEEN :fromDate AND :toDate ORDER BY t.transactionDate DESC",
//                Transaction.class);
//            query.setParameter("accountNumber", accountNumber);
//            query.setParameter("fromDate", fromDate);
//            query.setParameter("toDate", toDate);
//            return query.getResultList();
//        } finally {
//            em.close();
//        }
//    }
    
    @Override
    public List<Transaction> findDebitTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
            		"SELECT t FROM Transaction t WHERE " +
            	            "t.fromAccountNumber = :accountNumber " +
            	            "AND t.TransactionType = 'DEBIT' " +
            	            "AND t.valueDate BETWEEN :fromDate AND :toDate ORDER BY t.transactionDate DESC",
                Transaction.class);
            query.setParameter("accountNumber", accountNumber);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Transaction> findCreditTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Transaction> query = em.createQuery(
                "SELECT t FROM Transaction t WHERE " +
                "t.toAccountNumber = :accountNumber " +
                "AND t.TransactionType = 'CREDIT' " +
                "AND t.valueDate BETWEEN :fromDate AND :toDate ORDER BY t.transactionDate DESC",
                Transaction.class);
            query.setParameter("accountNumber", accountNumber);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Transaction> findTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        List<Transaction> debits = findDebitTransactionsByDateRange(accountNumber, fromDate, toDate);
        List<Transaction> credits = findCreditTransactionsByDateRange(accountNumber, fromDate, toDate);
        
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(debits);
        allTransactions.addAll(credits);
        
        // Sort by transaction date descending
        allTransactions.sort((t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        
        return allTransactions;
    }

    @Override
    public List<Transaction> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t ORDER BY t.transactionDate DESC", Transaction.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // Aliases
    @Override public Transaction findTransactionById(String transactionId) { return findById(transactionId); }
    @Override public Transaction findTransactionByRefNo(String transactionRefNo) { return findByTransactionRefNo(transactionRefNo); }
    @Override public List<Transaction> findAllTransactions() { return findAll(); }
    @Override public List<Transaction> findTransactionsByFromAccount(String accountNumber) {
        return queryList("SELECT t FROM Transaction t WHERE t.fromAccount.accountNumber = :acc ORDER BY t.transactionDate DESC", "acc", accountNumber);
    }
    @Override public List<Transaction> findTransactionsByToAccount(String accountNumber) {
        return queryList("SELECT t FROM Transaction t WHERE t.toAccount.accountNumber = :acc ORDER BY t.transactionDate DESC", "acc", accountNumber);
    }
    @Override public List<Transaction> findTransactionsByAccount(String accountNumber) { return findByAccountNumber(accountNumber); }
    @Override public List<Transaction> findTransactionsByType(String transactionType) {
        return queryList("SELECT t FROM Transaction t WHERE t.transactionType = :type ORDER BY t.transactionDate DESC", "type", transactionType);
    }
    @Override public List<Transaction> findTransactionsByStatus(String status) {
        return queryList("SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.transactionDate DESC", "status", status);
    }
    @Override public List<Transaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end ORDER BY t.transactionDate DESC", Transaction.class)
                    .setParameter("start", startDate).setParameter("end", endDate).getResultList();
        } finally {
            em.close();
        }
    }
    @Override public List<Transaction> findTransactionsByAmountRange(BigDecimal min, BigDecimal max) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t WHERE t.amount BETWEEN :min AND :max ORDER BY t.transactionDate DESC", Transaction.class)
                    .setParameter("min", min).setParameter("max", max).getResultList();
        } finally {
            em.close();
        }
    }
    @Override public List<Transaction> findTransactionsByTransferMethod(String transferMethod) {
        return queryList("SELECT t FROM Transaction t WHERE t.transferMethod = :tm ORDER BY t.transactionDate DESC", "tm", transferMethod);
    }
    @Override public List<Transaction> findTransactionsByCustomer(String customerId) {
        return queryList("SELECT t FROM Transaction t WHERE t.fromAccount.customer.customerId = :cid OR t.toAccount.customer.customerId = :cid ORDER BY t.transactionDate DESC", "cid", customerId);
    }

    private List<Transaction> queryList(String jpql, String paramName, Object value) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(jpql, Transaction.class).setParameter(paramName, value).getResultList();
        } finally {
            em.close();
        }
    }

    // ------------------- UPDATE -------------------
    @Override
    public Transaction update(Transaction transaction) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction updated = em.merge(transaction);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error updating transaction", e);
        } finally {
            em.close();
        }
    }

    @Override public boolean updateTransaction(Transaction transaction) {
        return update(transaction) != null;
    }

    @Override public boolean updateTransactionStatus(String transactionId, String status) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, transactionId);
            if (t != null) {
                t.setStatus(status);
                em.merge(t);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    @Override public boolean updateTransactionRemarks(String transactionId, String remarks) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, transactionId);
            if (t != null) {
                t.setRemarks(remarks);
                em.merge(t);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    // ------------------- DELETE -------------------
    @Override
    public void delete(String transactionId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction transaction = em.find(Transaction.class, transactionId);
            if (transaction != null) em.remove(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Error deleting transaction", e);
        } finally {
            em.close();
        }
    }

    @Override public boolean deleteTransaction(String transactionId) {
        delete(transactionId);
        return true;
    }

    // ------------------- EXISTS / AGGREGATES -------------------
    @Override
    public boolean transactionExists(String transactionId) {
        return findById(transactionId) != null;
    }

    @Override
    public boolean transactionRefExists(String transactionRefNo) {
        return findByTransactionRefNo(transactionRefNo) != null;
    }

    @Override
    public long getTotalTransactionCount() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(t) FROM Transaction t", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal getTotalTransactionAmount() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t", BigDecimal.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal getTotalTransactionAmountByAccount(String accountNumber) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount.accountNumber = :acc OR t.toAccount.accountNumber = :acc",
                    BigDecimal.class).setParameter("acc", accountNumber).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal getTotalDebitAmountByAccount(String accountNumber) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.fromAccount.accountNumber = :acc",
                    BigDecimal.class).setParameter("acc", accountNumber).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal getTotalCreditAmountByAccount(String accountNumber) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.toAccount.accountNumber = :acc",
                    BigDecimal.class).setParameter("acc", accountNumber).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List getAccountStatement(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        return findTransactionsByDateRange(accountNumber, fromDate, toDate);
    }

    @Override
    public List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Transaction t WHERE t.fromAccount.accountNumber = :acc OR t.toAccount.accountNumber = :acc ORDER BY t.transactionDate DESC",
                    Transaction.class).setParameter("acc", accountNumber).setMaxResults(limit).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Transaction> findHighValueTransactions(BigDecimal threshold) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t WHERE t.amount >= :threshold ORDER BY t.transactionDate DESC",
                    Transaction.class).setParameter("threshold", threshold).getResultList();
        } finally {
            em.close();
        }
    }
}

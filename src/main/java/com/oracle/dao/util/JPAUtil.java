// com.oracle.dao.util.JPAUtil.java
package com.oracle.dao.util;

import jakarta.persistence.*;
import java.util.logging.Logger;

public class JPAUtil {
    
    private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());
    private static EntityManagerFactory entityManagerFactory;
    
    static {
        try {
            // Initialize EntityManagerFactory using persistence.xml
            entityManagerFactory = Persistence.createEntityManagerFactory("banking-web");
            logger.info("JPA EntityManagerFactory initialized successfully");
        } catch (Exception e) {
            logger.severe("Error initializing EntityManagerFactory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }
    
    /**
     * Get EntityManager instance
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory is not initialized");
        }
        return entityManagerFactory.createEntityManager();
    }
    
    /**
     * Get EntityManagerFactory instance
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
    
    /**
     * Close EntityManagerFactory
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            logger.info("EntityManagerFactory closed successfully");
        }
    }
    
    /**
     * Check if EntityManagerFactory is open
     * @return true if open, false otherwise
     */
    public static boolean isEntityManagerFactoryOpen() {
        return entityManagerFactory != null && entityManagerFactory.isOpen();
    }
}
// com.oracle.dao.CustomerDAOJDBCImpl.java
package com.oracle.dao.impl;

import com.oracle.beans.Customer;
import com.oracle.dao.CustomerDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class CustomerJPAImpl implements CustomerDAO {
    
    private static final Logger logger = Logger.getLogger(CustomerJPAImpl.class.getName());
    
    @Override
    public boolean createCustomer(Customer customer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            customer.setCreatedAt(LocalDateTime.now());
            em.persist(customer);
            em.getTransaction().commit();
            logger.info("Customer created successfully with ID: " + customer.getCustomerId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error creating customer: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public Customer findCustomerById(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Customer customer = em.find(Customer.class, customerId);
            return customer;
        } catch (Exception e) {
            logger.severe("Error finding customer by ID: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public Customer findCustomerByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email", Customer.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No customer found with email: " + email);
            return null;
        } catch (Exception e) {
            logger.severe("Error finding customer by email: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public Customer findCustomerByMobile(String mobileNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.mobileNumber = :mobile", Customer.class);
            query.setParameter("mobile", mobileNumber);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No customer found with mobile: " + mobileNumber);
            return null;
        } catch (Exception e) {
            logger.severe("Error finding customer by mobile: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public Customer findCustomerByAadhar(String aadharNumber) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.aadharNumber = :aadhar", Customer.class);
            query.setParameter("aadhar", aadharNumber);
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info("No customer found with Aadhar: " + aadharNumber);
            return null;
        } catch (Exception e) {
            logger.severe("Error finding customer by Aadhar: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Customer> findAllCustomers() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c ORDER BY c.createdAt DESC", Customer.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all customers: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<Customer> findCustomersByStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.status = :status ORDER BY c.createdAt DESC", Customer.class);
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding customers by status: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean updateCustomer(Customer customer) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(customer);
            em.getTransaction().commit();
            logger.info("Customer updated successfully: " + customer.getCustomerId());
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error updating customer: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean updateCustomerStatus(String customerId, String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            if (customer != null) {
                customer.setStatus(status);
                em.merge(customer);
                em.getTransaction().commit();
                logger.info("Customer status updated: " + customerId + " to " + status);
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.severe("Error updating customer: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateCustomerContact(String customerId, String email, String mobile) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            if (customer != null) {
                customer.setEmail(email);
                customer.setMobileNumber(mobile);
                em.merge(customer);
                em.getTransaction().commit();
                logger.info("Updated contact info for customer: " + customerId);
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error updating contact info: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }


    @Override
    public boolean deleteCustomer(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            if (customer != null) {
                em.remove(customer);
                em.getTransaction().commit();
                logger.info("Deleted customer: " + customerId);
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            logger.severe("Error deleting customer: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean softDeleteCustomer(String customerId) {
        return updateCustomerStatus(customerId, "INACTIVE");
    }

	@Override
	public boolean customerExists(String customerId) {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        Customer customer = em.find(Customer.class, customerId);
	        return customer != null;
	    } finally {
	        em.close();
	    }
	}

	@Override
	public boolean emailExists(String email) {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        Long count = em.createQuery(
	            "SELECT COUNT(c) FROM Customer c WHERE c.email = :email", Long.class)
	            .setParameter("email", email)
	            .getSingleResult();
	        return count > 0;
	    } finally {
	        em.close();
	    }
	}

	@Override
	public boolean mobileExists(String mobileNumber) {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        Long count = em.createQuery(
	            "SELECT COUNT(c) FROM Customer c WHERE c.mobileNumber = :mobile", Long.class)
	            .setParameter("mobile", mobileNumber)
	            .getSingleResult();
	        return count > 0;
	    } finally {
	        em.close();
	    }
	}


	@Override
	public boolean aadharExists(String aadharNumber) {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        Long count = em.createQuery(
	            "SELECT COUNT(c) FROM Customer c WHERE c.aadharNumber = :aadhar", Long.class)
	            .setParameter("aadhar", aadharNumber)
	            .getSingleResult();
	        return count > 0;
	    } finally {
	        em.close();
	    }
	}

	@Override
	public long getTotalCustomerCount() {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        return em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult();
	    } finally {
	        em.close();
	    }
	}

	@Override
	public List<Customer> findCreatedBetween(LocalDate startDate, LocalDate endDate) {
	    EntityManager em = JPAUtil.getEntityManager();
	    try {
	        return em.createQuery(
	            "SELECT c FROM Customer c WHERE c.createdAt BETWEEN :start AND :end", Customer.class)
	            .setParameter("start", startDate.atStartOfDay())
	            .setParameter("end", endDate.atTime(23, 59, 59))
	            .getResultList();
	    } finally {
	        em.close();
	    }
	}

}
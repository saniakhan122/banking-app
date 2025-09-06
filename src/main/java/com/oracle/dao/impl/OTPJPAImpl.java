package com.oracle.dao.impl;

import com.oracle.beans.OtpRequest;
import com.oracle.dao.OTPDAO;
import com.oracle.dao.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OTPJPAImpl implements OTPDAO {

    private static final Logger logger = Logger.getLogger(OTPJPAImpl.class.getName());
    EntityManager entityManager = JPAUtil.getEntityManager();

    @Override
    public boolean saveOTP(OtpRequest otp) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(otp);
            entityManager.getTransaction().commit();
            logger.info("OTP saved successfully: " + otp.getOtpId());
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving OTP", e);
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return false;
        }
    }
    
    
    public OtpRequest getLatestOTPByCustomerId(String customerId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<OtpRequest> query = em.createQuery(
                "SELECT o FROM OtpRequest o " +
                "WHERE o.customerId = :customerId " +
                "ORDER BY o.generatedAt DESC", 
                OtpRequest.class
            );
            query.setParameter("customerId", customerId);
            query.setMaxResults(1); // only latest OTP
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


	@Override
	public OtpRequest getActiveOTPByCustomerIdAndPurpose(String customerId) {
		// TODO Auto-generated method stub
		return null;
	}


}

package com.att.developer.config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.stereotype.Component;

@Component
public class AtomikosJtaPlatform extends AbstractJtaPlatform {

	private static Logger logger = Logger.getLogger(AtomikosJtaPlatform.class);
	private static final long serialVersionUID = 1L;
	
	private UserTransaction userTransaction = null;
	private TransactionManager txManager = null;

	public UserTransaction getJNDIUserTx() {
		if (userTransaction == null) {
			try {
				Context ctx = new InitialContext();
				userTransaction = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
			} catch (NamingException e) {
				logger.error(e);
				new RuntimeException(e);
			}
		}
		return userTransaction;
	}

	public TransactionManager getJNDITransactionManager() {
		if(txManager == null) {
			try {
				Context ctx = new InitialContext();
				txManager = (TransactionManager) ctx.lookup("java:comp/env/TransactionManager");
			} catch (NamingException e) {
				logger.error(e);
				new RuntimeException(e);
			}
		}
		return txManager;
	}

	@Override
	protected TransactionManager locateTransactionManager() {
		return getJNDITransactionManager();
	}

	@Override
	protected UserTransaction locateUserTransaction() {
		return getJNDIUserTx();
	}
}
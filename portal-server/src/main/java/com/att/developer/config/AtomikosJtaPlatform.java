package com.att.developer.config;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;


public class AtomikosJtaPlatform extends AbstractJtaPlatform {

	private static final long serialVersionUID = 1L;

	private UserTransactionManager userTransactionManager;
	private UserTransactionImp userTransactionImp;

	public UserTransactionManager getAtomikosTransactionManager() {
		if (userTransactionManager == null) {
			userTransactionManager = new UserTransactionManager();
			userTransactionManager.setForceShutdown(false);
			userTransactionManager.setStartupTransactionService(false);
		}

		return userTransactionManager;
	}

	public UserTransactionImp getAtomikosUserTransaction() {
		if (userTransactionImp == null) {
			userTransactionImp = new UserTransactionImp();
			try {
				userTransactionImp.setTransactionTimeout(300);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}
		return userTransactionImp;
	}

	@Override
	protected TransactionManager locateTransactionManager() {
		return getAtomikosTransactionManager();
	}

	@Override
	protected UserTransaction locateUserTransaction() {
		return getAtomikosUserTransaction();
	}

	/**/
}
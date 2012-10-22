package org.ourgrid.common.statistics.util.hibernate;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateUtil {
	private static Logger logger = RootLogger.getLogger(HibernateUtil.class);
	private static Configuration configuration;
	private static SessionFactory sessionFactory;

	//private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();
	//private static final ThreadLocal<Transaction> threadTransaction = new ThreadLocal<Transaction>();

	public static void setUp(String confXMLPath) {
		try {
			configuration = new AnnotationConfiguration();
			Configuration config = configuration.configure(confXMLPath);
			sessionFactory = config.buildSessionFactory();
		} catch (Throwable ex) {
			logger.error(ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	/**
	 * Returns the SessionFactory used for this static class.
	 * 
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Returns the original Hibernate configuration.
	 * 
	 * @return Configuration
	 */
	public static Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Rebuild the SessionFactory with the static Configuration.
	 * 
	 */
	public static void rebuildSessionFactory() throws Exception {
		synchronized (sessionFactory) {
			sessionFactory = getConfiguration().buildSessionFactory();
		}
	}

	/**
	 * Rebuild the SessionFactory with the given Hibernate Configuration.
	 * 
	 * @param cfg
	 */
	public static void rebuildSessionFactory(Configuration cfg) throws Exception {
		synchronized (sessionFactory) {
				sessionFactory = cfg.buildSessionFactory();
				configuration = cfg;
		}
	}

	public static void recreateSchema() {
		SchemaExport se = new SchemaExport(getConfiguration());
		se.create(true, true);
	}
	
	/**
	 * Retrieves the current Session local to the thread. <p/> If no Session is
	 * open, opens a new Session for the running thread.
	 * 
	 * @return Session
	 */
	public static Session getSession() {
		Session session = (Session) sessionFactory.getCurrentSession();
		try {
			if (session == null || !session.isOpen()) {
				logger.debug("Opening new Session for this thread.");
				session = getSessionFactory().openSession();
				//threadSession.set(session);
			}
		} catch (HibernateException ex) {
			logger.error("getSession", ex);
		}
		return session;
	}

	/**
	 * Closes the Session local to the thread.
	 */
	public static void closeSession() {
		try {
			Session session = (Session) sessionFactory.getCurrentSession();
			//threadSession.set(null);
			if (session != null && session.isOpen()) {
				logger.trace("Closing Session of this thread.");
				session.close();
			}
		} catch (HibernateException ex) {
			logger.error("closeSession", ex);
		}
	}

	/**
	 * Start a new database transaction.
	 */
	public static void beginTransaction() {
		Transaction tx = (Transaction) sessionFactory.getCurrentSession().beginTransaction();
		try {
			if (tx == null) {
				logger.trace("Starting new database transaction in this thread.");
				tx = getSession().beginTransaction();
				//threadTransaction.set(tx);
			}
		} catch (HibernateException ex) {
			logger.error("beginTransaction", ex);
		}
	}

	/**
	 * Commit the database transaction.
	 */
	public static void commitTransaction() {
		Transaction tx = (Transaction) sessionFactory.getCurrentSession().getTransaction();
		try {
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				logger.trace("Committing database transaction of this thread.");
				tx.commit();
			}
			//threadTransaction.set(null);
		} catch (HibernateException ex) {
			rollbackTransaction();
			logger.error("commitTransaction", ex);
		} finally {
			closeSession();
		}
	}

	/**
	 * Rollback the database transaction.
	 */
	public static void rollbackTransaction() {
		Transaction tx = (Transaction) sessionFactory.getCurrentSession().getTransaction();
		try {
			//threadTransaction.set(null);
			if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
				logger.trace("Tyring to rollback database transaction of this thread.");
				tx.rollback();
			}
		} catch (HibernateException ex) {
			logger.error("rollbackTransaction", ex);
		}
	}
	
	/**
	 * Rollback the database transaction and close session.
	 */
	public static void rollbackAndCloseSession() {
		rollbackTransaction();
		closeSession();
	}
}

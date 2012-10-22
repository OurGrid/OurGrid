package org.ourgrid.common.statistics.util.hibernate;

import org.hibernate.Session;
import org.ourgrid.common.statistics.beans.peer.Attribute;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommuneLoggerFactory.getInstance();
		Session session = HibernateUtil.getSession();
		HibernateUtil.beginTransaction();
		try {
			Attribute att = new Attribute();
			session.save(att);
			HibernateUtil.commitTransaction();
		} catch (Exception e) {
			HibernateUtil.rollbackAndCloseSession();
			e.printStackTrace();
		}
	}

}


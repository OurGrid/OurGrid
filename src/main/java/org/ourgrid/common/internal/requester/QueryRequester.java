package org.ourgrid.common.internal.requester;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.request.QueryRequestTO;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.common.statistics.util.hibernate.HibernateUtil;


public class QueryRequester implements RequesterIF<QueryRequestTO> {


	public List<IResponseTO> execute(QueryRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		OperationSucceedResponseTO to = new OperationSucceedResponseTO();
		to.setClientAddress(request.getClientAddress());
		
		try {
			String result = "";
			
			HibernateUtil.getSession();
			HibernateUtil.beginTransaction();
			
			SQLQuery sqlQuery = HibernateUtil.getSession().createSQLQuery(request.getQuery());
			
			List<?> list = sqlQuery.list();
			
			if (list == null || list.isEmpty()) {
				result = "No data available";
				
			} else {
				
				for (Object line : list) {
					
					if (line == null) {
						result = result + "null; ";

					} else if (line instanceof Object[]) {
						Object[] objects = (Object[]) line;
						for (Object object : objects) {
							result = result + (object == null ? "null " : object.toString()) + "; ";
						}
						
					} else {
						result = result + line.toString() + "; ";
					}
					
					result += "\n";
				}
			}

			HibernateUtil.closeSession();
			
			to.setResult(result);
		} catch (Exception e) {
			to.setErrorCause(e);
		}
		
		responses.add(to);
		
		return responses;
	}
}

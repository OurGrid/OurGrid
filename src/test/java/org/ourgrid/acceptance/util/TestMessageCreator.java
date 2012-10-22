/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.acceptance.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.InvalidStubStateException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.message.MessageUtil;

public class TestMessageCreator implements InvocationHandler {

    private final Module module;
    private final DeploymentID targetID;

    public TestMessageCreator(Module module, DeploymentID targetID) {
		this.module = module;

		if (targetID == null) {
			throw new InvalidStubStateException("The target deployment id must be set");
		}
		
		this.targetID = targetID;
    }
    
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //DeploymentID sourceID = container.getExecutionContext().getRunningObject().getDeploymentID();

        String serviceName = targetID.getServiceName();
		ObjectDeployment objectDeployment = this.getObjectDeployment(serviceName);
		
		if (objectDeployment == null) {
			return null;
		}
		
		//getContainer().setExecutionContext(objectDeployment, sourceID, container.getMyCertPath());

		try {

			Object target = objectDeployment.getObject();
			Method concreteMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
			
			if (!isNotificationMethod(method)) {
				registerParameterInterests(objectDeployment, concreteMethod, method.getParameterTypes(), args);
			}
			
			method.invoke(target, args);
			
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
		
		}
        
        return null;
    }

	protected ObjectDeployment getObjectDeployment(String serviceName) {
		return getModule().getObjectRepository().get(serviceName);
	}
	

	private boolean isNotificationMethod(Method method) {
		
		return (method.getAnnotation(FailureNotification.class) != null) ||
			(method.getAnnotation(RecoveryNotification.class) != null);
	}

//	private boolean isFailureNotificationMethod(Method method) {
//		
//		return (method.getAnnotation(FailureNotification.class) != null);
//	}

	public void registerParameterInterests(ObjectDeployment objectDeployment, Method method, 
			Class<?>[] parameterTypes, Object[] parameterValues) {

		for (int i = 0; i < parameterTypes.length; i++) {
			Object parameterValue = parameterValues[i];
			Class<?> parameterType = parameterTypes[i];
			
			if (parameterValue == null) {
				continue;
			}
			
			if (MessageUtil.isRemoteType(parameterType)) {
				registerInterest(objectDeployment, method, i, parameterValue, parameterType);
				
			} else if (Collection.class.isAssignableFrom(parameterValue.getClass())) {
				
				Collection<?> stubParameterCollection = (Collection<?>) parameterValue;
				for (Object object : stubParameterCollection) {
					if (MessageUtil.isRemoteType(parameterType)) {
						registerInterest(objectDeployment, method, i, object, parameterType);
					}
				}
			}
		}
	}

	private void registerInterest(ObjectDeployment objectDeployment,
			Method method, int i, Object parameterValue, Class<?> parameterType) {
		
		
		DeploymentID stubDeploymentID = getModule().getStubDeploymentID(parameterValue);
		
		if (stubDeploymentID == null) {
			throw new CommuneRuntimeException("Stub not published: " + parameterValue);
		}
		
		getModule().registerParameterInterest(objectDeployment, method, i, parameterType, 
				stubDeploymentID.getServiceID());
	}

	private Module getModule() {
		return this.module;
	}
}
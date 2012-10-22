package org.ourgrid.peer.business.requester;
import static org.ourgrid.common.util.CommonUtils.checkKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.LoginControl;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.controller.WorkerProviderClientFailureController;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.LoginRequestTO;
import org.ourgrid.peer.response.LoginSuccededResponseTO;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.PeerUserReference;
import org.ourgrid.reqtrace.Req;

public class LoginRequester implements RequesterIF<LoginRequestTO> {


	public List<IResponseTO> execute(LoginRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String brokerPublicKey = request.getSenderPublicKey();
		
		String login = request.getLogin();
		
		PeerUser user = UserControl.getInstance().getUser(responses, login);
		if (request.isOnDemandPeer()) {
			try {
				if (user == null){
					UserControl.getInstance().addUser(responses, request.getLogin(), request.getMyUserAtServer(), 
							request.getMyCertSubjectDN(), request.getDescription(),request.getEmail(), 
							request.getLabel(), request.getLatitude(), request.getLongitude());

					user = UserControl.getInstance().getUser(responses, login);
				}

				UserControl.getInstance().registerPublicKey(responses, user, brokerPublicKey);
				user.setPublicKey(brokerPublicKey);
			} catch (Exception e) {
				//do nothing
			}
		} else {
			if (!validateLogin(responses, request, user)) {
				return responses;
			}
		}
		
		String workerProviderClientAddress = request.getWorkerProviderClientAddress();
		
		PeerUserReference loggedUser = PeerDAOFactory.getInstance().getUsersDAO().getLoggedUser(user.getPublicKey());
		
		if (loggedUser != null && !loggedUser.getWorkerProviderClientAddress().equals(workerProviderClientAddress)) {
			getLoginStatistics().localConsumerFailure(responses, user);
		}
		
		WorkerProviderClientFailureController.getInstance().finishUserRequests(responses, 
				StringUtil.addressToContainerID(request.getWorkerProviderClientAddress()), 
				brokerPublicKey, request.getMyCertSubjectDN());
		
		
		if (loggedUser != null){
			if (loggedUser.getWorkerProviderClientAddress().equals(workerProviderClientAddress)) {
				BrokerLoginResult loginResult = new BrokerLoginResult(); //TODO LoginResult.ALREADY_LOGGED);
				
				LoginSuccededResponseTO loginTO = new LoginSuccededResponseTO();
				loginTO.setLoginResult(loginResult);
				loginTO.setWorkerProviderClientAddress(workerProviderClientAddress);
				
				responses.add(loginTO);
				
				getLoginStatistics().login(responses, user, loginResult, request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
						request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
				
				return responses;
			}
			
			PeerDAOFactory.getInstance().getUsersDAO().removeLoggedUser(brokerPublicKey);
			PeerDAOFactory.getInstance().getConsumerDAO().removeLocalConsumer(brokerPublicKey);
			
		}
		
		doLogin(responses, request, user);
		
		return responses;
	}
	
	private boolean validateLogin(List<IResponseTO> responses, LoginRequestTO request, PeerUser user) {
		String workerProviderClientAddress = request.getWorkerProviderClientAddress();
		String brokerPublicKey = request.getSenderPublicKey();
		
		if (user == null) {
			
			BrokerLoginResult loginResult = new BrokerLoginResult(BrokerLoginResult.UNKNOWN_USER);
			
			LoginSuccededResponseTO loginTO = new LoginSuccededResponseTO();
			loginTO.setLoginResult(loginResult);
			loginTO.setWorkerProviderClientAddress(workerProviderClientAddress);
			
			responses.add(loginTO);
			
			user = new PeerUser(request.getUserName(), request.getServerName(), 
					"", false);
			
			getLoginStatistics().login(responses, user, loginResult, 
					request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
					request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(workerProviderClientAddress);
			
			responses.add(releaseTO);
			
			return false;
		}
		
		if(isFirstLogin(user)){
				try {
					if (UserControl.getInstance().userExists(responses, brokerPublicKey)) {
						BrokerLoginResult loginResult = new BrokerLoginResult(BrokerLoginResult.DUPLICATED_PUBLIC_KEY);
						
						LoginSuccededResponseTO loginTO = new LoginSuccededResponseTO();
						loginTO.setLoginResult(loginResult);
						loginTO.setWorkerProviderClientAddress(workerProviderClientAddress);
						
						responses.add(loginTO);
						
						getLoginStatistics().login(responses, user, loginResult, request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
								request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
						
						ReleaseResponseTO releaseTO = new ReleaseResponseTO();
						releaseTO.setStubAddress(workerProviderClientAddress);
						
						responses.add(releaseTO);
						
						return false;
					} else {
						UserControl.getInstance().registerPublicKey(responses, user, brokerPublicKey);
						user.setPublicKey(brokerPublicKey);
					}
				} catch (IOException e) {
					responses.add(new LoggerResponseTO(e.getMessage(), LoggerResponseTO.ERROR, e));
					BrokerLoginResult loginResult = new BrokerLoginResult(BrokerLoginResult.INTERNAL_ERROR);
					
					LoginSuccededResponseTO loginTO = new LoginSuccededResponseTO();
					loginTO.setLoginResult(loginResult);
					loginTO.setWorkerProviderClientAddress(workerProviderClientAddress);
					
					responses.add(loginTO);
					
					getLoginStatistics().login(responses, user, loginResult, request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
							request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
					
					ReleaseResponseTO releaseTO = new ReleaseResponseTO();
					releaseTO.setStubAddress(workerProviderClientAddress);
					
					responses.add(releaseTO);
				}
		}
		
		if(!checkKey(user.getPublicKey(), brokerPublicKey)){
			BrokerLoginResult loginResult = new BrokerLoginResult(BrokerLoginResult.WRONG_PUBLIC_KEY);
			
			LoginSuccededResponseTO loginTO = new LoginSuccededResponseTO();
			loginTO.setLoginResult(loginResult);
			loginTO.setWorkerProviderClientAddress(workerProviderClientAddress);
			
			responses.add(loginTO);
			
			getLoginStatistics().login(responses, user, loginResult, request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
					request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(workerProviderClientAddress);
			
			responses.add(releaseTO);
			
			return false;
		}					
		
		return true;
	}
	
	@Req("REQ108")
	private void doLogin(List<IResponseTO> responses, LoginRequestTO request, PeerUser user) {
		
		PeerDAOFactory.getInstance().getUsersDAO().addLoggedUser(request.getSenderPublicKey(), new PeerUserReference(request.getWorkerProviderClientAddress()));
		
		BrokerLoginResult loginResult = new BrokerLoginResult();
		LoginSuccededResponseTO loginTO = new LoginSuccededResponseTO();
		loginTO.setLoginResult(loginResult);
		loginTO.setWorkerProviderClientAddress(request.getWorkerProviderClientAddress());
		
		responses.add(loginTO);
		
		getLoginStatistics().login(responses, user, loginResult, request.getMyUserAtServer(), request.getMyCertSubjectDN(), request.getDescription(),
				request.getEmail(), request.getLabel(), request.getLatitude(), request.getLongitude());
	}
	
	/**
	 * @return
	 */
	private LoginControl getLoginStatistics() {
		return LoginControl.getInstance();
	}

	@Req("REQ108")
	private boolean isFirstLogin(PeerUser user) {
		return user.getPublicKey().equals("");
	}
}

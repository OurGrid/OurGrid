package org.ourgrid.peer.business.requester;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.WorkerLoginResult;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.WorkerLoginRequestTO;
import org.ourgrid.peer.response.WorkerLoginSucceededResponseTO;

import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;
import br.edu.ufcg.lsd.commune.network.signature.Util;

public class WorkerLoginRequester extends AbstractRegisterWorkerRequester<WorkerLoginRequestTO> {


	public List<IResponseTO> execute(WorkerLoginRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		if (!request.isVoluntary() && !verifyWorkerCertificate(request, responses)) {
			return responses;
		}

		if (PeerDAOFactory.getInstance().getLocalWorkersDAO().isWorkerUp(
				StringUtil.addressToUserAtServer(request.getWorkerAddress()))) {
			responses.add(new LoggerResponseTO("The worker [" + request.getWorkerAddress() + "] was identified " +
					"but it is already logged. Maybe it is recovering? Login with success.", LoggerResponseTO.WARN));
		}
		
		WorkerSpecification workerSpecification = request.getWorkerSpecification();
		registerNewWorker(responses, workerSpecification, request.getWorkerPublicKey(),  
				request.getMyUserAtServer());
		
		WorkerLoginResult workerLoginResult = new WorkerLoginResult(WorkerLoginResult.OK);
		
		WorkerLoginSucceededResponseTO workerLoginSucceededTO = new WorkerLoginSucceededResponseTO();
		workerLoginSucceededTO.setLoginResult(workerLoginResult);
		workerLoginSucceededTO.setWorkerManagementAddress(request.getWorkerAddress());
		responses.add(workerLoginSucceededTO);

		responses.add(new LoggerResponseTO("The worker [" + request.getWorkerAddress() + "] was identified. " +
				"Login with success.", LoggerResponseTO.INFO));

		return responses;
	}

	@SuppressWarnings("restriction")
	private boolean verifyWorkerCertificate(WorkerLoginRequestTO request, List<IResponseTO> responses) {
		
		WorkerLoginResult workerLoginResult = new WorkerLoginResult(WorkerLoginResult.OK);
		
		if (!CertificationUtils.isCertificateValid(request.getWorkerCertPath())) {
			workerLoginResult.setResultMessage(WorkerLoginResult.INVALID_CERT_PATH);

			WorkerLoginSucceededResponseTO workerLoginSucceededTO = new WorkerLoginSucceededResponseTO();
			workerLoginSucceededTO.setLoginResult(workerLoginResult);
			workerLoginSucceededTO.setWorkerManagementAddress(request.getWorkerAddress());

			responses.add(workerLoginSucceededTO);

			responses.add(new LoggerResponseTO("The worker certificate path " +
					"is not valid.", LoggerResponseTO.WARN));

			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(request.getWorkerAddress());

			responses.add(releaseTO);

			return false;
		}

		if (!verify(request.getMyPublicKey(), 
				request.getWorkerCertPath().getCertificates().get(0))) {
			
			workerLoginResult.setResultMessage(WorkerLoginResult.UNISSUED_CERT_PATH);

			WorkerLoginSucceededResponseTO workerLoginResultTO = new WorkerLoginSucceededResponseTO();
			workerLoginResultTO.setLoginResult(workerLoginResult);

			responses.add(workerLoginResultTO);

			responses.add(new LoggerResponseTO("The worker certificate path " +
					"is not issued by CA.", LoggerResponseTO.WARN));

			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(request.getWorkerAddress());

			responses.add(releaseTO);

			return false;
		}
		
		return true;
	}
	
	private static boolean verify(String peerPublicKey, 
			X509Certificate workerCertificate) {
		
		try {
			workerCertificate.verify(
					Util.decodePublicKey(peerPublicKey));
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
}
package org.ourgrid.common.specification.worker;

import java.util.Comparator;
import java.util.Map;



/**
 * Compares two WorkSpecs based on annotations of a jobs.
 * 
 * @author lauro
 *
 */

public class WorkerSpecificationAnnotationsComparator implements Comparator<WorkerSpecification> {

	private Map<String, String> jobAnnotations;

	public WorkerSpecificationAnnotationsComparator(Map<String, String> jobAnnotations){
		this.jobAnnotations = jobAnnotations;
	}
	
	public void setTags(Map<String, String> jobAnnotations){
		this.jobAnnotations = jobAnnotations;
	}
	
	public int compare( WorkerSpecification o1, WorkerSpecification o2 ) {		
		int matchedTags1 = evaluateAnnotations( jobAnnotations, o1 );
		int matchedTags2 = evaluateAnnotations( jobAnnotations, o2 );
		
		return matchedTags2 - matchedTags1;		
	}

	/**
	 * This method returns an int expressing how well is the matching of workSpec's annotations and job's Annotations
	 * Each property in the job's annotations that does not exist in the workSpec's annotations, counts 0. 
	 * Each perfect match, counts 1 more.
	 * Each property in the job's annotations that does not match in the workSpec's annotations, counts -1.
	 * @param jobAnnotations Job's annotations - a collection of pairs attribute=value.
	 * @param workerSpec The WorkerSpec
	 * @return an int expressing the matching.
	 */
	private int evaluateAnnotations( Map<String, String> jobAnnotations, WorkerSpecification workerSpec ) {
		if((jobAnnotations == null || jobAnnotations.size() == 0) 
				|| (workerSpec.getAnnotations() == null || workerSpec.getAnnotations().size() == 0)){
			return 0;
		}
		
		int matched = 0;
		for ( String jobProperty : jobAnnotations.keySet()) {
			String annotationWorkerValue = jobAnnotations.get( jobProperty );
			if( annotationWorkerValue == null )
				continue;			
			if( annotationWorkerValue.equals( jobAnnotations.get( jobProperty ) ) ){
				matched++;
			} else {
				matched--;
			}
		}
		
		return matched;
	}	
	
}

package edu.fudan.se.undergraduate.aggregator;

import java.util.List;

import edu.fudan.se.undergraduate.dbObject.WorkerResponse;


public interface Aggregator {
	public WorkerResponse aggrerator(List<WorkerResponse> reponses);

	public String aggrerator(String taskXML, String deadline);
}
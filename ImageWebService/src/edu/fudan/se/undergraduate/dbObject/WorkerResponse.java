package edu.fudan.se.undergraduate.dbObject;

public class WorkerResponse {
	public long id;
	public String worker;
	public String template;
	public String responseString;
	public boolean isAccepted;
	public String answerTime;
    public long taskid;
	public WorkerResponse(long id, String worker, String template,
			String responseString, boolean isAccepted, String answerTime,long taskid) {
		this.id = id;
		this.worker = worker;
		this.template = template;
		this.responseString = responseString;
		this.isAccepted = isAccepted;
		this.answerTime = answerTime;
		this.taskid = taskid;
	}
}

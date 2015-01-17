package edu.fudan.se.undergraduate.dbObject;

public class MicroTask {
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	long id;
	private String template;
	private String consumer;
	private int cost;
	private String state;
	private String deadline;

	public MicroTask(String template, String consumer, String state) {
		this.template = template;
		this.consumer = consumer;
		this.state = state;
		this.cost = 0;
		this.deadline = "not set";
	}

	public MicroTask(long id,String template, String consumer, String state, int cost,
			String deadline) {
		this.id = id;
		this.template = template;
		this.consumer = consumer;
		this.state = state;
		this.cost = cost;
		this.deadline = deadline;
	}
}

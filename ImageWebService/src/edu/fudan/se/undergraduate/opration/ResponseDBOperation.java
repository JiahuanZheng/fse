package edu.fudan.se.undergraduate.opration;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.fudan.se.undergraduate.dbObject.WorkerResponse;


public class ResponseDBOperation {

	/**
	 * 
	 * @param worker
	 *            guid of the worker.
	 * @param template
	 *            template XML file path of the task.
	 * @param response
	 *            response string from the worker.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	@SuppressWarnings("resource")
	public static boolean insertResponse(String worker, String template,
			String response) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn = ConnectDB.connect();
		String sql = "select * from workerresponse where worker=? and taskTemplate=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, worker);
		ps.setString(2, template);
		ResultSet rs = ps.executeQuery();
		if (rs.last()) {
			sql = "update workerresponse set responseString=? where worker=? and taskTemplate=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, response);
			ps.setString(2, worker);
			ps.setString(3, template);
			if (ps.executeUpdate() == 0) {
				System.out
						.println("insertResponse(): error occured when updating.");
				return false;
			}
		} else {
			sql = "insert into workerresponse (worker, taskTemplate, responseString, isAccepted, answerTime) values (?, ?, ?, ?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, worker);
			ps.setString(2, template);
			ps.setString(3, response);
			ps.setBoolean(4, false);
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			ps.setString(5, time);
			if (ps.executeUpdate() == 0) {
				System.out
						.println("insertResponse(): error occured when inserting.");
				return false;
			}
		}
		rs.close();
		ps.close();
		conn.close();
		return true;
	}
	
	 /* 
	  * Copy the below code from above and modified by ZJH .
	  * Time 201501120951
	  * */
	
	public static boolean insertResponse(long taskid ,String worker, String template,
			String response) throws Exception {
		Connection conn = ConnectDB.connect();
		String sql = "select * from workerresponse where worker=? and taskid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, worker);
		ps.setLong(2, taskid);
		ResultSet rs = ps.executeQuery();
		if (rs.last()) {
			sql = "update workerresponse set responseString=? where worker=? and taskid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, response);
			ps.setString(2, worker);
			ps.setLong(3, taskid);
			if (ps.executeUpdate() == 0) {
				System.out
						.println("insertResponse(): error occured when updating.");
				return false;
			}
		} else {
			sql = "insert into workerresponse (worker, taskid, responseString, isAccepted, answerTime) values (?, ?, ?, ?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, worker);
			ps.setLong(2, taskid);
			ps.setString(3, response);
			ps.setBoolean(4, false);
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			ps.setString(5, time);
			if (ps.executeUpdate() == 0) {
				System.out
						.println("insertResponse(): error occured when inserting.");
				return false;
			}
		}
		rs.close();
		ps.close();
		conn.close();
		return true;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @param template
	 *            template XML file path of the task.
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static List<WorkerResponse> queryResponse(String template)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		List<WorkerResponse> responses = new ArrayList<WorkerResponse>();
		Connection conn = ConnectDB.connect();
		String sql = "select * from workerresponse where taskTemplate=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, template);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			responses.add(new WorkerResponse(rs.getLong(1), rs.getString(2), rs
					.getString(3), rs.getString(4), rs.getBoolean(5), rs
					.getString(6),rs.getLong(7)));
		}

		return responses;
	}

//我按照上面的修改了一下。	
	public static List<WorkerResponse> queryResponse(long id)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		List<WorkerResponse> responses = new ArrayList<WorkerResponse>();
		Connection conn = ConnectDB.connect();
		String sql = "select * from workerresponse where taskid=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setLong(1, id);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			responses.add(new WorkerResponse(rs.getLong(1), rs.getString(2), rs
					.getString(3), rs.getString(4), rs.getBoolean(5), rs
					.getString(6),rs.getLong(7)));
		}
		return responses;
	}
	
	
	
	/**
	 * 
	 * @param id
	 *            id of the worker response.
	 * @return true if updating succeed; false is updating failed.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static boolean acceptResponse(long id) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Connection conn = ConnectDB.connect();
		String sql = "update workerresponse set isAccepted=? where id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setBoolean(1, true);
		ps.setLong(2, id);
		int rs = ps.executeUpdate();
		if (rs > 0) {
			return true;
		}
		System.out.println("acceptResponse(): update error.");
		return false;
	}
}

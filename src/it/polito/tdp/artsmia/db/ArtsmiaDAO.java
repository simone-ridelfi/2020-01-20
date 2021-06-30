package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Arco;
import it.polito.tdp.artsmia.model.ArtObject;
import it.polito.tdp.artsmia.model.Artist;
import it.polito.tdp.artsmia.model.Exhibition;

public class ArtsmiaDAO {

	public List<ArtObject> listObjects() {
		
		String sql = "SELECT * from objects";
		List<ArtObject> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
						res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
						res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
						res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
				
				result.add(artObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Exhibition> listExhibitions() {
		
		String sql = "SELECT * from exhibitions";
		List<Exhibition> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Exhibition exObj = new Exhibition(res.getInt("exhibition_id"), res.getString("exhibition_department"), res.getString("exhibition_title"), 
						res.getInt("begin"), res.getInt("end"));
				
				result.add(exObj);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> listRoles() {
		
		String sql = "SELECT DISTINCT role FROM authorship ORDER BY role ASC ";
		List<String> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(new String(res.getString("role")));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public void getVertici(String role, Map<Integer, Artist> map) {
		
		String sql = "SELECT DISTINCT ar.* "+
					 "FROM authorship au, artists ar "+
					 "WHERE au.artist_id = ar.artist_id AND role = ?";
		
		//List<Artist> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				map.put(res.getInt("artist_id"), new Artist(res.getInt("artist_id"), res.getString("name")));
			}
			conn.close();
			//return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return; // null;
		}
	}
	
	public List<Arco> getArchi(String role, Map<Integer,Artist> map) {
		
		String sql = "SELECT au1.artist_id AS a1, au2.artist_id AS a2, COUNT(DISTINCT ex1.exhibition_id) AS peso "+
					 "FROM authorship au1, authorship au2, exhibition_objects ex1, exhibition_objects ex2, objects o1, objects o2 "+
					 "WHERE au1.object_id = o1.object_id AND au2.object_id = o2.object_id AND ex1.exhibition_id = ex2.exhibition_id AND o1.object_id = ex1.object_id "
					 + "		AND o2.object_id = ex2.object_id AND au1.artist_id > au2.artist_id AND au1.role = ? AND au2.role = au1.role "+
					 "GROUP BY au1.artist_id, au2.artist_id";
		
		List<Arco> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, role);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				result.add(new Arco(map.get(res.getInt("a1")), map.get(res.getInt("a2")), res.getDouble("peso")));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

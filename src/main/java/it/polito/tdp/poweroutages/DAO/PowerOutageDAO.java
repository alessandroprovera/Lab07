package it.polito.tdp.poweroutages.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.poweroutages.model.Nerc;
import it.polito.tdp.poweroutages.model.PowerOutage;

public class PowerOutageDAO {
	
	public List<Nerc> getNercList() {

		String sql = "SELECT id, value FROM nerc";
		List<Nerc> nercList = new ArrayList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Nerc n = new Nerc(res.getInt("id"), res.getString("value"));
				nercList.add(n);
			}

			conn.close();

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return nercList;
	}
	
	public List<PowerOutage> getPowerOutagesByNerc(Nerc nerc){
		List<PowerOutage> powerOutages = new ArrayList<>();
		String sql = "SELECT * "
				+ "FROM PowerOutages as po, Nerc as n "
				+ "WHERE po.nerc_id = n.id and n.value = ?";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, nerc.getValue());
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				PowerOutage po = new PowerOutage(
						rs.getInt("id"),
						rs.getInt("event_type_id"),
						rs.getInt("tag_id"),
						rs.getInt("area_id"),
						rs.getInt("nerc_id"),
						rs.getInt("responsible_id"),
						rs.getInt("customers_affected"),
						rs.getTimestamp("date_event_began").toLocalDateTime(),
						rs.getTimestamp("date_event_finished").toLocalDateTime(),
						rs.getInt("demand_loss"));
				powerOutages.add(po);
			}
			conn.close();
			return powerOutages;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}

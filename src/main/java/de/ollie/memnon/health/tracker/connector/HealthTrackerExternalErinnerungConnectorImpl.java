package de.ollie.memnon.health.tracker.connector;

import de.ollie.memnon.core.model.ConnectorId;
import de.ollie.memnon.core.model.ErinnerungId;
import de.ollie.memnon.core.model.ExternalErinnerung;
import de.ollie.memnon.core.service.port.connector.ExternalErinnerungConnector;
import jakarta.inject.Named;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Named
@RequiredArgsConstructor
public class HealthTrackerExternalErinnerungConnectorImpl implements ExternalErinnerungConnector {

	private static final ConnectorId CONNECTOR_ID = new ConnectorId(UUID.randomUUID());

	private final HealthTrackerJdbcConfiguration configuration;

	@Override
	public boolean canBeConfirmed() {
		return false;
	}

	@Override
	public boolean confirm(ErinnerungId id) {
		String sql = "update DOCTOR_CONSULTATION set OPEN = ? WHERE id = ?";
		try (
			Connection conn = DriverManager.getConnection(
				configuration.getUrl(),
				configuration.getUsername(),
				configuration.getPassword()
			);
			PreparedStatement pstmt = conn.prepareStatement(sql)
		) {
			pstmt.setBoolean(1, false);
			pstmt.setObject(2, id.getUuid());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<ExternalErinnerung> findAllErinnerungen() {
		List<ExternalErinnerung> l = new ArrayList<>();
		try (
			Connection conn = DriverManager.getConnection(
				configuration.getUrl(),
				configuration.getUsername(),
				configuration.getPassword()
			);
			PreparedStatement pstmt = conn.prepareStatement(
				"select dc.DATE, dc.ID, concat(\"(\", DATE_FORMAT(dc.TIME, \"%H:%i\"), \") \", d.NAME, \" (\", dt.NAME, \")\") as DESCRIPTION " + //
				"from DOCTOR_CONSULTATION dc " + //
				"join DOCTOR d on d.ID = dc.DOCTOR " + //
				"join DOCTOR_TYPE dt on dt.ID = d.DOCTOR_TYPE " + //
				"where dc.`OPEN` = true"
			);
		) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					LocalDate date = rs.getObject("DATE", LocalDate.class);
					l.add(
						(ExternalErinnerung) new ExternalErinnerung()
							.setConnectorId(getId())
							.setBezugsdatum(date)
							.setNaechsterTermin(date)
							.setId(new ErinnerungId(rs.getObject("ID", UUID.class)))
							.setName(rs.getString("DESCRIPTION"))
							.setWiederholung(null)
					);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public ConnectorId getId() {
		return CONNECTOR_ID;
	}
}

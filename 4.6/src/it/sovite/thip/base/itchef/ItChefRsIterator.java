package it.sovite.thip.base.itchef;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.thera.thermfw.persist.Factory;

import it.thera.thip.cs.ResultSetIterator;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 06/02/2025
 * <br><br>
 * <b>71814    DSSOF3    06/02/2025</b>
 * <p></p>
 */

public class ItChefRsIterator extends ResultSetIterator {

	public ItChefRsIterator(ResultSet rs) {
		super(rs);
	}

	@Override
	protected Object createObject() throws SQLException {
		ItChefVendite obj = (ItChefVendite) Factory.createObject(ItChefVendite.class);
		obj.setIdImpianto(cursor.getString("idImpianto"));
		obj.setIdCliente(cursor.getString("idcliente"));
		obj.setIdArticoloVendita(cursor.getString("idarticolovendita"));
		obj.setDescrizione(cursor.getString("descrizione"));
		obj.setData(cursor.getTimestamp("data"));
		obj.setQuantita(cursor.getBigDecimal("quantita"));
		obj.setPrezzo(cursor.getBigDecimal("prezzo"));
		obj.setDataOraInserimento(cursor.getTimestamp("dataorainserimento"));
		obj.setFlag(cursor.getInt("flag"));
		return obj;
	}

}

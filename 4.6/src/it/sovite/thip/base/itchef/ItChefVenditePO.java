package it.sovite.thip.base.itchef;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import com.thera.thermfw.common.BaseComponentsCollection;
import com.thera.thermfw.common.BusinessObject;
import com.thera.thermfw.common.Deletable;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.persist.TableManager;
import com.thera.thermfw.security.Authorizable;
import com.thera.thermfw.security.ConflictableWithKey;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 06/02/2025
 * <br><br>
 * <b>71814    DSSOF3    06/02/2025</b>
 * <p></p>
 */

public abstract class ItChefVenditePO extends PersistentObject implements BusinessObject, Authorizable, Deletable, ConflictableWithKey {

	private static ItChefVendite cInstance;

	protected String idImpianto;         
	protected String idCliente;          
	protected String idArticoloVendita;  
	protected String descrizione;        
	protected Timestamp data;            
	protected BigDecimal quantita;       
	protected BigDecimal prezzo;         
	protected Timestamp dataOraInserimento;
	protected int flag;

	@SuppressWarnings("rawtypes")
	public static Vector retrieveList(String where, String orderBy, boolean optimistic)
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (cInstance == null)
			cInstance = (ItChefVendite) Factory.createObject(ItChefVendite.class);
		return PersistentObject.retrieveList(cInstance, where, orderBy, optimistic);
	}

	public static ItChefVendite elementWithKey(String key, int lockType) throws SQLException {
		return (ItChefVendite) PersistentObject.elementWithKey(ItChefVendite.class, key, lockType);
	}

	public String getIdImpianto() {
		return idImpianto;
	}
	public void setIdImpianto(String idImpianto) {
		this.idImpianto = idImpianto;
		setDirty();
	}

	public String getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
		setDirty();
	}

	public String getIdArticoloVendita() {
		return idArticoloVendita;
	}
	public void setIdArticoloVendita(String idArticoloVendita) {
		this.idArticoloVendita = idArticoloVendita;
		setDirty();
	}

	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
		setDirty();
	}

	public Timestamp getData() {
		return data;
	}
	public void setData(Timestamp data) {
		this.data = data;
		setDirty();
	}

	public BigDecimal getQuantita() {
		return quantita;
	}
	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
		setDirty();
	}

	public BigDecimal getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(BigDecimal prezzo) {
		this.prezzo = prezzo;
		setDirty();
	}

	public Timestamp getDataOraInserimento() {
		return dataOraInserimento;
	}
	public void setDataOraInserimento(Timestamp dataOraInserimento) {
		this.dataOraInserimento = dataOraInserimento;
		setDirty();
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
		setDirty();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Vector checkAll(BaseComponentsCollection components) {
		Vector errors = new Vector();
		components.runAllChecks(errors);
		return errors;
	}
	@Override
	protected TableManager getTableManager() throws SQLException {
		return ItChefVenditeTM.getInstance();
	}

	public String getKey() {
		String idImpianto = getIdImpianto();
		String idCliente = getIdCliente();
		String idArticoloVendita = getIdArticoloVendita();
		String descrizione = getDescrizione();
		Timestamp data = getData();
		Object[] keyParts = {idImpianto, idCliente, idArticoloVendita, descrizione, data};
		return KeyHelper.buildObjectKey(keyParts);
	}

	public void setKey(String key) {
		String objIdImpianto = KeyHelper.getTokenObjectKey(key, 1);
		setIdImpianto(objIdImpianto);

		String objIdCliente = KeyHelper.getTokenObjectKey(key, 2);
		setIdCliente(objIdCliente);

		String objIdArticoloVendita = KeyHelper.getTokenObjectKey(key, 3);
		setIdArticoloVendita(objIdArticoloVendita);

		String objDescrizione = KeyHelper.getTokenObjectKey(key, 4);
		setDescrizione(objDescrizione);

		Timestamp data = KeyHelper.stringToTimestamp(KeyHelper.getTokenObjectKey(key, 5));
		setData(data);
	}

	public boolean isDeletable() {
		return checkDelete() == null;
	}

	public String toString() {
		return getClass().getName() + " [" + KeyHelper.formatKeyString(getKey()) + "]";
	}
}

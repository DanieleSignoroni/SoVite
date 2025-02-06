package it.sovite.thip.base.itchef;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.thera.thermfw.ad.ClassADCollection;
import com.thera.thermfw.ad.ClassADCollectionManager;
import com.thera.thermfw.base.TimeUtils;
import com.thera.thermfw.base.Trace;
import com.thera.thermfw.batch.BatchRunnable;
import com.thera.thermfw.collector.ApiInfo;
import com.thera.thermfw.collector.BODataCollector;
import com.thera.thermfw.persist.CachedStatement;
import com.thera.thermfw.persist.ConnectionManager;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.KeyHelper;
import com.thera.thermfw.persist.PersistentObject;
import com.thera.thermfw.security.Authorizable;

import it.sovite.thip.base.cliente.YClientiVendita;
import it.sovite.thip.base.listini.YListinoVenditaRiga;
import it.sovite.thip.vendite.fogliVendita.YPsnDatiFogliVenItChef;
import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.azienda.Azienda;
import it.thera.thip.base.cliente.ClienteVendita;
import it.thera.thip.base.listini.ListinoVenditaScaglione;
import it.thera.thip.base.partner.Valuta;
import it.thera.thip.vendite.documentoVE.DocumentoVenRigaPrm;
import it.thera.thip.vendite.documentoVE.DocumentoVendita;
import it.thera.thip.vendite.generaleVE.CondizioniDiVendita;
import it.thera.thip.vendite.generaleVE.RicercaCondizioniDiVendita;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 06/02/2025
 * <br><br>
 * <b>71814    DSSOF3    06/02/2025</b>
 * <p></p>
 */

public class ImportazioneDocumentiVenditaItChef extends BatchRunnable implements Authorizable {

	protected static final String RETRIEVE_DOC_VEN_IMP = "SELECT * FROM "+ItChefVenditeTM.TABLE_NAME+" I "
			+ "WHERE I.flag = ? ";
	public static CachedStatement cRetrieveDocVenImp = new CachedStatement(RETRIEVE_DOC_VEN_IMP);

	private YPsnDatiFogliVenItChef persDatiFogliVenditaItChef = null;

	@SuppressWarnings("rawtypes")
	@Override
	protected boolean run() {
		boolean isOk = true;
		output.println(" -- INIZIO IMPORTAZIONE DOCUMENTI VENDITA DA TABELLE DI FORNTIERA "+ItChefVenditeTM.TABLE_NAME+" ");
		try {
			persDatiFogliVenditaItChef = YPsnDatiFogliVenItChef.getCurrentPersDatiDataIniPagam();
			if(persDatiFogliVenditaItChef != null) {
				output.println(" Recupero la lista dei documenti da importare ... ");
				Vector lista = recuperaDatiTabellaFrontiera();
				if(lista != null && lista.size() > 0) {
					output.println(" trovati "+lista.size()+" documenti da importare ... ");

					output.println(" raggruppo i documenti secondo il criterio cliente/articolo/magazzino/anno/mese/accorp.fatt.fogli ven ... ");
					LinkedHashMap<String, List<ItChefVendite>> map = raggruppaDocumenti(lista);

					isOk = runImportazione(map);

				}else {
					output.println(" non ho trovato nessun documenti da importare ... ");
				}
			}else {
				output.println(" personalizzazione dati vendita ItChef non definita ... ");
			}
		}catch (Exception e) {
			isOk = false;
			output.println(" ** Errore : "+e.getMessage());
			e.printStackTrace(Trace.excStream);
		}
		output.println(" -- TERMINE IMPORTAZIONE DOCUMENTI VENDITA DA TABELLE DI FORNTIERA "+ItChefVenditeTM.TABLE_NAME+" ");
		return isOk;
	}

	@SuppressWarnings("rawtypes")
	protected boolean runImportazione(LinkedHashMap<String, List<ItChefVendite>> map) {
		output.println(" inizio la scrittura dei documenti di vendita ... ");
		boolean ret = false;
		for (Map.Entry<String, List<ItChefVendite>> entry : map.entrySet()) {
			//String key = entry.getKey();
			List<ItChefVendite> value = entry.getValue();
			if(value.size() == 0) {
				//exc
			}
			//Creo un documento di vendita che avra' N righe quanti oggetti nel vettore
			DocumentoVendita docVen = null;
			BODataCollector boDC = createDataCollector("DocumentoVendita");
			if(boDC != null) {
				docVen = (DocumentoVendita) boDC.getBo();
				assegnaDatiTestata(docVen,value.get(0));

				int rc = boDC.save();
				if(rc != BODataCollector.OK) {
					output.println("Impossibile creare il documento vendiata ... "+boDC.messages().toString());
				}else {
					
					for (Iterator iterator = value.iterator(); iterator.hasNext();) {
						ItChefVendite riga = (ItChefVendite) iterator.next();
						BODataCollector boDCRig = createDataCollector("DocumentoVenRigPrm");
						DocumentoVenRigaPrm docVenRig = (DocumentoVenRigaPrm) boDCRig.getBo();
						
						docVenRig.setTestata(docVen);
						assegnaDatiRiga(docVenRig,riga);
						
					}
					
					rc = boDC.save();
					try {
						if(rc == BODataCollector.OK) {
							ConnectionManager.commit();
						}else {
							ConnectionManager.rollback();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
		output.println(" terminata la scrittura dei documenti di vendita ... ");
		return ret;
	}

	protected void assegnaDatiRiga(DocumentoVenRigaPrm docVenRig, ItChefVendite riga) {
		docVenRig.setArticolo(riga.getArticolo());
	}

	protected void assegnaDatiTestata(DocumentoVendita docVen, ItChefVendite itChefVendite) {
		docVen.getNumeratoreHandler().setIdSerie(persDatiFogliVenditaItChef.getIdSerie());
		docVen.getNumeratoreHandler().setDataDocumento(TimeUtils.getLastDayInMonth(TimeUtils.getDate(itChefVendite.getData())));
		docVen.setCliente(itChefVendite.getCliente());
		docVen.setCausale(((YClientiVendita)docVen.getCliente()).getCausalevenditasrv());
		docVen.setListinoPrezzi(persDatiFogliVenditaItChef.getListinovendita());

		docVen.completaBO();
	}

	/**
	 * Raggruppa la lista dei documenti da importare secondo la rottura : <br></br>
	 * 'cliente/magazzino/anno/mese/Accorp. fatt. fogli'.<br></br>
	 * 
	 * @param dati una lista di oggetti {@link ItChefVendite}
	 * @return una mappa ordinata dei documenti raggruppati
	 */
	@SuppressWarnings("rawtypes")
	public LinkedHashMap<String, List<ItChefVendite>> raggruppaDocumenti(Vector dati){
		LinkedHashMap<String, List<ItChefVendite>> map = new LinkedHashMap<String, List<ItChefVendite>>();
		for (Iterator iterator = dati.iterator(); iterator.hasNext();) {
			ItChefVendite dato = (ItChefVendite) iterator.next();

			String idCliente = dato.getIdCliente();
			String idMagazzino = dato.getIdImpianto();
			String anno = String.valueOf(TimeUtils.getYear(TimeUtils.getDate(dato.getData())));
			String mese = String.valueOf(TimeUtils.getMonth(TimeUtils.getDate(dato.getData())));

			String accorpFattFogliVendita = null;
			try {
				ClienteVendita cliente = (ClienteVendita) ClienteVendita.elementWithKey(ClienteVendita.class, KeyHelper.buildObjectKey(new String[] {Azienda.getAziendaCorrente(),idCliente}), PersistentObject.NO_LOCK);
				Articolo articolo = Articolo.elementWithKey(KeyHelper.buildObjectKey(new String[] {Azienda.getAziendaCorrente(),dato.getIdArticoloVendita()}), PersistentObject.NO_LOCK);
				if(cliente != null && articolo != null)
					accorpFattFogliVendita = trovaAccorpamentoFatturaFogliVendita(cliente,articolo,TimeUtils.getDate(dato.getData()));

				dato.setArticolo(articolo);
				dato.setCliente(cliente);
			} catch (SQLException e) {
				e.printStackTrace(Trace.excStream);
			}
			if(accorpFattFogliVendita != null) {
				String key = KeyHelper.buildObjectKey(new String[] {idCliente,idMagazzino,anno,mese,accorpFattFogliVendita});
				if(map.containsKey(key)) {
					List<ItChefVendite> grouped = map.get(key);
					grouped.add(dato);

					map.put(key, grouped);
				}else {
					map.put(key, (List<ItChefVendite>) Arrays.asList(dato));
				}
			}

		}
		return map;
	}

	protected String trovaAccorpamentoFatturaFogliVendita(ClienteVendita cliente, Articolo articolo, Date data) throws SQLException {
		String accorpFattFogliVendita = null;
		RicercaCondizioniDiVendita rcdv = new RicercaCondizioniDiVendita();
		CondizioniDiVendita cdv = null;
		cdv = rcdv.ricercaCondizioniDiVendita(
				Azienda.getAziendaCorrente(), 
				persDatiFogliVenditaItChef.getListinovendita(), 
				cliente, 
				articolo, 
				articolo.getUMDefaultVendita(), 
				BigDecimal.ONE, // BigDecimal quantita
				null, // BigDecimal importo
				null, // ModalitaPagamento modalita
				data, // dataValidita
				null, // Agente agente 
				null, // Agente subagente 
				null, // UnitaMisura unitaMag
				BigDecimal.ONE, // BigDecimal quantitaMag 
				(Valuta) Valuta.elementWithKey(Valuta.class, "EUR", 0), // Valuta valuta
				null, // UnitaMisura umSecMag
				null);
		ListinoVenditaScaglione scg = cdv.getListinoVenditaScaglione();
		YListinoVenditaRiga riga = (YListinoVenditaRiga) scg.getListinoRiga();
		if(riga.getAccorpFattFogliVendita() == null) {
			accorpFattFogliVendita = "0";
		}else {
			accorpFattFogliVendita = riga.getAccorpFattFogliVendita().toString();
		}
		return accorpFattFogliVendita;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector recuperaDatiTabellaFrontiera() {
		Vector list = new Vector();
		ResultSet rs = null;
		ItChefRsIterator prodottiIterator = null;
		try{
			PreparedStatement ps = cRetrieveDocVenImp.getStatement();
			ps.setInt(1, 0);
			rs = ps.executeQuery();
			prodottiIterator = new ItChefRsIterator(rs);
			while(prodottiIterator.hasNext()) {
				list.add((ItChefVendite) prodottiIterator.next());
			}
		} catch (SQLException e) {
			e.printStackTrace(Trace.excStream);
		}finally{
			try{
				rs.close();
			}catch(SQLException e){
				e.printStackTrace(Trace.excStream);
			}
		}
		return list;
	}

	protected BODataCollector createDataCollector(String classname) {
		try {
			ClassADCollection hdr = ClassADCollectionManager.collectionWithName(classname);
			return createDataCollector(hdr);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected BODataCollector createDataCollector(ClassADCollection classDescriptor) {
		BODataCollector dataCollector = null;
		String collectorName = classDescriptor.getBODataCollector();
		if (collectorName != null) {
			dataCollector = (BODataCollector) Factory.createObject(collectorName);
		} else {
			dataCollector = new BODataCollector();
		}

		//PJ - ApiInfo - inizio
		ApiInfo info = new ApiInfo();
		info.doNotAddNullComponentsToGroup = true;
		dataCollector.setApiInfo(info);
		dataCollector.initialize(classDescriptor.getClassName(), true);
		//PJ - ApiInfo - fine

		return dataCollector;
	}

	@Override
	protected String getClassAdCollectionName() {
		return "YImpDVItChef";
	} 
}

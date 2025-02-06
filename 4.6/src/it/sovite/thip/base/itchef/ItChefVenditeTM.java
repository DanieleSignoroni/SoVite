package it.sovite.thip.base.itchef;

import java.sql.SQLException;

import com.thera.thermfw.base.SystemParam;
import com.thera.thermfw.persist.Factory;
import com.thera.thermfw.persist.TableManager;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 06/02/2025
 * <br><br>
 * <b>71814    DSSOF3    06/02/2025</b>
 * <p></p>
 */

public class ItChefVenditeTM extends TableManager {

	public static final String IDIMPIANTO          = "IDIMPIANTO";
	public static final String IDCLIENTE           = "IDCLIENTE";
	public static final String IDARTICOLOVENDITA   = "IDARTICOLOVENDITA";
	public static final String DESCRIZIONE         = "DESCRIZIONE";
	public static final String DATA                = "DATA";
	public static final String QUANTITA            = "QUANTITA";
	public static final String PREZZO              = "PREZZO";
	public static final String DATAORAINSERIMENTO  = "DATAORAINSERIMENTO";
	public static final String FLAG                = "FLAG";

	public static final String TABLE_NAME = SystemParam.getSchema("ITCHEF") + "ITCHEF_VENDITE";

	private static final String CLASS_NAME = ItChefVendite.class.getName();
	private static TableManager cInstance;

	public synchronized static TableManager getInstance() throws SQLException {
		cInstance = null;
		if (cInstance == null) {
			cInstance = (TableManager) Factory.createObject(ItChefVenditeTM.class);
		}
		return cInstance;
	}

	public ItChefVenditeTM() throws SQLException {
		super();
	}

	@Override
	protected void initialize() throws SQLException {
		setTableName(TABLE_NAME);
		setObjClassName(CLASS_NAME);
		init();
	}

	@Override
	protected void initializeRelation() throws SQLException {
		super.initializeRelation();

		addAttribute("IdImpianto",         IDIMPIANTO);
		addAttribute("IdCliente",          IDCLIENTE);
		addAttribute("IdArticoloVendita",  IDARTICOLOVENDITA);
		addAttribute("Descrizione",        DESCRIZIONE);
		addAttribute("Data",               DATA);
		addAttribute("Quantita",           QUANTITA);
		addAttribute("Prezzo",             PREZZO);
		addAttribute("DataOraInserimento", DATAORAINSERIMENTO);
		addAttribute("Flag",               FLAG);

		setKeys(IDIMPIANTO + ", " + IDCLIENTE + ", " + IDARTICOLOVENDITA
				+ ", " + DESCRIZIONE + ", " + DATA);

	}

	private void init() throws SQLException {
		configure();
	}
	
}

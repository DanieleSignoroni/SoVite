package it.sovite.thip.base.itchef;

import com.thera.thermfw.common.ErrorMessage;

import it.thera.thip.base.articolo.Articolo;
import it.thera.thip.base.cliente.ClienteVendita;

/**
 * <h1>Softre Solutions</h1>
 * <br>
 * @author Daniele Signoroni 06/02/2025
 * <br><br>
 * <b>71814    DSSOF3    06/02/2025</b>
 * <p></p>
 */

public class ItChefVendite extends ItChefVenditePO {
	
	protected ClienteVendita cliente = null;
	protected Articolo articolo = null;
	
	@Override
	public ErrorMessage checkDelete() {
		return null;
	}

	public ClienteVendita getCliente() {
		return cliente;
	}

	public void setCliente(ClienteVendita cliente) {
		this.cliente = cliente;
	}

	public Articolo getArticolo() {
		return articolo;
	}

	public void setArticolo(Articolo articolo) {
		this.articolo = articolo;
	}
	
}

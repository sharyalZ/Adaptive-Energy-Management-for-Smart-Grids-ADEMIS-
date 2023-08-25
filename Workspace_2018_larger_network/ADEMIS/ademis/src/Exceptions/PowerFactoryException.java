package Exceptions;

/**
 * Utilisé si un problème avec PowerFactory connu est détecté (licence indisponible ou PowerFactory déjà ouvert par exemple). En cas de problème non connu, la communication avec Python sera interrompu et aucune information ne pourra être récupéré. Un moyen de débugger est d'essayer de manipuler PowerFactory directement à partir de Python en utilisant spyder sans l'AMAS
 * @author jbblanc
 *
 */
public class PowerFactoryException extends Exception {

	public PowerFactoryException() {
		
	}

	public PowerFactoryException(String arg0) {
		super(arg0);
		
	}


}

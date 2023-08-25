package Exceptions;

/**
 * Utilis� si un probl�me avec PowerFactory connu est d�tect� (licence indisponible ou PowerFactory d�j� ouvert par exemple). En cas de probl�me non connu, la communication avec Python sera interrompu et aucune information ne pourra �tre r�cup�r�. Un moyen de d�bugger est d'essayer de manipuler PowerFactory directement � partir de Python en utilisant spyder sans l'AMAS
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

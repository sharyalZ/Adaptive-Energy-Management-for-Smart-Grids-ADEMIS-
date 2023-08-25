package Interface;

import java.util.EnumMap;
import java.util.Map;

/**
 * Permet de stocker le chemin dans PF des �l�ments DSL (des objets de PowerFactory) n�cessaires (p.e. P-inputEV1 pour d�finir la puissance de recharge du v�hicule EV1)
 * @author sharyal et jbblanc
 *
 */
public class DSLModels {

	private Map<DSLModelType, String> dslPaths=new EnumMap<>(DSLModelType.class);
	
	
	public void addModel(DSLModelType type, String path) {
		dslPaths.put(type, path);
	}
	
	public String getModel(DSLModelType type) {
		if(dslPaths.containsKey(type)) {
			return dslPaths.get(type);
		}else {
			return null;
		}

	}

	@Override
	public String toString() {
		return "DSLModels [dslPaths=" + dslPaths + "]";
	}

	
}

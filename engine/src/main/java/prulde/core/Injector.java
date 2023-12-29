package prulde.core;

import prulde.core.dagger.DaggerEngineComponent;
import prulde.core.dagger.EngineComponent;

public class Injector {
	private static EngineComponent engineComponent;

	public static EngineComponent getEngineComponent() {
		if (engineComponent == null) {
			engineComponent = DaggerEngineComponent.create();
		}
		return engineComponent;
	}
}

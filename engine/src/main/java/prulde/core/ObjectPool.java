package prulde.core;

import java.lang.reflect.Array;

public abstract class ObjectPool<T extends Reusable> {
	private final T[] objects;
	private final int capacity;
	private int currentIndex;

	public ObjectPool(int maxCapacity) {
		capacity = maxCapacity;
		currentIndex = 0;
		objects = (T[]) Array.newInstance(Reusable.class, maxCapacity);
		for (int i = 0; i < maxCapacity; i++) {
			objects[i] = createObject();
			currentIndex++;
		}
		// capacity-1
		currentIndex--;
	}

	protected abstract T createObject();

	public T checkOut() {
		if (currentIndex == 0) throw new IllegalStateException("Array is empty");
		T obj = objects[currentIndex];
		objects[currentIndex] = null;
		currentIndex--;
		return obj;
	}

	public void checkIn(T obj) {
		if (obj == null) throw new IllegalArgumentException("object cannot be null");
		if (currentIndex + 1 > capacity) return;
		obj.reset();
		objects[currentIndex++] = obj;
	}
}

package utilities;

public class ArrayList<E> extends java.util.ArrayList<E> {
	private static final long serialVersionUID = 1L;

	public Integer[] indexesOf(E value) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < size(); i++) {
			if (get(i).equals(value)) {
				indexes.add(i);
			}
		}
		
		return indexes.toArray(new Integer[indexes.size()]);
	}
	
	public E[] valuesOfIndexes(Integer[] indexes, E[] result) {
		ArrayList<E> values = new ArrayList<E>();
		for(Integer index : indexes) {
			values.add(get(index));
		}

		return values.toArray(result);
	}
}

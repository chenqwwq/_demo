package cache;

/**
 * @author chen
 * @date 2021/9/25
 **/
public interface CustomCache<K, V> {

	class FreqNode<K, V> {
		long freq;
		K k;
		V v;

		public FreqNode(long freq, K k, V v) {
			this.freq = freq;
			this.k = k;
			this.v = v;
		}
	}

	V get(K k);

	int size();

	void put(K k, V v);
}

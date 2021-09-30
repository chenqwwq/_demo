package cache;

/**
 * @author chen
 * @date 2021/9/25
 **/
public interface CustomCache {

	class FreqNode {
		long freq;
		int k;
		int v;

		public FreqNode(long freq, int k, int v) {
			this.freq = freq;
			this.k = k;
			this.v = v;
		}
	}

	int get(int k);

	int size();

	void put(int k, int v);
}

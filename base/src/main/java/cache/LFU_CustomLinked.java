package cache;

import java.util.*;

/**
 * |  FreqNode(0) -> FreqNode(1) -> FreqNode(2) |
 * |    ^        |    ^          |    ^
 * v    |	      v    |	      v    |
 * ValueNode      ValueNode       ValueNode
 * |    ^
 * v    |
 * ValueNode
 * <p>
 * TODO: 简单测试
 *
 * @author chen
 * @date 2021/9/25
 **/
public class LFU_CustomLinked<K, V> implements CustomCache<K, V> {

	/**
	 * 初始頻率
	 */
	private static final int INITIAL_FREQ = 1;

	// inner class

	/**
	 * {@link cache.CustomCache.FreqNode} 表示频率的单向链表，每个节点表示一个频率
	 * 每个 {@link cache.CustomCache.FreqNode} 都会持有一个 {@link ValueNode} 为节点的双向链表
	 *
	 * @param <K> Key
	 * @param <V> Value
	 */
	private static class FreqNode<K, V> {
		FreqNode<K, V> prev;
		FreqNode<K, V> next;
		final int freq;
		int size;

		final ValueNode<K, V> head;
		ValueNode<K, V> end;

		public FreqNode() {
			freq = INITIAL_FREQ;
			// 哨兵
			head = end = new ValueNode<>();
		}

		public FreqNode(FreqNode<K, V> next, FreqNode<K, V> prev, int freq) {
			this.next = next;
			this.prev = prev;
			this.freq = freq;
			size = 0;
			// 哨兵
			head = end = new ValueNode<>();
		}

		/**
		 * 添加到 Value 链表的末尾
		 *
		 * @param node {@link ValueNode}
		 */
		public void addChildLast(ValueNode<K, V> node) {
			end.next = node;
			node.prev = end;
			end = node;
			this.size++;
			node.freq = this;
		}

		public ValueNode<K, V> removeChildFirst() {
			final ValueNode<K, V> next = head.next;
			if (Objects.isNull(next)) {
				return Objects.isNull(this.next)
						? this.next.removeChildFirst()
						: null;
			}

			next.deleteSelf();
			return next;
		}

		/**
		 * 添加下一個 {@link FreqNode}
		 *
		 * @return 新增的頻率節點
		 */
		public FreqNode<K, V> addNextFreq() {
			if (this.next != null && this.next.freq == this.freq + 1) {
				return this.next;
			}
			final FreqNode<K, V> newFreq = new FreqNode<K, V>(this.next, this, freq + 1);
			if (Objects.nonNull(this.next)) {
				this.next.prev = newFreq;
			}
			this.next = newFreq;
			return newFreq;
		}

		/**
		 * 遞減 size，如果 size == 0，需要刪除自身
		 */
		public void decreasing() {
			this.size--;
			if (this.size != 0 || this.prev == null) {
				return;
			}
			this.prev.next = this.next;
			this.next.prev = this.prev;
		}
	}

	/**
	 * 存储在 {@link Map} 和 {@link cache.CustomCache.FreqNode} 的数据包装节点
	 *
	 * @param <K> Key
	 * @param <V> Value
	 */
	private static class ValueNode<K, V> {
		ValueNode<K, V> next;
		ValueNode<K, V> prev;
		FreqNode<K, V> freq;

		K key;
		V val;

		public ValueNode() {
		}

		public ValueNode(K key, V val) {
			this.key = key;
			this.val = val;
		}

		/**
		 * 删除自身
		 */
		public void deleteSelf() {
			this.freq.decreasing();
			if (this.next == null) {
				this.prev.next = null;
				return;
			}
			this.prev.next = this.next;
			this.next.prev = this.prev;
		}


	}

	private Map<K, ValueNode<K, V>> cache;
	private int capacity;
	private FreqNode<K, V> head;

	// construction

	public LFU_CustomLinked(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity must be positive");
		}
		this.capacity = capacity;
		this.cache = new HashMap<>();
		// 哨兵
		this.head = new FreqNode<>();
	}


	// method

	@Override
	public V get(K k) {
		final ValueNode<K, V> value = cache.get(k);
		if (Objects.isNull(value)) {
			return null;
		}
		access(value);
		return value.val;
	}

	/**
	 * 将 {@link ValueNode} 绑定的 {@link FreqNode} 进一
	 *
	 * @param value 需要改变的节点
	 */
	private void access(ValueNode<K, V> value) {
		// 当前的频率节点
		final FreqNode<K, V> freq = value.freq;
		// 没有后续频率节点,或者频率节点的频数不对
		if (freq.next != null && freq.next.freq != freq.freq + 1) {
			freq.next.addChildLast(value);
		} else {
			final FreqNode<K, V> newFreq = freq.addNextFreq();
			value.deleteSelf();
			newFreq.addChildLast(value);
		}
	}


	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public void put(K k, V v) {
		final ValueNode<K, V> val = cache.get(k);
		if (Objects.isNull(val)) {
			final ValueNode<K, V> newNode = new ValueNode<>(k, v);
			head.addChildLast(newNode);
			while (size() >= capacity) {
				final ValueNode<K, V> valueNode = head.removeChildFirst();
				if (Objects.isNull(valueNode)) {
					break;
				}
				cache.remove(valueNode.key);
			}
			cache.put(k, newNode);
			return;
		}
		val.val = v;
		access(val);
	}

	public List<K> asKeyList() {
		List<K> ans = new ArrayList<>(capacity);
		FreqNode<K, V> freqNode = head;
		while (freqNode != null) {
			ValueNode<K, V> node = freqNode.head;
			while (node.next != null) {
				node = node.next;
				ans.add(node.key);
			}
			freqNode = freqNode.next;
		}
		return ans;
	}

}

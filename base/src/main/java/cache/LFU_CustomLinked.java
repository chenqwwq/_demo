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
 * TODO: 中间无用节点可以去除
 *
 * @author chen
 * @date 2021/9/25
 **/
public class LFU_CustomLinked implements CustomCache {

	/**
	 * 初始頻率
	 */
	private static final int INITIAL_FREQ = 0;

	// inner class

	/**
	 * {@link cache.CustomCache.FreqNode} 表示频率的单向链表，每个节点表示一个频率
	 * 每个 {@link cache.CustomCache.FreqNode} 都会持有一个 {@link ValueNode} 为节点的双向链表
	 */
	private static class FreqNode {
		final int freq;
		int size;

		FreqNode prev;
		FreqNode next;

		ValueNode head;
		ValueNode end;

		public FreqNode() {
			freq = INITIAL_FREQ;
		}

		public FreqNode(FreqNode next, FreqNode prev, int freq) {
			this.next = next;
			this.prev = prev;
			this.freq = freq;
			size = 0;
		}

		/**
		 * 添加到 Value 链表的末尾
		 *
		 * @param node {@link ValueNode}
		 */
		public void addChildLast(ValueNode node) {
			this.size++;
			node.freq = this;
			if (head == null) {
				head = end = node;
				return;
			}
			end.next = node;
			node.prev = end;
			end = node;
		}

		public ValueNode removeChildFirst() {
			if (head != null) {
				ValueNode node = head;
				delete(node);
				return node;
			}
			return next != null ? next.removeChildFirst() : null;
		}

		/**
		 * 添加下一個 {@link FreqNode}
		 *
		 * @return 新增的頻率節點
		 */
		public FreqNode addNextFreq() {
			if (this.next != null && this.next.freq == this.freq + 1) {
				return this.next;
			}
			final FreqNode newFreq = new FreqNode(this.next, this, freq + 1);
			if (Objects.nonNull(this.next)) {
				this.next.prev = newFreq;
			}
			this.next = newFreq;
			return newFreq;
		}

		/**
		 * 删除自身
		 */
		public void delete(ValueNode node) {
			this.size--;
			if (head == node && end == node) {
				head = end = null;
			} else if (head == node) {
				head = node.next;
				head.prev = null;
			} else if (end == node) {
				end = node.prev;
				end.next = null;
			} else {
				node.next.prev = node.prev;
				node.prev.next = node.next;
			}
			node.prev = node.next = null;
		}
	}

	/**
	 * 存储在 {@link Map} 和 {@link cache.CustomCache.FreqNode} 的数据包装节点
	 */
	private static class ValueNode {
		ValueNode next;
		ValueNode prev;
		FreqNode freq;

		int key;
		int val;

		public ValueNode() {
		}

		public ValueNode(int key, int val) {
			this.key = key;
			this.val = val;
		}

	}

	private Map<java.lang.Integer, ValueNode> cache;
	private int capacity;
	private FreqNode head;

	// construction

	public LFU_CustomLinked(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("capacity must be positive");
		}
		this.capacity = capacity;
		this.cache = new HashMap<>();
		// 哨兵,始终保持初始访问频率的节点
		this.head = new FreqNode();
	}


	// method

	@Override
	public int get(int k) {
		final ValueNode value = cache.get(k);
		if (Objects.isNull(value)) {
			return -1;
		}
		access(value);
		return value.val;
	}

	/**
	 * 将 {@link ValueNode} 绑定的 {@link FreqNode} 进一
	 *
	 * @param value 需要改变的节点
	 */
	private void access(ValueNode value) {
		final FreqNode freq = value.freq;
		freq.delete(value);
		if (freq.next != null && freq.next.freq == freq.freq + 1) {
			freq.next.addChildLast(value);
		} else {
			final FreqNode newFreq = freq.addNextFreq();
			newFreq.addChildLast(value);
		}
	}


	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public void put(int k, int v) {
		if (capacity == 0) {
			return;
		}
		final ValueNode val = cache.get(k);
		if (Objects.isNull(val)) {
			final ValueNode newNode = new ValueNode(k, v);
			while (size() >= capacity) {
				final ValueNode valueNode = head.removeChildFirst();
				if (Objects.isNull(valueNode)) {
					break;
				}
				cache.remove(valueNode.key);
			}
			head.addChildLast(newNode);
			cache.put(k, newNode);
			return;
		}
		val.val = v;
		access(val);
	}

	public List<Integer> asKeyList() {
		List<Integer> ans = new ArrayList<>(capacity);
		FreqNode freqNode = head;
		while (freqNode != null) {
			ValueNode node = freqNode.head;
			while (node.next != null) {
				node = node.next;
				ans.add(node.key);
			}
			freqNode = freqNode.next;
		}
		return ans;
		}

}

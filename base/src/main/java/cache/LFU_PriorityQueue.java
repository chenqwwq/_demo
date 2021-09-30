package cache;

import java.util.*;

/**
 * LFU 的优先级队列实现方法
 * <p>
 * 使用{@link PriorityQueue 优先级队列}维护频次排行,超出之后淘汰频次最小的节点
 * <p>
 * LFU 存在一个问题，就是新增元素的访问量肯定不高，连续插入容易直接被删除
 *
 * @author chen
 * @date 2021/9/25
 **/
public class LFU_PriorityQueue implements CustomCache {

	private PriorityQueue<FreqNode> freqNodes;
	private Map<Integer, FreqNode> cache;
	private final int capacity;

	public LFU_PriorityQueue(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity must be positive");
		}
		this.capacity = capacity;
		freqNodes = new PriorityQueue<>(Comparator.comparingLong(o -> o.freq));
		cache = new HashMap<>();
	}

	@Override
	public int get(int k) {
		final FreqNode node = cache.get(k);
		if (Objects.isNull(node)) {
			return -1;
		}
		freqNodes.remove(node);
		node.freq++;
		freqNodes.offer(node);
		return node.v;
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public void put(int k, int v) {
		final FreqNode node = cache.get(k);
		if (Objects.isNull(node)) {
			if (capacity == cache.size()) {
				final FreqNode del = freqNodes.poll();
				if (Objects.nonNull(del)) {
					cache.remove(del.k);
				}
			}
			final FreqNode value = new FreqNode(1, k, v);
			cache.put(k, value);
			freqNodes.offer(value);
			return;
		}
		freqNodes.remove(node);
		node.freq++;
		node.v = v;
		freqNodes.add(node);
	}
}

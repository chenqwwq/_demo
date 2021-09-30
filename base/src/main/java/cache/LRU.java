package cache;

import org.checkerframework.checker.units.qual.K;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 {@link java.util.LinkedHashMap} 的 LRU 实现
 * <p>
 * {@link LinkedHashMap} 是对 {@link java.util.HashMap} 的扩展实现，实现了 {@link java.util.HashMap} 中的模板方法，如下:
 * {@link LinkedHashMap#afterNodeInsertion(boolean) 在节点插入后触发}
 * {@link LinkedHashMap#afterNodeRemoval(Node)} 节点删除时触发 }
 * {@link LinkedHashMap#afterNodeAccess(Node)} 节点被访问的时候触发}
 *
 * @author chen
 * @date 2021/9/13
 **/
public class LRU implements CustomCache {

	private LinkedHashMap<Integer, Integer> cache;

	public LRU(int capacity) {
		this.capacity = capacity;
		cache = new LinkedHashMap<Integer,Integer>(capacity) {
			/**
			 * 是否需要删除最老的对象
			 * {@link LinkedHashMap#afterNodeInsertion(boolean) 中调用}
			 *
			 * @param eldest 需要判断的对象
			 * @return true -> 删除
			 */
			@Override
			public boolean removeEldestEntry(Map.Entry eldest) {
				return size() > capacity;
			}
		};
	}

	@Override
	public int get(int k) {
		return cache.get(k);
	}

	@Override
	public int size() {
		return cache.size();
	}

	@Override
	public void put(int k,int v) {
		cache.put(k, v);
	}

	/**
	 * 缓存大小
	 */
	private final int capacity;
}

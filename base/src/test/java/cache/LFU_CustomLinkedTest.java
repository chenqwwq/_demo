package cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author chen
 * @date 2021/9/27
 **/
class LFU_CustomLinkedTest {

	@Test
	void put() {
		LFU_CustomLinked<Integer, Integer> lfu = new LFU_CustomLinked<>(6);
		for (int i = 0; i < 8; i++) {
			lfu.put(i, i);
			for (int j = 0; j < i; j++) {
				lfu.get(i);
			}
		}
		final List<Integer> keys = lfu.asKeyList();
		for (int i = 2; i < 8; i++) {
			Assertions.assertEquals(i, keys.get(i - 1));
		}
	}
}
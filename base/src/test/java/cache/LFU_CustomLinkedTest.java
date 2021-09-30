package cache;

import org.junit.jupiter.api.Test;

/**
 * @author chen
 * @date 2021/9/27
 **/
class LFU_CustomLinkedTest {

	@Test
	public void testAccess() {
		final LFU_CustomLinked lfu = new LFU_CustomLinked(2);
		lfu.put(1, 1);
		lfu.put(2, 2);
		lfu.get(2);
		lfu.get(2);
		lfu.put(3, 3);
		System.out.println(lfu.asKeyList());
	}

}
package limiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 基于内存的令牌桶限流实现
 * <p>
 * 简单来说令牌桶的限流算法: 以固定的速度产生令牌并放入桶中,每次用户的请求都需要申请获得令牌才可以执行，不足就直接拒绝或者等待
 * <p>
 * 相对比漏桶算法，令牌桶更能面对突发的流畅
 *
 * @author chen
 * @date 2021/9/13
 **/
public class TokensBucketLimiter {
	/**
	 * 当前令牌数量
	 */
	private volatile int tokens;

	/**
	 * 令牌生成速度
	 * <p>
	 * rate 秒生成一个
	 */
	private int rate;

	/**
	 * 桶的容量，超过之后无法继续新增
	 */
	private int cap;


	private final ScheduledExecutorService scheduled;


	public TokensBucketLimiter(int tokens, int rate, int cap) {
		this.tokens = tokens;
		this.rate = rate;
		this.cap = cap;
		this.scheduled = Executors.newSingleThreadScheduledExecutor();
	}

	class AddTokensTask implements Runnable {

		@Override
		public void run() {

		}
	}

}

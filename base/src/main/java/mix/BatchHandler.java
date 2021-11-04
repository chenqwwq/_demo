package mix;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 批量操作的处理
 *
 * @author chen
 * @date 2021-11-03
 **/
public abstract class BatchHandler<T> {

	/**
	 * 最大间隔时间
	 */
	private final long interval;

	/**
	 * 间隔的时间单位
	 */
	private final TimeUnit intervalUnit;

	/**
	 * 触发阈值
	 */
	private final long threshold;

	/**
	 * 缓冲池
	 */
	private final BlockingQueue<T> blockingQueue;

	/**
	 * 定时器
	 */
	private final ScheduledExecutorService scheduledExecutorService;

	/**
	 * 计时器
	 */
	private final Stopwatch stopwatch;

	/**
	 * 上次执行的时间戳
	 */
	private volatile long pc = 0;

	private BatchHandler(long interval, TimeUnit intervalUnit, long threshold) {
		this.interval = interval;
		this.threshold = threshold;
		this.intervalUnit = intervalUnit;
		blockingQueue = new LinkedBlockingQueue<>();
		scheduledExecutorService = new ScheduledThreadPoolExecutor(1, (r -> new Thread(r, "schedule-thread")));
		stopwatch = Stopwatch.createUnstarted();
	}

	final class ActionHandle implements Runnable {

		@Override
		public void run() {
			final long elapsed = stopwatch.elapsed(intervalUnit);
			// 超过时间
			if (elapsed - pc >= interval) {
				handle();
				scheduledExecutorService.schedule(this, interval, intervalUnit);
				pc = elapsed;
			} else {
				scheduledExecutorService.schedule(this, interval, intervalUnit);
			}
		}
	}

	public void start() {
		stopwatch.start();
		scheduledExecutorService.schedule(new ActionHandle(), interval, intervalUnit);
	}

	public void add(T t) {
		blockingQueue.add(t);
		if (blockingQueue.size() >= threshold) {
			handle();
		}
	}

	private void handle() {
		handle(stopwatch.elapsed(intervalUnit));
	}

	private void handle(long curr) {
		List<T> actions = new ArrayList<>();
		blockingQueue.drainTo(actions);
		if (!actions.isEmpty()) {
			handle(actions);
		}
		this.pc = curr;
	}

	/**
	 * 模板方法模式，子类处理
	 *
	 * @param batch 批量数据
	 */
	public abstract void handle(List<T> batch);
}

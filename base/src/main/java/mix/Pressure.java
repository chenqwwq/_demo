package mix;

import com.sun.istack.internal.NotNull;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static common.Log.log;

/**
 * 压力测试，测试 TPS
 *
 * @author chen
 * @date 2021/11/2
 **/
public class Pressure {

	/**
	 * 压测论次
	 */
	private volatile int round;

	/**
	 * 压测线程数
	 */
	private final int threadCnt;

	/**
	 * 压测时间
	 */
	private final long time;

	/**
	 * 压测事件单位
	 */
	private final TimeUnit timeUnit;

	/**
	 * success count
	 */
	private long[] sc;

	/**
	 * exception count
	 */
	private long[] ec;

	/**
	 * 需要执行的任务
	 */
	private final Runnable runnable;

	/**
	 * 统计的时候需要所有线程都完成
	 */
	private CountDownLatch cdl;

	/**
	 * 计数栅栏,线程需要一起跑,而不是开一个跑一个
	 */
	private CyclicBarrier cb;

	/**
	 * 定时任务
	 */
	private final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	private ExecutorService executor;

	/**
	 * 测试的工作线程
	 */
	class Worker implements Runnable {

		private final int id;

		private int curr;

		public Worker(int id, int round) {
			this.id = id;
			this.curr = round;
		}

		@Override
		public void run() {
			// 外层的状态正确
			while (true) {
				try {
					// 等待
					cb.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					return;
				}
				// 回合数相同
				while (round == curr) {
					try {
						runnable.run();
						sc[id]++;
					} catch (Exception e) {
						ec[id]++;
					}
				}
				curr--;
				cdl.countDown();
			}
		}
	}

	public Pressure(int round, int threadCnt, long time, TimeUnit timeUnit, Runnable runnable) {
		this.round = round;
		this.threadCnt = threadCnt;
		this.time = time;
		this.timeUnit = timeUnit;
		this.runnable = runnable;
		init();
	}

	private void init() {
		sc = new long[threadCnt];
		ec = new long[threadCnt];
		cb = new CyclicBarrier(threadCnt + 1);
		cdl = new CountDownLatch(threadCnt);
		executor = Executors.newFixedThreadPool(threadCnt, new ThreadFactory() {
			final AtomicInteger id = new AtomicInteger();

			@Override
			public Thread newThread(@NotNull Runnable r) {
				return new Thread(r, "Worker-" + id.addAndGet(1));
			}
		});
	}

	public void start() throws BrokenBarrierException, InterruptedException {
		for (int i = 0; i < threadCnt; i++) {
			executor.execute(new Worker(i, round));
		}
		while (round > 0) {
			scheduled.schedule(() -> {
				cb.reset();
				round--;
			}, time, timeUnit);
			// 开启测试
			cb.await();
			// 等待线程跑完
			cdl.await();
			// 统计数据
			printRes();
			// 清空统计
			Arrays.fill(sc, 0);
			Arrays.fill(ec, 0);
			// 重新定义 CountDownLatch
			cdl = new CountDownLatch(threadCnt);
		}
		executor.shutdownNow();
		scheduled.shutdownNow();
		log("测试结束 \n");
	}

	/**
	 * 输出结果
	 */
	private void printRes() {
		log("第 %d 次测试 \n", round);
		long s = 0, e = 0;
		for (long cnt : sc) {
			s += cnt;
		}
		for (long cnt : ec) {
			e += cnt;
		}
		log("成功数量为: %d,失败数量为: %d \n", s, e);
	}

	public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
		final Pressure testPressure = new Pressure(10, 10, 2, TimeUnit.SECONDS, () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		testPressure.start();
	}
}


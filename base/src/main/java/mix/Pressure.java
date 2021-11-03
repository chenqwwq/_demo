package mix;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 压力测试，测试 TPS
 *
 * @author chen
 * @date 2021/11/2
 **/
public class Pressure {

	private static final int NEW = 0;
	private static final int RUNNING = 1;
	private static final int SHUTDOWN = -1;

	/**
	 * 压测次数
	 */
	private volatile int round;

	/**
	 * 压测线程数
	 */
	private int threadCnt;

	/**
	 * 压测时间
	 */
	private long time;

	/**
	 * 压测事件单位
	 */
	private TimeUnit timeUnit;

	/**
	 * success count
	 */
	private long[] sc;

	/**
	 * exception count
	 */
	private long[] ec;

	/**
	 * 当前状态
	 */
	private volatile int status = NEW;

	/**
	 * 需要执行的任务
	 */
	private Runnable runnable;

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
	private ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	class Worker implements Runnable {

		private int id;

		private int curr;

		public Worker(int id, int round) {
			this.id = id;
			this.curr = round;
		}

		@Override
		public void run() {
			// 外层的状态正确
			while (status == RUNNING) {
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
	}

	public void start() throws BrokenBarrierException, InterruptedException {
		status = RUNNING;
		for (int i = 0; i < threadCnt; i++) {
			new Thread(new Worker(i, round)).start();
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
		status = SHUTDOWN;
	}

	/**
	 * 输出结果
	 */
	private void printRes() {
		long s = 0, e = 0;
		for (long cnt : sc) {
			s += cnt;
		}
		for (long cnt : ec) {
			e += cnt;
		}
		System.out.println("成功数量为:" + s);
		System.out.println("失败数量为:" + e);
	}

	public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
		final Pressure testPressure = new Pressure(10, 10, 10, TimeUnit.SECONDS, () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		testPressure.start();
	}
}


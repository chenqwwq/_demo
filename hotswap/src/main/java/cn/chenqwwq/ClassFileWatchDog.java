package cn.chenqwwq;

import cn.chenqwwq.target.Hotswap;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author chen
 * @date 2021/7/23
 **/
public class ClassFileWatchDog {

	private static final String CLASS_FILE_SUF = ".class";

	public void watch(String path, String className) {
		ScheduledExecutor.SERVICE.scheduleAtFixedRate(new WatchTask(path, className), 0, 5, TimeUnit.SECONDS);
	}

	private static class WatchTask implements Runnable {

		private final String filePath;

		private final String path;

		private final String className;

		private HotSwapClassLoader classLoader;

		/**
		 * 文件的 md5
		 */
		private String md5;

		public WatchTask(String path, String className) {
			this.path = path;
			this.className = className;
			this.filePath = path + (path.endsWith("/") ? "" : "/") + className.replace(".", "/") + CLASS_FILE_SUF;
		}

		private String fileMd5Hex() {
			try (final FileInputStream input = new FileInputStream(filePath)) {
				return DigestUtils.md5Hex(input);
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		public void run() {
			final String md5Hex = fileMd5Hex();
			if (Objects.isNull(md5Hex) || md5Hex.equals(md5)) {
				return;
			}
			if (Objects.nonNull(this.classLoader)) {
				try {
					this.classLoader.close();
				} catch (IOException e) {
					System.out.println("原 ClassLoader 卸载失败");
				}
			}
			try {
				this.classLoader = new HotSwapClassLoader(new URL[]{new URL("file:" + path)});
				final Class<?> aClass = classLoader.loadClass(className);
				final Hotswap hotswap = (Hotswap) aClass.newInstance();
				hotswap.swap();
				final Method test = aClass.getMethod("swap");
				test.invoke(aClass.newInstance());
				this.md5 = md5Hex;
			} catch (Exception e) {
				System.out.println("类加载异常");
				// NOOP
			}
		}
	}


	private static class ScheduledExecutor {

		private static final ScheduledExecutorService SERVICE = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "WATCH-DOG-THREAD");
			}
		});

	}
}

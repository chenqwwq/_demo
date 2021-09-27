package cn.chenqwwq;

import java.net.URLClassLoader;

/**
 * @author chen
 * @date 2021/7/26
 **/
public class Trigger {

	private static final String CLASS_NAME = "cn.chenqwwq.target.HotswapDemo";
	private static final String PATH = "/home/chen/github/incubator/hotswap/src/main/java/";

	static {
		final ClassLoader classLoader = Trigger.class.getClassLoader();
		new ClassFileWatchDog().watch(PATH, CLASS_NAME, (URLClassLoader) classLoader);
	}
}

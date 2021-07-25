package cn.chenqwwq;

/**
 * @author chen
 * @date 2021/7/22
 **/
public class Main {


	private static final String CLASS_NAME = "cn.chenqwwq.target.HotswapDemo";
	private static final String PATH = "/home/chen/github/incubator/hotswap/src/main/java/";

	public static void main(String[] args) throws Exception {
		final String simpleName = Thread.currentThread().getContextClassLoader().getClass().getSimpleName();
		System.out.println(simpleName);
		new ClassFileWatchDog().watch(PATH, CLASS_NAME);
	}
}

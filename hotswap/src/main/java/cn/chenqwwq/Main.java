package cn.chenqwwq;

import java.io.File;
import java.net.URL;

/**
 * @author chen
 * @date 2021/7/22
 **/
public class Main {


	private static final String name = "cn.chenqwwq.target.Test";
	private static final String interfa = "cn.chenqwwq.target.TestInterface";

	public static void main(String[] args) throws Exception {
		HotSwapClassLoader classLoader = new HotSwapClassLoader(new URL[]{new File("/home/chen/github/incubator/hotswap/src/main/java/").toURI().toURL()});
		HotSwapClassLoader classLoader1 = new HotSwapClassLoader(new URL[]{new File("/home/chen/github/incubator/hotswap/src/main/java/").toURI().toURL()});
		final Class<?> aClass = classLoader.loadClass(name);
		final Class<?> aClass1 = classLoader1.loadClass(name);
		System.out.println(aClass1.getClassLoader());
		System.out.println(aClass.getClassLoader());
	}
}

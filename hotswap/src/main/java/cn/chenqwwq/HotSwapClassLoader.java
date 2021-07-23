package cn.chenqwwq;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 热更新的 ClassLoader
 *
 * @author chen
 * @date 2021/7/22
 **/
public class HotSwapClassLoader extends URLClassLoader {

	/**
	 * 父类加载器跳过 AppClassLoader
	 * 一般来说 main 方法都是 AppClassLoader 加载的
	 * 在不违背双亲委派的情况下使用 AppClassLoader 作为父类会导致 ClassPath 下的所有类由 AppClassLoader 加载而避开了当前的累加载器
	 *
	 * @param urls 资源地址
	 */
	public HotSwapClassLoader(URL[] urls) {
		this(urls, ClassLoader.getSystemClassLoader().getParent());
	}

	public HotSwapClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
}
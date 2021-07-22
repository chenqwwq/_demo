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
	 * 构造函数,没有父类
	 * 所有的 class 文件自己加载
	 *
	 * @param urls 资源地址
	 */
	public HotSwapClassLoader(URL[] urls) {
		super(urls, null);
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
}
package cn.chenqwwq;

import java.util.function.Supplier;

/**
 * @author chen
 * @date 2021/7/28
 **/
public class TYest {
	public void main() {
		A a = new A(123);
		((Main) (a::soutI)).main();
	}

	static class A {
		int i;

		public A(int i) {
			this.i = i;
		}

		public void soutI() {
			System.out.println(i);
		}
	}

	@FunctionalInterface
	interface Main {
		void main();
	}
}

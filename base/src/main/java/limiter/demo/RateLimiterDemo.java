package limiter.demo;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author chen
 * @date 2021/9/13
 **/
public class RateLimiterDemo {

	public static void base() {
		final RateLimiter rateLimiter = RateLimiter.create(1);
		while (true) {
			final double acquire = rateLimiter.acquire();

			System.out.println(acquire);
		}
	}

	public static void main(String[] args) {
		base();
	}
}

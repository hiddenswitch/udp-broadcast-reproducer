package udp.broadcast.reproducer;

import io.vertx.core.Verticle;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface Broadcaster extends Verticle {
	static Broadcaster create() {
		return new BroadcasterImpl(6112);
	}

	/**
	 * Retrieves the configured address used for UDP multicasting.
	 *
	 * @return
	 */
	String getMulticastAddress();

	/**
	 * Retrieves the message expected from clients to respond to.
	 *
	 * @return
	 */
	String getRequest();

	int getMulticastPort();

	String getResponse();

	AtomicBoolean isListening();
}


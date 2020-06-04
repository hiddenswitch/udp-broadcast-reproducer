package udp.broadcast.reproducer;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;

import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcasterImpl extends AbstractVerticle implements Broadcaster {
	private final String multicastAddress = "230.0.0.1";
	private final String request = "LOXX";
	private final String responsePrefix = "SPLL";
	private final AtomicBoolean listening = new AtomicBoolean();
	private final int port;
	private DatagramSocket socket;

	public BroadcasterImpl(int port) {
		this.port = port;
	}

	@Override
	public void start(Promise<Void> promise) throws Exception {
		socket = createDatagramSocket(promise);
	}

	private DatagramSocket createDatagramSocket(Promise<Void> isListening) throws SocketException {
		return vertx.createDatagramSocket(new DatagramSocketOptions()
				.setReuseAddress(true)
				.setReusePort(true))
				.listen(getMulticastPort(), "0.0.0.0", next -> {
					next.result().listenMulticastGroup(multicastAddress, HeuristicNetworkInterface.mainInterface().getName(), null, listener -> {
						if (listener.failed()) {
							isListening.fail(listener.cause());
						}
						DatagramSocket socket = listener.result();
						socket.handler(packet -> {
							if (!packet.data().getString(0, request.length()).equals(request)) {
								return;
							}

							socket.send(getResponse(), packet.sender().port(), packet.sender().host(), Promise.promise());
						});
						isListening.complete();
						listening.compareAndExchange(false, true);
					});
				});
	}

	@Override
	public void stop(Promise<Void> promise) {
		if (socket != null) {
			socket.close(promise);
		}
	}

	@Override
	public String getMulticastAddress() {
		return multicastAddress;
	}

	@Override
	public String getRequest() {
		return request;
	}

	@Override
	public int getMulticastPort() {
		return port;
	}

	@Override
	public String getResponse() {
		return responsePrefix;
	}

	@Override
	public AtomicBoolean isListening() {
		return listening;
	}
}

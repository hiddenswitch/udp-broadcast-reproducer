package udp.broadcast.reproducer;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HeuristicNetworkInterface {
	/**
	 * Heuristically retrieves the primary networking interface for this device.
	 *
	 * @return A Java {@link NetworkInterface} object that can be used by {@link io.vertx.core.Vertx}.
	 */
	static NetworkInterface mainInterface() {
		final ArrayList<NetworkInterface> interfaces;
		try {
			interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		final NetworkInterface networkInterface = interfaces.stream().filter(ni -> {
			boolean isLoopback = false;
			boolean supportsMulticast = false;
			boolean isVirtualbox = false;
			boolean isSelfAssigned = false;
			try {
				isSelfAssigned = ni.inetAddresses().anyMatch(i -> i.getHostAddress().startsWith("169"));
				isLoopback = ni.isLoopback();
				supportsMulticast = ni.supportsMulticast();
				isVirtualbox = ni.getDisplayName().contains("VirtualBox") || ni.getDisplayName().contains("Host-Only");
			} catch (IOException failure) {
			}
			final boolean hasIPv4 = ni.getInterfaceAddresses().stream().anyMatch(ia -> ia.getAddress() instanceof Inet4Address);
			return supportsMulticast && !isSelfAssigned && !isLoopback && !ni.isVirtual() && hasIPv4 && !isVirtualbox;
		}).sorted(Comparator.comparing(NetworkInterface::getName)).findFirst().orElse(null);
		return networkInterface;
	}
}

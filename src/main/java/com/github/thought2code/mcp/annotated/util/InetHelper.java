package com.github.thought2code.mcp.annotated.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Utility class for network-related operations.
 *
 * <p>This class provides helper methods for working with network interfaces and addresses. It is
 * designed to be a static utility class and cannot be instantiated.
 *
 * @author codeboyzhou
 */
public final class InetHelper {
  private InetHelper() {}

  /**
   * Finds and returns the first non-loopback IPv4 address available on the system.
   *
   * <p>This method iterates through all available network interfaces and returns the first IPv4
   * address that is not associated with a loopback, virtual, or down interface. If no suitable
   * address is found or an exception occurs, the loopback address (127.0.0.1) is returned as a
   * fallback.
   *
   * @return the first non-loopback IPv4 address, or the loopback address if none is found
   */
  public static InetAddress findFirstNonLoopbackAddress() {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface net = interfaces.nextElement();
        if (net.isLoopback() || net.isVirtual() || !net.isUp()) {
          continue;
        }

        Enumeration<InetAddress> addresses = net.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress address = addresses.nextElement();
          if (address instanceof Inet4Address) {
            return address;
          }
        }
      }
    } catch (SocketException e) {
      return Inet4Address.getLoopbackAddress();
    }
    return Inet4Address.getLoopbackAddress();
  }
}

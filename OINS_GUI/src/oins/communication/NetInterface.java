package oins.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;

public class NetInterface {

	private String[] interfaces;
	private static String[] MAC;
	private static String[] IpAddress;
	private static PcapIf device;
	private static int devId;
	private static Integer[] currIp;
	private static Integer[] currMac;

	public String[] getInterfaces() {
		return interfaces;
	}

	public NetInterface() {

		List<PcapIf> alldevs = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}

		int i = 0;
		interfaces = new String[alldevs.size()];
		MAC = new String[alldevs.size()];
		IpAddress = new String[alldevs.size()];
		JBuffer macbuf = new JBuffer(6);
		for (PcapIf device : alldevs) {
			try {
				macbuf.setByteArray(0, device.getHardwareAddress());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MAC[i] = macbuf.toHexdump().substring(6, 28);
			String ip = device.getAddresses().get(0).getAddr().toString();
			IpAddress[i] = ip.substring(7, ip.length() - 1);
			interfaces[i++] = device.getDescription();
		}

	}

	public static String getMAC(int i) {
		return MAC[i];
	}

	public static String getIpAddress(int i) {
		return IpAddress[i];
	}

	public static void setDeviceID(int id) {

		List<PcapIf> alldevs = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}
		setDevice(alldevs.get(id));
		setDevId(id);
		setCurrIp(Conversion.toInt(device.getAddresses().get(0).getAddr()
				.getData()));
		try {
			setCurrMac(Conversion.toInt(device.getHardwareAddress()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setDevice(PcapIf device) {
		NetInterface.device = device;
	}

	public static PcapIf getDevice() {
		return device;
	}

	private static void setDevId(int devId) {
		NetInterface.devId = devId;
	}

	public static int getDevId() {
		return devId;
	}

	private static void setCurrIp(Integer[] currIp) {
		NetInterface.currIp = currIp;
	}

	public static Integer[] getCurrIp() {
		return currIp;
	}

	private static void setCurrMac(Integer[] currMac) {
		NetInterface.currMac = currMac;
	}

	public static Integer[] getCurrMac() {
		return currMac;
	}

}

package oins.communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;

import oins.core.ConvFrame;
import oins.panels.ContactPanel;
import oins.panels.ConversationPanel;
import oins.panels.OtherConvPanel;
import oins.panels.SendArpPanel;
import oins.tables.ContactTable;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Arp;

public class ArpListener extends Thread {

    private static final Arp arp = new Arp();

    private Pcap pcap;
    JPacketHandler<String> jpacketHandler;
    private static int pid;
    private static boolean recieving;
    private static boolean sending;
    private static boolean running, closePcap;
    private static String currIp;
    private static Integer[] currIpInt;

    public ArpListener() {

        StringBuilder errbuf = new StringBuilder();

        int snaplen = 61; // capture packet less than 61B
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
       
        int timeout = 1;
        setPcap(Pcap.openLive(NetInterface.getDevice().getName(), snaplen, flags, timeout, errbuf));

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: " + errbuf.toString());
            return;
        }

        PcapBpfProgram program = new PcapBpfProgram();
        String expression = "ether proto \\arp";

        int optimize = 0; // 0 = false
        int netmask = Conversion.netmask(NetInterface.getDevice().getAddresses().get(0).getNetmask().getData());

        if (pcap.compile(program, expression, optimize, netmask) != Pcap.OK) {
            System.err.println(pcap.getErr());
            return;
        }

        if (pcap.setFilter(program) != Pcap.OK) {
            System.err.println(pcap.getErr());
            return;
        }

        JPacketHandler<String> jHandler = new JPacketHandler<String>() {

            public void nextPacket(JPacket packet, String user) {

                try {
                    if (packet.hasHeader(arp)) {
                        for (Integer[] temp : ContactPanel.getIpAdressesInt()) {
                            // najpierw opcja : Arp do nas od kogos z listy
                            if (Conversion.equal(arp.spa(), Conversion.convert(temp))
                                    && Conversion.equal(arp.tpa(), Conversion.convert(NetInterface.getCurrIp()))) {
                                ArpPacketDecoding dec = new ArpPacketDecoding(packet);
                       
                                setPid(dec.checkPID());

                                if (getPid() == 1 || getPid() == 2) {
                                    SendArpPanel.setSendPid(getPid());
                                    setRecieving(true);
                                    if (isSending() == false) {
                                       
                                        ConversationPanel.setUserName(ContactTable.searchUserName(temp));
                                        int response = JOptionPane.showConfirmDialog(null, "Czy akceptujesz rozmowe od u�ytkownika: "
                                                + ConversationPanel.getUserName(), "Question", JOptionPane.YES_NO_OPTION);
                                        if (response == JOptionPane.YES_OPTION) {
                                            setCurrIp(Conversion.toString(temp));
                                            setCurrIpInt(temp);

                                            ConvFrame.create();

                                        } else
                                            return;

                                    } else {
                                        setCurrIp(Conversion.toString(temp));
                                        setCurrIpInt(temp);
                                        SendArpPanel.setTxtF2("Odebrano pakiet Arp");
                                        SendArpPanel.setTxtF1("Wys�ano pakiet Arp");
                                        SendArpPanel.getBut1().setEnabled(true);
                                        if (getPid() == 1) {
                                            SendArpPanel.setTxtF3("TCP");
                                        } else if (getPid() == 2) {
                                            SendArpPanel.setTxtF3("ICMP");
                                        }
                                    }

                                } else if (getPid() == 3) {
                                    SendArpPanel.setSendPid(getPid());
                                   
                                    ContactTable.updateRowAvail("Dostepny", ContactTable.searchColumnNumber(temp));

                                    ArpPacket arpPacket;
                                    try {
                                        arpPacket = new ArpPacket(temp, 4);
                                        arpPacket.sendArp();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else if (getPid() == 4) {
                                    SendArpPanel.setSendPid(getPid());
                                    ContactTable.updateRowAvail("Dostepny", ContactTable.searchColumnNumber(temp));

                                } else {
                                
                                }

                            }

                        }
                        // opcja : Arp do kogos z listy
                        for (Integer[] temp : ContactPanel.getIpAdressesInt()) {
                            if (Conversion.equal(arp.tpa(), Conversion.convert(temp))
                                    && !(Conversion.equal(Conversion.convert(NetInterface.getCurrIp()), Conversion.convert(temp)))) {
                                // sprawdzamy czy jest on wysylany od kogos z
                                // listy
                                for (Integer[] temp2 : ContactPanel.getIpAdressesInt()) {
                                    if (Conversion.equal(arp.spa(), Conversion.convert(temp2))) {

                                        ArpPacketDecoding dec = new ArpPacketDecoding(packet);
                                        int pidd = dec.checkPID();
                                        String protSend;
                                        String ipSend = Conversion.toString(Conversion.toInt(arp.spa()));
                                        String ipRec = Conversion.toString(Conversion.toInt(arp.tpa()));
                                        if (pidd == 1) {
                                            protSend = "TCP";
                                            OtherConvPanel.addRow(ipSend, ipRec, protSend);
                                        } else if (pidd == 2) {
                                            protSend = "ICMP";
                                            OtherConvPanel.addRow(ipSend, ipRec, protSend);
                                        } else {
                                            // protSend="Nieokreslony";
                                        }

                                    }
                                }

                            }
                        }

                    }

                } catch (NoSuchAlgorithmException e) {
                    JOptionPane.showMessageDialog(null, "NIE UDALO SIE ODCZYTAC PAKIETU!", "Error", JOptionPane.ERROR_MESSAGE);
                    // e.printStackTrace();
                }

            }
        };
        setJpacketHandler(jHandler);

    }

    @Override
    public void run() {
        setRunning(true);
        setClosePcap(false);
        while (isRunning()) {
            // ilosc pakietow
            System.out.println("Dzialam");
            getPcap().loop(100000, getJpacketHandler(), "OINS");
            System.out.println("Koniec");
            if (isClosedPcap()) {
                pcap.close();
            }
        }

    }

    public Pcap getPcap() {
        return pcap;
    }

    private void setPcap(Pcap pcap) {
        this.pcap = pcap;
    }

    public JPacketHandler<String> getJpacketHandler() {
        return jpacketHandler;
    }

    private void setJpacketHandler(JPacketHandler<String> jpacketHandler) {
        this.jpacketHandler = jpacketHandler;
    }

    private static void setPid(int pid) {
        ArpListener.pid = pid;
    }

    public static int getPid() {
        return pid;
    }

    public static void setSending(boolean sending) {
        ArpListener.sending = sending;
    }

    public static boolean isSending() {
        return sending;
    }

    public static void setRecieving(boolean recieving) {
        ArpListener.recieving = recieving;
    }

    public static boolean isRecieving() {
        return recieving;
    }

    public static String getCurrIp() {
        return currIp;
    }

    private static void setCurrIp(String currIp) {
        ArpListener.currIp = currIp;
    }

    public static Integer[] getCurrIpInt() {
        return currIpInt;
    }

    private static void setCurrIpInt(Integer[] currIpInt) {
        ArpListener.currIpInt = currIpInt;
    }

    public static void setRunning(boolean running) {
        ArpListener.running = running;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setClosePcap(boolean closePcap) {
        ArpListener.closePcap = closePcap;
    }

    public static boolean isClosedPcap() {
        return closePcap;
    }

}

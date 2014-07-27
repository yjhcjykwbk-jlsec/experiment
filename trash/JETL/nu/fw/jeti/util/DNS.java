package nu.fw.jeti.util;
/**
 * Class for SRV record handling, needs the dnsjava package (see dnsjava.org)
 * to enable SRV handling uncomment the SRV code in nu.fw.jeti.backend.Connect
 * (Does not work in windows) 
 */


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DNS {
	private static Comparator srvComparator = new SRVRecordComparator();	
	
    private static class SRVRecordComparator implements Comparator {
        private final static Random random = new Random();
        public int compare (Object a, Object b) {
            int pa = ((SRVRecord)a).getPriority();
            int pb = ((SRVRecord)b).getPriority();
            return (pa == pb) ? (512 - random.nextInt(1024)) : pa - pb;
        }
    }		
    
    private static String allowIPLiteral(String host) {
        if ((host.charAt(host.length() - 1) == '.')) {
            String possible_ip_literal = host.substring(0, host.length() - 1);
            if (org.xbill.DNS.Address.isDottedQuad(possible_ip_literal)) {
                host = possible_ip_literal;
            }
        }
        return host;
    }    
    
    private static InetAddress getByName(String host) throws UnknownHostException {
        return org.xbill.DNS.Address.getByName(allowIPLiteral(host));
    }
    
	public static String[] findSRVRecords(String hostname) {
		String srvhost;
		Record [] answers;		
		
		String srvstr = "_xmpp-client._tcp." + hostname;
		
		String[] servers = null;
		
		try {			
			answers = new Lookup(srvstr, Type.SRV).run();
			if (answers != null) {
	            SRVRecord srvAnswers[] = new SRVRecord[answers.length];
	            for (int i = 0; i < answers.length; i++) {
	                srvAnswers[i] = (SRVRecord)answers[i];
	            }				
	            Arrays.sort(srvAnswers, srvComparator);
	            servers = new String[srvAnswers.length];
	            for (int i = 0; i < srvAnswers.length; i++) {
	                servers[i] = srvAnswers[i].getTarget().toString();
	                System.out.println("Found SRV record " + srvAnswers[i].getTarget().toString());
	            }	            
	            return servers;
			}
			else {
				return null;
			}
		} catch (TextParseException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}  finally {			
            if (servers == null) {
                System.out.println("Couldn't resolve SRV records for domain " + hostname);               
                try {
                    getByName(hostname);
                    servers = new String[1];
                    servers[0] = hostname;
                } catch (UnknownHostException uhe) {
                    System.out.println("Couldn't resolve IP address for host " + hostname);
                }
            }			
		} 
		return null;	
	}	
}
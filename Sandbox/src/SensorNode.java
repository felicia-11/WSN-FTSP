import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.radio.ieee_802_15_4.FrameIO;
import com.virtenio.radio.ieee_802_15_4.RadioDriver;
import com.virtenio.radio.ieee_802_15_4.RadioDriverFrameIO;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.driver.device.at86rf231.*;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.vm.Time;

/**
 * Kelas SensorNode merepresentasikan node-node sensor biasa dalam WSN.
 * @author Felicia Christiany / 2015730006
 */
public class SensorNode {
	
	// Atribut myRootID menunjukkan ID dari node sensor yang menjadi root
	private int myRootID;
	
	// Atribut highestSeqNum digunakan untuk mencegah menerima pesan duplikat
	private int highestSeqNum;
	
	// Atribut timeTable menyimpan nilai-nilai reference point
	private long[][] timeTable;
	
	// Atribut numEntries menyatakan baris akhir tabel yang telah terisi
	private int numEntries;

	// Atribut tableLength menyatakan banyak referensi yang dapat ditampung tabel
	private int tableLength = 3;

	// Atribut isSync menyatakan status sinkronisasi node sensor
	private boolean isSync;

	// Atribut ADDR_NODE menyatakan alamat milik node sensor
	private final int ADDR_NODE  = PropertyHelper.getInt("remote.addr",0xAFAF);
//	private final int ADDR_NODE  = PropertyHelper.getInt("remote.addr",0xBEBA);
//	private final int ADDR_NODE  = PropertyHelper.getInt("remote.addr",0xEFEF);
//	private final int ADDR_NODE  = PropertyHelper.getInt("remote.addr",0xAFFE);
//	private final int ADDR_NODE  = PropertyHelper.getInt("remote.addr",0xBABE);

	// Atribut BROADCAST menyatakan alamat untuk broadcast
	private final int BROADCAST = PropertyHelper.getInt("remote.addr",0xFFFF);
	
	// Atribut COMMON_PANID menyatakan alamat jaringan PAN yang digunakan
	private final int COMMON_PANID = PropertyHelper.getInt("radio.panid",0xCABE);
	
	/**
	 * Konstruktor untuk kelas SensorNode.
	 * @param length panjang maksimum tabel referensi
	 */
	public SensorNode() {
		this.myRootID = Integer.MAX_VALUE;
		this.highestSeqNum = Integer.MIN_VALUE;
		this.timeTable = new long[this.tableLength][2];
		this.numEntries = 0;
		this.isSync = false;
	}
	
	/**
	 * Method untuk menginisialisasi jaringan radio yang akan digunakan.
	 * dan memanggil method receive(frameIO) agar node sensor siap menerima pesan.
	 */
	public void run() {
		try {
			System.out.println("Nilai waktu mula-mula '" + Integer.toHexString(ADDR_NODE) + "' : " + Time.currentTimeMillis());
			System.out.println("--------------------------------------");
			final AT86RF231 radio = RadioInit.initRadio();
			radio.open();
			radio.setAddressFilter(COMMON_PANID,ADDR_NODE,ADDR_NODE,false);
			final RadioDriver driver = new AT86RF231RadioDriver(radio);
			final FrameIO frameIO = new RadioDriverFrameIO(driver);
			receive(frameIO);
		}
		catch(Exception e) {}
	}
	
	/**
	 * Method untuk menerima pesan.
	 * Method menggunakan pengulangan while(true) sehingga node sensor akan selalu siap menerima pesan.
	 */
	public void receive(final FrameIO frameIO) throws Exception{
		Thread reader = new Thread() {
			@Override
			public void run() {
				while(true) {
					Frame f = null;
					try {
						f = new Frame();
						frameIO.receive(f);
					}
					catch(Exception e) {}
					if(f!=null) {
						byte[] dg = f.getPayload();
						String message = new String(dg, 0, dg.length);
						String time = "";
						boolean isTime = false;
						// Memisahkan isi pesan
						for(int i=0;i<message.length();i++) {
							if(isTime && message.charAt(i) != ',') {
								time += message.charAt(i);
							}
							else if(message.charAt(i) == ',') {
								isTime = true;
							}
						}
						int rootID = Integer.parseInt(message.substring(0, 1));
						long globalTime = Long.parseLong(time);
						processSynchronizationMessage(frameIO, rootID, f.getSequenceNumber(), globalTime);							
					}
				}
			}
		};
		reader.start();
	}
	
	/**
	 * Method untuk memproses pesan sinkronisasi.
	 * @param frameIO FrameIO yang akan digunakan untuk mengirimkan pesan.
	 * @param rootID ID milik root yang tersimpan dalam pesan.
	 * @param seqNum nomor pesan untuk memastikan node sensor tidak menerima pesan duplikat.
	 * @param timestamp nilai waktu global yang didapat dari root atau node sensor tersinkronisasi.
	 */
	public void processSynchronizationMessage(final FrameIO frameIO, int rootID, int seqNum, long globalTime) {
		// Pesan diterima
		if(rootID <= this.myRootID && seqNum > this.highestSeqNum) {
			// Mengganti dengan nilai terbaru
			this.myRootID = rootID;
			this.highestSeqNum = seqNum;
			// Jika nilai index belum mencapai batas ketentuan, maka reference point baru akan disimpan
			if(numEntries < this.tableLength) {
				this.timeTable[numEntries][0] = globalTime;
				this.timeTable[numEntries][1] = Time.currentTimeMillis();
				System.out.println("(" + this.highestSeqNum + ") Waktu global : " + this.timeTable[numEntries][0]);
				System.out.println("(" + this.highestSeqNum + ") Waktu lokal  : " + this.timeTable[numEntries][1]);
				System.out.println("Selisih waktu : " + Math.abs(this.timeTable[numEntries][0]
						- this.timeTable[numEntries][1]) + " ms");
				this.numEntries++;
				if(this.isSync) {
					broadcastSynchronizationMessage(frameIO);
				}
			}
			// Jika nilai index telah melebihi batas ketentuan, maka nilai tabel akan diestimasi
			if(numEntries >= this.tableLength) {
				long estimatedDifference = 0;
				long totalDifference = 0;
				for(int i=0;i<this.tableLength;i++) {
					totalDifference += this.timeTable[i][0] - this.timeTable[i][1];
				}
				estimatedDifference = totalDifference / this.tableLength;
				long difference = Time.currentTimeMillis() + estimatedDifference - this.timeTable[this.tableLength-1][0];
				// Memastikan penambahan waktu terhadap waktu lokal tidak menyebabkan pembesaran perbedaan nilai
				if(difference < 20 && difference > (-20)) {
					Time.setCurrentTimeMillis(Time.currentTimeMillis() + estimatedDifference);
					System.out.println("Node sensor melakukan sinkronisasi.");
					System.out.println("Nilai waktu saat ini : " + Time.currentTimeMillis());
					if(!this.isSync) {
						System.out.println("** Node sensor dapat mulai mengirimkan pesan sinkronisasi. **");
					}
					this.isSync = true;
				}
				this.timeTable = new long[this.tableLength][2];
				this.numEntries = 0;
				System.out.println("--------------------------------------");
			}
		}
		// Pesan ditolak karena serupa, pernah diterima sebelumnya
		else {
			System.out.println("Pesan ditolak.");
		}
	}
	
	/**
	 * Method untuk melakukan broadcast pesan sinkronisasi.
	 * Method hanya dapat dipanggil saat sudah menerima referensi sebanyak tableLength.
	 */
	public void broadcastSynchronizationMessage(final FrameIO frameIO){
		try {
			Frame frame = new Frame(Frame.TYPE_DATA | Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.SRC_ADDR_16);
			frame.setSrcAddr(ADDR_NODE);
			frame.setDestAddr(BROADCAST);
			frame.setDestPanId(COMMON_PANID);
			frame.setSequenceNumber(highestSeqNum);
			String message = myRootID + "," + Time.currentTimeMillis();
			frame.setPayload(message.getBytes());
			frameIO.transmit(frame);
		}
		catch(Exception e) {}
	}

	/**
	 * Method yang akan dijalankan pertama kali saat node sensor menyala.
	 */
	public static void main(String[] args) {
		SensorNode node = new SensorNode();
		node.run();
	}

}
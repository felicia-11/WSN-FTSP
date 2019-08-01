import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.radio.ieee_802_15_4.FrameIO;
import com.virtenio.radio.ieee_802_15_4.RadioDriver;
import com.virtenio.radio.ieee_802_15_4.RadioDriverFrameIO;
import com.virtenio.preon32.examples.common.USARTConstants;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.driver.device.at86rf231.*;
import com.virtenio.driver.usart.NativeUSART;
import com.virtenio.driver.usart.USARTParams;
import com.virtenio.driver.usart.USART;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.vm.Time;
import java.io.OutputStream;

/**
 * Kelas Root merepresentasikan node sensor root dalam WSN
 * @author Felicia Christiany / 2015730006
 */
public class Root {
	
	// Atribut myID merepresentasikan ID node sensor
	private int myID;
	
	// Atribut seqNum merepresentasikan nomor pesan
	private int seqNum;

	// Atribut stopper menunjukkan status broadcast root
	private boolean stopper;
	
	// Atribut usart digunakan untuk memanggil fungsi-fungsi kelas USART
	private USART usart;
	
	// Atribut data berisi sebuah String untuk dikirimkan pada PC
	private static volatile String data;
	
	// Atribut out digunakan untuk menuliskan data ke PC
	private static OutputStream out;
	
	// Atribut ROOT_ADDR berisi alamat root atau base station
	private final int ROOT_ADDR = PropertyHelper.getInt("local.addr",0xBAFE);
	
	// Atribut BROADCAST berisi alamat untuk broadcast
	private final int BROADCAST = PropertyHelper.getInt("remote.addr",0xFFFF);
	
	// Atribut COMMON_PANID berisi alamat jaringan PAN yang digunakan
	private final int COMMON_PANID = PropertyHelper.getInt("radio.panid",0xCABE);
	
	/**
	 * Konstruktor untuk kelas Root.
	 */
	public Root() {
		this.myID = 1;
		this.seqNum = 1;
		this.stopper = false;
	}

	/**
	 * Method untuk menginisialisasi atribut usart.
	 */
	public void useUSART() throws Exception{
		usart = configUSART();
	}
	
	/**
	 * Method untuk mengatur koneksi antara node sensor root dengan antarmuka.
	 */
	private USART configUSART() throws Exception{
		int instanceID = 0;
		USARTParams params = USARTConstants.PARAMS_115200;
		NativeUSART usart = NativeUSART.getInstance(instanceID);
		try {
			usart.close();
			usart.open(params);
			return usart;
		}
		catch(Exception e) {
			return null;
		}
	}
	
	/**
	 * Method untuk menginisialisasi jaringan radio yang akan digunakan.
	 * Method akan selalu siap menerima masukan dari user melalui antarmuka.
	 */
	public void run() {
		try {
			final AT86RF231 radio = RadioInit.initRadio();
			radio.open();
			radio.setAddressFilter(COMMON_PANID,ROOT_ADDR,ROOT_ADDR,false);
			final RadioDriver driver = new AT86RF231RadioDriver(radio);
			final FrameIO frameIO = new RadioDriverFrameIO(driver);
			boolean isRun = true;
			out = usart.getOutputStream();
			
			while(isRun) {
				int choice = usart.read();
				// Memulai penyebaran pesan sinkronisasi
				if(choice == 1) {
					if(!stopper) {
						stopper = true;
						startSynchronization(frameIO);
					}
				}
				// Menghentikan penyebaran pesan sinkronisasi
				else if(choice == 2) {
					stopper = false;
				}
				// Mematikan node sensor root
				else if(choice == 0) {
					stopper = false;
					isRun = false;
				}
				// Tidak melakukan apa-apa jika masukan user salah
				else { /**DO NOTHING*/ }
			}
		}
		catch(Exception e) {}
	}
	
	/**
	 * Method untuk melakukan penyebaran pesan sinkronisasi.
	 * Penyebaran pesan dilakukan setiap 1.5 detik sekali.
	 */
	public void startSynchronization(final FrameIO frameIO) throws Exception{
		Thread syncThread = new Thread() {
			@Override
			public void run() {
				while(stopper) {
					try {
						Frame frame = new Frame(Frame.TYPE_DATA | Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.SRC_ADDR_16);
						frame.setSrcAddr(ROOT_ADDR);
						frame.setDestAddr(BROADCAST);
						frame.setDestPanId(COMMON_PANID);
						frame.setSequenceNumber(seqNum);
						String message = myID + "," + Time.currentTimeMillis();
						frame.setPayload(message.getBytes());
						frameIO.transmit(frame);
						data = seqNum + "," + Time.currentTimeMillis() + ",";
						out.write(data.getBytes(), 0, data.length());
						Thread.sleep(1500);
						seqNum++;
					}
					catch(Exception e) {}
				}
			}
		};
		syncThread.start();
	}
	
	/**
	 * Method yang akan dijalankan pertama kali saat root menyala.
	 */
	public static void main(String[] args) {
		Root root = new Root();
		try {
			root.useUSART();
		}
		catch(Exception e) {}
		root.run();
	}
	
}

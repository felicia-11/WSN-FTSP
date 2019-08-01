import com.virtenio.commander.toolsets.preon32.Preon32Helper;
import com.virtenio.commander.io.DataConnection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Project;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;

public class CMD{
	
	/**
	 * Atribut sc berfungsi agar user dapat memasukkan input pada antarmuka
	 */
	private Scanner sc;
	
	/**
	 * Atribut date berfungsi untuk mengatur dan memformat nilai waktu dalam bentuk milidetik
	 */
	private Date date;
	
	/**
	 * Atribut isRun menandakan jika proses penyebaran pesan sinkronisasi masih berlangsung
	 */
	private boolean isRun;
	
	/**
	 * Atribut isSync menandakan jika menu 1 sedang berjalan
	 */
	private boolean isSync;
	
	/**
	 * Konstruktor untuk kelas CMD.
	 */
	public CMD() {
		this.isRun = false;
		this.isSync = false;
		this.date = new Date();
	}
	
	/**
	 * Method untuk mengeluarkan log yang dijalankan ant script pada console.
	 */
	private static DefaultLogger getConsoleLogger() {
		DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
        return consoleLogger;
	}
	
	/**
	 * Method untuk memasuki salah satu context sesuai dengan parameter.
	 * @param context merupakan nomor context yang ingin dipanggil.
	 */
	private void setContext(String context) throws Exception{
		DefaultLogger consoleLogger = getConsoleLogger();
		File buildUser = new File("D:\\Sandbox\\buildUser.xml");
		Project antProject = new Project();
		antProject.setUserProperty("ant.file", buildUser.getAbsolutePath());
		antProject.addBuildListener(consoleLogger);
		try {
			antProject.fireBuildStarted();
			antProject.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			antProject.addReference("ant.ProjectHelper", helper);
			helper.parse(antProject, buildUser);
			antProject.executeTarget(context);
			antProject.fireBuildFinished(null);
		}
		catch(BuildException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method untuk mensinkronisasikan waktu di node sensor root dengan perangkat yang terhubung.
	 */
	private void synchronizeRoot() throws Exception{
		DefaultLogger consoleLogger = getConsoleLogger();
		File build = new File("D:\\Sandbox\\build.xml");
		Project antProject = new Project();
		antProject.setUserProperty("ant.file", build.getAbsolutePath());
		antProject.addBuildListener(consoleLogger);
		try {
			antProject.fireBuildStarted();
			antProject.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			helper.parse(antProject, build);
			antProject.executeTarget("cmd.time.synchronize");
			antProject.fireBuildFinished(null);
		}
		catch(BuildException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method untuk menginisiasi hubungan antara antarmuka dengan root.
	 * Nilai port harus sesuai dengan yang terpasang oleh root.
	 * Setelah terhubungkan, user dapat memasukkan input yang diberikan melalui command line.
	 */
	public void init() throws Exception{
		try {
			Preon32Helper nodeHelper = new Preon32Helper("COM5",115200);
			DataConnection connection = nodeHelper.runModule("root");
			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
			int menu = -1;
			sc = new Scanner(System.in);
			connection.flush();
			
			do {
				try {
					System.out.println("Pilih menu yang Anda inginkan :");
					System.out.println("1. Mulai sinkronisasi");
					System.out.println("2. Berhenti sinkronisasi");
					System.out.println("0. Keluar dari program");
					System.out.print("Pilihan Anda : ");
					
					menu = sc.nextInt();
					connection.write(menu);
					
					// Menghentikan program
					if(menu == 0) {
						System.out.println("Program selesai.");
						isRun = false;
						break;
					}
					// Memberi perintah mulai sinkronisasi pada root
					else if(menu == 1) {
						isRun = true;
						if(!isSync) { // Mencegah agar tidak membuat thread baru
							isSync = true;
							Thread syncThread = new Thread() {
								@Override
								public void run() {
									try {
										while(isRun) { // Menjaga thread agar tetap berjalan
											Thread.sleep(100);
											byte[] buffer = new byte[1024];
											while(in.available() > 0) {
												in.read(buffer);
												String result = new String(buffer);
												connection.flush();
												String[] log = result.split(",");
												date.setTime(Long.parseLong(log[1]));
												String dateResult = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(date);
												System.out.println("(" + log[0] + ") Terkirim : " + dateResult); 
											}
										}
										// Membuka kembali peluang untuk membuat thread
										if(!isRun) {
											isSync = false;
										}
									}
									catch(NumberFormatException nfe) {
										try {
											connection.write(2);
											isRun = false;
											isSync = false;
											System.out.println("Root masih memproses pesan sebelumnya. Silakan kirim ulang menu 1.");
										}
										catch(Exception e){
											e.printStackTrace();
										}
									}
									catch(Exception e) {
										e.printStackTrace();
									}
								}
							};
							syncThread.start();
						}
						else {
							System.out.println("Penyebaran pesan sinkronisasi masih dilakukan.");
						}
					}
					// Memberi perintah berhenti melakukan sinkronisasi
					else if(menu == 2) {
						isRun = false;
						System.out.println("Berhenti melakukan sinkronisasi.");
					}
					else {
						System.out.println("Tidak tersedia menu yang Anda pilih.");
					}
					Thread.sleep(200);
					System.out.println();
				}
				catch(InputMismatchException ime) {
					sc.nextLine();
					System.out.println("Format masukan tidak sesuai.\n");
					continue;
				}
			}
			while(menu != 0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method yang akan dijalankan pertama kali saat program dijalankan.
	 * Waktu root akan terlebih dulu disinkronisasikan dengan waktu
	 * milik perangkat yang menjadi antarmuka.
	 */
	public static void main(String[] args) throws Exception{
		CMD cmd = new CMD();
		cmd.setContext("context.set.1");
		cmd.synchronizeRoot();
		cmd.init();
	}	
}

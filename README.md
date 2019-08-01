# WSN-FTSP

## Profil

Nama : Felicia Christiany

Skripsi : Pengembangan Sinkronisasi Waktu di Wireless Sensor Network dengan Algoritma Flooding Time Synchronization Protocol

Dosen Pembimbing : Elisati Hulu, M.T.

## Deskripsi

Tujuan Skripsi :
  * Membangun perangkat lunak yang dapat mensinkronisasikan waktu setiap node sensor dalam _Wireless Sensor Network_ (WSN) dengan menggunakan _Flooding Time Synchronization Protocol_.
  * Membandingkan hasil analisis performa algoritma _Flooding Time Synchronization Protocol_ dibandingkan dengan _Reference Broadcast Synchronization_.
  
Batasan Perangkat Lunak :
  * Hanya menangani _delay_ yang timbul pada _application layer_, _presentation layer_, dan _session layer_.
  * Hanya mengestimasi _clock offset_.
  * Node _root_ ditetapkan sejak awal dan diasumsikan selalu menyala karena terhubung langsung dengan antarmuka.
  
## Software Requirement

1. Eclipse IDE
2. Java JDK & JRE
3. Apache Ant

## Hardware Requirement

1. Node sensor _Preon32_ atau _Preon32 Shuttle_
2. Kabel USB Tipe A
3. Antarmuka (laptop/komputer) sebanyak daerah persebaran yang diperlukan

## Cara Menjalankan Perangkat Lunak

1. Unggah kode program pada node sensor _root_ :</br>
   a. Buka **context1.properties** pada folder **config**.</br>
   b. Sesuaikan nilai **comport** dengan nomor _port_ node sensor (dapat dilihat pada "Device Manager -> Ports (COM & LPT)").</br>
   c. Pilih menu **context.set.1** pada Apache Ant bagian _Preon32 Sandbox User_ untuk masuk pada konteks tersebut.</br>
   d. Pilih menu **.all** pada Apache Ant bagian _Preon32 Sandbox_ untuk mengunggah kode program _Root_ pada node sensor.
2. Unggah kode program pada node sensor biasa :</br>
  a. Buka **context2.properties** atau # seterusnya pada **config**.</br>
  b. Sesuaikan nilai **comport** dengan nomor _port_ node sensor.</br>
  c. Pilih menu **context.set.2** atau # seterusnya pada Apache Ant bagian _Preon32 Sandbox User_ untuk masuk pada konteks tersebut.</br>
  d. Pilih menu **.all** pada Apache Ant bagian _Preon32 Sandbox_ untuk mengunggah kode program _SensorNode_ pada node sensor.
3. Sesuaikan nomor _port_ node sensor _root_ pada **CMD.java** (line 109).
4. Klik kanan pada project **Sandbox** dan pilih menu Export.
5. Pilih "Java -> Runnable JAR file".
6. Pilih "CMD - CommandLine" pada menu _Launch configuration_.
7. Tempatkan hasil _jar_ pada folder yang diinginkan melalui menu _Export destination_.
8. Setelah _jar_ terbentuk, perangkat lunak dapat dijalankan melalui _command prompt_.</br>
   Contoh jika nama file "Commander.jar" : **java -jar Commander.jar**
  
## Fitur Perangkat Lunak

1. Memulai proses penyebaran pesan sinkronisasi oleh _root_ secara kontinu setiap 1.5 detik.</br>
   Perintah untuk memanggil fitur : **1**
2. Memberhentikan proses penyebaran pesan sinkronisasi oleh _root_.</br>
   Perintah untuk memanggil fitur : **2**
3. Keluar dari program dan mematikan node sensor _root_.</br>
   Perintah untuk memanggil fitur : **0**

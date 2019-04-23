# CRDT Collab
## Deskripsi
CRDT Collab adalah real time text editor untuk kolaborasi. Made with love using Java.

## Installation
1. Clone this repository.
2. Run the server. (SignalCommand)
3. Run the client. (ControllerNode)
4. Start Collaborate in real time.

## Dependencies
* Java WebSocket
* JavaFX
* GSON
* Passion to Java

## Collaborators
1. Muhammad Sulthan Adhipradhana : websocket, peer-to-peer, CRDT (33%)
2. Christian Jonathan : Laporan, Desain dan implementasi datatype CharInfo, Operation, CRDT (33%)
3. Hafizh Budiman : GUI(JavaFX), CRDT, Desain dan implementasi datatype CharInfo, Operation (33%)
4. Jokowi : Presiden RI (1%)

# Laporan

## Cara kerja dan desain program
### Penjelasan Singkat
Program bekerja dengan menggunakan satu signaling server yang berguna sebagai penyatu dari beberapa node (editor). ketika node-node tersebut melakukan perubahan pada editor localnya, mereka mengirimkan CRDT(akan dijelaskan lebih lanjut) ke node-node lain dan lalu node-node itu akan diupdate sehingga semua node sama. Adapun GUI dari editor diimplementasikan dengan menggunakan javaFX. GUI diintegrasikan dengan program CRDT dengan memanfaatkan websocket untuk passing data
### Desain Program
* CharInfo adalah class yang berisikan value dari character, siteId (ID dari node), array integer position (posisi char pada editor) dan counter (jumlah operasi suatu node). Kelas ini digunakan sebagai basis datatype yang akan di-passing di CRDT.
* Operation adalah class yang berisikan CharInfo sebagai data, tipe operasi (operationType) dan VersionVector. Operation adalah datatype yang di-passing oleh CRDT.
* VersionVector menyimpan 2 data, yaitu version node lain dan version local yang nantinya digunakan sebagai validasi jumlah operasi setiap node sehingga dengan pasti setiap node mendapatkan value yang ter-update.
* CRDT merupakan class dengan isi siteId, list of CharInfo sebagai data, VersionVector, ServerPeerNode dan text. CRDT digunakan untuk passing data dari 1 node ke node lain. 
* ServerPeerNode merupakan class yang digunakan sebagai server dalam koneksi peer-to-peer, kelas ini digunakan untuk broadcast CRDT dari 1 node ke node-node lain 
* ClientPeerNode merupakan class yang digunakan sebagai client dalam koneksi peer-to-peer, kelas ini digunakan untuk menerima broadcast CRDT dari ServerPeerNode node lain
* SignalCommand merupakan class yang digunakan untuk memerintah SignalServer
* SignalServer merupakan penyatu utama yang memenfaatkan websocket untuk membuat koneksi peer-to-peer (menyimpan address server)

## Fungsi CRDT, Version Vector dan Deletion Buffer
### CRDT
CRDT digunakan untuk melakukan insert / delete dari node lokal lalu melakukan broadcast CRDT nya ke node-node lain yang akan dibaca lalu melakukan update untuk mendapatkan value ter-update
### Version Vector
Version Vector digunakan untuk mendapatkan jumlah operasi yang dilakukan oleh masing-masing node(dilihat dari siteId-nya). Hal ini digunakan agar setiap node dapat melakukan validasi value agar setiap node memiliki value ter-update
### Deletion Buffer
Deletion Buffer digunakan untuk memberikan sifat yang komutatif pada deletion. Operasi-operasi deletion itu akan disimpan pada Deletion Buffer yang nantinya baru dilakukan setelah insert sudah dilakukan semua.

## Analisis Program
Walaupun program berjalan dengan baik, namun terdapat solusi yang dapat memberikan performa yang lebih bagus lagi, terutama ketika value yang diberikan banyak. Salah satu solusi-nya adalah dengan memberikan posisi character menjadi semacam matriks, sehingga saat melakukan pencarian posisi suatu character kompleksitasnya dari O(N) menjadi O(1).

## Hasil Screenshot

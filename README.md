# Microkafka - ,EDA tabanlı mikroservis örneği

Bu kod örneğinde en basit haliyle bir olay tabanlı mikroservis uygulaması bulacaksınız.

## Kurulum

### Altyapının ayağa kaldırılması

1- AWS'den Ubuntu 18.04 işletim sistemli en az 4vCpu - 16 GB Ram'ki bi sunucu kiralanır. Örneğin: t2.xlarge
2- 8 GB disk eklenir
3- Güvenlik ayarları tüm portlara izin verilecek şekilde ayarlanır. (Gerçek hayatta tavsiye etmiyoruz, sadece sorunsuz deneme için bu seferlik...)
4- Son aşamada yeni bir keyPair dosyası oluşturulur. Tip olarak PPK dosyası tercih edilerek keyPair dosyasına isim verilir ve dosya download edilir. (Bu dosya az sonra girişte lazım olacak)

### Yazılımsal kurulumlar

1- Kiralanan sunucuya MobeXterm uygulaması ile SSH üzerinden bağlanılır. Bağlanırken bağlantı tipinin SSH, kullanıcı adının Ubuntu ve auth dosyası az önce indirdiğiniz PPK dosyası olacak şekilde giriş yapılır.
2- Ubuntu repo temizliği için aşağıdaki komut çalıştırılır:
```
sudo apt-get update
```
3- Docker kurulumu aşağıdaki komut ile yapılır. Kurulum esnasında gelen soruya Y cevabı verilerek ilerlenir:
```
sudo apt-get install docker.io
```
4- Docker kurulumunu kontrol etmek için aşağıdaki komut çalıştırılır. Ekrana boş bir tablo ve başlıklarının düşmesi beklenir:
```
sudo docker ps
```
4- Kafka kurulum komutu için sunucunun AWS konsolundan 'public IPv4' bilgisi alınır. Örneğin bu public IP'miz 11.22.33.44 ise aşağıdaki komutu hemen çalıştırmamız gerekir:
```
docker run --rm --net=host -e ADV_HOST=11.22.33.44 lensesio/fast-data-dev
```
5- Bir 10 saniye bekledikten sonra Kafka kurulumunun kontrolü için daha önce aldığımız public IP'ye iki nokta üst üste Port no eklenerek bir tarayıcıdan url'i açarız. Örneğin IP'miz 11.22.33.44 ise Chrome veya Firefox'tan http://11.22.33.44:3030 adresini açmaya çalışırız. Gelen ekranda fast-data-dev'in Topics vb. başlıklarını görüyorsak sorun yok demektir.

###

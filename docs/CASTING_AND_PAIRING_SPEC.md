# BÖRÜ CAST — PIN, QR, ekran yansıtma ve dosya aktarımı

Bu belge ürünün bağlantı ve yayın kabul kriterlerini sabitler. Bir özellik ancak gerçek Android telefon ve gerçek TV/alıcı üzerinde denenip doğrulandığında tamamlandı sayılır.

## Kullanıcı akışı
1. TV alıcısı açılır ve aynı yerel ağda görünür olur.
2. Alıcı kısa ömürlü altı haneli PIN ve QR kod üretir. QR; cihaz kimliği, oturum kimliği, yerel bağlantı adresi ve tek kullanımlık eşleştirme belirtecini taşır. PIN elle giriş için alternatiftir.
3. Android gönderici QR kodu tarar veya PIN'i girer. Kullanıcı TV ekranındaki eşleşme isteğini onaylar.
4. İki taraf şifreli, yerel ağda kimliği doğrulanmış oturum açar. PIN yalnızca eşleştirme içindir; medya oturum anahtarı olarak kullanılmaz.
5. Oturum kapatılınca belirteç geçersiz kılınır. PIN süresi dolar, deneme sınırı ve bekleme süresi uygulanır.

## Canlı ekran yansıtma
- Android MediaProjection izni her yayın başlangıcında sistem onayıyla alınır.
- Ekran ve ses yakalama ayrı izin/yetenek kontrolleriyle yönetilir.
- Medya aktarımı WebRTC üzerinden; oturum sinyalleşmesi doğrulanmış WebSocket kanalıyla yapılır.
- Ağ kalitesine göre çözünürlük, kare hızı ve bitrate uyarlanır. Zayıf Wi-Fi'da akıcılık için kalite düşürülebilir.
- TV alıcısı video/ses senkronizasyonu, yeniden bağlanma ve yayın durdurma durumlarını yönetir.
- Kullanıcı yayın sırasında Android'in ekran paylaşımı göstergesini görür; izin gizlice alınmaz.

## Dosya ve medya aktarımı
- Fotoğraf, video, ses, PDF, belge ve genel dosya türleri MIME türü ve dosya adıyla aktarılır.
- Büyük dosyalar akış halinde gönderilir; tüm dosya belleğe yüklenmez.
- Alıcıda ilerleme, aktarım hızı, kalan süre, iptal ve tekrar deneme durumu gösterilir.
- Video akışında HTTP byte-range/seek desteği ve uygun codec uyumluluğu test edilir.
- Aynı oturum içinde birden fazla dosya kuyruğa alınabilir. Dosya aktarımı ve ekran yansıtma birbirinden bağımsız başlatılıp durdurılabilir.

## Uzaktan kumanda
- Oynat/duraklat, ses, ileri/geri sarma ve bağlantıyı kesme komutları doğrulanmış oturum kanalından gönderilir.
- TV alıcısı desteklemediği komutları açıkça bildirir; arayüz sahte başarı göstermez.

## Güvenlik ve gizlilik
- Varsayılan mod yalnızca aynı yerel ağda çalışır; bulut rölesi varsayılan değildir.
- Eşleştirme PIN'i kısa ömürlü, tek kullanımlık ve deneme sınırına tabidir.
- Her oturum için ayrı rastgele kimlik bilgisi üretilir; oturum kapanınca iptal edilir.
- Android dosya seçici ve MediaProjection sistem izinleri kullanılır.
- Uygulama, kullanıcı onayı olmadan ekran veya dosya paylaşımı başlatmaz.

## Gerçek cihaz kabul testleri
- Redmi 12 gönderici ile Android TV alıcısı: PIN ve QR eşleştirme.
- Android TV olmayan akıllı TV: tarayıcı alıcısının desteklediği medya ve dosya indirme.
- 1080p yayın, hareketli görüntü, ses senkronu, Wi-Fi sinyal zayıflaması ve yeniden bağlanma.
- 1 GB üzeri dosya aktarımı, iptal/yeniden deneme, boş alan ve bozuk bağlantı senaryoları.
- Uygulama arka plana alındığında, ekran kilitlendiğinde ve yayın izni iptal edildiğinde güvenli kapanış.

## Mevcut durum
Şu anki Android uygulaması, telefondaki seçili içeriği yerel HTTP sunucusuyla tarayıcı alıcısına sunan ilk aktarım temelini içerir. Bu, PIN/QR eşleştirme veya WebRTC ekran yansıtma değildir. Yukarıdaki maddeler henüz geliştirme ve gerçek cihaz doğrulaması gerektirir.
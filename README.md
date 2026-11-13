# BÖRÜ CAST

**Telefonundan televizyona. Yerel ağda, sade ve güvenli.**

BÖRÜ CAST; Android telefonlardan Android TV'ye veya tarayıcı üzerinden açılabilen TV alıcısına fotoğraf, video, müzik ve dosya göndermek; ekran yansıtmak ve temel uzaktan kontrol sağlamak için geliştirilen açık kaynaklı bir projedir.

## Hedefler
- Android gönderici uygulaması (Kotlin + Jetpack Compose)
- Android TV alıcısı
- Tarayıcı tabanlı alıcı (akıllı TV tarayıcıları için)
- Yerel ağda cihaz keşfi ve QR/PIN ile eşleştirme
- Fotoğraf, video, müzik ve dosya aktarımı
- MediaProjection izniyle canlı ekran yansıtma
- Oynat/duraklat, ses ve bağlantı kontrolü
- Türkçe arayüz, koyu/açık tema ve erişilebilir tasarım

## Geliştirme durumu — 0.1.0
- [x] Android/Compose proje iskeleti
- [x] Türkçe başlangıç arayüzü
- [x] Fotoğraf, video, ses ve dosya seçicileri
- [x] GitHub Actions debug APK derleme hattı
- [ ] Android TV alıcısı
- [ ] Tarayıcı alıcısı
- [ ] Gerçek cihaz keşfi ve eşleştirme
- [x] Aynı Wi-Fi ağında tarayıcı üzerinden fotoğraf/video/ses akışı ve dosya indirme
- [ ] Android TV yerel alıcı uygulaması
- [ ] MediaProjection + WebRTC ekran yansıtma
- [ ] Uzaktan kumanda


## Şu an çalışan aktarım (tarayıcı alıcısı)
1. Telefon ve televizyonu aynı Wi-Fi ağına bağla.
2. Android uygulamasında Fotoğraf/video, Müzik veya Dosya gönder bölümünden bir içerik seç.
3. **TV'ye aktarımı başlat** düğmesine dokun.
4. Uygulamanın gösterdiği `http://telefon-ip:8080` adresini televizyonun internet tarayıcısına yaz.
5. Desteklenen fotoğraf, video ve ses dosyaları tarayıcıda açılır. Diğer dosyalar indirme bağlantısıyla alınabilir.
6. Aktarımı durdurmak için uygulamadaki düğmeyi kullan. Telefon uygulaması açık kalmalıdır.

**Geliştirme durumu:** Bu sürüm yerel ağda tarayıcı üzerinden gerçek medya akışı ve dosya indirme sağlar. Otomatik TV keşfi/eşleştirme, ayrı Android TV alıcısı, MediaProjection ile canlı ekran yansıtma ve uzaktan kumanda henüz tamamlanmamıştır. Bu özellikler test edilmeden tamamlandı olarak işaretlenmeyecektir.

## Derleme gereksinimleri
- JDK 17
- Android SDK Platform 35
- Gradle 8.9
- Android Gradle Plugin 8.7.3

Derleme komutu: gradle :android-sender:assembleDebug

APK çıktısı: android-sender/build/outputs/apk/debug/android-sender-debug.apk

## Güvenlik ilkeleri
- Ekran yakalama yalnızca Android'in sistem izin ekranı üzerinden başlatılır.
- Varsayılan aktarım modeli aynı yerel ağdır; bulut aktarımı varsayılan değildir.
- Eşleştirme kodları kısa ömürlü ve tek kullanımlık tasarlanacaktır.
- Ağ oturumları doğrulanmadan dosya veya komut kabul edilmemelidir.

## Lisans
MIT

package app.borucast.sender

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.security.SecureRandom
import java.util.UUID

class LocalCastServer(
    private val resolver: ContentResolver,
    private val source: Uri,
    port: Int = 8080
) : NanoHTTPD("0.0.0.0", port) {
    private val random = SecureRandom()
    val pairingPin: String = (100000 + random.nextInt(900000)).toString()
    private val pinExpiresAt = System.currentTimeMillis() + 5 * 60 * 1000L
    @Volatile private var pairedSession: String? = null
    @Volatile private var pinUsed = false

    override fun serve(session: IHTTPSession): Response {
        return when (session.uri) {
            "/" -> newFixedLengthResponse(Response.Status.OK, "text/html; charset=utf-8", PAGE)
            "/pair" -> {
                val supplied = session.parameters["pin"]?.firstOrNull()
                if (supplied == null || supplied != pairingPin || pinUsed ||
                    System.currentTimeMillis() > pinExpiresAt) {
                    newFixedLengthResponse(Response.Status.FORBIDDEN, "application/json; charset=utf-8",
                        JSONObject().put("error", "PIN geçersiz, kullanılmış veya süresi dolmuş").toString())
                } else {
                    pinUsed = true
                    pairedSession = UUID.randomUUID().toString()
                    newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",
                        JSONObject().put("token", pairedSession).toString())
                }
            }
            "/info" -> if (!isPaired(session)) forbidden() else {
                val mime = resolver.getType(source) ?: "application/octet-stream"
                val name = resolver.query(source, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { c -> if (c.moveToFirst()) c.getString(0) else null } ?: "BoruCast-medya"
                newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",
                    JSONObject().put("mime", mime).put("name", name).toString())
            }
            "/media", "/download" -> {
                if (!isPaired(session)) return forbidden()
                val stream = resolver.openInputStream(source)
                    ?: return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Dosya açılamadı")
                val mime = resolver.getType(source) ?: "application/octet-stream"
                newChunkedResponse(Response.Status.OK, mime, stream).apply {
                    addHeader("Cache-Control", "no-store")
                    addHeader("X-Content-Type-Options", "nosniff")
                    addHeader("Content-Disposition",
                        if (session.uri == "/download") "attachment; filename=\"boru-cast-dosya\"" else "inline")
                }
            }
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Bulunamadı")
        }
    }

    private fun isPaired(session: IHTTPSession): Boolean =
        pairedSession != null && session.parameters["token"]?.firstOrNull() == pairedSession

    private fun forbidden() = newFixedLengthResponse(
        Response.Status.FORBIDDEN, "text/plain; charset=utf-8", "Önce PIN ile eşleşme gerekli"
    )

    companion object {
        private val PAGE = """<!doctype html><html lang="tr"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><meta name="theme-color" content="#080d18"><title>BÖRÜ CAST</title><style>
*{box-sizing:border-box}body{margin:0;background:#080d18;color:#f4f7fb;font:16px system-ui;min-height:100vh;display:grid;place-items:center;padding:22px}main{width:min(900px,100%);background:#141e30;border:1px solid #26374e;border-radius:26px;padding:clamp(18px,4vw,34px)}.brand{color:#59d9e8;font-weight:900;letter-spacing:3px;font-size:13px}h1{font-size:clamp(24px,5vw,38px);margin:12px 0 6px}p{color:#9eacc0;margin:0 0 22px}#stage{width:100%;min-height:220px;max-height:70vh;background:#05080e;border-radius:18px;display:grid;place-items:center;overflow:hidden}video,audio{width:100%;max-height:70vh}img{max-width:100%;max-height:70vh;object-fit:contain}.pair{display:flex;gap:10px;flex-wrap:wrap;margin:20px 0}.pair input{min-width:0;flex:1;background:#080d18;color:white;border:1px solid #42536c;border-radius:12px;padding:14px;font-size:22px;letter-spacing:7px}.pair button,a{border:0;color:#06131c;background:#59d9e8;text-decoration:none;font-weight:800;padding:13px 18px;border-radius:12px;cursor:pointer}.actions{display:flex;gap:12px;flex-wrap:wrap;margin-top:18px}small{display:block;color:#9eacc0;margin-top:16px}#status{color:#ffb4ab;min-height:22px}</style></head><body><main><div class="brand">BÖRÜ CAST</div><h1>TV alıcısı</h1><p>Telefon uygulamasındaki 6 haneli PIN'i girerek eşleşmeyi başlat.</p><form class="pair" id="pair"><input id="pin" inputmode="numeric" autocomplete="one-time-code" maxlength="6" pattern="[0-9]{6}" placeholder="••••••" aria-label="6 haneli PIN" required><button>EŞLEŞTİR</button></form><div id="status" role="status"></div><section id="content" hidden><p id="filename"></p><div id="stage">İçerik hazırlanıyor…</div><div class="actions"><a id="download">Dosyayı indir</a><a id="open" target="_blank" rel="noopener">Medyayı ayrı aç</a></div></section><small>Yerel ağ bağlantısı • PIN 5 dakika geçerlidir ve bir kez kullanılabilir.</small></main><script>
let token='';const auth=path=>path+'?token='+encodeURIComponent(token);
document.getElementById('pair').addEventListener('submit',async e=>{e.preventDefault();const status=document.getElementById('status');status.textContent='Eşleştiriliyor…';try{const response=await fetch('/pair?pin='+encodeURIComponent(document.getElementById('pin').value));const result=await response.json();if(!response.ok)throw new Error(result.error||'Eşleşme başarısız');token=result.token;const info=await(await fetch(auth('/info'))).json();document.getElementById('filename').textContent=info.name;const stage=document.getElementById('stage');let media;if(info.mime.startsWith('image/')){media=document.createElement('img');media.alt=info.name}else if(info.mime.startsWith('audio/')){media=document.createElement('audio');media.controls=true;media.autoplay=true}else if(info.mime.startsWith('video/')){media=document.createElement('video');media.controls=true;media.autoplay=true;media.playsInline=true}else{stage.textContent='Bu dosya TV tarayıcısında önizlenemiyor. Dosyayı indirebilirsin.'}if(media){media.src=auth('/media');stage.replaceChildren(media)}document.getElementById('download').href=auth('/download');document.getElementById('open').href=auth('/media');document.getElementById('content').hidden=false;document.getElementById('pair').hidden=true;status.textContent='Bağlantı kuruldu.'}catch(error){status.textContent=error.message||'Bağlantı kurulamadı.'}});
</script></body></html>"""
    }
}

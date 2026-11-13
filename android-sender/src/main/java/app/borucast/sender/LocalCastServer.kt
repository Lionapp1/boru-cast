package app.borucast.sender

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject

class LocalCastServer(
    private val resolver: ContentResolver,
    private val source: Uri,
    port: Int = 8080
) : NanoHTTPD("0.0.0.0", port) {
    override fun serve(session: IHTTPSession): Response = when (session.uri) {
        "/" -> newFixedLengthResponse(Response.Status.OK, "text/html; charset=utf-8", PAGE)
        "/info" -> {
            val mime = resolver.getType(source) ?: "application/octet-stream"
            val name = resolver.query(source, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { c -> if (c.moveToFirst()) c.getString(0) else null } ?: "BoruCast-medya"
            newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",
                JSONObject().put("mime", mime).put("name", name).toString())
        }
        "/media", "/download" -> {
            val stream = resolver.openInputStream(source)
                ?: return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Dosya açılamadı")
            val mime = resolver.getType(source) ?: "application/octet-stream"
            newChunkedResponse(Response.Status.OK, mime, stream).apply {
                addHeader("Cache-Control", "no-store")
                addHeader("Content-Disposition", if (session.uri == "/download") "attachment; filename=\"boru-cast-dosya\"" else "inline")
            }
        }
        else -> newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Bulunamadı")
    }

    companion object {
        private val PAGE = """<!doctype html><html lang="tr"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>BÖRÜ CAST</title><style>
*{box-sizing:border-box}body{margin:0;background:#080d18;color:#f4f7fb;font:16px system-ui;min-height:100vh;display:grid;place-items:center;padding:22px}main{width:min(900px,100%);background:#141e30;border:1px solid #26374e;border-radius:26px;padding:clamp(18px,4vw,34px)}.brand{color:#59d9e8;font-weight:900;letter-spacing:3px;font-size:13px}h1{font-size:clamp(24px,5vw,38px);margin:12px 0 6px}p{color:#9eacc0;margin:0 0 22px}#stage{width:100%;min-height:220px;max-height:70vh;background:#05080e;border-radius:18px;display:grid;place-items:center;overflow:hidden}video,audio{width:100%;max-height:70vh}img{max-width:100%;max-height:70vh;object-fit:contain}.actions{display:flex;gap:12px;flex-wrap:wrap;margin-top:18px}a{color:#06131c;background:#59d9e8;text-decoration:none;font-weight:800;padding:13px 18px;border-radius:12px}small{display:block;color:#9eacc0;margin-top:16px}</style></head><body><main><div class="brand">BÖRÜ CAST</div><h1>TV alıcısı hazır</h1><p id="filename">İçerik yükleniyor…</p><div id="stage">Bağlantı bekleniyor…</div><div class="actions"><a href="/download">Dosyayı indir</a><a href="/media" target="_blank">Medyayı ayrı aç</a></div><small>Telefon ve TV aynı Wi-Fi ağında olmalı. Telefon uygulaması açık kalmalıdır.</small></main><script>
(async()=>{try{const d=await(await fetch('/info')).json();document.getElementById('filename').textContent=d.name;const s=document.getElementById('stage');let e;if(d.mime.startsWith('image/')){e=document.createElement('img');e.alt=d.name}else if(d.mime.startsWith('audio/')){e=document.createElement('audio');e.controls=true;e.autoplay=true}else if(d.mime.startsWith('video/')){e=document.createElement('video');e.controls=true;e.autoplay=true;e.playsInline=true}else{s.textContent='Bu dosya türü TV tarayıcısında önizlenemiyor. İndirme düğmesini kullan.';return}e.src='/media';s.replaceChildren(e)}catch(e){document.getElementById('stage').textContent='İçerik yüklenemedi.'}})();
</script></body></html>"""
    }
}
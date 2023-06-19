package com.qhy040404.mihoyogacha.ui

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.qhy040404.mihoyogacha.BuildConfig
import com.qhy040404.mihoyogacha.R
import com.qhy040404.mihoyogacha.base.BaseActivity
import com.qhy040404.mihoyogacha.constant.Game
import com.qhy040404.mihoyogacha.constant.URLManager
import com.qhy040404.mihoyogacha.databinding.ActivityMainBinding
import com.qhy040404.mihoyogacha.dto.AccountDTO
import com.qhy040404.mihoyogacha.dto.AuthKeyDTO
import com.qhy040404.mihoyogacha.dto.AuthKeyPostData
import com.qhy040404.mihoyogacha.dto.GameRole
import com.qhy040404.mihoyogacha.dto.GameRolesDTO
import com.qhy040404.mihoyogacha.dto.MultiTokenDTO
import com.qhy040404.mihoyogacha.utils.copyToClipBoard
import com.qhy040404.mihoyogacha.utils.decode
import com.qhy040404.mihoyogacha.utils.md5
import com.qhy040404.mihoyogacha.utils.showToast
import com.qhy040404.mihoyogacha.utils.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder
import kotlin.math.floor

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var gameRoles: List<GameRole>

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        binding.apply {
            root.bringChildToFront(binding.appbar)

            webview.apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        val currentUrl = url ?: return
                        if (currentUrl.contains("account/home")) {
                            getAuthKey(CookieManager.getInstance().getCookie(URLManager.MIHOYO_USER_CENTER)).also {
                                if (it.isEmpty()) return@also
                                gameRoles = it
                                gachaUrl.setText("登录成功")
                            }
                        }
                    }
                }

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }

                loadUrl(URLManager.MIHOYO_USER_CENTER)
            }

            gsBtn.setOnClickListener {
                if (this@MainActivity::gameRoles.isInitialized.not() || gameRoles.isEmpty()) {
                    gachaUrl.setText("请先登录")
                    return@setOnClickListener
                }
                val keys = gameRoles.filter { it.type == Game.GENSHIN }
                if (keys.isEmpty()) {
                    gachaUrl.setText("无原神角色")
                    return@setOnClickListener
                }

                if (keys.size > 1) {
                    AlertDialog.Builder(this@MainActivity)
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setItems(
                            keys.map { it.uid }.toTypedArray()
                        ) { _, which ->
                            val key = keys[which]
                            generateUrl(key).also {
                                it.copyToClipBoard()
                                showToast("已复制到剪贴板")
                                gachaUrl.setText("原神\n${key.nickname} ${key.uid}\n\n$it")
                            }
                        }
                        .show()
                } else {
                    val key = keys.first()
                    generateUrl(key).also {
                        it.copyToClipBoard()
                        showToast("已复制到剪贴板")
                        gachaUrl.setText("原神\n${key.nickname} ${key.uid}\n\n$it")
                    }
                }
                gachaUrl.setOnClickListener {
                    "https://${gachaUrl.text.toString().substringAfter("https://")}".copyToClipBoard()
                }
            }

            srBtn.setOnClickListener {
                if (this@MainActivity::gameRoles.isInitialized.not() || gameRoles.isEmpty()) {
                    gachaUrl.setText("请先登录")
                    return@setOnClickListener
                }

                if (BuildConfig.DEBUG.not()) {
                    gachaUrl.setText("暂不可用")
                    return@setOnClickListener
                }

                val keys = gameRoles.filter { it.type == Game.STAR_RAIL }
                if (keys.isEmpty()) {
                    gachaUrl.setText("无星铁角色")
                    return@setOnClickListener
                }

                if (keys.size > 1) {
                    AlertDialog.Builder(this@MainActivity)
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setItems(
                            keys.map { it.uid }.toTypedArray()
                        ) { _, which ->
                            val key = keys[which]
                            generateUrl(key).also {
                                it.copyToClipBoard()
                                showToast("已复制到剪贴板")
                                gachaUrl.setText("星铁\n${key.nickname} ${key.uid}\n\n$it")
                            }
                        }
                        .show()
                } else {
                    val key = keys.first()
                    generateUrl(key).also {
                        it.copyToClipBoard()
                        showToast("已复制到剪贴板")
                        gachaUrl.setText("星铁\n${key.nickname} ${key.uid}\n\n$it")
                    }
                }
                gachaUrl.setOnClickListener {
                    "https://${gachaUrl.text.toString().substringAfter("https://")}".copyToClipBoard()
                }
            }
        }
    }

    private fun getAuthKey(cookie: String): List<GameRole> {
        return runBlocking(Dispatchers.IO) {
            runCatching {
                val client = OkHttpClient()
                val accountInfo = client.newCall(
                    Request.Builder()
                        .url("https://webapi.account.mihoyo.com/Api/login_by_cookie?t=${System.currentTimeMillis()}")
                        .header("Cookie", cookie)
                        .build()
                ).execute().body!!.string().decode<AccountDTO>()!!.data.account_info
                val uid = accountInfo.account_id
                val token = accountInfo.weblogin_token

                val multiDataList = client.newCall(
                    Request.Builder()
                        .url("https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?login_ticket=${token}&token_types=3&uid=${uid}")
                        .header("Cookie", cookie)
                        .build()
                ).execute().body!!.string().decode<MultiTokenDTO>()!!.data.list
                val newCookie = buildString {
                    append("stuid=${uid};")
                    multiDataList.forEach {
                        append("${it.name}=${it.token};")
                    }
                    append(cookie)
                }

                val uidGenshinGameRoles = client.newCall(
                    Request.Builder()
                        .url("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn")
                        .header("Cookie", newCookie)
                        .build()
                ).execute().body!!.string().decode<GameRolesDTO>()!!.data.list

                val uidStarRailGameRoles = client.newCall(
                    Request.Builder()
                        .url("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hkrpg_cn")
                        .header("Cookie", newCookie)
                        .build()
                ).execute().body!!.string().decode<GameRolesDTO>()!!.data.list

                buildList {
                    uidGenshinGameRoles.forEach {
                        add(
                            GameRole(
                                Game.GENSHIN,
                                it.nickname,
                                it.game_uid,
                                it.region,
                                it.game_biz,
                                URLEncoder.encode(
                                    client.newCall(
                                        Request.Builder()
                                            .url("https://api-takumi.mihoyo.com/binding/api/genAuthKey")
                                            .header("Content-Type", "application/json;charset=utf-8")
                                            .header("Host", "api-takumi.mihoyo.com")
                                            .header("Accept", "application/json, text/plain, */*")
                                            .header("x-rpc-app_version", "2.49.1")
                                            .header("x-rpc-client_type", "5")
                                            .header("x-rpc-device_id", "CBEC8312-AA77-489E-AE8A-8D498DE24E90")
                                            .header("DS", ds)
                                            .header("Cookie", newCookie)
                                            .post(
                                                AuthKeyPostData(
                                                    "webview_gacha",
                                                    it.game_biz,
                                                    it.game_uid,
                                                    it.region
                                                ).toJson()!!
                                                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                                            )
                                            .build()
                                    ).execute().body!!.string().decode<AuthKeyDTO>()!!.data.authkey,
                                    "utf-8"
                                )
                            )
                        )
                    }
                    uidStarRailGameRoles.forEach {
                        add(
                            GameRole(
                                Game.STAR_RAIL,
                                it.nickname,
                                it.game_uid,
                                it.region,
                                it.game_biz,
                                URLEncoder.encode(
                                    client.newCall(
                                            Request.Builder()
                                                .url("https://api-takumi.mihoyo.com/binding/api/genAuthKey")
                                                .header("Content-Type", "application/json;charset=utf-8")
                                                .header("Host", "api-takumi.mihoyo.com")
                                                .header("Accept", "application/json, text/plain, */*")
                                                .header("Origin","https://webstatic.mihoyo.com")
                                                .header("Referer","https://webstatic.mihoyo.com")
                                                .header("x-rpc-app_version", "2.49.1")
                                                .header("x-rpc-client_type", "5")
                                                .header("x-rpc-device_id", "CBEC8312-AA77-489E-AE8A-8D498DE24E90")
                                                .header("DS", ds)
                                                .header("Cookie", newCookie)
                                                .post(
                                                    AuthKeyPostData(
                                                        "webview_gacha",
                                                        it.game_biz,
                                                        it.game_uid,
                                                        it.region
                                                    ).toJson()!!
                                                        .toRequestBody("application/json;charset=utf-8".toMediaType())
                                                )
                                                .build()
                                            ).execute().body!!.string().decode<AuthKeyDTO>()!!.data.authkey,
                                    "utf-8"
                                )
                            )
                        )
                    }
                }
            }.getOrDefault(listOf())
        }
    }

    private fun generateUrl(gameRole: GameRole): String {
        val type = gameRole.type
        val region = gameRole.region
        val authKey = gameRole.authKey
        val gameBiz = gameRole.biz
        return when (type) {
            Game.GENSHIN -> {
                "https://hk4e-api.mihoyo.com/event/gacha_info/api/getGachaLog?win_mode=fullscreen&authkey_ver=1&sign_type=2&auth_appid=webview_gacha&init_type=301&gacha_id=01941e8b10a4a7747ef2ecf7315e46a792ce1cd9&timestamp=1684884971&lang=zh-cn&device_type=pc&game_version=CNRELWin3.7.0_R15495264_S15338072_D15203785&plat_type=pc&region=${region}&authkey=${authKey}&game_biz=${gameBiz}&gacha_type=301&page=1&size=5&end_id=0"
            }

            Game.STAR_RAIL -> {
                "https://api-takumi.mihoyo.com/common/gacha_record/api/getGachaLog?authkey_ver=1&sign_type=2&auth_appid=webview_gacha&win_mode=fullscreen&gacha_id=4a59ab6e4f20fde5701402a1b8c72f609200a2&timestamp=1686096286&region=${region}&default_gacha_type=11&lang=zh-cn&authkey=${authKey}&game_biz=${gameBiz}&os_system=Windows&device_model=All&plat_type=pc&page=1&size=5&gacha_type=11&end_id=0"
            }
        }.also { println(it) }
    }

    private val ds: String
        get() {
            val salt = "DG8lqMyc9gquwAUFc7zBS62ijQRX9XF7"
            val time = System.currentTimeMillis() / 1000
            val str = buildString {
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".let {
                    repeat(6) { _ ->
                        append(it[floor(Math.random() * it.length).toInt()])
                    }
                }
            }

            return "${time},${str},${md5("salt=${salt}&t=${time}&r=${str}".toByteArray())}"
        }
}
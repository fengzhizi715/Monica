package cn.netdiscovery.monica.http

import cn.netdiscovery.http.core.serializer.Serializer
import com.google.gson.Gson
import java.lang.reflect.Type

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.http.GsonSerializer
 * @author: Tony Shen
 * @date: 2025/4/3 18:58
 * @version: V1.0 <描述当前版本功能>
 */
class GsonSerializer: Serializer {

    private val gson: Gson = Gson()

    override fun <T> fromJson(json: String, type: Type): T = gson.fromJson(json,type)

    override fun toJson(data: Any): String = gson.toJson(data)
}
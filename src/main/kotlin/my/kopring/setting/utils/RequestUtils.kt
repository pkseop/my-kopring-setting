package my.kopring.setting.utils

import javax.servlet.http.HttpServletRequest

object RequestUtils {

    fun getClientIp(request: HttpServletRequest): String {
        var ip = request.getHeader("X-Forwarded-For")
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip == null || ip.isEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        return ip
    }


}
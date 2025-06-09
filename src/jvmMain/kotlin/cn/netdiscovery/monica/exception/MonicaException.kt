package cn.netdiscovery.monica.exception

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.exception.MonicaException
 * @author: Tony Shen
 * @date: 2025/6/9 11:25
 * @version: V1.0 <描述当前版本功能>
 */
class MonicaException : RuntimeException {
    constructor() : super()

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(message: String?) : super(message)

    constructor(cause: Throwable?) : super(cause)
}
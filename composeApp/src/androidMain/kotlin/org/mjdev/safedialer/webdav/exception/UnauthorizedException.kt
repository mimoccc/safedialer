package org.mjdev.safedialer.webdav.exception

import okhttp3.Response

class UnauthorizedException: HttpException {
    constructor(response: Response) : super(response) {
        if (response.code != 401)
            throw IllegalArgumentException("Status code must be 401")
    }
}

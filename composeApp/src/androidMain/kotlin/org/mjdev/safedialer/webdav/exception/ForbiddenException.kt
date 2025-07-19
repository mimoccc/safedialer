package org.mjdev.safedialer.webdav.exception

import okhttp3.Response

class ForbiddenException: HttpException {
    constructor(response: Response) : super(response) {
        if (response.code != 403)
            throw IllegalArgumentException("Status code must be 403")
    }
}

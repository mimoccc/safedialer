package org.mjdev.safedialer.webdav.exception

import okhttp3.Response

class ConflictException: HttpException {
    constructor(response: Response) : super(response) {
        if (response.code != 409)
            throw IllegalArgumentException("Status code must be 409")
    }
}

package org.mjdev.safedialer.webdav.exception

import okhttp3.Response

class NotFoundException : HttpException {
    constructor(response: Response) : super(response) {
        if (response.code != 404)
            throw IllegalArgumentException("Status code must be 404")
    }
}

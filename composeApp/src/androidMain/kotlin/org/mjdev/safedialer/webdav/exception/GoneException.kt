package org.mjdev.safedialer.webdav.exception

import okhttp3.Response

class GoneException: HttpException {
    constructor(response: Response) : super(response) {
        if (response.code != 410)
            throw IllegalArgumentException("Status code must be 410")
    }
}

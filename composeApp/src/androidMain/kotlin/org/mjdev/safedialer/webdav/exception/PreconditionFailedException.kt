package org.mjdev.safedialer.webdav.exception

import okhttp3.Response

class PreconditionFailedException: HttpException {
    constructor(response: Response) : super(response) {
        if (response.code != 412)
            throw IllegalArgumentException("Status code must be 412")
    }
}

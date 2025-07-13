package org.mjdev.safedialer.dao.base

class DAOException(
    message:String,
    cause: Throwable? = null
) : Exception(message, cause)
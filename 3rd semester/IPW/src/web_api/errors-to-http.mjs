// Errors translated to HTTP responses

import { ERROR_CODES } from "../common/errors.mjs";

function httpResponse(status, body){
    this.status = status,
    this.body = body
}

export default function(error) {
    switch(error.code) {
        case ERROR_CODES.INVALID_ARGUMENT: return new httpResponse(400, error)
        case ERROR_CODES.NOT_AUTHORIZED: return new httpResponse(401, error)
        case ERROR_CODES.USER_NOT_FOUND: return new httpResponse(401, error)
        case ERROR_CODES.NOT_FOUND: return new httpResponse(404, error) 
        case ERROR_CODES.ALEADY_EXISTS: return new httpResponse(403, error)
        case ERROR_CODES.INVALID_QUERY_PARAM: return new httpResponse(400, error)
        case ERROR_CODES.INVALID_BODY: return new httpResponse(400, error)
        default: return new httpResponse(500, "Internal server error")
    }
}
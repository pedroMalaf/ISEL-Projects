// Every possible error in this application is defined in this file.

export const ERROR_CODES = {
    INVALID_ARGUMENT: 1,
    NOT_FOUND: 2,
    NOT_AUTHORIZED: 3,
    USER_NOT_FOUND: 4,
    ALEADY_EXISTS: 5,
    INVALID_QUERY_PARAM: 6,
    INVALID_BODY: 7
}

function Error(code, description){
    this.code = code
    this.description = description
}

export default {
    INVALID_ARGUMENT: argName => {
        return new Error(ERROR_CODES.INVALID_ARGUMENT, `Invalid argument: ${argName}`)
    },
    NOT_FOUND: (what) => {
        return new Error(ERROR_CODES.NOT_FOUND, `${what} not found`)
    },
    NOT_AUTHORIZED: (who, what) => {
        return new Error(ERROR_CODES.NOT_AUTHORIZED, `${who} not authorized to access ${what}`)
    },
    USER_NOT_FOUND: () => { 
        return new Error(ERROR_CODES.USER_NOT_FOUND,`User not found`)
    },
    ALEADY_EXISTS: (what) => { 
        return new Error(ERROR_CODES.ALEADY_EXISTS,`${what} already exists`)
    },
    INVALID_QUERY_PARAM: (paramName) => {
        return new Error(ERROR_CODES.INVALID_QUERY_PARAM, `Invalid query param: ${paramName}`)
    },
    INVALID_BODY: (requires) => {
        return new Error(ERROR_CODES.INVALID_BODY, `Invalid body: body requires ${requires}`)
    }
}
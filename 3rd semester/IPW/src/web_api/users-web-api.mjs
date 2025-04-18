import errorToHttp from "./errors-to-http.mjs"

export default function (userServices){
    if (!userServices){
        throw errors.INVALID_ARGUMENT("userServices")
    }

    return {
        insertUser
    }

    async function insertUser(req, rsp){
        try{
            const username = req.body.name
            await userServices.insertUser(username)
            rsp.status(201).json("User added")
        }
        catch(e){
            const rspError = errorToHttp(e)
            rsp.status(rspError.status).json(rspError.body)
        }
    }
}
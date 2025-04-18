import errors from '../common/errors.mjs'

export default function (userData){
    if(!userData){
        throw errors.INVALID_ARGUMENT("userData")
    }

    return {
        _insertUser,
        _getUserId,
        _getUserByUsername
    }

    async function _insertUser(username, password){
        const user = await userData.insertUser(username, password)
        if (user){
            return user
        }
        throw errors.ALEADY_EXISTS("User")
    }
    
    async function _getUserByUsername(username, password){
        if(!username || !password){
            throw errors.INVALID_ARGUMENT("username or password")
        }
        const user = await userData.getUserByUsername(username)
        if(user && user[0].password == password) {
            return user
        }
        throw errors.USER_NOT_FOUND("")
    }
    
    async function _getUserId(userToken){
        const user = await userData.getUserByToken(userToken)
        if(user) {
            return user.id
        }
        throw errors.USER_NOT_FOUND("")
    }
}
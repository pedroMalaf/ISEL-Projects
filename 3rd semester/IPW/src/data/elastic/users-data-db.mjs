import crypto from 'node:crypto'
import {get, post, del, put} from './fetch-wrapper.mjs'
import uriManager from './uri-manager.mjs'

export default async function(indexName = 'users'){

    const URI_MANAGER = await uriManager(indexName)

    return {
        insertUser,
        getUserByToken,
        getUserByUsername
    }

    async function insertUser(username, password){
        const user = await getUserByUsername(username)
        if (user.length == 0){
            const user = {
                username: username,
                password: password,
                token: crypto.randomUUID()
            }
            return post(URI_MANAGER.create(), user)
                .then(() => true)
        }
        else {
            return false 
        }
    }
    
    async function getUserByUsername(username){
        return getUserBy('username', username)
    }

    async function getUserByToken(userToken){
        return getUserBy('token', userToken)
    }

    async function getUserBy(propName, Value){
        const uri = `${URI_MANAGER.getAll()}?q=${propName}:${Value}`
        return get(uri)
            .then(body => body.hits.hits.map(createUserFromElastic))
    }

    function createUserFromElastic(taskElastic) {
        let user = Object.assign({id: taskElastic._id}, taskElastic._source)
        return user
    }
}
import e from 'express'
import crypto from 'node:crypto'
import { get } from 'node:http'

const users = [
    {
        id: 1,
        name: "Pedro",
        token: "b2f7f962-e384-4cb0-b658-59705dbc668c"
    },
    {
        id: 2,
        name: "Ana",
        token: "fdd863ec-fcd6-484d-b3ad-50319a9a0fa2"
    },
    {
        id: 3,
        name: "Ricardo",
        token: "39495f46-af47-4851-8f45-d8018026674f"
    }
]

let nextId = users.length + 1 

export default function(){
    
    return {
        insertUser,
        getUserByToken,
        getUserByUsername
    }

    async function insertUser(username){
        if(getUserByUsername(username)){
            const user = {
                id: nextId++,
                name: username,
                token: crypto.randomUUID()
            }
            users.push(user)
            return true
        }
        else{
            return false
        }
    }

    async function getUserByUsername(username){
        const getUser = users.filter(u => u.name === username) 
        if(getUser){
            return getUser
        }
        else{
            return false
        }
    }

    async function getUserByToken(userToken){
        return Promise.resolve(users.find(u => u.token === userToken))
    }
}

// Implement functionalities for the SECA API
// This module doen't "know" what HTTP is

import errors from '../common/errors.mjs'

export default function (tmData, secaData, usersServices){
    if(!tmData){
        throw errors.INVALID_ARGUMENT("tmData")
    }
    if(!secaData){
        throw errors.INVALID_ARGUMENT("secaData")
    }

    return {
        getPopularEvents,
        getEventByName,
        getAllGroups,
        getGroup,
        createGroup,
        updateGroup,
        addEventToGroup,
        deleteGroup,
        deleteEventFromGroup
    }

    async function getPopularEvents(s, p){
        if(isNaN(s)){
            throw errors.INVALID_QUERY_PARAM("s")
        }
        if(s == 0 || s > 200){
            throw errors.INVALID_QUERY_PARAM("s must be between 1 to 200")
        }
        if(isNaN(p)){
            throw errors.INVALID_QUERY_PARAM("p")
        }
        if(s * p >= 1000){
            throw errors.INVALID_QUERY_PARAM("p * s must be less than 1000")
        }
        return await tmData.getPopularEvents(s, p)
    }
    
    async function getEventByName(eventName, s, p){
        if(isNaN(s)){
            throw errors.INVALID_ARGUMENT("s")
        }
        if(s == 0 || s > 200){
            throw errors.INVALID_QUERY_PARAM("s must be between 1 to 200")
        }
        if(isNaN(p)){
            throw errors.INVALID_ARGUMENT("p")
        }
        if(s * p >= 1000){
            throw errors.INVALID_QUERY_PARAM("p * s must be less than 1000")
        }
        const events = await tmData.getEventByName(eventName, s, p)
        if (events == undefined) {
            throw errors.NOT_FOUND(eventName)
        }
        return events
    }
    
    async function getAllGroups(token){
        return await secaData.getAllGroups(token)
    }
    
    async function getGroup(groupId, token){
        const group = await secaData.getGroup(groupId)
        if(group.name == undefined){
            throw errors.NOT_FOUND("group")
        }
        if (group.token == token) {
            return group
        } 
        throw errors.NOT_AUTHORIZED(`User ${token}`, `Group ${groupId}`)
    }
    
    async function createGroup(group, token){
        if(
            group.name == '' ||
            group.description == '' 
        ) {
            throw errors.INVALID_BODY("<name>;<description>")
        }
        return await secaData.createGroup(group, token)
    }
    
    async function updateGroup(group, groupId, token){
        if(
            group.name == '' ||
            group.description == '' 
        ) {
            throw errors.INVALID_BODY("<name>;<description>")
        }
        const groupToUpdate = await secaData.getGroup(groupId)
        if(groupToUpdate == undefined){
            throw errors.NOT_FOUND("group")
        }
        groupToUpdate.name = group.name
        groupToUpdate.description = group.description
        return await secaData.updateGroup(groupToUpdate, groupId, token)
    }
    
    async function addEventToGroup(groupId, token, eventName){
        const group = await secaData.getGroup(groupId)
        if(group == undefined){
            throw errors.NOT_FOUND("group")
        }
        if (group.token != token) {
            throw errors.NOT_AUTHORIZED(`User ${token}`, `Group ${groupId}`)
        }
        const defaultS = 1
        const defaultP = 0
        const getEvent = await getEventByName(eventName, defaultS, defaultP)
        await secaData.addEventToGroup(group, getEvent[0])
        return getEvent
    }
    
    async function deleteGroup(groupId, token){
        const removed = await secaData.deleteGroup(groupId, token)
        if(removed == undefined){
            throw errors.NOT_FOUND("group")
        }
        return removed
    }
    
    async function deleteEventFromGroup(groupId, eventId, token){
        const group = await secaData.getGroup(groupId)
        if(group == undefined){
            throw errors.NOT_FOUND("group")
        }
        if (group.token != token) {
            throw errors.NOT_AUTHORIZED(`User ${token}`, `Group ${groupId}`)
        }
        const removed = await secaData.deleteEventFromGroup(group, eventId)
        if(removed == undefined){
            throw errors.NOT_FOUND("event")
        }
        return removed
    }
}
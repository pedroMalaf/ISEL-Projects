// Handling of all the HTTP requests for the SECA web API
// Gets information from requests and sends information back to the client through the response object

import errorToHttp from "./errors-to-http.mjs"

const defaultNrEvents = 30 
const defaultPageNr = 1

export default function (secaServices) {
    if(!secaServices){
        throw errors.INVALID_ARGUMENT("secaServices")
    }

    return {
        getPopularEvents,
        getEventByName,
        getAllGroups: processRequest(_getAllGroups),
        getGroup: processRequest(_getGroup),
        createGroup: processRequest(_createGroup),
        updateGroup: processRequest(_updateGroup),
        addEventToGroup: processRequest(_addEventToGroup),
        deleteGroup: processRequest(_deleteGroup),
        deleteEventFromGroup: processRequest(_deleteEventFromGroup)
    }

    function processRequest(reqProcessor) {
        return async function(req, rsp) {
            const token =  getToken(req)
            if(!token) {
                rsp.status(401).json("Not authorized")  
            }
            req.token = token
            try {
                return await reqProcessor(req, rsp)
            }
            catch(e){
                const rspError = errorToHttp(e)
                rsp.status(rspError.status).json(rspError.body)
            }
        }
    }
    
    async function getPopularEvents(req, rsp){
        try{
            const s  = req.query.s || defaultNrEvents
            const p = req.query.p || defaultPageNr
            const popularEvents = await secaServices.getPopularEvents(s, p) 
            rsp.json(popularEvents)
        }
        catch(e){
            const rspError = errorToHttp(e)
            rsp.status(rspError.status).json(rspError.body)
        }
    }
    
    async function getEventByName(req, rsp){
        try{
            const name = req.params.name
            const s  = req.query.s || defaultNrEvents
            const p = req.query.p || defaultPageNr
            const event = await secaServices.getEventByName(name, s, p)
            rsp.json(event)
        }
        catch(e){
            const rspError = errorToHttp(e)
            rsp.status(rspError.status).json(rspError.body)
        }
    }
    
    async function _getAllGroups(req, rsp){
        const groups = await secaServices.getAllGroups(req.token)
        return rsp.json(groups)
    }
    
    async function _getGroup(req, rsp){
        const groupId = req.params.id
        const group = await secaServices.getGroup(groupId, req.token)
        if (group){
            return rsp.json(group)
        }
        else rsp.status(404).json("Group not found")
    }
    
    async function _createGroup(req, rsp){
        const group = {
            name: req.body.n,
            description: req.body.d
        }
        const newGroup = await secaServices.createGroup(group, req.token) 
        rsp.status(201).json(newGroup)
    }
    
    async function _updateGroup(req, rsp){
        const groupid = req.params.id
        const group={
            name: req.body.n,
            description: req.body.d
        }
        const updatedGroup= await secaServices.updateGroup(group, groupid, req.token)
        rsp.status(200).json(updatedGroup)
    }
    
    async function _addEventToGroup(req, rsp){
        const groupid = req.params.groupid
        const eventName = req.body.n
        const newEvent = await secaServices.addEventToGroup(groupid, req.token, eventName)
        return rsp.status(200).json(newEvent) 
    }
    
    async function _deleteGroup(req, rsp){
        const group = await secaServices.deleteGroup(req.params.id, req.token)
        return rsp.status(200).json(group)
    }
    
    async function _deleteEventFromGroup(req, rsp){
        const groupId = req.params.groupid
        const eventId = req.params.eventid
        const deletedEvent = await secaServices.deleteEventFromGroup(groupId, eventId, req.token)
        return rsp.status(200).json(deletedEvent)
    }
    
    function getToken(req){
        let token = req.get("Authorization")
        if(token){
            return token = token.split(" ")[1]
        }
    }
}


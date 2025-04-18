import {get, post, del, put} from './fetch-wrapper.mjs'
import uriManager from './uri-manager.mjs'

export default async function(indexName = 'groups'){

    const URI_MANAGER = await uriManager(indexName)

    return {
        getAllGroups,
        getGroup,
        createGroup,
        updateGroup,
        addEventToGroup,
        deleteGroup,
        deleteEventFromGroup
    }

    async function getAllGroups(userId){
        const query = {
            query: {
              match: {
                "token": userId
              }
            }
          }
        return post(URI_MANAGER.getAll(), query)
            .then(body => body.hits.hits.map(createGroupFromElastic))
    }

    async function getGroup(groupId){
        return get(URI_MANAGER.get(groupId)).then(createGroupFromElastic)
    }

    async function createGroup(group, userID){
        const newGroup = {
            name: group.name,
            description: group.description,
            token: userID,
            events: []
        }
        return post(URI_MANAGER.create(), newGroup)
            .then(body => {newGroup.id = body._id; return newGroup})
    }

    async function updateGroup(group, groupId, userID){
        return put(URI_MANAGER.update(groupId), group)
    }

    async function addEventToGroup(group, event){
        group.events.push(event)
        return put(URI_MANAGER.update(group.id), group)
    }

    async function deleteGroup(groupId, userID){
        const groupExits = await getGroup(groupId)
        if(groupExits == undefined){
            return undefined
        }
        const result = del(URI_MANAGER.delete(groupId)) 
        return groupExits
    }

    async function deleteEventFromGroup(group, eventId){
        group.events = group.events.filter(event => event.id != eventId)
        return put(URI_MANAGER.update(group.id), group)
    }

    function createGroupFromElastic(taskElastic) {
        let task = Object.assign({id: taskElastic._id}, taskElastic._source)
        return task
    }
}

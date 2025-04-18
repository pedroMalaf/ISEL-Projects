//const num_events = 40 

//const events = new Array(num_events).fill(0).map( (_, idx) => {
//    return {id: idx+1, name: `Event ${idx+1}`}
//})

//export async function getPopularEvents(eventsNr, pageNr){
//    return Promise.resolve(events)
//}
//
//export async function getEventByName(eventName, eventsNr, pageNr){
//    return Promise.resolve(events.find(e => e.name === eventName))
//}

let groups = [{id: 1, name: "Group 1", description: "asdf", userId: 1, events: []},
                {id: 2, name: "Group 2", description: "asdf", userId: 2, events: []}]

export default function(){

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
        return Promise.resolve(groups.filter(g => g.userId == userId))

    }

    async function getGroup(groupId){
        return Promise.resolve(groups.find(g => g.id == groupId))
    }

    async function createGroup(group, userID){
        const newGroup = {
            id: groups.length + 1,
            name: group.name,
            description: group.description,
            userId: userID,
            events: []
        }
        groups.push(newGroup)
        return Promise.resolve(newGroup)
    }

    async function updateGroup(group, groupId, userID){
        const groupIdx = groups.findIndex(group=> group.id == groupId && group.owner == userID)
        if (groupIdx !== -1) {
            groups[groupIdx].name = group.name
            groups[groupIdx].description = group.description
            return Promise.resolve(groups[groupIdx]); 
        } else {
            return undefined
    }}

    async function addEventToGroup(group, event){
        return Promise.resolve(group.events.push(event))
    }

    async function deleteGroup(groupId, userID){
        const groupIdx = groups.findIndex(group=> group.id == groupId && group.userId == userID)
        if(groupIdx == -1){
            return undefined
        } 
        return Promise.resolve(groups.splice(groupIdx,1))
    }

    async function deleteEventFromGroup(group, eventId){
        const eventIdx = group.events.findIndex(event => event.id == eventId)
        if(eventIdx == -1){
            return undefined
        }
        return Promise.resolve(group.events.splice(eventIdx,1))
    }
}

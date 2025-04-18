import errorToHttp from '../web_api/errors-to-http.mjs'
const defaultNrEvents = 30 
const defaultPageNr = 1


export default function(secaServices) {
    if(!secaServices)
        throw errors.INVALID_ARGUMENT("taskServices")

    return {
        getAllGroups: processRequest(_getAllGroups),
        getGroup: processRequest(_getGroup),
        createGroup: processRequest(_createGroup),
        createGroupForm: processRequest(_createGroupForm),
        updateGroup: processRequest(_updateGroup),
        updateGroupForm: processRequest(_updateGroupForm),
        addEventToGroup: processRequest(_addEventToGroup),
        deleteGroup: processRequest(_deleteGroup),
        deleteEventFromGroup: processRequest(_deleteEventFromGroup),
        getPopularEvents:getPopularEvents,
        getEventByName:getEventByName,
        home:home,
        search:search,
        
    }

    function processRequest(reqProcessor) {
        return async function(req, rsp) {

            const token = getToken(req)
            if(!token) {
                rsp
                    .status(401)
                    .json({error: `Invalid authentication token`})
            }
            try {
                return await reqProcessor(req, rsp)
            } catch (e) {
                const rspError = errorToHttp(e)
                rsp.status(rspError.status).json(rspError.body)
                console.log(e)
            }
        }
    }

    async function home(req, res){
        res.render('home');
    }

    async function getPopularEvents(req, rsp){
        const s  = req.query.s || defaultNrEvents
        const p = req.query.p || defaultPageNr
        const popularEvents = await secaServices.getPopularEvents(s, p) 
        rsp.render('events',{
            events: popularEvents
        })

    }

    async function getEventByName(req, rsp){
        const name = req.params.name
        const s  = req.query.s || defaultNrEvents
        const p = req.query.p || defaultPageNr
        const event = await secaServices.getEventByName(name, s, p)
        rsp.render('event', {
            title: 'Shows and Events Results',
            events: event
        });

    }

    async function  _getAllGroups(req, rsp) {
        const groups = await secaServices.getAllGroups(req.token)
        rsp.render('groups', {title: 'All Groups', groups:groups})
    }

    async function _getGroup(req, rsp){
        const group = await secaServices.getGroup(req.params.id, req.token)
        rsp.render('group',  {group: group})
    }
    
    async function _createGroup(req, rsp) {
        try{
            const group = {
                name: req.body.name,
                description: req.body.description
            }
            await secaServices.createGroup(group, req.token)
            rsp.redirect("/auth/site/groups")  
        }
        catch {
            rsp.render('create_group', {message: "Group needs a name and a description"})
        }
    }
    async function _createGroupForm(req, rsp) {
        rsp.render('create_group')
    }


    async function _updateGroup(req, rsp){
        try{
            const group = {
                name: req.body.name,
                description: req.body.description 
            }
            await secaServices.updateGroup(group, req.params.id, req.token)
            rsp.redirect("/auth/site/groups")
        }
        catch {
            rsp.render('group_updated', {message:"Group needs a name and a description"}) 
        }
    }

    async function _updateGroupForm(req, rsp) {
        const id = req.params.id
        const group = await secaServices.getGroup(id, req.token)
        rsp.render('group_updated', { group: group})
    }

    async function _addEventToGroup(req, rsp){
        const groupid = req.params.groupid
        const eventName = req.body.n
        await secaServices.addEventToGroup(groupid, req.token, eventName)
        rsp.redirect("/auth/site/groups")
    }

    async function _deleteGroup(req, rsp){
        await secaServices.deleteGroup(req.params.id, req.token)
        rsp.redirect("/auth/site/groups",)
    }

    async function _deleteEventFromGroup(req, rsp){
        const groupId = req.params.groupid
        const eventId = req.params.eventid
        await secaServices.deleteEventFromGroup(groupId, eventId, req.token)
        rsp.redirect("/auth/site/groups")
    }

    async function search(req, rsp){
        rsp.render('search_Event')
    }

    function getToken(req) {
        req.token = req.user
        return req.token
    }
}

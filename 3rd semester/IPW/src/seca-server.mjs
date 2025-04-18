import express from 'express'
import swaggerUi from 'swagger-ui-express'
import yaml from 'yamljs' 
import url from 'url'
import path from 'path'
import hbs from 'hbs'
import cors from 'cors' 
import morgan from 'morgan'

//import secaDataInit from './data/memory/seca-data-mem.mjs'
import secaDataInit from './data/elastic/seca-data-db.mjs'
//import usersDataInit from './data/memory/users-data-mem.mjs'
import usersDataInit from './data/elastic/users-data-db.mjs'
import tmDataInit from './data/tm-events-data.mjs'
import secaServicesInit from './services/seca-services.mjs'
import usersServicesInit from './services/users-services.mjs'
import secaWebApiInit from './web_api/seca-web-api.mjs'
import usersWebApiInit from './web_api/users-web-api.mjs'
import secaSiteInit from './web_site/seca-web-site.mjs'
import authUiFunction from './web_site/seca-web-site-users.mjs'

const secaData = await secaDataInit()
const tmData = tmDataInit()
const usersData = await usersDataInit()
const usersServices = usersServicesInit(usersData)
const secaServices = secaServicesInit(tmData, secaData, usersServices)
const secaApi = secaWebApiInit(secaServices)
const usersApi = usersWebApiInit(usersServices)
const secaSite= secaSiteInit(secaServices)
const authRouter = authUiFunction(usersServices)

const PORT = 1906

const swaggerDocument = yaml.load('./docs/seca-api-spec.yaml')
const currentFileDir = url.fileURLToPath(new URL('.', import.meta.url));
const viewsDir = path.join(currentFileDir, 'web_site', 'views')

console.log("Setting up server")
let app = express()

app.use(morgan('dev'))
app.use(cors())
app.use(express.json())
app.use(express.urlencoded())
app.set('view engine', 'hbs')
app.set('views', viewsDir)
hbs.registerPartials(path.join(viewsDir, 'partials'))
hbs.handlebars.registerHelper("strong", function(idx,  options) {
    return idx%2 == 0 ? `<strong>${options.fn(this)}</strong>` : options.fn(this)
})

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument))
app.use(express.json())
app.use(authRouter)

// events
app.get('/popularevents', secaApi.getPopularEvents) // Gets the popular events 
app.get('/events/:name', secaApi.getEventByName) // Gets events by name

// groups
app.get('/groups', secaApi.getAllGroups) // Gets all the groups 
app.post('/groups', secaApi.createGroup) // Creates a new group 
app.get('/groups/:id', secaApi.getGroup) // Gets a group by its id 
app.put('/groups/:id', secaApi.updateGroup) // Edits a group 
app.delete('/groups/:id', secaApi.deleteGroup) // Deletes a group by its id 
app.post('/groups/:groupid/events', secaApi.addEventToGroup) // Adds an event to a group 
app.delete('/groups/:groupid/events/:eventid', secaApi.deleteEventFromGroup) // Deletes an event from a group by its id 
// site

app.get('/site/events/:name',secaSite.getEventByName)
app.get('/auth/site/groups',secaSite.getAllGroups)
app.post('/auth/site/groups/create',secaSite.createGroup)
app.get('/auth/site/groups/create',secaSite.createGroupForm)
app.get('/auth/site/groups/get/:id',secaSite.getGroup)
app.get('/auth/site/groups/:id/update',secaSite.updateGroupForm)
app.post('/auth/site/groups/:id/update',secaSite.updateGroup)
app.post('/auth/site/groups/:id/delete',secaSite.deleteGroup)
app.post('/auth/site/groups/:groupid/events',secaSite.addEventToGroup)
app.post('/auth/site/groups/:groupid/events/:eventid',secaSite.deleteEventFromGroup)
app.get('/site/popularevents',secaSite.getPopularEvents) 
app.get('/',secaSite.home)
app.get('/site/search',secaSite.search)


// users
app.post('/user', usersApi.insertUser) // Creates a new user

app.listen(PORT, () => console.log(`Server listening in http://localhost:${PORT}`))
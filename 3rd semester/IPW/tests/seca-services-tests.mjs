import assert from  'assert'

import * as usersServices from '../src/services/users-services.mjs';
import * as secaServices from '../src/services/seca-services.mjs'
import * as e from '../src/common/errors.mjs'
import crypto from 'crypto'
import { describe, it } from 'node:test'

describe('SECA services', function () {
  describe('SECA Tests ', function () {
        it('get Popular Events', async function() {
      let obj = await secaServices.getPopularEvents(30,1)
      assert.equal(obj != undefined, true)
      assert.equal(obj[0].id != undefined && obj[0].name != undefined, true)
      assert.equal(obj.length, 30)
    })
    it('get groups of user that are not in database', async function() {
      // Arrange
        const userId = crypto.randomUUID()
      // Act 
      try {
        await secaServices.getGroup(1 , userId,)
      } catch(e) {
        assert.equal(e.code, 4)
        assert.equal(e.description, 'User not found') 
        return
      }
      assert.fail("Exception should be thrown")
    })
    it(' get event by name', async function(){
      let eventName="Event 3"
      let obj =await secaServices.getEventByName(eventName,10,1)
      assert.equal(obj!=undefined,true)
      assert.equal(eventName,obj.name)
      assert.equal(3,obj.id)
     

    })
    it(' create group', async function() {
      let userId= users[0].token
      let newGroup = await secaServices.createGroup({name:"createGroupTest", events:[]}, userId)
      assert.equal(newGroup != undefined, true)
    })
    it('delete a group', async function() {
      let userId= users[0].token
  
      await secaServices.deleteGroup(1,userId)
     
      try{
       await secaServices.getGroup( 1,userId)
      }catch(e){
        assert.equal(e.code, 2)
        assert.equal(`groupId not found`, e.description)
        return
      }
      assert.fail("It should have throwed exception")
    })
    it('Create Group and update group', async function() {
      // Arrange
      let userId = users[0].token
      let newGroup = await secaServices.createGroup({name:"createGroupTest", events:[]}, userId)
      const idGroup = newGroup.id
      await secaServices.updateGroup({name:"change",events:[]},idGroup,userId)
      const group = await secaServices.getGroup( idGroup,userId)
      assert.equal(group.name, "change")
    })
    it('Add event to a group and delete it', async function() {
      let userId = users[0].token
      let newGroup = await secaServices.createGroup({name:"createGroupTest", events:[]}, userId)
      const idGroup = newGroup.id
      
      await secaServices.addEventToGroup( idGroup,userId,{id: 8, name: "event 8"} )
      const group =await secaServices.getGroup( idGroup,userId)
      
      assert.equal(group.events[0].name, "event 8")
      await secaServices.deleteEventFromGroup(idGroup,8, userId)
      assert.equal(group.events.length, 0)
    })
    it('Create 3 groups and get All groups', async function() {
      let userId = users[0].token
      await secaServices.createGroup({name:"createGroupTest", events:[]}, userId)
      await secaServices.createGroup({name:"createGroupTest1", events:[]}, userId)
      await secaServices.createGroup({name:"createGroupTest3", events:[]}, userId)
      const groups = await secaServices.getAllGroups(userId)
      assert.equal(groups.length, 6)
    })
    })
  })

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



import fetch from "node-fetch";
import { writeFile } from "node:fs";
import fs from "node:fs/promises"

const key = "k_b6ggrexw"

let totalDuration = 0

const readPromise = fs.readFile("movie-ids.json")

readPromise
    .then(data => {
        const movieIdsArr = JSON.parse(data)['movie-ids']
        movieIdsArr.forEach(movie_id => {
            fetch('https://imdb-api.com/API/Title/'+key+'/'+movie_id)
            .then (reply => reply.json())
            .then (data => {
                totalDuration += parseInt(data.runtimeMins, 10)
                let returnObject = {"total-duration": totalDuration, "movies": [] }
                const newObject = { }
                newObject["id"] = data.id
                newObject["title"] = data.title
                newObject["duration"] = data.runtimeMins
                returnObject.movies.push(newObject) 
            })
        });
    })
    .catch(error => console.log("Catch1",error))


fs.writeFile('./movies.json', JSON.stringify(returnObject, null, 2), error => {
    if (error){
        console.log(error)
    }
    else {
        console.log('.json file created')
    }
} ) 
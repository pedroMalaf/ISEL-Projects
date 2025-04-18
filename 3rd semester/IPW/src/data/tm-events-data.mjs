import fetch from 'node-fetch';
const API_KEY1='HADvTjEoGGQ79J5A7vqTfJ7U65myvSHw'
const URL='https://app.ticketmaster.com/discovery/v2/events.json?apikey='+API_KEY1

export default function(){

    return {
        getPopularEvents,
        getEventByName
    }

    async function getPopularEvents(size, page) {
        const eventFetch = await fetch(URL + `&sort + &size=${size}` + `&page=${page}`)
        const handle = await eventFetch.json()
        const events = handle._embedded.events.map(element => ({
            name: element.name,
            id: element.id,
            date: element.dates.start.dateTime,
            segment: element.classifications[0].segment.name,
            genre: element.classifications[0].genre.name,
        }));
        return events 
    }
    async function getEventByName(eventName, size, page) {
        const eventFetch = await fetch(URL + `&size=${size}` + `&page=${page}` + `&keyword=${eventName}`)
        const handle = await eventFetch.json()
        if(handle.hasOwnProperty('_embedded')){
            const events = handle._embedded.events.map(element => {
                
                const saleStart = element.sales && element.sales.public && element.sales.public.startDateTime;
                const saleEnd = element.sales && element.sales.public && element.sales.public.endDateTime;
                let segment = '??';
                if(element.classifications !== undefined) segment=element.classifications[0].segment.name
                let genre='??'
                if(element.classifications !== undefined) genre =element.classifications[0].genre.name;
                let subGenre='??'
                if(element.classifications !== undefined)element.classifications[0].subGenre.name;
                const image = element.images[0]?.url;
    
                return {
                    name: element.name,
                    id: element.id,
                    saleStart: saleStart || '??',
                    saleEnd: saleEnd || '??',
                    date: element.dates.start.dateTime || '??',
                    segment: segment,
                    genre: genre,
                    subGenre: subGenre,
                    image: image || '??',
                };
        });
        return events }
        return undefined
    }
 
}
 

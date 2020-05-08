import {urlPrefix} from './constants'

export const createGame = (gameCreateParams)=> fetch(`${urlPrefix}/api/game`,{
        method:'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(gameCreateParams)
    }).then(response=> {
        return response.json();
        
    })

export const fetchForGame =  (name) => fetch(`${urlPrefix}/api/game/${name}`).then(response=>response.json())

export const touchACell = (name,row,col) => fetch(`${urlPrefix}/api/game/${name}/open?row=${row}&col=${col}`)
.then(response=>response.json())

export const flagACell = (name,row,col) => fetch(`${urlPrefix}/api/game/${name}/flag?row=${row}&col=${col}`)
.then(response=>response.json())
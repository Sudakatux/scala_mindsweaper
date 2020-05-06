import {urlPrefix} from './constants'

export const createGame = async (gameCreateParams)=>{
    const response = await fetch(`${urlPrefix}/api/game`,{
        method:'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(gameCreateParams)
    });
    return response.json()
}
export const fetchForGame =  (name) => fetch(`${urlPrefix}/api/game/${name}`).then(response=>response.json())

export const touchACell = (name,row,col) => fetch(`${urlPrefix}/api/game/${name}/open?row=${row}&col=${col}`)
.then(response=>response.json())
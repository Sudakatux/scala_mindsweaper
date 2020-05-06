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
import React, {useState} from 'react';
import { Redirect } from 'react-router-dom';
import {isNil,isEmpty} from 'ramda';
import mind from './images/mind.svg';
import {createGame} from './api';


const Field = ({name,type="number",onChangeValue, state={}})=>(
<>
    <label className="form-label">{name.split(/(?=[A-Z])/).join(' ')}</label>
    <input type={type} name={name} onChange={onChangeValue(type,name)} value={state[name]}/>
</>)



export const NewGame = ()=>{
    const [state,setState] = useState({
        gameName:'',
        rowCount: 4,
        colCount: 3,
        bombAmount: 1,
        created: false,
        status:'',
    })

    const onChangeValue = (type, fieldKey)=>
        ({target:{value}}) => 
        setState({...state,[fieldKey]: type==='number' ? parseInt(value) : value})
    
    const onSubmit = () => createGame(state)
    .then(({status})=>
        isNil(status)?{...state,created:true}:{...state,status})
    .then(setState)
     


    if(state.created) {
        return (<Redirect to={`/game/${state.gameName}`}/>)
    }

    const errorExists = !isEmpty(state.status)
    
    return (
    <div className="form-container">
        <div>
            <img width={400} height={400} src={mind} alt="mind-logo"/>
        </div>
        {errorExists && <div className="error">{state.status}</div>}
        <div className="form-item">
            <Field name="gameName" type="text" onChangeValue={onChangeValue}/>
        </div>
        <div className="form-item">
            <Field name="rowCount" onChangeValue={onChangeValue}/>
        </div>
        <div className="form-item">
            <Field name="colCount" onChangeValue={onChangeValue}/>
        </div>
        <div className="form-item">
            <Field name="bombAmount" onChangeValue={onChangeValue}/>
        </div>
        <div>
            <button onClick={onSubmit}>Create Game</button>
        </div>
    </div>
)}
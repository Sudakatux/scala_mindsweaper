import React, {useState} from 'react';
import { Redirect } from 'react-router-dom';

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
    })

    const onChangeValue = (type, fieldKey)=>
        ({target:{value}}) => 
        setState({...state,[fieldKey]: type==='number' ? parseInt(value) : value})
    
    const onSubmit = () => {
        const submitValue = async ()=>{
            await createGame(state);
            setState({ ...state, created:true});
        }
        submitValue();
    }

    if(state.created) {
        return (<Redirect to={`/game/${state.gameName}`}/>)
    }
    
    return (
    <div className="form-container">
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
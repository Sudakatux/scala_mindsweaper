import React, {useState} from 'react';

const urlPrefix = 'http://localhost:9000'

const Field = ({name,type="number",onChangeValue, state={}})=>(
<>
<label>{name}</label>
<input type={type} name={name} onChange={onChangeValue(type,name)} value={state[name]}/>
</>)

const onSubmit = async (formState)=>{
    const response = await fetch(`${urlPrefix}/api/game`,{
        method:'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formState)
    });
    console.log('Creation succesfull',response.json())
}

export const NewGame = ()=>{
    const [state,setState] = useState({
        gameName:'',
        rowCount:4,
        colCount:3,
        bombAmount:1
    })

    const onChangeValue = (type,fieldKey)=> ({target:{value}}) => setState({...state,[fieldKey]: type==='number' ? parseInt(value) : value})
    
    return (
    <div className="container">
        <div className="item">
            <Field name="gameName" type="text" onChangeValue={onChangeValue}/>
        </div>
        <div className="item">
            <Field name="rowCount" onChangeValue={onChangeValue}/>
        </div>
        <div className="item">
            <Field name="colCount" onChangeValue={onChangeValue}/>
        </div>
        <div className="item">
            <Field name="bombAmount" onChangeValue={onChangeValue}/>
        </div>
        <div>
            <button onClick={()=>onSubmit(state)}>Create Game</button>
        </div>
    </div>
)}
import React,{useEffect,useState} from 'react';
import { useParams } from 'react-router-dom';
import {splitEvery, isEmpty} from 'ramda'
import {urlPrefix} from './constants'

const EmptyCell = () => (<div className="cell empty-cell"/>)
const AdjacentCell = ({amount}) => (<div className="cell adjacent-cell">{amount}</div>)
const UnknownCell = ({onClick}) => (<div onClick={onClick} className="cell unknown-cell"/>)
const Bomb = ({onClick}) => (<div onClick={onClick} className="cell bomb-cell"/>)

const colManager = (playCell) => (row,colIdx) =>(
    <div className="game-row">
        {row.map(({cellType,display},rowIdx)=>{
            switch (cellType) {
                case 'Empty':
                 return <EmptyCell key={`${colIdx}_${rowIdx}`}/>;
                case 'Adjacent':
                  return <AdjacentCell key={`${colIdx}_${rowIdx}`} amount={display}/>
                case 'Bomb':
                  return <Bomb key={`${colIdx}_${rowIdx}`} />
                default:
                  return <UnknownCell onClick={()=>playCell(rowIdx,colIdx)} />;
            }
            }
        )}
    </div>
)

const fetchForGame =  (name) => fetch(`${urlPrefix}/api/game/${name}`).then(response=>response.json())
const playOnCellEffect = (name,stateUpdateEffect) => 
                        (row,col) => fetch(`${urlPrefix}/api/game/${name}/open?row=${row}&col=${col}`)
                        .then(response=>response.json())
                        .then(json=>stateUpdateEffect(json))

export const ExistingGame = ()=>{
    const {name} = useParams();
    const [state,setState] = useState({board:[],rowCount:0,name:'',gameState:''});
    useEffect(() => {
        const fetchData = async () => {
         const data =  await fetchForGame(name);
         setState(data);
        }
        fetchData();
    },[name]);

    const {board=[],rowCount=1} = state;
    console.log('This is the state',state);
    
    if(isEmpty(board)){
        return <div>Loading...</div>;
    }
    
    const partitionByRowCount = splitEvery(rowCount,board);
    const renderCol = colManager(playOnCellEffect(name,setState));
    
    return (
    <div className="game-container">
      {partitionByRowCount.map(renderCol)}
    </div>)
}